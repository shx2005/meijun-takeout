<template>
	<view class="order-list-container">
		<scroll-view scroll-y="true" class="order-list-scroll" :style="{height: scrollHeight + 'px'}" @scrolltolower="onScrollToLower" refresher-enabled="true" :refresher-triggered="refresherTriggered" @refresherrefresh="onRefresh">
	<view class="order-list">
				<u-empty v-if="orders.length === 0" mode="order" icon="http://cdn.uviewui.com/uview/empty/order.png" text="暂无订单"></u-empty>
		<view v-for="order in orders" :key="order.id" class="order-card">
			<view class="order-header">
				<text class="order-id">订单号：{{ order.id }}</text>
						<text :class="['order-status', getStatusClass(order.status)]">{{ getStatusText(order.status) }}</text>
			</view>
					<view class="order-time">下单时间：{{ formatOrderTime(order.orderTime) }}</view>
			<view class="order-items">
						<view v-for="(item, index) in order.items" :key="index" class="order-item">
							<text>{{ item.name || '菜品' }} x{{ item.quantity }}</text>
						</view>
						<view v-if="order.items && order.items.length > 3" class="more-items">
							<text>等{{ order.items.length }}件商品</text>
				</view>
			</view>
			<view class="order-total">总金额：￥{{ (order.totalAmount / 100).toFixed(2) }}</view>
					<view class="order-actions">
						<button class="action-btn detail-btn" @tap="viewOrderDetail(order)">订单详情</button>
						<button class="action-btn after-sale-btn" v-if="canApplyAfterSale(order)" @tap="applyAfterSale(order)">申请售后</button>
					</view>
				</view>
				
				<!-- 加载更多提示 -->
				<u-loadmore :status="loadStatus" :icon-type="iconType" :load-text="loadText" />
		</view>
		</scroll-view>
	</view>
</template>

<script>
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import regeneratorRuntime, {
		async
	} from '../../lib/runtime/runtime';
	import {
		addOrderApi,
		orderListApi,
		orderPagingApi,
		orderAgainApi,
		deleteOrderApi,
		getOrderDetailApi,
		applyAfterSaleApi,
		getOrdersApi,
		getOrderOverviewApi
	} from '../../api/orderList.js';
	
	// 调试模式配置
	const DEBUG_MODE = false; // 禁用调试模式，始终使用真实API数据
	
	export default {
		mixins: [MescrollMixin], // 使用mixin
		data() {
			return {
				scrollHeight: 0, // 滚动区域高度
				refresherTriggered: false, // 下拉刷新状态
				orders: [],
				page: 1,
				pageSize: 10,
				total: 0,
				loadStatus: 'loadmore', // loadmore, loading, nomore
				iconType: 'flower',
				loadText: {
					loadmore: '点击或上拉加载更多',
					loading: '正在加载...',
					nomore: '没有更多了'
				},
				// 测试数据，仅在调试模式使用
				testOrders: [
					{
						id: '20240601001',
						orderTime: '2024-06-01 12:30:00',
						status: 1, // 待付款
						totalAmount: 5680,
						items: [
							{ itemId: '1', name: '红烧肉', quantity: 1 },
							{ itemId: '2', name: '宫保鸡丁', quantity: 2 }
						]
					},
					{
						id: '20240601002',
						orderTime: '2024-06-01 13:10:00',
						status: 3, // 已派送
						totalAmount: 4200,
						items: [
							{ itemId: '3', name: '糖醋排骨', quantity: 1 },
							{ itemId: '4', name: '清蒸鱼', quantity: 1 }
						]
					},
					{
						id: '20240531001',
						orderTime: '2024-05-31 18:30:00',
						status: 4, // 已完成
						totalAmount: 6980,
						items: [
							{ itemId: '5', name: '麻婆豆腐', quantity: 1 },
							{ itemId: '6', name: '水煮鱼', quantity: 1 },
							{ itemId: '7', name: '米饭', quantity: 2 },
							{ itemId: '8', name: '可乐', quantity: 2 }
						]
					},
					{
						id: '20240530001',
						orderTime: '2024-05-30 12:15:00',
						status: 5, // 已取消
						totalAmount: 3200,
						items: [
							{ itemId: '9', name: '黄焖鸡米饭', quantity: 1 },
							{ itemId: '10', name: '矿泉水', quantity: 1 }
						]
					}
				]
			}
		},
		onLoad() {
			// 获取系统信息设置滚动区域高度
			const systemInfo = uni.getSystemInfoSync();
			this.scrollHeight = systemInfo.windowHeight;
			
			// 检查登录状态
			const token = uni.getStorageSync('token');
			if (!token && !DEBUG_MODE) {
				uni.showModal({
					title: '提示',
					content: '请先登录',
					showCancel: false,
					success: () => {
						uni.switchTab({
							url: '/pages/my/my'
						});
					}
				});
				return;
			}
			
			// 清除缓存数据
			try {
				uni.removeStorageSync('orderHistoryData');
				console.log('已清除缓存的订单历史数据');
			} catch (e) {
				console.error('清除订单缓存失败:', e);
			}
			
			// 初始化数据
			this.loadOrders();
		},
		// 每次显示页面时触发，确保数据实时更新
		onShow() {
			console.log('订单页面显示，重新加载数据');
			
			// 清除缓存数据
			try {
				uni.removeStorageSync('orderHistoryData');
				console.log('显示页面时清除缓存的订单历史数据');
			} catch (e) {
				console.error('清除订单缓存失败:', e);
			}
			
			// 重置页码并刷新数据
			this.page = 1;
			this.loadOrders();
		},
		// 下拉刷新
		downCallback() {
			this.page = 1;
			this.loadOrders();
		},
		// 上拉加载
		upCallback() {
			this.page++;
			this.loadOrders(true);
		},
		methods: {
			// 加载订单数据
			async loadOrders(isLoadMore = false, isRefresh = false) {
				try {
					this.loadStatus = 'loading';
					
					// 显示加载提示
					if (!isLoadMore) {
						uni.showLoading({
							title: '加载中...'
						});
					}
					
					if (DEBUG_MODE) {
						// 使用测试数据
						setTimeout(() => {
							if (!isLoadMore) {
								this.orders = [...this.testOrders];
							} else {
								// 模拟加载更多，实际应用中不需要这样
								if (this.page <= 3) {
									// 创建新的测试数据，修改ID以示区别
									const moreOrders = this.testOrders.map(order => ({
										...order,
										id: order.id + this.page
									}));
									this.orders = [...this.orders, ...moreOrders];
								}
							}
							
							this.loadStatus = this.page <= 3 ? 'loadmore' : 'nomore';
							
							// 结束下拉刷新
							if (isRefresh) {
								this.refresherTriggered = false;
							}
							
							// 结束mescroll的刷新
							if (this.mescroll && this.mescroll.endSuccess) {
								this.mescroll.endSuccess();
							}
							
							// 隐藏加载提示
							uni.hideLoading();
						}, 1000);
						return;
					}
					
					// 清除缓存确保获取最新数据
					try {
						uni.removeStorageSync('orderHistoryData');
					} catch (e) {
						console.error('清除订单缓存失败:', e);
					}
					
					console.log('正在请求最新订单数据');
					
					// 使用获取订单概览的API，获取所有订单
					const res = await getOrderOverviewApi();
					
					if (res && (res.code === 0 || res.code === 200) && res.data) {
						const orderList = Array.isArray(res.data) ? res.data : [];
						
						// 处理并格式化订单数据
						const formattedOrders = orderList.map(order => {
							// 格式化创建时间
							let orderTime = '';
							if (order.createTime && Array.isArray(order.createTime)) {
								const [year, month, day, hour, minute, second] = order.createTime;
								orderTime = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')} ${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}:${second.toString().padStart(2, '0')}`;
							}
							
							// 格式化状态
							let status = 0;
							switch(order.status) {
								case 'pending': status = 1; break;  // 待付款
								case 'delivering': status = 2; break;  // 正在派送
								case 'delivered': status = 3; break;  // 已派送
								case 'completed': status = 4; break;  // 已完成
								case 'cancelled': status = 5; break;  // 已取消
								default: status = 0;
							}
							
							return {
								id: order.id,
								orderTime: orderTime || order.orderTime || '',
								status: status,
								totalAmount: order.total * 100 || 0, // 转换为分
								items: order.items || [] // 可能需要根据实际数据结构调整
							};
						});
						
						this.orders = formattedOrders;
						this.total = formattedOrders.length;
						this.loadStatus = 'nomore'; // 全部加载完毕
						
						console.log('成功从API获取订单数据:', formattedOrders);
					} else {
						uni.showToast({
							title: res?.msg || '获取订单失败',
							icon: 'none'
						});
						this.loadStatus = 'loadmore';
					}
				} catch (error) {
					console.error('获取订单列表失败', error);
					uni.showToast({
						title: '获取订单列表失败',
						icon: 'none'
					});
					this.loadStatus = 'loadmore';
				} finally {
					// 结束下拉刷新
					if (isRefresh) {
						this.refresherTriggered = false;
					}
					
					// 结束mescroll的刷新
					if (this.mescroll && this.mescroll.endSuccess) {
						this.mescroll.endSuccess();
					}
					
					// 隐藏加载提示
					uni.hideLoading();
				}
			},
			
			// 获取订单状态文本
			getStatusText(status) {
				switch (status) {
					case 1: return '待付款';
					case 2: return '正在派送';
					case 3: return '已派送';
					case 4: return '已完成';
					case 5: return '已取消';
					default: return '未知';
				}
			},
			
			// 获取订单状态对应的样式类
			getStatusClass(status) {
				switch (status) {
					case 1: return 'status-pending';
					case 2: return 'status-shipping';
					case 3: return 'status-delivered';
					case 4: return 'status-completed';
					case 5: return 'status-cancelled';
					default: return '';
				}
			},
			
			// 格式化订单时间
			formatOrderTime(timeStr) {
				if (!timeStr) return '';
				
				// 简单格式化，实际项目中可能需要更复杂的处理
				return timeStr.replace('T', ' ').substring(0, 19);
			},
			
			// 判断是否可以申请售后
			canApplyAfterSale(order) {
				// 只有已完成的订单可以申请售后，且时间在7天内
				if (order.status !== 4) return false;
				
				// 获取订单时间，计算是否在7天内
				const orderTime = new Date(order.orderTime.replace('T', ' '));
				const now = new Date();
				const diffDays = Math.floor((now - orderTime) / (24 * 60 * 60 * 1000));
				
				return diffDays <= 7;
			},
			
			// 查看订单详情
			viewOrderDetail(order) {
				uni.navigateTo({
					url: `/pages/orderDetail/orderDetail?orderId=${order.id}`
				});
			},
			
			// 申请售后
			applyAfterSale(order) {
				uni.navigateTo({
					url: `/pages/afterSale/afterSale?orderId=${order.id}&amount=${order.totalAmount}`
				});
			},
			
			// 监听滚动到底部
			onScrollToLower() {
				if (this.loadStatus !== 'loading' && this.loadStatus !== 'nomore') {
					this.page++;
					this.loadOrders(true);
				}
			},
			
			// 下拉刷新
			onRefresh() {
				this.refresherTriggered = true;
				this.page = 1;
				this.loadOrders(false, true);
			}
		}
	}
</script>

<style scoped>
	.order-list-container {
		position: relative;
		width: 100%;
		height: 100vh;
		background-color: #f8f8f8;
	}
	
	.order-list-scroll {
		width: 100%;
	}
	
	.order-list {
		padding: 24rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}
	
	.order-card {
		background: #fff;
		border-radius: 12rpx;
		box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
		margin-bottom: 24rpx;
		padding: 24rpx;
	}
	
	.order-header {
		display: flex;
		justify-content: space-between;
		margin-bottom: 12rpx;
	}
	
	.order-id {
		color: #666;
		font-size: 28rpx;
	}
	
	.order-status {
		font-size: 28rpx;
		font-weight: bold;
	}
	
	.status-pending {
		color: #ff9900;
	}
	
	.status-shipping {
		color: #007aff;
	}
	
	.status-delivered {
		color: #4cd964;
	}
	
	.status-completed {
		color: #8f8f8f;
	}
	
	.status-cancelled {
		color: #ff3b30;
	}
	
	.order-time {
		color: #999;
		font-size: 24rpx;
		margin-bottom: 16rpx;
	}
	
	.order-items {
		margin-bottom: 16rpx;
		padding: 12rpx;
		background-color: #f9f9f9;
		border-radius: 8rpx;
	}
	
	.order-item {
		color: #333;
		font-size: 26rpx;
		margin-bottom: 8rpx;
	}
	
	.more-items {
		color: #999;
		font-size: 24rpx;
	}
	
	.order-total {
		color: #ff5050;
		font-weight: bold;
		font-size: 28rpx;
		text-align: right;
		margin-bottom: 16rpx;
	}
	
	.order-actions {
		display: flex;
		justify-content: flex-end;
		border-top: 1rpx solid #eee;
		padding-top: 16rpx;
	}
	
	.action-btn {
		font-size: 24rpx;
		padding: 10rpx 20rpx;
		border-radius: 30rpx;
		margin-left: 16rpx;
		border: none;
		line-height: 1.5;
	}
	
	.detail-btn {
		background-color: #f5f5f5;
		color: #666;
	}
	
	.after-sale-btn {
		background-color: #fff;
		color: #ff9900;
		border: 1rpx solid #ff9900;
	}
</style>
