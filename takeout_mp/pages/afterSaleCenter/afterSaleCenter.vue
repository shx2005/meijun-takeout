<template>
	<view class="after-sale-center">
		<scroll-view scroll-y="true" class="center-scroll" :style="{height: scrollHeight + 'px'}" 
			refresher-enabled="true" :refresher-triggered="refresherTriggered" @refresherrefresh="onRefresh">
			<view class="center-content">
				<!-- 页面标题 -->
				<view class="page-header">
					<text class="page-title">售后中心</text>
					<text class="page-desc">查看您的售后申请状态</text>
				</view>
				
				<!-- 售后申请列表 -->
				<view class="after-sale-list">
					<u-empty v-if="afterSaleList.length === 0" mode="order" icon="http://cdn.uviewui.com/uview/empty/order.png" text="暂无售后申请"></u-empty>
					
					<view v-for="item in afterSaleList" :key="item.id" class="after-sale-card">
						<view class="card-header">
							<view class="order-info">
								<text class="order-id">订单号：{{ item.orderId }}</text>
								<text class="apply-time">申请时间：{{ formatTime(item.createTime) }}</text>
								<text class="order-amount" v-if="item.orderAmount">订单金额：￥{{ (item.orderAmount / 100).toFixed(2) }}</text>
							</view>
							<view :class="'status-badge ' + getStatusClass(item.status)">
								{{ getStatusText(item.status) }}
							</view>
						</view>
						
						<view class="card-content">
							<view class="info-row">
								<text class="label">售后类型：</text>
								<text class="value">{{ getTypeText(item.type) }}</text>
							</view>
							<view class="info-row">
								<text class="label">申请原因：</text>
								<text class="value">{{ item.reason }}</text>
							</view>
							<view class="info-row" v-if="item.content">
								<text class="label">问题描述：</text>
								<text class="value">{{ item.content }}</text>
							</view>
							<view class="info-row" v-if="item.updateTime && item.updateTime !== item.createTime">
								<text class="label">更新时间：</text>
								<text class="value">{{ formatTime(item.updateTime) }}</text>
							</view>
						</view>
						
						<view class="card-actions">
							<button class="action-btn detail-btn" @tap="viewOrderDetail(item.orderId)">查看订单</button>
							<button class="action-btn contact-btn" @tap="contactService">联系客服</button>
							<button class="action-btn cancel-btn" v-if="canCancel(item)" @tap="cancelAfterSale(item)">取消申请</button>
						</view>
					</view>
				</view>
				
				<!-- 底部空间 -->
				<view class="bottom-space"></view>
			</view>
		</scroll-view>
		
		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<button class="bar-btn" @tap="goToAfterSale">申请新的售后</button>
			<button class="bar-btn primary" @tap="refreshData">刷新数据</button>
		</view>
	</view>
</template>

<script>
	import regeneratorRuntime, { async } from '../../lib/runtime/runtime';
	import { 
		getAfterSaleStatusApi,
		getAfterSaleListApi,
		deleteAfterSaleApi,
		AfterSaleType,
		AfterSaleStatus 
	} from '../../api/afterSale.js';
	import { getServiceInfoText } from '../../utils/serviceConfig.js';
	
	// 调试模式配置
	const DEBUG_MODE = false;
	
	export default {
		data() {
			return {
				scrollHeight: 0, // 滚动区域高度
				refresherTriggered: false, // 下拉刷新状态
				afterSaleList: [],
				loading: false,
				
				// 测试数据
				testAfterSaleList: [
					{
						id: 1,
						orderId: 1,
						userId: 1,
						type: 'refund',
						reason: '商品质量问题',
						content: '菜品口味不佳，申请退款',
						status: 'pending',
						createTime: '2024-12-07T18:30:00',
						updateTime: '2024-12-07T18:30:00',
						orderAmount: 5680
					},
					{
						id: 2,
						orderId: 2,
						userId: 1,
						type: 'replace',
						reason: '送错商品',
						content: '订购的是宫保鸡丁，送来的是鱼香肉丝',
						status: 'pending',
						createTime: '2024-12-06T15:20:00',
						updateTime: '2024-12-07T10:15:00',
						orderAmount: 3200
					},
					{
						id: 3,
						orderId: 3,
						userId: 1,
						type: 'refund',
						reason: '配送延误',
						content: '订单超时配送，申请退款',
						status: 'completed',
						createTime: '2024-12-05T12:10:00',
						updateTime: '2024-12-06T09:30:00',
						orderAmount: 4500
					}
				]
			}
		},
		onLoad() {
			console.log('售后中心页面加载');
			
			// 获取系统信息设置滚动区域高度
			const systemInfo = uni.getSystemInfoSync();
			const bottomBarHeight = 50; // 底部操作栏高度
			this.scrollHeight = systemInfo.windowHeight - bottomBarHeight;
			
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
			
			// 加载售后申请列表
			this.loadAfterSaleList();
		},
		onShow() {
			// 每次显示页面时刷新数据
			this.loadAfterSaleList();
		},
		methods: {
			// 加载售后申请列表
			async loadAfterSaleList(isRefresh = false) {
				try {
					this.loading = true;
					
					if (!isRefresh) {
						uni.showLoading({ title: '加载中...' });
					}
					
					// 获取用户ID
					const userInfo = uni.getStorageSync('userInfo');
					const userId = userInfo?.id || 1;
					
					if (DEBUG_MODE) {
						// 调试模式：使用测试数据
						setTimeout(() => {
							this.afterSaleList = [...this.testAfterSaleList];
							this.loading = false;
							uni.hideLoading();
							
							if (isRefresh) {
								this.refresherTriggered = false;
								uni.showToast({
									title: '刷新成功',
									icon: 'success',
									duration: 1500
								});
							}
						}, 1000);
						return;
					}
					
					// 从本地存储获取完整的售后申请列表
					const storageKey = `afterSaleList_${userId}`;
					let localAfterSaleList = uni.getStorageSync(storageKey);
					
					// 如果没有售后申请记录
					if (!localAfterSaleList || !Array.isArray(localAfterSaleList) || localAfterSaleList.length === 0) {
						this.afterSaleList = [];
						this.loading = false;
						uni.hideLoading();
						
						if (isRefresh) {
							this.refresherTriggered = false;
							uni.showToast({
								title: '暂无售后记录',
								icon: 'none',
								duration: 1500
							});
						}
						return;
					}
					
					console.log('本地存储的售后申请列表:', localAfterSaleList);
					
					// 并发查询所有订单的最新售后状态
					const statusPromises = localAfterSaleList.map(localItem => 
						this.updateAfterSaleStatus(localItem)
					);
					
					try {
						const statusResults = await Promise.allSettled(statusPromises);
						const afterSaleList = [];
						
						statusResults.forEach((result, index) => {
							if (result.status === 'fulfilled' && result.value) {
								afterSaleList.push(result.value);
							} else {
								// 如果API调用失败，使用本地数据
								console.warn(`订单 ${localAfterSaleList[index].orderId} 的售后状态查询失败，使用本地数据`);
								afterSaleList.push(localAfterSaleList[index]);
							}
						});
						
						// 按创建时间倒序排列
						afterSaleList.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
						
						this.afterSaleList = afterSaleList;
						console.log('最终的售后列表:', this.afterSaleList);
						
						// 更新本地存储中的状态信息
						uni.setStorageSync(storageKey, afterSaleList);
						
					} catch (error) {
						console.error('批量查询售后状态失败:', error);
						// 如果批量查询失败，直接使用本地数据
						this.afterSaleList = localAfterSaleList.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
					}
					
					this.loading = false;
					uni.hideLoading();
					
					if (isRefresh) {
						this.refresherTriggered = false;
						uni.showToast({
							title: '刷新成功',
							icon: 'success',
							duration: 1500
						});
					}
				} catch (error) {
					console.error('加载售后申请列表失败:', error);
					this.loading = false;
					uni.hideLoading();
					
					if (isRefresh) {
						this.refresherTriggered = false;
					}
					
					uni.showToast({
						title: '加载失败，请稍后再试',
						icon: 'none'
					});
				}
			},
			
			// 更新售后状态（结合本地数据和API响应）
			async updateAfterSaleStatus(localItem) {
				try {
					// 调用API查询最新状态
					const response = await getAfterSaleStatusApi(localItem.orderId);
					
					console.log(`订单 ${localItem.orderId} 的API响应:`, response);
					
					// 如果API返回成功但data为null，说明后端没有这个售后记录的详细信息
					// 我们使用本地存储的信息，但可以尝试从API响应中获取状态更新
					if (response && response.success) {
						if (response.data && response.data !== null) {
							// 如果API返回了具体数据，使用API数据
							return {
								id: response.data.id || localItem.id,
								orderId: localItem.orderId,
								userId: localItem.userId,
								type: response.data.type || localItem.type,
								reason: response.data.reason || localItem.reason,
								content: response.data.content || localItem.content,
								status: response.data.status || localItem.status,
								createTime: response.data.createTime || localItem.createTime,
								updateTime: response.data.updateTime || new Date().toISOString(),
								orderAmount: localItem.orderAmount
							};
						} else {
							// 如果API返回成功但data为null，使用本地数据但更新查询时间
							return {
								...localItem,
								updateTime: new Date().toISOString(),
								// 可以根据API的其他信息推断状态，这里暂时保持原状态
								status: localItem.status
							};
						}
					}
					
					// 如果API调用失败，返回本地数据
					return localItem;
				} catch (error) {
					console.error(`更新订单 ${localItem.orderId} 售后状态失败:`, error);
					// 返回本地数据
					return localItem;
				}
			},
			
			// 获取售后类型文本
			getTypeText(type) {
				switch (type) {
					case 'refund': return '退款';
					case 'replace': return '换货';
					case 'other': return '其他';
					default: return '未知';
				}
			},
			
			// 获取售后状态文本
			getStatusText(status) {
				switch (status) {
					case 'pending': return '待处理';
					case 'approved': return '已同意';
					case 'rejected': return '已拒绝';
					case 'completed': return '已完成';
					default: return '未知状态';
				}
			},
			
			// 获取状态样式类
			getStatusClass(status) {
				switch (status) {
					case 'pending': return 'status-pending';
					case 'approved': return 'status-approved';
					case 'rejected': return 'status-rejected';
					case 'completed': return 'status-completed';
					default: return '';
				}
			},
			
			// 格式化时间
			formatTime(timeStr) {
				if (!timeStr) return '';
				
				try {
					const date = new Date(timeStr);
					const year = date.getFullYear();
					const month = String(date.getMonth() + 1).padStart(2, '0');
					const day = String(date.getDate()).padStart(2, '0');
					const hour = String(date.getHours()).padStart(2, '0');
					const minute = String(date.getMinutes()).padStart(2, '0');
					
					return `${year}-${month}-${day} ${hour}:${minute}`;
				} catch (error) {
					return timeStr.replace('T', ' ').substring(0, 16);
				}
			},
			
			// 是否可以取消
			canCancel(item) {
				const result = item.status === 'pending';
				console.log(`订单 ${item.orderId} 是否可以取消:`, result, '状态:', item.status);
				return result;
			},
			
			// 查看订单详情
			viewOrderDetail(orderId) {
				uni.navigateTo({
					url: `/pages/orderDetail/orderDetail?orderId=${orderId}`
				});
			},
			
			// 联系客服
			contactService() {
				uni.showModal({
					title: '联系客服',
					content: getServiceInfoText(),
					showCancel: false,
					confirmText: '知道了'
				});
			},
			
			// 取消售后申请
			async cancelAfterSale(item) {
				try {
					const res = await uni.showModal({
						title: '确认取消',
						content: '确定要取消这个售后申请吗？',
						confirmText: '确定取消',
						cancelText: '我再想想'
					});
					
					if (!res.confirm) return;
					
					uni.showLoading({ title: '取消中...' });
					
					try {
						// 调用删除售后申请API
						const response = await deleteAfterSaleApi(item.id);
						
						console.log('删除售后申请响应:', response);
						
						if (response && (response.code === 200 || response.success === true)) {
							// API调用成功，从列表中移除该项
							const index = this.afterSaleList.findIndex(sale => sale.id === item.id);
							if (index !== -1) {
								this.afterSaleList.splice(index, 1);
							}
							
							// 同时从本地存储中移除
							this.removeFromLocalStorage(item);
							
							uni.hideLoading();
							uni.showToast({
								title: '取消成功',
								icon: 'success'
							});
						} else {
							// API调用失败，但仍然从本地移除（因为用户已确认取消）
							console.warn('API删除失败，但仍从本地移除:', response);
							
							const index = this.afterSaleList.findIndex(sale => sale.id === item.id);
							if (index !== -1) {
								this.afterSaleList.splice(index, 1);
							}
							
							this.removeFromLocalStorage(item);
							
							uni.hideLoading();
							uni.showToast({
								title: '已取消申请',
								icon: 'success'
							});
						}
					} catch (error) {
						console.error('删除售后申请API调用失败:', error);
						
						// 即使API调用失败，也从本地移除（用户体验优先）
						const index = this.afterSaleList.findIndex(sale => sale.id === item.id);
						if (index !== -1) {
							this.afterSaleList.splice(index, 1);
						}
						
						this.removeFromLocalStorage(item);
						
						uni.hideLoading();
						uni.showToast({
							title: '已取消申请',
							icon: 'success'
						});
					}
				} catch (error) {
					console.error('取消售后申请失败:', error);
					uni.hideLoading();
					uni.showToast({
						title: '操作失败，请稍后再试',
						icon: 'none'
					});
				}
			},
			
			// 从本地存储中移除售后申请
			removeFromLocalStorage(item) {
				try {
					const userInfo = uni.getStorageSync('userInfo');
					const userId = userInfo?.id || 1;
					const storageKey = `afterSaleList_${userId}`;
					
					let localAfterSaleList = uni.getStorageSync(storageKey);
					if (localAfterSaleList && Array.isArray(localAfterSaleList)) {
						// 根据订单ID移除对应的记录
						localAfterSaleList = localAfterSaleList.filter(localItem => 
							localItem.orderId !== item.orderId
						);
						
						// 更新本地存储
						uni.setStorageSync(storageKey, localAfterSaleList);
						console.log('已从本地存储移除售后申请:', item.orderId);
					}
				} catch (error) {
					console.error('从本地存储移除售后申请失败:', error);
				}
			},
			
			// 申请新的售后
			goToAfterSale() {
				uni.showModal({
					title: '申请售后',
					content: '请先选择需要申请售后的订单',
					showCancel: true,
					confirmText: '查看订单',
					cancelText: '取消',
					success: (res) => {
						if (res.confirm) {
							// 跳转到订单列表页面
							uni.switchTab({
								url: '/pages/orderList2/orderList2'
							});
						}
					}
				});
			},
			
			// 刷新数据
			refreshData() {
				this.loadAfterSaleList();
			},
			
			// 下拉刷新
			onRefresh() {
				this.refresherTriggered = true;
				this.loadAfterSaleList(true);
			},
			
			// 获取本地存储的售后订单ID列表
			getLocalAfterSaleOrderIds(userId) {
				try {
					const storageKey = `afterSaleList_${userId}`;
					let afterSaleList = uni.getStorageSync(storageKey);
					
					if (!afterSaleList || !Array.isArray(afterSaleList)) {
						return [];
					}
					
					// 返回订单ID列表
					return afterSaleList.map(item => item.orderId);
				} catch (error) {
					console.error('获取本地售后订单ID失败:', error);
					return [];
				}
			},
			
			// 获取本地存储的完整售后申请列表
			getLocalAfterSaleList(userId) {
				try {
					const storageKey = `afterSaleList_${userId}`;
					let afterSaleList = uni.getStorageSync(storageKey);
					
					if (!afterSaleList || !Array.isArray(afterSaleList)) {
						return [];
					}
					
					return afterSaleList;
				} catch (error) {
					console.error('获取本地售后申请列表失败:', error);
					return [];
				}
			},
			
			// 清除本地存储的售后订单ID（可选功能）
			clearLocalAfterSaleOrderIds(userId) {
				try {
					const storageKey = `afterSaleList_${userId}`;
					uni.removeStorageSync(storageKey);
					console.log('已清除本地售后申请记录');
				} catch (error) {
					console.error('清除本地售后申请记录失败:', error);
				}
			}
		}
	}
</script>

<style scoped>
	.after-sale-center {
		width: 100%;
		height: 100vh;
		background-color: #f8f8f8;
		position: relative;
	}
	
	.center-scroll {
		width: 100%;
	}
	
	.center-content {
		padding: 24rpx;
		min-height: 100%;
	}
	
	.page-header {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 16rpx;
		padding: 40rpx 32rpx;
		margin-bottom: 32rpx;
		text-align: center;
	}
	
	.page-title {
		display: block;
		font-size: 40rpx;
		font-weight: bold;
		color: #fff;
		margin-bottom: 12rpx;
	}
	
	.page-desc {
		display: block;
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.8);
	}
	
	.after-sale-list {
		margin-bottom: 120rpx;
	}
	
	.after-sale-card {
		background: #fff;
		border-radius: 16rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
		margin-bottom: 24rpx;
		overflow: hidden;
	}
	
	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		padding: 24rpx 24rpx 16rpx 24rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}
	
	.order-info {
		flex: 1;
	}
	
	.order-id {
		display: block;
		font-size: 28rpx;
		font-weight: 500;
		color: #333;
		margin-bottom: 8rpx;
	}
	
	.apply-time {
		display: block;
		font-size: 24rpx;
		color: #999;
	}
	
	.order-amount {
		display: block;
		font-size: 24rpx;
		color: #999;
		margin-top: 8rpx;
	}
	
	.status-badge {
		padding: 8rpx 16rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		font-weight: 500;
	}
	
	.status-pending {
		background: #fff3cd;
		color: #856404;
	}
	
	.status-approved {
		background: #d4edda;
		color: #155724;
	}
	
	.status-rejected {
		background: #f8d7da;
		color: #721c24;
	}
	
	.status-completed {
		background: #d1ecf1;
		color: #0c5460;
	}
	
	.card-content {
		padding: 16rpx 24rpx;
	}
	
	.info-row {
		display: flex;
		margin-bottom: 12rpx;
		align-items: flex-start;
	}
	
	.info-row:last-child {
		margin-bottom: 0;
	}
	
	.info-row .label {
		width: 160rpx;
		font-size: 26rpx;
		color: #666;
		flex-shrink: 0;
	}
	
	.info-row .value {
		flex: 1;
		font-size: 26rpx;
		color: #333;
		line-height: 1.4;
	}
	
	.card-actions {
		display: flex;
		justify-content: flex-end;
		padding: 16rpx 24rpx 24rpx 24rpx;
		border-top: 1rpx solid #f0f0f0;
	}
	
	.action-btn {
		font-size: 24rpx;
		padding: 12rpx 24rpx;
		border-radius: 30rpx;
		margin-left: 16rpx;
		border: none;
		line-height: 1.2;
	}
	
	.detail-btn {
		background: #f8f9fa;
		color: #6c757d;
	}
	
	.contact-btn {
		background: #e3f2fd;
		color: #1976d2;
	}
	
	.cancel-btn {
		background: #ffebee;
		color: #d32f2f;
	}
	
	.bottom-space {
		height: 120rpx;
	}
	
	.bottom-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		height: 100rpx;
		background: #fff;
		border-top: 1rpx solid #e9ecef;
		display: flex;
		align-items: center;
		padding: 0 24rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
	}
	
	.bar-btn {
		flex: 1;
		height: 64rpx;
		line-height: 64rpx;
		text-align: center;
		border-radius: 32rpx;
		font-size: 28rpx;
		margin: 0 12rpx;
		border: none;
		background: #f8f9fa;
		color: #6c757d;
	}
	
	.bar-btn.primary {
		background: linear-gradient(to right, #667eea, #764ba2);
		color: #fff;
		font-weight: 500;
	}
</style> 