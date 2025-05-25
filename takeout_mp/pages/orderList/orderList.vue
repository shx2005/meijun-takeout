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
							<view class="item">
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
								<view class="btnAgain" @click="addOrderAgain(order)">再来一单</view>
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
				// const token = uni.getStorageSync('token'); // 获取token的语句可以保留或删除
				
				// if (!token) { // 移除或注释掉登录检查和强制跳转
				// 	uni.showModal({
				// 		title: '提示',
				// 		content: '请登录',
				// 		showCancel: false,
				// 		success: function(res) {
				// 			if (res.confirm) {
				// 				uni.switchTab({
				// 					url: '/pages/my/my'
				// 				});
				// 			}
				// 		}
				// 	});
				// 	this.isloading = false;
				// 	return;
				// }
				
				try {
					const res = await getOrdersApi({
						page: this.paging.page,
						size: this.paging.pageSize
					});
					
					if (res && Array.isArray(res.records)) {
						// 处理订单数据
						const records = res.records;
						
						if (records.length > 0) {
							this.show = false;
							
							// 计算订单商品总数
							records.forEach(item => {
								let number = 0;
								if (item.orderDetails && Array.isArray(item.orderDetails)) {
									item.orderDetails.forEach(ele => {
										number += ele.number;
									});
								}
								item.sumNum = number;
							});
							
							// 添加到订单列表
							this.orderList.push(...records);
							this.countToal = res.total || records.length;
							
							// 更新分页状态
							if (this.paging.page >= (res.pages || Math.ceil(this.countToal / this.paging.pageSize))) {
								this.status = 'nomore';
							}
							
							this.paging.page++;
						} else if (this.orderList.length === 0) {
							this.show = true;
						}
					} else {
						uni.$showMsg('获取订单失败');
						if (this.orderList.length === 0) {
							this.show = true;
						}
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
