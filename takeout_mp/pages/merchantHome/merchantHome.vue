<template>
	<view class="merchant-home-container">
		<view class="header">
			<text class="title">商家管理系统</text>
		</view>
		
		<!-- 主导航栏 -->
		<view class="main-nav">
			<view 
				v-for="(nav, index) in mainNavs" 
				:key="index" 
				:class="['nav-item', activeMainNav === index ? 'active' : '']"
				@click="changeMainNav(index)"
			>
				{{nav.name}}
			</view>
		</view>
		
		<!-- 订单模块 -->
		<scroll-view v-if="activeMainNav === 0" scroll-y class="module-scroll-view">
			<view class="order-status-tabs">
				<view 
					v-for="(tab, index) in tabs" 
					:key="index" 
					:class="['tab-item', activeTab === index ? 'active' : '']"
					@click="changeTab(index)"
				>
					{{tab.name}}
				</view>
			</view>
			
			<view class="order-list-container">
				<template v-if="orderList.length > 0">
					<view v-for="(item, index) in orderList" :key="index" class="order-item">
						<view class="order-header">
							<text class="order-number">订单号：{{item.orderNumber || item.number}}</text>
							<text :class="['order-status', getStatusClass(item.status)]">{{getStatusText(item.status)}}</text>
						</view>
						
						<view class="order-content">
							<view class="dish-list">
								<view v-for="(dish, dishIndex) in item.orderDetails" :key="dishIndex" class="dish-item">
									<text class="dish-name">{{dish.name}}</text>
									<text class="dish-count">x{{dish.number}}</text>
								</view>
							</view>
							
							<view class="order-info">
								<view class="info-item">
									<text class="label">下单时间：</text>
									<text class="value">{{item.orderTime}}</text>
								</view>
								<view class="info-item">
									<text class="label">配送地址：</text>
									<text class="value">{{item.address}}</text>
								</view>
								<view class="info-item">
									<text class="label">联系电话：</text>
									<text class="value">{{item.phone}}</text>
								</view>
								<view class="info-item">
									<text class="label">支付金额：</text>
									<text class="value price">¥{{item.amount}}</text>
								</view>
							</view>
						</view>
						
						<view class="order-actions">
							<view v-if="item.status === 2" class="action-btn accept" @click="acceptOrder(item.id)">接单</view>
							<view v-if="item.status === 3" class="action-btn deliver" @click="deliverOrder(item.id)">配送</view>
							<view v-if="item.status === 3" class="action-btn complete" @click="completeOrder(item.id)">完成</view>
							<view v-if="item.status === 1" class="action-btn cancel" @click="cancelOrder(item.id)">取消</view>
						</view>
					</view>
				</template>
				
				<view v-else class="empty-container">
					<image class="empty-image" src="/static/images/empty-order.png" mode="aspectFit"></image>
					<text class="empty-text">暂无相关订单</text>
				</view>
			</view>
		</scroll-view>
		
		<!-- 数据模块 -->
		<scroll-view v-if="activeMainNav === 1" scroll-y class="module-scroll-view">
			<view class="data-module">
				<view class="data-cards">
					<view class="data-card">
						<view class="data-title">今日订单</view>
						<view class="data-value">15</view>
						<view class="data-change up">+20%</view>
					</view>
					<view class="data-card">
						<view class="data-title">今日销售额</view>
						<view class="data-value">¥1,280</view>
						<view class="data-change up">+15%</view>
					</view>
					<view class="data-card">
						<view class="data-title">热销菜品</view>
						<view class="data-value">宫保鸡丁</view>
						<view class="data-sub">已售28份</view>
					</view>
				</view>
				
				<view class="sales-chart">
					<view class="chart-title">近7天销售统计</view>
					<view class="chart-placeholder">
						<view class="chart-bar" style="height: 60%;"></view>
						<view class="chart-bar" style="height: 80%;"></view>
						<view class="chart-bar" style="height: 40%;"></view>
						<view class="chart-bar" style="height: 90%;"></view>
						<view class="chart-bar" style="height: 70%;"></view>
						<view class="chart-bar" style="height: 50%;"></view>
						<view class="chart-bar" style="height: 85%;"></view>
					</view>
					<view class="chart-labels">
						<text>周一</text>
						<text>周二</text>
						<text>周三</text>
						<text>周四</text>
						<text>周五</text>
						<text>周六</text>
						<text>周日</text>
					</view>
				</view>
				
				<view class="sales-chart">
					<view class="chart-title">热销菜品排行</view>
					<view class="rank-list">
						<view class="rank-item">
							<view class="rank-num">1</view>
							<view class="rank-info">
								<view class="rank-name">宫保鸡丁</view>
								<view class="rank-sales">销量: 28份</view>
							</view>
							<view class="rank-price">¥38</view>
						</view>
						<view class="rank-item">
							<view class="rank-num">2</view>
							<view class="rank-info">
								<view class="rank-name">水煮肉片</view>
								<view class="rank-sales">销量: 25份</view>
							</view>
							<view class="rank-price">¥48</view>
						</view>
						<view class="rank-item">
							<view class="rank-num">3</view>
							<view class="rank-info">
								<view class="rank-name">干锅牛蛙</view>
								<view class="rank-sales">销量: 22份</view>
							</view>
							<view class="rank-price">¥68</view>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<!-- 管理模块 -->
		<scroll-view v-if="activeMainNav === 2" scroll-y class="module-scroll-view">
			<view class="manage-module">
				<view class="manage-menu">
					<view class="manage-item" @click="navigateToSubPage('staff')">
						<view class="manage-icon">👨‍💼</view>
						<view class="manage-text">
							<view class="manage-title">员工管理</view>
							<view class="manage-desc">员工信息、权限设置</view>
						</view>
						<view class="manage-arrow">
							<u-icon name="arrow-right" color="#999" size="24"></u-icon>
						</view>
					</view>
					
					<view class="manage-item" @click="navigateToSubPage('dish')">
						<view class="manage-icon">🍲</view>
						<view class="manage-text">
							<view class="manage-title">菜品管理</view>
							<view class="manage-desc">菜品维护、价格设置</view>
						</view>
						<view class="manage-arrow">
							<u-icon name="arrow-right" color="#999" size="24"></u-icon>
						</view>
					</view>
					
					<view class="manage-item" @click="navigateToSubPage('afterSale')">
						<view class="manage-icon">🛎️</view>
						<view class="manage-text">
							<view class="manage-title">售后管理</view>
							<view class="manage-desc">退款处理、投诉处理</view>
						</view>
						<view class="manage-arrow">
							<u-icon name="arrow-right" color="#999" size="24"></u-icon>
						</view>
					</view>
				</view>
				
				<view class="manage-menu" style="margin-top: 20rpx;">
					<view class="manage-item" @click="navigateToSubPage('settings')">
						<view class="manage-icon">⚙️</view>
						<view class="manage-text">
							<view class="manage-title">店铺设置</view>
							<view class="manage-desc">营业时间、配送范围</view>
						</view>
						<view class="manage-arrow">
							<u-icon name="arrow-right" color="#999" size="24"></u-icon>
						</view>
					</view>
					
					<view class="manage-item" @click="navigateToSubPage('finance')">
						<view class="manage-icon">💰</view>
						<view class="manage-text">
							<view class="manage-title">财务管理</view>
							<view class="manage-desc">收支明细、账务统计</view>
						</view>
						<view class="manage-arrow">
							<u-icon name="arrow-right" color="#999" size="24"></u-icon>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { orderPagingApi, cancelOrderApi } from '../../api/orderList.js';
	
	export default {
		data() {
			return {
				activeMainNav: 0, // 默认显示订单模块
				mainNavs: [
					{ name: '订单', icon: 'file-text' },
					{ name: '数据', icon: 'chart' },
					{ name: '管理', icon: 'setting' }
				],
				activeTab: 0,
				tabs: [
					{ name: '全部订单', status: null },
					{ name: '待付款', status: 1 },
					{ name: '待接单', status: 2 },
					{ name: '待配送', status: 3 },
					{ name: '配送中', status: 4 },
					{ name: '已完成', status: 5 },
					{ name: '已取消', status: 6 }
				],
				orderList: [],
				pageInfo: {
					page: 1,
					size: 10,
					total: 0
				},
				loading: false
			}
		},
		onLoad() {
			this.getOrderList();
		},
		methods: {
			changeMainNav(index) {
				this.activeMainNav = index;
				if (index === 0) {
					this.getOrderList();
				}
			},
			
			changeTab(index) {
				this.activeTab = index;
				this.pageInfo.page = 1;
				this.orderList = [];
				this.getOrderList();
			},
			
			navigateToSubPage(type) {
				// 目前只是提示，后续可以实现对应页面跳转
				uni.showToast({
					title: `即将开发${type === 'staff' ? '员工管理' : type === 'dish' ? '菜品管理' : '售后管理'}功能`,
					icon: 'none'
				});
			},
			
			async getOrderList() {
				this.loading = true;
				try {
					// 模拟订单数据，因为后端未完成
					this.orderList = this.mockOrderData();
					// 实际调用应该是这样：
					// const params = {
					//     page: this.pageInfo.page,
					//     size: this.pageInfo.size,
					//     status: this.tabs[this.activeTab].status
					// };
					// const res = await orderPagingApi(params);
					// if (res.code === 0) {
					//     this.orderList = res.data.records;
					//     this.pageInfo.total = res.data.total;
					// }
				} catch (e) {
					console.error(e);
					uni.showToast({
						title: '获取订单列表失败',
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},
			
			mockOrderData() {
				// 模拟订单数据
				const statusFilter = this.tabs[this.activeTab].status;
				const allOrders = [
					{
						id: '1001',
						orderNumber: 'O2024050100001',
						status: 2,
						orderTime: '2024-05-01 12:30:45',
						address: '北京市海淀区中关村大街1号',
						phone: '139****1234',
						amount: 68.5,
						orderDetails: [
							{ name: '宫保鸡丁', number: 1 },
							{ name: '米饭', number: 2 },
							{ name: '可乐', number: 1 }
						]
					},
					{
						id: '1002',
						orderNumber: 'O2024050100002',
						status: 3,
						orderTime: '2024-05-01 13:20:15',
						address: '北京市朝阳区朝阳门外大街1号',
						phone: '138****5678',
						amount: 98.0,
						orderDetails: [
							{ name: '水煮肉片', number: 1 },
							{ name: '青椒土豆丝', number: 1 },
							{ name: '米饭', number: 2 }
						]
					}
				];
				
				if (statusFilter === null) {
					return allOrders;
				} else {
					return allOrders.filter(order => order.status === statusFilter);
				}
			},
			
			getStatusText(status) {
				switch (status) {
					case 1: return '待付款';
					case 2: return '待接单';
					case 3: return '待配送';
					case 4: return '配送中';
					case 5: return '已完成';
					case 6: return '已取消';
					default: return '未知状态';
				}
			},
			
			getStatusClass(status) {
				switch (status) {
					case 1: return 'waiting-payment';
					case 2: return 'waiting-accept';
					case 3: return 'waiting-delivery';
					case 4: return 'delivering';
					case 5: return 'completed';
					case 6: return 'cancelled';
					default: return '';
				}
			},
			
			// 以下方法应该调用API，但当前只是模拟功能
			acceptOrder(orderId) {
				uni.showModal({
					title: '确认接单',
					content: '确定要接受该订单吗？',
					success: (res) => {
						if (res.confirm) {
							// 模拟接单成功
							this.updateOrderStatus(orderId, 3);
							uni.showToast({
								title: '接单成功',
								icon: 'success'
							});
						}
					}
				});
			},
			
			deliverOrder(orderId) {
				uni.showModal({
					title: '确认配送',
					content: '确定该订单开始配送吗？',
					success: (res) => {
						if (res.confirm) {
							// 模拟开始配送
							this.updateOrderStatus(orderId, 4);
							uni.showToast({
								title: '订单已开始配送',
								icon: 'success'
							});
						}
					}
				});
			},
			
			completeOrder(orderId) {
				uni.showModal({
					title: '确认完成',
					content: '确定该订单已完成吗？',
					success: (res) => {
						if (res.confirm) {
							// 模拟完成订单
							this.updateOrderStatus(orderId, 5);
							uni.showToast({
								title: '订单已完成',
								icon: 'success'
							});
						}
					}
				});
			},
			
			cancelOrder(orderId) {
				uni.showModal({
					title: '确认取消',
					content: '确定要取消该订单吗？',
					success: (res) => {
						if (res.confirm) {
							// 模拟取消订单
							this.updateOrderStatus(orderId, 6);
							uni.showToast({
								title: '订单已取消',
								icon: 'success'
							});
						}
					}
				});
			},
			
			updateOrderStatus(orderId, newStatus) {
				// 模拟更新订单状态
				const orderIndex = this.orderList.findIndex(order => order.id === orderId);
				if (orderIndex !== -1) {
					this.orderList[orderIndex].status = newStatus;
				}
				
				// 如果当前是按状态筛选，则可能需要从列表中移除
				if (this.tabs[this.activeTab].status !== null && this.tabs[this.activeTab].status !== newStatus) {
					this.orderList = this.orderList.filter(order => order.id !== orderId);
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.merchant-home-container {
		background-color: #f5f5f5;
		min-height: 100vh;
		padding-bottom: 20rpx;
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
	
	.main-nav {
		display: flex;
		background-color: #fff;
		padding: 0 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.nav-item {
			flex: 1;
			padding: 20rpx 0;
			font-size: 30rpx;
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
	
	/* 添加可滚动区域样式 */
	.module-scroll-view {
		height: calc(100vh - 170rpx); /* 减去顶部标题栏和主导航栏的高度 */
	}
	
	.order-status-tabs {
		display: flex;
		background-color: #fff;
		padding: 0 20rpx;
		overflow-x: auto;
		white-space: nowrap;
		
		.tab-item {
			padding: 20rpx 30rpx;
			font-size: 28rpx;
			color: #666;
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
	
	.order-list-container {
		padding: 20rpx;
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
			
			.order-status {
				font-size: 26rpx;
				
				&.waiting-payment {
					color: #FF9800;
				}
				
				&.waiting-accept {
					color: #2196F3;
				}
				
				&.waiting-delivery {
					color: #2196F3;
				}
				
				&.delivering {
					color: #FF5722;
				}
				
				&.completed {
					color: #4CAF50;
				}
				
				&.cancelled {
					color: #9E9E9E;
				}
			}
		}
		
		.order-content {
			padding: 20rpx 0;
			
			.dish-list {
				margin-bottom: 30rpx;
				
				.dish-item {
					display: flex;
					justify-content: space-between;
					font-size: 28rpx;
					margin-bottom: 10rpx;
					color: #333;
					
					.dish-name {
						flex: 1;
					}
					
					.dish-count {
						color: #666;
					}
				}
			}
			
			.order-info {
				.info-item {
					display: flex;
					font-size: 26rpx;
					margin-bottom: 10rpx;
					
					.label {
						color: #999;
						width: 160rpx;
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
				
				&.accept {
					background-color: #2196F3;
					color: #fff;
				}
				
				&.deliver {
					background-color: #FF5722;
					color: #fff;
				}
				
				&.complete {
					background-color: #4CAF50;
					color: #fff;
				}
				
				&.cancel {
					border: 2rpx solid #9E9E9E;
					color: #9E9E9E;
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
	
	/* 数据模块样式 */
	.data-module {
		padding: 20rpx;
	}
	
	.data-cards {
		display: flex;
		justify-content: space-between;
		margin-bottom: 20rpx;
	}
	
	.data-card {
		flex: 1;
		background-color: #fff;
		border-radius: 12rpx;
		padding: 20rpx;
		margin: 0 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.data-title {
			font-size: 24rpx;
			color: #666;
			margin-bottom: 10rpx;
		}
		
		.data-value {
			font-size: 32rpx;
			color: #333;
			font-weight: bold;
			margin-bottom: 10rpx;
		}
		
		.data-change {
			font-size: 22rpx;
			
			&.up {
				color: #4CAF50;
			}
			
			&.down {
				color: #F44336;
			}
		}
		
		.data-sub {
			font-size: 22rpx;
			color: #999;
		}
	}
	
	.sales-chart {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.chart-title {
			font-size: 28rpx;
			color: #333;
			margin-bottom: 30rpx;
		}
		
		.chart-placeholder {
			display: flex;
			height: 300rpx;
			align-items: flex-end;
			justify-content: space-between;
			padding: 0 20rpx;
			
			.chart-bar {
				width: 30rpx;
				background-color: #FF8C00;
				border-radius: 30rpx 30rpx 0 0;
			}
		}
		
		.chart-labels {
			display: flex;
			justify-content: space-between;
			margin-top: 20rpx;
			padding: 0 10rpx;
			
			text {
				font-size: 22rpx;
				color: #999;
			}
		}
	}
	
	/* 排行榜样式 */
	.rank-list {
		margin-top: 20rpx;
	}
	
	.rank-item {
		display: flex;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 2rpx solid #f5f5f5;
		
		&:last-child {
			border-bottom: none;
		}
		
		.rank-num {
			width: 40rpx;
			height: 40rpx;
			border-radius: 50%;
			background-color: #FF8C00;
			color: #fff;
			font-size: 24rpx;
			font-weight: bold;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-right: 20rpx;
		}
		
		.rank-info {
			flex: 1;
			
			.rank-name {
				font-size: 28rpx;
				color: #333;
				font-weight: 500;
				margin-bottom: 6rpx;
			}
			
			.rank-sales {
				font-size: 24rpx;
				color: #999;
			}
		}
		
		.rank-price {
			font-size: 28rpx;
			color: #FF5722;
			font-weight: bold;
		}
	}
	
	/* 管理模块样式 */
	.manage-module {
		padding: 20rpx;
	}
	
	.manage-menu {
		background-color: #fff;
		border-radius: 12rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
	}
	
	.manage-item {
		display: flex;
		align-items: center;
		padding: 30rpx;
		border-bottom: 2rpx solid #f5f5f5;
		
		&:last-child {
			border-bottom: none;
		}
		
		.manage-icon {
			font-size: 40rpx;
			margin-right: 20rpx;
		}
		
		.manage-text {
			flex: 1;
			
			.manage-title {
				font-size: 30rpx;
				color: #333;
				margin-bottom: 6rpx;
			}
			
			.manage-desc {
				font-size: 24rpx;
				color: #999;
			}
		}
	}
</style> 