<template>
	<view class="merchant-order-overview">
		<view class="header">
			<text class="title">订单概览</text>
		</view>
		
		<!-- 订单状态筛选 -->
		<view class="filter-tabs">
			<view 
				v-for="(tab, index) in statusTabs" 
				:key="index" 
				:class="['tab-item', currentStatus === tab.value ? 'active' : '']"
				@click="changeStatus(tab.value)"
			>
				{{tab.name}}
			</view>
		</view>
		
		<!-- 订单列表 -->
		<scroll-view scroll-y class="order-scroll-view" @scrolltolower="loadMore">
			<view class="order-list">
				<template v-if="orderList.length > 0">
					<view v-for="(order, index) in orderList" :key="index" class="order-item">
						<view class="order-header">
							<text class="order-number">订单号：{{order.orderNumber || `ORDER-${order.id}`}}</text>
							<view class="status-group">
								<text :class="['order-status', getStatusClass(order.status)]">{{getStatusText(order.status)}}</text>
								<text :class="['pay-status', getPayStatusClass(order.payStatus)]">{{getPayStatusText(order.payStatus)}}</text>
							</view>
						</view>
						
						<view class="order-content">
							<view class="customer-info">
								<text class="label">用户ID：</text>
								<text class="value">{{order.userId}}</text>
							</view>
							<view class="order-time">
								<text class="label">下单时间：</text>
								<text class="value">{{formatTime(order.orderTime)}}</text>
							</view>
							<view class="order-amount">
								<text class="label">订单金额：</text>
								<text class="value price">¥{{order.total}}</text>
							</view>
							
							<!-- 商品列表简要 -->
							<view class="dish-summary" v-if="order.items && order.items.length > 0">
								<text class="dish-text">{{getDishSummary(order.items)}}</text>
							</view>
							
							<view v-if="order.remark" class="order-remark">
								<text class="label">备注：</text>
								<text class="value">{{order.remark}}</text>
							</view>
						</view>
						
						<view class="order-actions">
							<view class="action-btn detail" @click="viewOrderDetail(order.id)">查看详情</view>
							<view v-if="canDeliver(order.status)" class="action-btn deliver" @click="deliverOrder(order.id)">开始配送</view>
							<view v-if="canComplete(order.status)" class="action-btn complete" @click="completeOrder(order.id)">完成订单</view>
						</view>
					</view>
				</template>
				
				<view v-else class="empty-container">
					<image class="empty-image" src="/static/images/empty-order.png" mode="aspectFit"></image>
					<text class="empty-text">暂无相关订单</text>
				</view>
			</view>
			
			<!-- 加载更多提示 -->
			<view v-if="loading" class="loading-tip">
				<text>加载中...</text>
			</view>
			<view v-if="!hasMore && orderList.length > 0" class="no-more-tip">
				<text>没有更多订单了</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { 
		getOrderDetailApi,
		deliverOrderApi,
		completeOrderApi
	} from '../../api/merchant.js';
	
	export default {
		data() {
			return {
				currentStatus: 'all',
				statusTabs: [
					{ name: '全部', value: 'all' },
					{ name: '已确认', value: 'confirmed' },
					{ name: '配送中', value: 'delivering' },
					{ name: '已完成', value: 'completed' }
				],
				orderList: [],
				loading: false,
				hasMore: true,
				page: 1,
				pageSize: 20
			}
		},
		onLoad() {
			console.log('商家订单概览页面加载');
			
			// 检查商家登录状态
			const token = uni.getStorageSync('merchantToken');
			if (!token) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				setTimeout(() => {
					uni.redirectTo({
						url: '/pages/merchantLogin/merchantLogin'
					});
				}, 1500);
				return;
			}
			
			this.loadOrderList();
		},
		methods: {
			// 切换状态筛选
			changeStatus(status) {
				this.currentStatus = status;
				this.page = 1;
				this.hasMore = true;
				this.orderList = [];
				this.loadOrderList();
			},
			
			// 加载订单列表
			async loadOrderList() {
				if (this.loading || !this.hasMore) return;
				
				try {
					this.loading = true;
					uni.showLoading({ title: '加载中...' });
					
					// 通过遍历订单ID来获取订单详情
					const startId = (this.page - 1) * this.pageSize + 1;
					const endId = this.page * this.pageSize;
					
					const orderPromises = [];
					for (let orderId = startId; orderId <= endId; orderId++) {
						orderPromises.push(
							getOrderDetailApi(orderId).then(order => ({
								success: true,
								data: order
							})).catch(error => ({
								success: false,
								orderId: orderId,
								error: error
							}))
						);
					}
					
					const results = await Promise.allSettled(orderPromises);
					
					// 过滤出成功的订单
					const newOrders = [];
					results.forEach((result, index) => {
						if (result.status === 'fulfilled' && result.value.success) {
							const order = result.value.data;
							if (order && order.id) {
								const formattedOrder = this.formatOrder(order);
								
								// 根据状态筛选
								if (this.currentStatus === 'all' || this.matchesStatus(formattedOrder.status, this.currentStatus)) {
									newOrders.push(formattedOrder);
								}
							}
						}
					});
					
					if (newOrders.length === 0) {
						this.hasMore = false;
					} else {
						this.orderList.push(...newOrders);
						this.page++;
					}
					
					console.log(`加载了 ${newOrders.length} 个订单，当前总数: ${this.orderList.length}`);
					
				} catch (error) {
					console.error('加载订单列表失败:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				} finally {
					this.loading = false;
					uni.hideLoading();
				}
			},
			
			// 格式化订单数据
			formatOrder(order) {
				return {
					id: order.id,
					orderNumber: order.orderNumber || `ORDER-${order.id}`,
					userId: order.userId || 0,
					merchantId: order.merchantId || 0,
					orderTime: order.orderTime || order.createTime,
					status: this.mapOrderStatus(order.status),
					statusText: this.getStatusText(order.status),
					payStatus: order.payStatus || 'paid',
					payStatusText: this.getPayStatusText(order.payStatus),
					total: order.total || 0,
					items: order.items || [],
					remark: order.remark || ''
				};
			},
			
			// 映射订单状态（商家端专用）
			mapOrderStatus(status) {
				const statusMap = {
					'pending': 'confirmed',     // pending映射为已确认
					'confirmed': 'confirmed',   // 已确认
					'delivering': 'delivering', // 配送中
					'completed': 'completed'    // 已完成
				};
				
				if (typeof status === 'string') {
					return statusMap[status.toLowerCase()] || 'confirmed';
				}
				
				return 'confirmed';
			},
			
			// 检查状态是否匹配筛选条件
			matchesStatus(orderStatus, filterStatus) {
				if (filterStatus === 'all') return true;
				return orderStatus === filterStatus;
			},
			
			// 获取状态文本
			getStatusText(status) {
				const mappedStatus = this.mapOrderStatus(status);
				const statusTexts = {
					'confirmed': '已确认',
					'delivering': '配送中',
					'completed': '已完成'
				};
				
				return statusTexts[mappedStatus] || '已确认';
			},
			
			// 获取状态样式类
			getStatusClass(status) {
				const mappedStatus = this.mapOrderStatus(status);
				switch (mappedStatus) {
					case 'confirmed': return 'status-confirmed';
					case 'delivering': return 'status-delivering';
					case 'completed': return 'status-completed';
					default: return 'status-confirmed';
				}
			},
			
			// 获取支付状态文本
			getPayStatusText(payStatus) {
				const statusTexts = {
					'unpaid': '未支付',
					'paid': '已支付',
					'refunded': '已退款',
					'refunding': '退款中'
				};
				
				return statusTexts[payStatus] || '已支付';
			},
			
			// 获取支付状态样式类
			getPayStatusClass(payStatus) {
				switch (payStatus) {
					case 'unpaid': return 'pay-unpaid';
					case 'paid': return 'pay-paid';
					case 'refunded': return 'pay-refunded';
					case 'refunding': return 'pay-refunding';
					default: return 'pay-paid';
				}
			},
			
			// 获取菜品摘要
			getDishSummary(items) {
				if (!items || items.length === 0) return '无商品信息';
				
				if (items.length === 1) {
					return `${items[0].name} x${items[0].quantity}`;
				} else if (items.length <= 3) {
					return items.map(item => `${item.name} x${item.quantity}`).join('，');
				} else {
					const firstTwo = items.slice(0, 2).map(item => `${item.name} x${item.quantity}`).join('，');
					return `${firstTwo} 等${items.length}件商品`;
				}
			},
			
			// 判断是否可以开始配送
			canDeliver(status) {
				return status === 'confirmed';
			},
			
			// 判断是否可以完成订单
			canComplete(status) {
				return status === 'delivering';
			},
			
			// 开始配送
			async deliverOrder(orderId) {
				uni.showModal({
					title: '确认配送',
					content: '确定该订单开始配送吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({ title: '处理中...' });
								
								const response = await deliverOrderApi(orderId);
								console.log('配送响应:', response);
								
								uni.hideLoading();
								
								if (response && (response.success || response.code === 200)) {
									this.updateOrderStatus(orderId, 'delivering');
									uni.showToast({
										title: '已开始配送',
										icon: 'success'
									});
								} else {
									throw new Error('配送失败');
								}
							} catch (error) {
								uni.hideLoading();
								console.error('开始配送失败', error);
								uni.showToast({
									title: '操作失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示
								this.updateOrderStatus(orderId, 'delivering');
							}
						}
					}
				});
			},
			
			// 完成订单
			async completeOrder(orderId) {
				uni.showModal({
					title: '确认完成',
					content: '确定该订单已完成吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({ title: '处理中...' });
								
								const response = await completeOrderApi(orderId);
								console.log('完成订单响应:', response);
								
								uni.hideLoading();
								
								if (response && (response.success || response.code === 200)) {
									this.updateOrderStatus(orderId, 'completed');
									uni.showToast({
										title: '订单已完成',
										icon: 'success'
									});
								} else {
									throw new Error('完成订单失败');
								}
							} catch (error) {
								uni.hideLoading();
								console.error('完成订单失败', error);
								uni.showToast({
									title: '操作失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示
								this.updateOrderStatus(orderId, 'completed');
							}
						}
					}
				});
			},
			
			// 本地更新订单状态
			updateOrderStatus(orderId, newStatus) {
				const order = this.orderList.find(item => item.id === orderId);
				if (order) {
					order.status = newStatus;
					order.statusText = this.getStatusText(newStatus);
				}
			},
			
			// 查看订单详情
			viewOrderDetail(orderId) {
				console.log('查看订单详情:', orderId);
				uni.navigateTo({
					url: `/pages/merchantOrderDetail/merchantOrderDetail?orderId=${orderId}`
				});
			},
			
			// 格式化时间
			formatTime(timeStr) {
				if (!timeStr) return '未知时间';
				
				try {
					let date;
					
					if (Array.isArray(timeStr)) {
						const [year, month, day, hour, minute, second] = timeStr;
						date = new Date(year, month - 1, day, hour, minute, second || 0);
					} else if (typeof timeStr === 'string') {
						if (timeStr.includes('T') || timeStr.includes('-')) {
							date = new Date(timeStr);
						} else {
							date = new Date(parseInt(timeStr));
						}
					} else if (typeof timeStr === 'number') {
						date = new Date(timeStr);
					} else {
						return '时间格式错误';
					}
					
					if (isNaN(date.getTime())) {
						return '无效时间';
					}
					
					const year = date.getFullYear();
					const month = String(date.getMonth() + 1).padStart(2, '0');
					const day = String(date.getDate()).padStart(2, '0');
					const hours = String(date.getHours()).padStart(2, '0');
					const minutes = String(date.getMinutes()).padStart(2, '0');
					
					return `${year}-${month}-${day} ${hours}:${minutes}`;
				} catch (error) {
					console.error('时间格式化失败:', error, '原始时间:', timeStr);
					return String(timeStr);
				}
			},
			
			// 加载更多
			loadMore() {
				this.loadOrderList();
			}
		}
	}
</script>

<style lang="scss" scoped>
	.merchant-order-overview {
		background-color: #f5f5f5;
		min-height: 100vh;
	}
	
	.header {
		background-color: #FF8C00;
		height: 90rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		
		.title {
			color: #fff;
			font-size: 36rpx;
			font-weight: bold;
		}
	}
	
	.filter-tabs {
		display: flex;
		background-color: #fff;
		padding: 0 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.tab-item {
			flex: 1;
			padding: 20rpx 0;
			font-size: 28rpx;
			color: #666;
			text-align: center;
			position: relative;
			
			&.active {
				color: #FF8C00;
				font-weight: bold;
				
				&::after {
					content: '';
					position: absolute;
					bottom: 0;
					left: 50%;
					transform: translateX(-50%);
					width: 60rpx;
					height: 4rpx;
					background-color: #FF8C00;
				}
			}
		}
	}
	
	.order-scroll-view {
		height: calc(100vh - 170rpx);
	}
	
	.order-list {
		padding: 0 20rpx;
	}
	
	.order-item {
		background-color: #fff;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
		padding: 30rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.order-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding-bottom: 20rpx;
			border-bottom: 2rpx solid #f5f5f5;
			
			.order-number {
				font-size: 28rpx;
				color: #333;
			}
			
			.status-group {
				display: flex;
				align-items: center;
				
				.order-status {
					font-size: 26rpx;
					
					&.status-confirmed {
						color: #2196F3;
					}
					
					&.status-delivering {
						color: #FF5722;
					}
					
					&.status-completed {
						color: #4CAF50;
					}
				}
				
				.pay-status {
					font-size: 24rpx;
					padding: 4rpx 12rpx;
					border-radius: 12rpx;
					margin-left: 20rpx;
					
					&.pay-unpaid {
						background-color: #FFF3E0;
						color: #FF9800;
					}
					
					&.pay-paid {
						background-color: #E8F5E8;
						color: #4CAF50;
					}
					
					&.pay-refunded {
						background-color: #FFEBEE;
						color: #F44336;
					}
					
					&.pay-refunding {
						background-color: #F3E5F5;
						color: #9C27B0;
					}
				}
			}
		}
		
		.order-content {
			padding: 20rpx 0;
			
			.customer-info, .order-time, .order-amount, .order-remark {
				display: flex;
				font-size: 26rpx;
				margin-bottom: 10rpx;
				
				.label {
					color: #999;
					width: 120rpx;
				}
				
				.value {
					color: #333;
					flex: 1;
					
					&.price {
						color: #FF5722;
						font-weight: bold;
					}
				}
			}
			
			.dish-summary {
				margin: 15rpx 0;
				padding: 10rpx 15rpx;
				background-color: #f9f9f9;
				border-radius: 8rpx;
				
				.dish-text {
					font-size: 24rpx;
					color: #666;
				}
			}
		}
		
		.order-actions {
			display: flex;
			justify-content: flex-end;
			margin-top: 20rpx;
			padding-top: 20rpx;
			border-top: 2rpx solid #f5f5f5;
			
			.action-btn {
				padding: 10rpx 30rpx;
				border-radius: 30rpx;
				font-size: 26rpx;
				margin-left: 20rpx;
				
				&.detail {
					border: 2rpx solid #FF8C00;
					color: #FF8C00;
					background-color: #fff;
				}
				
				&.deliver {
					background-color: #FF5722;
					color: #fff;
				}
				
				&.complete {
					background-color: #4CAF50;
					color: #fff;
				}
			}
		}
	}
	
	.empty-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
		
		.empty-image {
			width: 200rpx;
			height: 200rpx;
			margin-bottom: 30rpx;
		}
		
		.empty-text {
			font-size: 28rpx;
			color: #999;
		}
	}
	
	.loading-tip, .no-more-tip {
		text-align: center;
		padding: 30rpx;
		font-size: 26rpx;
		color: #999;
	}
</style> 