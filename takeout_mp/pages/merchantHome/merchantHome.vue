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
						<view class="data-value">{{ statistics.today.orderCount }}</view>
						<view class="data-change" :class="statistics.today.compareYesterday >= 0 ? 'up' : 'down'">
							{{ statistics.today.compareYesterday >= 0 ? '+' : '' }}{{ statistics.today.compareYesterday }}%
						</view>
					</view>
					<view class="data-card">
						<view class="data-title">今日销售额</view>
						<view class="data-value">¥{{ statistics.today.totalAmount }}</view>
						<view class="data-change" :class="statistics.today.compareYesterday >= 0 ? 'up' : 'down'">
							{{ statistics.today.compareYesterday >= 0 ? '+' : '' }}{{ statistics.today.compareYesterday }}%
						</view>
					</view>
					<view class="data-card">
						<view class="data-title">热销菜品</view>
						<view class="data-value">{{ statistics.topDish.name || '暂无数据' }}</view>
						<view class="data-sub" v-if="statistics.topDish.name">已售{{ statistics.topDish.count }}份</view>
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
	import { 
		getOrderListApi, 
		getOrderDetailApi, 
		acceptOrderApi, 
		deliverOrderApi, 
		completeOrderApi, 
		cancelOrderApi,
		getStatisticsApi,
		getMerchantInfoApi
	} from '../../api/merchant.js';
	
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
				loading: false,
				merchantInfo: null,
				// 统计数据
				statistics: {
					today: {
						orderCount: 0,
						totalAmount: 0,
						compareYesterday: 0 // 环比增长率
					},
					topDish: {
						name: '',
						count: 0
					}
				}
			}
		},
		onLoad() {
			// 检查token
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
			
			// 获取商家信息
			this.getMerchantInfo();
			
			// 获取订单列表
			this.getOrderList();
			
			// 获取统计数据
			this.getStatisticsData();
		},
		methods: {
			changeMainNav(index) {
				this.activeMainNav = index;
				if (index === 0) {
					this.getOrderList();
				} else if (index === 1) {
					this.getStatisticsData();
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
					// 检查token
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
					
					// 设置请求参数
					const params = {
						page: this.pageInfo.page,
						pageNum: this.pageInfo.page, // 添加pageNum参数（有些API可能使用pageNum而不是page）
						size: this.pageInfo.size,
						pageSize: this.pageInfo.size // 添加pageSize参数
					};
					
					// 添加状态过滤
					const statusFilter = this.tabs[this.activeTab].status;
					if (statusFilter !== null) {
						params.status = statusFilter;
					}
					
					// 调用API获取订单列表
					try {
						console.log('正在获取订单列表，参数:', params);
						const res = await getOrderListApi(params);
						console.log('订单列表响应:', res);
						
						if (res && (res.records || res.list)) {
							// 处理成功的API响应
							const orderList = res.records || res.list || [];
							this.orderList = orderList.map(order => {
								// 处理订单项数据结构以适配页面显示
								return {
									...order,
									orderNumber: order.orderNumber || order.number,
									orderTime: order.orderTime || order.createTime,
									orderDetails: order.items || order.orderDetails || [],
									amount: order.total || order.amount,
									status: this.convertOrderStatus(order.status)
								};
							});
							this.pageInfo.total = res.total || 0;
						} else if (Array.isArray(res)) {
							// 如果直接返回数组
							this.orderList = res.map(order => {
								return {
									...order,
									orderNumber: order.orderNumber || order.number,
									orderTime: order.orderTime || order.createTime,
									orderDetails: order.items || order.orderDetails || [],
									amount: order.total || order.amount,
									status: this.convertOrderStatus(order.status)
								};
							});
							this.pageInfo.total = res.length;
						} else {
							// API调用成功但返回了意外格式
							console.warn('获取订单列表返回格式异常', res);
							uni.showToast({
								title: '订单数据格式异常',
								icon: 'none'
							});
							// 降级使用模拟数据
							this.orderList = this.mockOrderData();
						}
					} catch (error) {
						console.error('获取订单列表失败', error);
						// 降级使用模拟数据
						this.orderList = this.mockOrderData();
						
						uni.showToast({
							title: '获取订单列表失败，使用本地数据',
							icon: 'none'
						});
					}
				} finally {
					this.loading = false;
				}
			},
			
			// 获取统计数据
			async getStatisticsData() {
				try {
					// 实际API调用
					const params = {
						type: 'day' // 获取今日数据
					};
					
					try {
						console.log('正在获取统计数据，参数:', params);
						const res = await getStatisticsApi(params);
						console.log('统计数据响应:', res);
						
						if (res) {
							// 后端可能有不同的数据结构，尝试适配
							// 今日订单总数
							this.statistics.today.orderCount = res.orderCount || res.count || 0;
							// 今日销售额
							this.statistics.today.totalAmount = res.totalAmount || res.total || 0;
							// 环比增长
							this.statistics.today.compareYesterday = res.compareYesterday || 0;
							
							// 热销商品
							if (res.topDish || (Array.isArray(res) && res.length > 0)) {
								const topDish = res.topDish || res[0];
								this.statistics.topDish.name = topDish.name || '';
								this.statistics.topDish.count = topDish.count || topDish.sales || 0;
							}
						}
					} catch (error) {
						console.error('获取统计数据失败', error);
						// 保持使用默认数据
					}
				} catch (e) {
					console.error(e);
				}
			},
			
			// 转换订单状态
			convertOrderStatus(status) {
				// 将后端返回的状态转换为前端使用的状态码
				if (typeof status === 'string') {
					switch (status.toUpperCase()) {
						case 'PENDING': return 1;
						case 'UNCOMFIRMED': return 2;
						case 'CONFIRMED': return 3;
						case 'DELIVERING': return 4;
						case 'COMPLETED': return 5;
						case 'CANCELED': return 6;
						default: return 0;
					}
				}
				// 如果已经是数字，直接返回
				return status;
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
			
			// 接单
			async acceptOrder(orderId) {
				uni.showModal({
					title: '确认接单',
					content: '确定要接受该订单吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								console.log('接单：', orderId);
								const response = await acceptOrderApi(orderId);
								if (response) {
									// 更新成功
									this.updateOrderStatus(orderId, 3);
									uni.showToast({
										title: '接单成功',
										icon: 'success'
									});
								}
							} catch (error) {
								console.error('接单失败', error);
								uni.showToast({
									title: '接单失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示
								this.updateOrderStatus(orderId, 3);
							}
						}
					}
				});
			},
			
			// 开始配送
			async deliverOrder(orderId) {
				uni.showModal({
					title: '确认配送',
					content: '确定该订单开始配送吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								console.log('开始配送：', orderId);
								const response = await deliverOrderApi(orderId);
								if (response) {
									// 更新成功
									this.updateOrderStatus(orderId, 4);
									uni.showToast({
										title: '已开始配送',
										icon: 'success'
									});
								}
							} catch (error) {
								console.error('开始配送失败', error);
								uni.showToast({
									title: '操作失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示
								this.updateOrderStatus(orderId, 4);
							}
						}
					}
				});
			},
			
			// 完成订单
			async completeOrder(orderId) {
				uni.showModal({
					title: '确认完成',
					content: '确定该订单已完成配送吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								console.log('完成订单：', orderId);
								const response = await completeOrderApi(orderId);
								if (response) {
									// 更新成功
									this.updateOrderStatus(orderId, 5);
									uni.showToast({
										title: '订单已完成',
										icon: 'success'
									});
								}
							} catch (error) {
								console.error('完成订单失败', error);
								uni.showToast({
									title: '操作失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示
								this.updateOrderStatus(orderId, 5);
							}
						}
					}
				});
			},
			
			// 取消订单
			async cancelOrder(orderId) {
				uni.showModal({
					title: '确认取消',
					content: '确定要取消该订单吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								const data = { 
									reason: '商家主动取消' 
								};
								console.log('取消订单：', orderId, data);
								const response = await cancelOrderApi(orderId, data);
								if (response) {
									// 更新成功
									this.updateOrderStatus(orderId, 6);
									uni.showToast({
										title: '订单已取消',
										icon: 'success'
									});
								}
							} catch (error) {
								console.error('取消订单失败', error);
								uni.showToast({
									title: '操作失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示
								this.updateOrderStatus(orderId, 6);
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
				}
			},
			
			// 获取商家信息
			async getMerchantInfo() {
				try {
					const res = await getMerchantInfoApi();
					console.log('商家信息:', res);
					
					if (res) {
						this.merchantInfo = res;
						// 存储商家信息到本地
						uni.setStorageSync('merchantInfo', JSON.stringify(res));
					}
				} catch (error) {
					console.error('获取商家信息失败', error);
					// 尝试从本地存储获取
					const merchantInfoStr = uni.getStorageSync('merchantInfo');
					if (merchantInfoStr) {
						try {
							this.merchantInfo = JSON.parse(merchantInfoStr);
						} catch (e) {
							console.error('解析商家信息失败', e);
						}
					}
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