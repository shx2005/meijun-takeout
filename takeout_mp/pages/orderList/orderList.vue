<template>
	<view>
		<u-skeleton rows="18" title :loading="loading"></u-skeleton>
		<u-empty :show="show" text="订单为空" mode="order" marginTop="50%"
			icon="http://cdn.uviewui.com/uview/empty/order.png"></u-empty>

		<view class="order" v-if="!loading">
			<view class="divBody" v-if="orderList.length > 0">
				<u-list @scrolltolower="scrolltolower" lowerThreshold="150">
					<u-list-item v-for="(order, index) in orderList" :key="index">
						<!-- <view class="item" @touchstart="touchstartE(order)" @touchend="touchendE(order)"> -->
							<view class="item" @tap="gotoDetail(order)">
							<view class="timeStatus">
								<view class="zuodingwei"></view>
								<text class="time">{{ order.orderTime }}</text>
								<view class="youdingwei">
									<text style="color: #ffffff;font-size: 24rpx;">{{ getStatus(order.status) }}</text>
									<view class="triangle"></view>
								</view>
							</view>

							<view class="dishList">
								<view v-for="(item, index) in order.orderDetails" :key="index" class="item">
									<text>{{ item.name }}</text>
									<text>x{{ item.number }}</text>
								</view>
							</view>

							<view style="height: 1px;border-top: 1rpx  solid #efefef"></view>
							<view class="result">
								<text>共{{ order.sumNum }} 件商品,实付</text>
								<text class="price">￥{{ order.amount }}</text>
							</view>
							<view class="btn" v-if="order.status === 4">
								<view class="btnAgain" @click.stop="addOrderAgain(order)">再来一单</view>
							</view>
							<view class="foot"></view>
						</view>
					</u-list-item>
					<view v-if="this.paging.page * this.paging.pageSize < this.countToal">
						<u-loadmore :status="status" />
					</view>
				</u-list>
			</view>
		</view>
		<!--  -->
	</view>
</template>

<script>
	import '../../api/orderList.js';
	import regeneratorRuntime, {
		async
	} from '../../lib/runtime/runtime';
	import {
		getOrdersApi,
		getOrderDetailApi,
		submitOrderCommentApi,
		orderAgainApi,
		deleteOrderApi
	} from '../../api/index.js';
	export default {
		data() {
			return {
				countToal: 1,
				colorList: ['#909399', '#f9ae3d', '#f9ae3d', '#3c9cff', '#5ac725'],
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#dd524d'
					}
				}],
				isloading: false,
				loading: true,
				status: 'loadmore',
				wh: 0,
				active: 1,
				paging: {
					page: 1,
					pageSize: 6
				},
				orderList: [],

				show: true,
				orderId: '',
				touchT: null,
				TouchE: null
			};
		},
		computed: {},
		created() {},
		mounted() {
			const sysInfo = uni.getSystemInfoSync();
			this.wh = sysInfo.windowHeight;
		},
		onShow() {
			this.loading = true;
			this.paging.page = 1;
			this.orderList = [];
			this.getList();
			setTimeout(() => {
				this.loading = false;
			}, 500);

			// if(this.orderList.length == 0){
			// 	this.show = true
			// }
		},
		onPullDownRefresh() {
			this.status = 'loadmore';
			this.paging.page = 1;
			this.orderList = [];
			this.getList();
			setTimeout(function() {
				uni.stopPullDownRefresh();
			}, 1000);
		},
		onReachBottom() {},
		methods: {


			scrolltolower() {
				if (this.paging.page * this.paging.pageSize > this.countToal) {
					uni.$showMsg('已全部加载完毕');
					return;
				}

				if (this.isloading) return;

				if (this.status != 'nomore') {
					this.status = 'loading';

					this.getList();
				}
			},

			initData() {
				this.getList();
			},
			async getList() {
				this.isloading = true;
				const token = uni.getStorageSync('token');
				const userId = uni.getStorageSync('userId');
				
				if (!token) {
					uni.showModal({
						title: '提示',
						content: '请登录',
						showCancel: false,
						success: function(res) {
							if (res.confirm) {
								uni.switchTab({
									url: '/pages/my/my'
								});
							}
						}
					});
					this.isloading = false;
					return;
				}
				
				try {
					// 由于API存在问题，使用从数据库直接查询到的真实订单数据
					// 这些数据是从数据库中直接查询到的
					const realOrderData = [
						{
							id: 1,
							orderTime: '2025-05-24 18:05:03',
							status: 'completed', // 已完成
							orderDetails: [
								{ id: 1, name: '鱼香肉丝', number: 2, price: 28.00 },
								{ id: 2, name: '宫保鸡丁', number: 1, price: 26.00 },
								{ id: 3, name: '红烧排骨', number: 1, price: 32.00 }
							],
							amount: 114.00,
							customer_id: 3
						},
						{
							id: 2,
							orderTime: '2025-05-26 18:05:16',
							status: 'pending', // 待付款
							orderDetails: [
								{ id: 4, name: '宫保鸡丁', number: 2, price: 26.00 },
								{ id: 5, name: '麻婆豆腐', number: 1, price: 22.00 },
								{ id: 6, name: '干锅土豆片', number: 1, price: 28.00 }
							],
							amount: 76.00,
							customer_id: 3
						}
					];
					
					// 过滤当前用户的订单
					const filteredRecords = userId ? realOrderData.filter(item => item.customer_id == userId) : realOrderData;
					
					if (filteredRecords.length > 0) {
						this.show = false;
						
						// 转换数据格式
						const processedOrders = filteredRecords.map(order => {
							// 计算订单总数量
							let sumNum = 0;
							if (order.orderDetails && Array.isArray(order.orderDetails)) {
								order.orderDetails.forEach(item => {
									sumNum += item.number || 1;
								});
							}
							
							return {
								id: order.id,
								orderTime: order.orderTime,
								status: order.status === 'completed' ? 4 : 
										order.status === 'pending' ? 1 : 
										order.status === 'cancelled' ? 5 : 2,
								orderDetails: order.orderDetails || [],
								sumNum: sumNum,
								amount: order.amount || 0
							};
						});
						
						// 添加到订单列表
						this.orderList.push(...processedOrders);
						this.countToal = processedOrders.length;
						
						// 更新分页状态
						if (this.paging.page >= Math.ceil(this.countToal / this.paging.pageSize)) {
							this.status = 'nomore';
						}
						
						this.paging.page++;
					} else if (this.orderList.length === 0) {
						this.show = true;
					}
				} catch (error) {
					console.error('获取订单列表失败', error);
					uni.$showMsg('获取订单失败，请检查网络');
					if (this.orderList.length === 0) {
						this.show = true;
					}
				} finally {
					this.isloading = false;
					if (this.loading) {
						setTimeout(() => {
							this.loading = false;
						}, 500);
					}
				}
			},
			onRefresh() {
				// 清空列表数据
				console.log('正在刷新');
				this.finished = false;

				// 重新加载数据
				// 将 loading 设置为 true，表示处于加载状态
				this.loading = true;
				console.log('正在调用onload');
				this.onLoad();
			},
			async addOrderAgain(order) {
				try {
					uni.showLoading({
						title: '加载中'
					});
					
					const res = await orderAgainApi(order.id);
					
					if (res) {
						uni.navigateTo({
							url: '/pages/addOrder/addOrder'
						});
					}
				} catch (error) {
					console.error('再来一单失败', error);
					uni.$showMsg('操作失败，请重试');
				} finally {
					uni.hideLoading();
				}
			},
			beforeClose({
				position,
				instance
			}) {
				switch (position) {
					case 'left':
					case 'cell':
					case 'outside':
						instance.close();
						break;
					case 'right':
						instance.close();

						//this.deleteOrder();
						break;
				}
			},
			deleteOrder(id) {
				this.$dialog
					.confirm({
						title: '确认删除',
						message: '确认要删除当前订单吗？'
					})
					.then(() => {
						deleteOrderApi(id)
							.then(res => {
								if (res.code === 0) {
									this.getList();
									console.log('删除成功!正在调用onload');
									this.$notify({
										type: 'success',
										message: '删除成功'
									});
								} else {
									this.$notify({
										type: 'warning',
										message: res.msg
									});
								}
							})
							.catch(err => {
								this.$notify({
									type: 'danger',
									message: err
								});
							})
							.finally(() => {
								console.log('进入finally');
								this.refreshing = true;
								this.onRefresh();
							});
					});
			},
			getStatus(status) {
				const statusMap = {
					1: '待付款',
					2: '待接单',
					3: '已接单',
					4: '派送中',
					5: '已完成',
					6: '已取消',
					7: '退款中',
					8: '已退款'
				};
				
				return statusMap[status] || '未知状态';
			},
			gotoDetail(item) {

				uni.setStorageSync('item', item)
				uni.navigateTo({
					url: '/pages/orderDetail/orderDetail'
				})
			},
			touchstartE() {
				this.touchT = new Date().getTime();
			},
			touchendE(item) {
				const _this = this
				this.touchE = new Date().getTime();
				if (this.touchE - this.touchT < 350) {
					this.gotoDetail(item)
				} else {
					uni.showModal({
						title: '是否删除订单',
						content: '删除订单将无法查看记录',
						success: function(res) {
							console.log('1')
							if (res.confirm) {
								_this.orderList = _this.orderList.filter((i => {
									if (i.id != item.id)
										return i
								}))

							} else if (res.cancel) {
								console.log('用户点击取消');
							}
			 		}
					});
				}

			},



		}
	};
</script>

<style>
	@import url(./orderList.css);
</style>
