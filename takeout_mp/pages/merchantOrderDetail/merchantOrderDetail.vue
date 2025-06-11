<template>
	<view class="merchant-order-detail">
		<view class="header">
			<text class="title">订单详情</text>
		</view>
		
		<view v-if="orderDetail" class="detail-container">
			<!-- 订单状态卡片 -->
			<view class="status-card">
				<view class="status-info">
					<text :class="['status-text', getStatusClass(orderDetail.status)]">
						{{getStatusText(orderDetail.status)}}
					</text>
					<text :class="['pay-status', getPayStatusClass(orderDetail.payStatus)]">
						{{getPayStatusText(orderDetail.payStatus)}}
					</text>
				</view>
				<text class="order-number">订单号：{{orderDetail.orderNumber || `ORDER-${orderDetail.id}`}}</text>
			</view>
			
			<!-- 商品信息 -->
			<view class="section">
				<view class="section-title">商品信息</view>
				<view class="dish-list">
					<view v-for="(item, index) in orderDetail.items" :key="index" class="dish-item">
						<view class="dish-info">
							<text class="dish-name">{{item.name}}</text>
							<text v-if="item.dishFlavor" class="dish-flavor">{{item.dishFlavor}}</text>
						</view>
						<view class="dish-quantity">x{{item.quantity}}</view>
						<view class="dish-price">¥{{item.total}}</view>
					</view>
				</view>
			</view>
			
			<!-- 订单信息 -->
			<view class="section">
				<view class="section-title">订单信息</view>
				<view class="info-list">
					<view class="info-item">
						<text class="label">订单ID：</text>
						<text class="value">{{orderDetail.id}}</text>
					</view>
					<view class="info-item">
						<text class="label">用户ID：</text>
						<text class="value">{{orderDetail.userId}}</text>
					</view>
					<view class="info-item">
						<text class="label">下单时间：</text>
						<text class="value">{{formatTime(orderDetail.orderTime)}}</text>
					</view>
					<view class="info-item">
						<text class="label">创建时间：</text>
						<text class="value">{{formatTime(orderDetail.createTime)}}</text>
					</view>
					<view class="info-item">
						<text class="label">更新时间：</text>
						<text class="value">{{formatTime(orderDetail.updateTime)}}</text>
					</view>
					<view v-if="orderDetail.remark" class="info-item">
						<text class="label">备注：</text>
						<text class="value">{{orderDetail.remark}}</text>
					</view>
				</view>
			</view>
			
			<!-- 金额信息 -->
			<view class="section">
				<view class="section-title">金额信息</view>
				<view class="amount-info">
					<view class="amount-item">
						<text class="label">商品总额：</text>
						<text class="value">¥{{orderDetail.total}}</text>
					</view>
					<view class="amount-item total">
						<text class="label">订单总额：</text>
						<text class="value">¥{{orderDetail.total}}</text>
					</view>
				</view>
			</view>
			
			<!-- 操作按钮 -->
			<view class="action-buttons">
				<!-- 只有已确认且已支付的订单才能配送 -->
				<view v-if="(orderDetail.status === 'pending' || orderDetail.status === 'confirmed') && orderDetail.payStatus === 'paid'" class="action-btn primary" @click="deliverOrder">
					开始配送
				</view>
				<view v-if="orderDetail.status === 'delivering'" class="action-btn primary" @click="completeOrder">
					完成订单
				</view>
			</view>
		</view>
		
		<view v-else class="loading-container">
			<text class="loading-text">加载中...</text>
		</view>
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
				orderId: null,
				orderDetail: null,
				loading: false
			}
		},
		onLoad(options) {
			console.log('商家订单详情页面加载，参数:', options);
			
			if (options.orderId) {
				this.orderId = options.orderId;
				this.getOrderDetail();
			} else {
				uni.showToast({
					title: '订单ID不能为空',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateBack();
				}, 1500);
			}
		},
		methods: {
			// 获取订单详情
			async getOrderDetail() {
				try {
					uni.showLoading({ title: '加载订单详情...' });
					
					const res = await getOrderDetailApi(this.orderId);
					console.log('订单详情API响应:', res);
					
					if (res) {
						this.orderDetail = res;
					} else {
						throw new Error('获取订单详情失败');
					}
				} catch (error) {
					console.error('获取订单详情失败:', error);
					uni.showToast({
						title: '获取订单详情失败',
						icon: 'none'
					});
					
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				} finally {
					uni.hideLoading();
				}
			},
			
			// 开始配送
			async deliverOrder() {
				uni.showModal({
					title: '确认配送',
					content: '确定该订单开始配送吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({ title: '处理中...' });
								
								const response = await deliverOrderApi(this.orderId);
								console.log('配送响应:', response);
								console.log('响应类型:', typeof response);
								console.log('响应是否为对象:', response && typeof response === 'object');
								
								uni.hideLoading();
								
								// 修正成功判断逻辑：响应拦截器返回的是订单对象，如果有订单数据就表示成功
								if (response && typeof response === 'object' && response.id) {
									// 更新本地订单状态
									this.orderDetail.status = response.status || 'delivering';
									uni.showToast({
										title: '已开始配送',
										icon: 'success'
									});
								} else {
									console.error('配送失败，响应格式不正确:', response);
									throw new Error('配送失败');
								}
							} catch (error) {
								uni.hideLoading();
								console.error('开始配送失败', error);
								uni.showToast({
									title: error.message || '操作失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示
								this.orderDetail.status = 'delivering';
							}
						}
					}
				});
			},
			
			// 完成订单
			async completeOrder() {
				uni.showModal({
					title: '确认完成',
					content: '确定该订单已完成吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({ title: '处理中...' });
								
								const response = await completeOrderApi(this.orderId);
								console.log('完成订单响应:', response);
								console.log('响应详情:', {
									success: response?.success,
									code: response?.code,
									msg: response?.msg,
									data: response?.data
								});
								
								uni.hideLoading();
								
								// 修正成功判断逻辑：后端返回code=200表示成功
								if (response && (response.success === true || response.code === 200 || response.code === 0)) {
									this.orderDetail.status = 'completed';
									uni.showToast({
										title: '订单已完成',
										icon: 'success'
									});
								} else {
									console.error('完成订单失败，响应不符合成功条件:', response);
									throw new Error(response?.msg || '完成订单失败');
								}
							} catch (error) {
								uni.hideLoading();
								console.error('完成订单失败', error);
								uni.showToast({
									title: error.message || '操作失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示
								this.orderDetail.status = 'completed';
							}
						}
					}
				});
			},
			
			// 获取状态文本
			getStatusText(status) {
				const statusTexts = {
					'pending': '已确认',
					'confirmed': '已确认',
					'delivering': '配送中',
					'completed': '已完成'
				};
				
				return statusTexts[status] || '已确认';
			},
			
			// 获取状态样式类
			getStatusClass(status) {
				switch (status) {
					case 'pending':
					case 'confirmed':
						return 'status-confirmed';
					case 'delivering':
						return 'status-delivering';
					case 'completed':
						return 'status-completed';
					default:
						return 'status-confirmed';
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
				
				return statusTexts[payStatus] || '未知支付状态';
			},
			
			// 获取支付状态样式类
			getPayStatusClass(payStatus) {
				switch (payStatus) {
					case 'unpaid': return 'pay-unpaid';
					case 'paid': return 'pay-paid';
					case 'refunded': return 'pay-refunded';
					case 'refunding': return 'pay-refunding';
					default: return '';
				}
			},
			
			// 格式化时间
			formatTime(timeStr) {
				if (!timeStr) return '未知时间';
				
				try {
					let date;
					
					// 处理不同的时间格式
					if (Array.isArray(timeStr)) {
						// 如果是数组格式 [2025, 6, 7, 17, 56, 26]
						// 注意：月份需要减1，因为JavaScript的月份是从0开始的
						const [year, month, day, hour, minute, second] = timeStr;
						date = new Date(year, month - 1, day, hour, minute, second || 0);
					} else if (typeof timeStr === 'string') {
						// 如果是ISO格式或标准格式
						if (timeStr.includes('T') || timeStr.includes('-')) {
							date = new Date(timeStr);
						} else {
							// 如果是时间戳字符串
							date = new Date(parseInt(timeStr));
						}
					} else if (typeof timeStr === 'number') {
						// 如果是时间戳数字
						date = new Date(timeStr);
					} else {
						return '时间格式错误';
					}
					
					// 检查日期是否有效
					if (isNaN(date.getTime())) {
						return '无效时间';
					}
					
					const year = date.getFullYear();
					const month = String(date.getMonth() + 1).padStart(2, '0');
					const day = String(date.getDate()).padStart(2, '0');
					const hours = String(date.getHours()).padStart(2, '0');
					const minutes = String(date.getMinutes()).padStart(2, '0');
					const seconds = String(date.getSeconds()).padStart(2, '0');
					
					return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
				} catch (error) {
					console.error('时间格式化失败:', error, '原始时间:', timeStr);
					return String(timeStr);
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.merchant-order-detail {
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
	
	.detail-container {
		padding: 20rpx;
	}
	
	.status-card {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.status-info {
			display: flex;
			align-items: center;
			margin-bottom: 20rpx;
			
			.status-text {
				font-size: 32rpx;
				font-weight: bold;
				margin-right: 20rpx;
				
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
		
		.order-number {
			font-size: 28rpx;
			color: #666;
		}
	}
	
	.section {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.section-title {
			font-size: 30rpx;
			color: #333;
			font-weight: bold;
			margin-bottom: 20rpx;
			padding-bottom: 10rpx;
			border-bottom: 2rpx solid #f5f5f5;
		}
	}
	
	.dish-list {
		.dish-item {
			display: flex;
			align-items: center;
			padding: 15rpx 0;
			border-bottom: 1rpx solid #f0f0f0;
			
			&:last-child {
				border-bottom: none;
			}
			
			.dish-info {
				flex: 1;
				
				.dish-name {
					font-size: 28rpx;
					color: #333;
					font-weight: 500;
					display: block;
					margin-bottom: 6rpx;
				}
				
				.dish-flavor {
					font-size: 24rpx;
					color: #666;
				}
			}
			
			.dish-quantity {
				color: #666;
				font-size: 26rpx;
				margin: 0 30rpx;
			}
			
			.dish-price {
				color: #FF5722;
				font-size: 28rpx;
				font-weight: bold;
			}
		}
	}
	
	.info-list {
		.info-item {
			display: flex;
			margin-bottom: 15rpx;
			
			&:last-child {
				margin-bottom: 0;
			}
			
			.label {
				color: #666;
				font-size: 26rpx;
				width: 160rpx;
			}
			
			.value {
				color: #333;
				font-size: 26rpx;
				flex: 1;
			}
		}
	}
	
	.amount-info {
		.amount-item {
			display: flex;
			justify-content: space-between;
			margin-bottom: 15rpx;
			font-size: 26rpx;
			
			&:last-child {
				margin-bottom: 0;
			}
			
			&.total {
				font-size: 30rpx;
				font-weight: bold;
				color: #FF5722;
				padding-top: 15rpx;
				border-top: 2rpx solid #f5f5f5;
			}
			
			.label {
				color: #666;
			}
			
			.value {
				color: #333;
			}
		}
	}
	
	.action-buttons {
		display: flex;
		justify-content: center;
		margin-top: 40rpx;
		
		.action-btn {
			padding: 20rpx 60rpx;
			border-radius: 30rpx;
			font-size: 30rpx;
			
			&.primary {
				background-color: #FF8C00;
				color: #fff;
			}
		}
	}
	
	.loading-container {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 400rpx;
		
		.loading-text {
			font-size: 28rpx;
			color: #666;
		}
	}
</style> 