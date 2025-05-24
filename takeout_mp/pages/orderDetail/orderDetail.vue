<template>
	<view class="order-detail-container">
		<scroll-view scroll-y="true" class="order-detail-scroll" :style="{height: scrollHeight + 'px'}" refresher-enabled="true" :refresher-triggered="refresherTriggered" @refresherrefresh="onRefresh">
			<view class="order-detail">
				<!-- 订单状态卡片 -->
				<view class="status-card">
					<view class="status">
						<text class="status-text">{{ getStatusText(orderDetail.status) }}</text>
						<text class="status-desc">{{ getStatusDesc(orderDetail.status) }}</text>
					</view>
					<image class="status-image" :src="getStatusImage(orderDetail.status)" mode="aspectFit"></image>
				</view>
				
				<!-- 配送信息 -->
				<view class="detail-card">
					<view class="card-title">
						<text>配送信息</text>
					</view>
					<view class="info-item">
						<text class="label">收货人：</text>
						<text class="content">{{ orderDetail.consignee }}</text>
					</view>
					<view class="info-item">
						<text class="label">手机号：</text>
						<text class="content">{{ orderDetail.phone }}</text>
					</view>
					<view class="info-item">
						<text class="label">送达时间：</text>
						<text class="content">{{ formatDeliveryTime(orderDetail.deliveryTime, orderDetail.orderTime) }}</text>
			</view>
					<view class="info-item">
						<text class="label">配送地址：</text>
						<text class="content">{{ orderDetail.address }}</text>
			</view>
		</view>
				
				<!-- 订单信息 -->
				<view class="detail-card">
					<view class="card-title">
						<text>订单信息</text>
					</view>
					<view class="info-item">
						<text class="label">订单编号：</text>
						<text class="content">{{ orderDetail.id }}</text>
						<text class="copy-btn" @tap="copyOrderId">复制</text>
					</view>
					<view class="info-item">
						<text class="label">下单时间：</text>
						<text class="content">{{ formatOrderTime(orderDetail.orderTime) }}</text>
			</view>
					<view class="info-item">
						<text class="label">支付方式：</text>
						<text class="content">{{ getPayMethod(orderDetail.payMethod) }}</text>
			</view>
					<view class="info-item">
						<text class="label">备注：</text>
						<text class="content">{{ orderDetail.remark || '无' }}</text>
			</view>
		</view>
				
				<!-- 商品列表 -->
				<view class="detail-card">
					<view class="card-title">
						<text>商品列表</text>
					</view>
					<view class="order-items">
						<view v-for="(item, index) in orderDetail.orderDetails" :key="index" class="order-item">
							<image class="item-image" :src="item.image" mode="aspectFill"></image>
							<view class="item-info">
								<text class="item-name">{{ item.name }}</text>
								<text class="item-specs" v-if="item.dishFlavor">{{ item.dishFlavor }}</text>
							</view>
							<view class="item-price-qty">
								<text class="item-price">￥{{ (item.amount / 100).toFixed(2) }}</text>
								<text class="item-qty">x{{ item.number }}</text>
							</view>
						</view>
					</view>
			</view>
				
				<!-- 订单金额 -->
				<view class="detail-card">
					<view class="card-title">
						<text>订单金额</text>
			</view>
					<view class="price-item">
				<text>商品金额</text>
						<text>￥{{ (orderDetail.totalAmount / 100).toFixed(2) }}</text>
			</view>
					<view class="price-item">
				<text>配送费</text>
						<text>￥{{ (orderDetail.deliveryFee / 100 || 0).toFixed(2) }}</text>
					</view>
					<view class="price-item">
						<text>优惠金额</text>
						<text>￥{{ (orderDetail.discount / 100 || 0).toFixed(2) }}</text>
					</view>
					<view class="price-item total">
						<text>实付金额</text>
						<text class="total-price">￥{{ (orderDetail.amount / 100).toFixed(2) }}</text>
			</view>
			</view>
				
				<!-- 底部空间，确保内容不被底部按钮遮挡 -->
				<view class="bottom-space" v-if="showActions"></view>
			</view>
		</scroll-view>
		
		<!-- 底部操作按钮 -->
		<view class="bottom-actions" v-if="showActions">
			<button class="action-btn" v-if="canComment()" @tap="goToComment">评价订单</button>
			<button class="action-btn refund-btn" v-if="canRefund()" @tap="applyRefund">申请退款</button>
			<button class="action-btn" v-if="canAfterSale()" @tap="goToAfterSale">申请售后</button>
			<button class="action-btn" v-if="canReorder()" @tap="reorder">再来一单</button>
			<button class="action-btn service-btn" @tap="showServicePopup">联系客服</button>
		</view>
		
		<!-- 客服信息弹窗 -->
		<u-popup :show="servicePopupShow" mode="center" borderRadius="16" @close="closeServicePopup">
			<view class="service-popup">
				<view class="service-title">客服信息</view>
				<view class="service-content">
					<view class="service-item">
						<text class="service-label">客服热线：</text>
						<text class="service-phone" @tap="callPhone(serviceInfo.phone)">{{ serviceInfo.phone }}</text>
					</view>
					<view class="service-item">
						<text class="service-label">服务时间：</text>
						<text class="service-text">{{ serviceInfo.serviceTime }}</text>
					</view>
					<view class="service-item">
						<text class="service-label">客服邮箱：</text>
						<text class="service-text">{{ serviceInfo.email }}</text>
					</view>
					<view class="service-desc">{{ serviceInfo.description }}</view>
				</view>
				<button class="service-close-btn" @tap="closeServicePopup">关闭</button>
		</view>
		</u-popup>
	</view>
</template>

<script>
	import regeneratorRuntime, {
		async
	} from '../../lib/runtime/runtime';
	import {
		getOrderDetailApi,
		orderAgainApi,
		getServiceInfoApi
	} from '../../api/orderList.js';
	
	// 调试模式配置
	const DEBUG_MODE = true;
	
export default {
	data() {
		return {
				orderId: '',
				scrollHeight: 0, // 滚动区域高度
				refresherTriggered: false, // 下拉刷新状态
				orderDetail: {
					id: '',
					status: 0,
					orderTime: '',
					payMethod: 1,
					amount: 0,
					totalAmount: 0,
					deliveryFee: 0,
					discount: 0,
					consignee: '',
					phone: '',
					address: '',
					deliveryTime: '',
					remark: '',
					orderDetails: []
				},
				// 测试数据
				testOrderDetail: {
					id: '20240601001',
					status: 3, // 已派送
					orderTime: '2024-06-01 12:30:00',
					payMethod: 1, // 微信支付
					amount: 5880, // 实付金额（分）
					totalAmount: 5680, // 商品总金额（分）
					deliveryFee: 500, // 配送费（分）
					discount: 300, // 优惠金额（分）
					consignee: '张三',
					phone: '138****1234',
					address: '北京市海淀区中关村大街1号',
					deliveryTime: '2024-06-01 13:00:00',
					remark: '不要辣',
					orderDetails: [
						{
							id: 1,
							name: '红烧肉',
							image: 'https://img.yzcdn.cn/vant/cat.jpeg',
							dishFlavor: '微辣',
							number: 1,
							amount: 3680
						},
						{
							id: 2,
							name: '宫保鸡丁',
							image: 'https://img.yzcdn.cn/vant/cat.jpeg',
							dishFlavor: '中辣',
							number: 2,
							amount: 2000
						}
					]
				},
				servicePopupShow: false,
				serviceInfo: {
					phone: '10086',
					serviceTime: '周一至周日 9:00-18:00',
					email: 'service@example.com',
					description: '我们的客服团队随时为您服务，解答您的疑问和问题。'
				}
			}
		},
		computed: {
			showActions() {
				return this.orderDetail.status >= 3; // 已派送、已完成状态显示底部按钮
			}
	},
		onLoad(options) {
			// 获取系统信息设置滚动区域高度
			// 底部按钮高度大约为100rpx转换为px
			const systemInfo = uni.getSystemInfoSync();
			const buttonHeight = this.showActions ? 50 : 0; // 预留底部按钮高度
			this.scrollHeight = systemInfo.windowHeight - buttonHeight;
			
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
			
			if (options.orderId) {
				this.orderId = options.orderId;
				this.getOrderDetail();
				// 加载客服信息
				this.getServiceInfo();
			} else {
				uni.showToast({
					title: '订单ID不存在',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateBack();
				}, 1500);
			}
		},
		methods: {
			// 获取订单详情
			async getOrderDetail(isRefresh = false) {
				try {
					if (DEBUG_MODE) {
						// 使用测试数据
						setTimeout(() => {
							this.orderDetail = this.testOrderDetail;
							if (isRefresh) {
								this.refresherTriggered = false;
								uni.showToast({
									title: '刷新成功',
									icon: 'success',
									duration: 1500
								});
							}
						}, 500);
						return;
					}
					
					const res = await getOrderDetailApi(this.orderId);
					if (res.code === 0 && res.data) {
						this.orderDetail = res.data;
						if (isRefresh) {
							this.refresherTriggered = false;
							uni.showToast({
								title: '刷新成功',
								icon: 'success',
								duration: 1500
							});
						}
					} else {
						uni.showToast({
							title: res.msg || '获取订单详情失败',
							icon: 'none'
						});
						if (isRefresh) {
							this.refresherTriggered = false;
						}
					}
				} catch (error) {
					console.error('获取订单详情失败', error);
					uni.showToast({
						title: '获取订单详情失败',
						icon: 'none'
					});
					if (isRefresh) {
						this.refresherTriggered = false;
					}
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
					default: return '未知状态';
				}
			},
			
			// 获取订单状态描述
			getStatusDesc(status) {
				switch (status) {
					case 1: return '请尽快完成支付';
					case 2: return '骑手正在配送中，请耐心等待';
					case 3: return '您的订单已送达，请查收';
					case 4: return '订单已完成，感谢您的支持';
					case 5: return '订单已取消';
					default: return '';
				}
			},
			
			// 获取订单状态图片
			getStatusImage(status) {
				switch (status) {
					case 1: return '/static/images/order/wait_pay.png';
					case 2: return '/static/images/order/delivering.png';
					case 3: return '/static/images/order/delivered.png';
					case 4: return '/static/images/order/completed.png';
					case 5: return '/static/images/order/canceled.png';
					default: return '/static/images/order/default.png';
				}
			},
			
			// 格式化订单时间
			formatOrderTime(timeStr) {
				if (!timeStr) return '';
				return timeStr.replace('T', ' ').substring(0, 19);
			},
			
			// 格式化配送时间
			formatDeliveryTime(deliveryTime, orderTime) {
				if (deliveryTime) {
					return this.formatOrderTime(deliveryTime);
				}
				
				// 如果没有配送时间，默认显示预计30分钟送达
				if (orderTime) {
					const orderDate = new Date(orderTime.replace('T', ' '));
					orderDate.setMinutes(orderDate.getMinutes() + 30);
					return `预计 ${orderDate.getHours()}:${orderDate.getMinutes().toString().padStart(2, '0')} 送达`;
				}
				
				return '暂无配送时间';
			},
			
			// 获取支付方式
			getPayMethod(method) {
				switch (method) {
					case 1: return '微信支付';
					case 2: return '支付宝';
					case 3: return '现金支付';
					default: return '未知支付方式';
				}
			},
			
			// 复制订单ID
			copyOrderId() {
				uni.setClipboardData({
					data: this.orderDetail.id,
					success: () => {
						uni.showToast({
							title: '复制成功',
							icon: 'success'
						});
					}
				});
			},
			
			// 是否可以评价
			canComment() {
				return this.orderDetail.status === 4 && !this.orderDetail.isCommented;
			},
			
			// 是否可以申请售后
			canAfterSale() {
				// 只有已完成的订单可以申请售后，且时间在7天内
				if (this.orderDetail.status !== 4) return false;
				
				// 计算订单完成时间是否在7天内
				const orderTime = new Date(this.orderDetail.orderTime.replace('T', ' '));
				const now = new Date();
				const diffDays = Math.floor((now - orderTime) / (24 * 60 * 60 * 1000));
				
				return diffDays <= 7;
			},
			
			// 是否可以再来一单
			canReorder() {
				return this.orderDetail.status === 4; // 已完成的订单可以再来一单
			},
			
			// 跳转到评价页面
			goToComment() {
				uni.navigateTo({
					url: `/pages/comment/comment?orderId=${this.orderDetail.id}`
				});
			},
			
			// 跳转到售后页面
			goToAfterSale() {
				uni.navigateTo({
					url: `/pages/afterSale/afterSale?orderId=${this.orderDetail.id}&amount=${this.orderDetail.amount}`
				});
			},
			
			// 再来一单
			async reorder() {
				try {
					uni.showLoading({
						title: '加载中...'
					});
					
					if (DEBUG_MODE) {
						setTimeout(() => {
							uni.hideLoading();
							uni.switchTab({
								url: '/pages/home/home'
							});
							uni.showToast({
								title: '已添加到购物车',
								icon: 'success'
							});
						}, 1000);
						return;
					}
					
					const res = await orderAgainApi(this.orderDetail.id);
					
					uni.hideLoading();
					
					if (res.code === 0) {
						uni.switchTab({
							url: '/pages/home/home'
						});
						uni.showToast({
							title: '已添加到购物车',
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: res.msg || '操作失败',
							icon: 'none'
						});
					}
				} catch (error) {
					uni.hideLoading();
					console.error('再来一单失败', error);
					uni.showToast({
						title: '操作失败',
						icon: 'none'
					});
				}
			},
			
			// 下拉刷新
			onRefresh() {
				this.refresherTriggered = true;
				this.getOrderDetail(true);
			},
			
			// 显示客服信息弹窗
			showServicePopup() {
				this.servicePopupShow = true;
			},
			
			// 关闭客服信息弹窗
			closeServicePopup() {
				this.servicePopupShow = false;
			},
			
			// 拨打客服电话
			callPhone(phone) {
				uni.makePhoneCall({
					phoneNumber: phone
				});
			},
			
			// 获取客服信息
			async getServiceInfo() {
				try {
					const res = await getServiceInfoApi(this.orderId);
					if (res.code === 0 && res.data) {
						this.serviceInfo = res.data;
					} else {
						uni.showToast({
							title: res.msg || '获取客服信息失败',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('获取客服信息失败', error);
					uni.showToast({
						title: '获取客服信息失败',
						icon: 'none'
					});
				}
			},
			
			// 是否可以申请退款
			canRefund() {
				// 只有待付款、正在派送和已派送的订单可以申请退款
				// 状态: 1待付款，2正在派送，3已派送，4已完成，5已取消
				return [1, 2, 3].includes(this.orderDetail.status);
			},
			
			// 跳转到退款申请页面
			applyRefund() {
				uni.navigateTo({
					url: `/pages/refund/refund?orderId=${this.orderDetail.id}&amount=${this.orderDetail.amount}&status=${this.orderDetail.status}`
				});
	}
		}
	}
</script>

<style>
	.order-detail-container {
		position: relative;
		width: 100%;
		height: 100vh;
		background-color: #f8f8f8;
	}
	
	.order-detail-scroll {
		width: 100%;
	}
	
	.order-detail {
		padding: 24rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}
	
	.status-card {
		background: linear-gradient(to right, #ff9500, #ff5e3a);
		border-radius: 12rpx;
		padding: 32rpx;
		color: #fff;
	display: flex;
	justify-content: space-between;
	align-items: center;
		margin-bottom: 24rpx;
	}
	
	.status-text {
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 16rpx;
		display: block;
	}
	
	.status-desc {
		font-size: 24rpx;
		opacity: 0.8;
		display: block;
	}
	
	.status-image {
		width: 100rpx;
		height: 100rpx;
	}
	
	.detail-card {
		background: #fff;
		border-radius: 12rpx;
		padding: 24rpx;
		margin-bottom: 24rpx;
	}
	
	.card-title {
		font-size: 32rpx;
		font-weight: bold;
		margin-bottom: 24rpx;
		border-bottom: 1rpx solid #f2f2f2;
		padding-bottom: 16rpx;
	}
	
	.info-item {
		display: flex;
		padding: 12rpx 0;
		font-size: 28rpx;
		color: #333;
		position: relative;
	}
	
	.label {
		color: #999;
		width: 160rpx;
	}
	
	.content {
		flex: 1;
	}
	
	.copy-btn {
		color: #007aff;
		margin-left: 24rpx;
		font-size: 24rpx;
	}
	
	.order-items {
		margin-top: 16rpx;
	}
	
	.order-item {
	display: flex;
		align-items: center;
		padding: 16rpx 0;
		border-bottom: 1rpx solid #f2f2f2;
	}
	
	.order-item:last-child {
		border-bottom: none;
}
	
	.item-image {
		width: 120rpx;
		height: 120rpx;
		border-radius: 8rpx;
		background-color: #f5f5f5;
	}
	
	.item-info {
		flex: 1;
		padding: 0 20rpx;
}
	
	.item-name {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 8rpx;
		display: block;
	}
	
	.item-specs {
		font-size: 24rpx;
		color: #999;
		display: block;
		}
	
	.item-price-qty {
		text-align: right;
	}
	
	.item-price {
		font-size: 28rpx;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
	}
	
	.item-qty {
		font-size: 24rpx;
		color: #999;
	}
	
	.price-item {
		display: flex;
		justify-content: space-between;
		padding: 12rpx 0;
		font-size: 28rpx;
		color: #666;
	}
	
	.total {
		margin-top: 16rpx;
		padding-top: 16rpx;
		border-top: 1rpx solid #f2f2f2;
		font-weight: bold;
		color: #333;
	}
	
	.total-price {
		color: #ff5050;
		}
	
	.bottom-space {
		height: 120rpx;
	}
	
	.bottom-actions {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		display: flex;
		justify-content: flex-end;
		padding: 20rpx 24rpx;
		background-color: #fff;
		box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
		z-index: 10;
	}
	
	.action-btn {
		background-color: #ff9900;
		color: #fff;
		border-radius: 30rpx;
		font-size: 28rpx;
		padding: 0 32rpx;
		margin-left: 24rpx;
		line-height: 2.5;
		border: none;
	}
	
	.refund-btn {
		background-color: #ff5050;
	}
	
	.service-btn {
		background-color: #007aff;
	}
	
	.service-popup {
		width: 600rpx;
		padding: 40rpx;
		background-color: #fff;
		border-radius: 16rpx;
		}
	
	.service-title {
		font-size: 36rpx;
		font-weight: bold;
		text-align: center;
		margin-bottom: 30rpx;
		color: #333;
		}
	
	.service-content {
		margin-bottom: 30rpx;
	}
	
	.service-item {
		display: flex;
		margin-bottom: 20rpx;
		font-size: 28rpx;
		color: #333;
	}
	
	.service-label {
		color: #999;
		width: 180rpx;
		flex-shrink: 0;
	}
	
	.service-phone {
		color: #007aff;
		text-decoration: underline;
	}
	
	.service-text {
		color: #333;
	}
	
	.service-desc {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		margin-top: 20rpx;
		padding-top: 20rpx;
		border-top: 1rpx solid #f2f2f2;
	}
	
	.service-close-btn {
		width: 100%;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		background-color: #f8f8f8;
		color: #333;
		border-radius: 40rpx;
		font-size: 28rpx;
		margin-top: 20rpx;
		border: none;
}
</style>
