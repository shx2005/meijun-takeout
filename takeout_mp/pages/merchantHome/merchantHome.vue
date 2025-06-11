<template>
	<view class="merchant-home-container">
		<view class="header">
			<text class="title">{{pageTitle}}</text>
		</view>
		
		<!-- 主导航栏 -->
		<view class="main-nav">
			<view 
				v-for="(nav, index) in visibleNavs" 
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
					:class="['tab-item', currentTab === tab ? 'active' : '']"
					@click="changeTab(tab)"
				>
					{{tabNames[tab]}}
				</view>
				<!-- 添加刷新按钮 -->
				<view class="refresh-btn" @click="refreshOrderCache">
					<text class="refresh-icon">🔄</text>
					<text class="refresh-text">刷新</text>
				</view>
			</view>
			
			<!-- 缓存状态提示 -->
			<view class="cache-status" v-if="orderCache.size > 0">
				<text class="cache-info">缓存: {{orderCache.size}}个订单 | 更新时间: {{formatCacheTime()}}</text>
			</view>
			
			<view class="order-list-container">
				<template v-if="orderList.length > 0">
					<view v-for="(item, index) in orderList" :key="index" class="order-item">
						<view class="order-header">
							<text class="order-number">订单号：{{item.orderNumber}}</text>
							<view class="status-group">
								<text :class="['order-status', getStatusClass(item.status)]">{{item.statusText}}</text>
								<text :class="['pay-status', getPayStatusClass(item.payStatus)]">{{item.payStatusText}}</text>
							</view>
						</view>
						
						<view class="order-content">
							<view class="dish-list">
								<view v-for="(dish, dishIndex) in item.items" :key="dishIndex" class="dish-item">
									<text class="dish-name">{{dish.name}}</text>
									<text class="dish-flavor" v-if="dish.dishFlavor">{{dish.dishFlavor}}</text>
									<text class="dish-count">x{{dish.quantity}}</text>
									<text class="dish-price">¥{{dish.total}}</text>
								</view>
							</view>
							
							<view class="order-info">
								<view class="info-item">
									<text class="label">用户ID：</text>
									<text class="value">{{item.userId}}</text>
								</view>
								<view class="info-item">
									<text class="label">下单时间：</text>
									<text class="value">{{formatTime(item.orderTime)}}</text>
								</view>
								<view class="info-item">
									<text class="label">订单金额：</text>
									<text class="value price">¥{{item.totalAmount}}</text>
								</view>
								<view class="info-item" v-if="item.remark">
									<text class="label">备注：</text>
									<text class="value">{{item.remark}}</text>
								</view>
							</view>
						</view>
						
						<view class="order-actions">
							<view class="action-btn detail" @click="viewOrderDetail(item.id)">查看详情</view>
							<!-- 只有已支付状态的订单才能配送 -->
							<view v-if="item.status === 2 && item.payStatus === 'paid'" class="action-btn deliver" @click="deliverOrder(item.id)">配送</view>
							<view v-if="item.status === 3" class="action-btn complete" @click="completeOrder(item.id)">完成</view>
						</view>
					</view>
				</template>
				
				<view v-else class="empty-container">
					<image class="empty-image" src="/static/images/empty-order.png" mode="aspectFit"></image>
					<text class="empty-text">暂无相关订单</text>
				</view>
			</view>
		</scroll-view>
		
		<!-- 管理模块 -->
		<scroll-view v-if="activeMainNav === 1" scroll-y class="module-scroll-view">
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
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { 
		getOrderListApi, 
		getOrderDetailApi,
		getMerchantOrderDetailApi,
		updateOrderStatusApi,
		deliverOrderApi, 
		completeOrderApi, 
		rejectOrderApi,
		getStatisticsApi,
		getSalesDataApi,
		getTrafficDataApi,
		getMerchantInfoApi,
		getAfterSaleListApi,
		approveAfterSaleApi,
		rejectAfterSaleApi
	} from '../../api/merchant.js';
	
	export default {
		data() {
			return {
				activeMainNav: 0, // 默认显示订单模块
				mainNavs: [
					{ name: '订单', icon: 'file-text' },
					{ name: '管理', icon: 'setting' }
				],
				userType: '', // 用户类型：现在员工登录后也是merchant
				isEmployee: false, // 是否为员工登录
				currentTab: 'all',
				tabs: ['all', 'confirmed', 'delivering', 'completed'],
				tabNames: {
					'all': '全部订单',
					'confirmed': '已确认', 
					'delivering': '配送中',
					'completed': '已完成'
				},
				orderList: [],
				// 添加缓存相关属性
				orderCache: new Map(), // 订单缓存，key为orderId，value为订单数据
				cacheExpireTime: 5 * 60 * 1000, // 缓存过期时间：5分钟
				lastCacheTime: 0, // 上次缓存时间
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
				},
				// 售后数据
				afterSaleList: [],
				// 其他数据
				trafficData: {},
				employeeList: [],
				storeList: [],
				couponList: [],
				promotionList: [],
				userList: []
			}
		},
		computed: {
			// 根据是否为员工显示可见的导航项
			visibleNavs() {
				if (this.isEmployee) {
					// 员工只能看到订单模块
					return [{ name: '订单', icon: 'file-text' }];
				}
				// 商家可以看到所有模块
				return this.mainNavs;
			},
			
			// 根据是否为员工显示页面标题
			pageTitle() {
				if (this.isEmployee) {
					return '员工工作台';
				}
				return '商家管理系统';
			}
		},
		onLoad() {
			console.log('商家端页面加载开始');
			
			// 检查token和用户类型
			// 注意：员工登录后也使用merchantToken和merchant身份
			const token = uni.getStorageSync('merchantToken');
			const userType = uni.getStorageSync('merchantUserType');
			const merchantInfoStr = uni.getStorageSync('merchantInfo');
			
			// 解析商家信息，判断是否为员工登录
			let merchantInfo = null;
			try {
				merchantInfo = merchantInfoStr ? JSON.parse(merchantInfoStr) : null;
			} catch (e) {
				console.error('解析merchantInfo失败:', e);
			}
			
			// 设置用户类型和员工标识
			this.userType = userType || 'merchant';
			this.isEmployee = merchantInfo?.isEmployee || false;
			
			console.log('商家登录状态检查:', {
				token: token ? '已存在' : '不存在',
				userType: this.userType,
				isEmployee: this.isEmployee,
				employeeName: merchantInfo?.employeeName || '',
				note: this.isEmployee ? '员工使用merchant身份和权限' : '商家直接登录'
			});
			
			if (!token) {
				console.log('未找到商家token，跳转到登录页');
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
			
			console.log('商家登录状态正常，开始加载数据');
			console.log('当前身份:', this.isEmployee ? `员工(${merchantInfo?.employeeName})使用merchant权限` : '商家');
			
			// 获取商家信息
			this.getMerchantInfo();
			
			// 获取订单列表
			this.getOrderList();
			
			// 获取统计数据
			this.getStatisticsData();
		},
		onShow() {
			console.log('商家端页面显示');
			// 如果缓存过期，重新获取数据
			const now = Date.now();
			const cacheExpired = (now - this.lastCacheTime) >= this.cacheExpireTime;
			
			if (cacheExpired && this.activeMainNav === 0) {
				console.log('缓存已过期，重新获取订单数据');
				this.getOrderList();
			}
		},
		onHide() {
			console.log('商家端页面隐藏');
			// 这里可以选择是否清理缓存，目前选择保留以提高用户体验
		},
		onUnload() {
			console.log('商家端页面卸载，清理缓存');
			this.clearOrderCache();
		},
		methods: {
			changeMainNav(index) {
				this.activeMainNav = index;
				if (index === 0) {
					this.getOrderList();
				}
			},
			
			changeTab(tab) {
				this.currentTab = tab;
				// 切换标签时使用缓存数据进行过滤，不需要重新请求
				this.filterOrdersByTab();
			},
			
			// 根据标签过滤订单
			filterOrdersByTab() {
				if (this.orderCache.size === 0) {
					// 如果缓存为空，重新获取数据
					this.getOrderList();
					return;
				}

				const allOrders = Array.from(this.orderCache.values());
				
				if (this.currentTab === 'all') {
					this.orderList = allOrders;
				} else {
					const statusMap = {
						'confirmed': [2], // 已确认
						'delivering': [3], // 配送中
						'completed': [4] // 已完成
					};
					
					const targetStatuses = statusMap[this.currentTab] || [];
					this.orderList = allOrders.filter(order => 
						targetStatuses.includes(order.status)
					);
				}

				console.log(`标签切换到 ${this.currentTab}，显示 ${this.orderList.length} 个订单`);
			},
			
			navigateToSubPage(type) {
				switch (type) {
					case 'staff':
						uni.navigateTo({
							url: '/pages/staffManagement/staffManagement'
						});
						break;
					case 'dish':
						uni.navigateTo({
							url: '/pages/dishManagement/dishManagement'
						});
						break;
					case 'afterSale':
						// 跳转到售后管理页面
						this.showAfterSaleManagement();
						break;
					case 'settings':
						uni.showToast({
							title: '店铺设置功能开发中',
							icon: 'none'
						});
						break;
					case 'finance':
						uni.showToast({
							title: '财务管理功能开发中',
							icon: 'none'
						});
						break;
					default:
						uni.showToast({
							title: '功能开发中',
							icon: 'none'
						});
				}
			},
			
			// 显示售后管理
			async showAfterSaleManagement() {
				try {
					uni.showLoading({ title: '加载售后列表...' });
					
					// 获取售后列表
					const afterSaleList = await this.getAfterSaleList();
					
					uni.hideLoading();
					
					if (afterSaleList && afterSaleList.length > 0) {
						// 显示售后列表选择框
						const itemList = afterSaleList.map(item => 
							`订单${item.orderId} - ${this.getAfterSaleTypeText(item.type)} - ${this.getAfterSaleStatusText(item.status)}`
						);
						
						uni.showActionSheet({
							itemList: itemList,
							success: (res) => {
								const selectedItem = afterSaleList[res.tapIndex];
								this.handleAfterSaleItem(selectedItem);
							}
						});
					} else {
						uni.showToast({
							title: '暂无售后申请',
							icon: 'none'
						});
					}
				} catch (error) {
					uni.hideLoading();
					console.error('获取售后列表失败:', error);
					uni.showToast({
						title: '获取售后列表失败',
						icon: 'none'
					});
				}
			},
			
			// 获取售后列表
			async getAfterSaleList() {
				try {
					const res = await getAfterSaleListApi();
					console.log('售后列表响应:', res);
					return res || [];
				} catch (error) {
					console.error('获取售后列表失败:', error);
					return [];
				}
			},
			
			// 处理售后项目
			handleAfterSaleItem(item) {
				if (item.status === 'pending') {
					// 待处理的售后申请，显示审批选项
					// 先显示详情，然后提供操作选择
					uni.showModal({
						title: '售后申请详情',
						content: `订单${item.orderId}申请${this.getAfterSaleTypeText(item.type)}\n原因：${item.reason}\n描述：${item.content || '无'}\n\n请选择处理方式`,
						showCancel: false,
						confirmText: '选择操作',
						success: (res) => {
							if (res.confirm) {
								// 显示操作选择
								uni.showActionSheet({
									itemList: ['同意申请', '拒绝申请', '暂不处理'],
									success: (actionRes) => {
										switch (actionRes.tapIndex) {
											case 0: // 同意申请
												this.approveAfterSale(item);
												break;
											case 1: // 拒绝申请
												this.rejectAfterSale(item);
												break;
											case 2: // 暂不处理
												uni.showToast({
													title: '已取消操作',
													icon: 'none',
													duration: 1500
												});
												break;
										}
									},
									fail: () => {
										// 用户取消选择，相当于暂不处理
										uni.showToast({
											title: '已取消操作',
											icon: 'none',
											duration: 1500
										});
									}
								});
							}
						}
					});
				} else {
					// 已处理的售后申请，显示详情
					uni.showModal({
						title: '售后详情',
						content: `订单${item.orderId}\n类型：${this.getAfterSaleTypeText(item.type)}\n状态：${this.getAfterSaleStatusText(item.status)}\n原因：${item.reason}\n描述：${item.content || '无'}`,
						showCancel: false,
						confirmText: '知道了'
					});
				}
			},
			
			// 同意售后申请
			async approveAfterSale(item) {
				try {
					uni.showLoading({ title: '处理中...' });
					
					const res = await approveAfterSaleApi({
						requestId: item.id,
						userId: item.userId
					});
					
					uni.hideLoading();
					
					if (res) {
						uni.showToast({
							title: '已同意售后申请',
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: '处理失败，请重试',
							icon: 'none'
						});
					}
				} catch (error) {
					uni.hideLoading();
					console.error('同意售后申请失败:', error);
					uni.showToast({
						title: '处理失败，请重试',
						icon: 'none'
					});
				}
			},
			
			// 拒绝售后申请
			async rejectAfterSale(item) {
				try {
					uni.showLoading({ title: '处理中...' });
					
					const res = await rejectAfterSaleApi({
						requestId: item.id,
						userId: item.userId
					});
					
					uni.hideLoading();
					
					if (res) {
						uni.showToast({
							title: '已拒绝售后申请',
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: '处理失败，请重试',
							icon: 'none'
						});
					}
				} catch (error) {
					uni.hideLoading();
					console.error('拒绝售后申请失败:', error);
					uni.showToast({
						title: '处理失败，请重试',
						icon: 'none'
					});
				}
			},
			
			// 获取售后类型文本
			getAfterSaleTypeText(type) {
				switch (type) {
					case 'refund': return '退款';
					case 'replace': return '换货';
					case 'other': return '其他';
					default: return '未知';
				}
			},
			
			// 获取售后状态文本
			getAfterSaleStatusText(status) {
				switch (status) {
					case 'pending': return '待处理';
					case 'approved': return '已同意';
					case 'rejected': return '已拒绝';
					case 'completed': return '已完成';
					default: return '未知状态';
				}
			},
			
			// 获取订单列表
			async getOrderList() {
				try {
					uni.showLoading({
						title: '加载订单中...'
					});

					// 检查缓存是否存在且未过期
					const now = Date.now();
					const cacheValid = this.orderCache.size > 0 && 
						(now - this.lastCacheTime) < this.cacheExpireTime;

					let allOrders = [];

					if (cacheValid) {
						console.log('使用缓存数据，缓存订单数量:', this.orderCache.size);
						// 使用缓存数据
						allOrders = Array.from(this.orderCache.values());
					} else {
						console.log('缓存无效或不存在，重新获取订单数据');
						// 缓存无效或不存在，重新获取1-60号订单
						allOrders = await this.fetchAndCacheOrders();
					}

					// 根据当前选中的状态过滤订单
					if (this.currentTab === 'all') {
						this.orderList = allOrders;
					} else {
						const statusMap = {
							'confirmed': [2], // 已确认
							'delivering': [3], // 配送中
							'completed': [4] // 已完成
						};
						
						const targetStatuses = statusMap[this.currentTab] || [];
						this.orderList = allOrders.filter(order => 
							targetStatuses.includes(order.status)
						);
					}

					console.log(`当前显示 ${this.orderList.length} 个订单，缓存中共有 ${this.orderCache.size} 个订单`);

				} catch (error) {
					console.error('获取订单列表失败:', error);
					uni.showToast({
						title: '获取订单失败',
						icon: 'error'
					});
					this.orderList = [];
				} finally {
					uni.hideLoading();
				}
			},
			
			// 获取并缓存1-60号订单
			async fetchAndCacheOrders() {
				console.log('开始获取1-60号订单并缓存');
				
				// 通过遍历订单ID 1-60来获取订单详情
				const orderPromises = [];
				for (let orderId = 1; orderId <= 60; orderId++) {
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

				// 并发执行所有请求
				const results = await Promise.allSettled(orderPromises);
				
				// 清空旧缓存
				this.orderCache.clear();
				
				// 过滤出成功的订单并缓存
				const successfulOrders = [];
				results.forEach((result, index) => {
					if (result.status === 'fulfilled' && result.value.success) {
						const order = result.value.data;
						// 确保订单有必要的字段
						if (order && order.id) {
							const processedOrder = {
								id: order.id,
								orderNumber: order.orderNumber || `ORDER-${order.id}`,
								customerName: order.customerName || order.customer?.name || `用户${order.userId || '未知'}`,
								totalAmount: order.total || 0,
								status: this.mapOrderStatus(order.status),
								statusText: this.getStatusText(order.status),
								payStatus: order.payStatus || 'unpaid',
								payStatusText: this.getPayStatusText(order.payStatus),
								createTime: order.createTime || order.orderTime || new Date().toISOString(),
								orderTime: order.orderTime || order.createTime || new Date().toISOString(),
								items: order.items || [],
								remark: order.remark || '',
								userId: order.userId || 0,
								merchantId: order.merchantId || 0
							};
							
							// 缓存订单
							this.orderCache.set(order.id, processedOrder);
							successfulOrders.push(processedOrder);
						}
					}
				});

				// 更新缓存时间
				this.lastCacheTime = Date.now();
				
				console.log(`成功缓存 ${successfulOrders.length} 个订单`);
				return successfulOrders;
			},

			// 更新单个订单缓存
			async updateOrderCache(orderId) {
				try {
					console.log('更新订单缓存:', orderId);
					
					// 重新获取订单详情
					const order = await getOrderDetailApi(orderId);
					
					if (order && order.id) {
						const processedOrder = {
							id: order.id,
							orderNumber: order.orderNumber || `ORDER-${order.id}`,
							customerName: order.customerName || order.customer?.name || `用户${order.userId || '未知'}`,
							totalAmount: order.total || 0,
							status: this.mapOrderStatus(order.status),
							statusText: this.getStatusText(order.status),
							payStatus: order.payStatus || 'unpaid',
							payStatusText: this.getPayStatusText(order.payStatus),
							createTime: order.createTime || order.orderTime || new Date().toISOString(),
							orderTime: order.orderTime || order.createTime || new Date().toISOString(),
							items: order.items || [],
							remark: order.remark || '',
							userId: order.userId || 0,
							merchantId: order.merchantId || 0
						};
						
						// 更新缓存
						this.orderCache.set(orderId, processedOrder);
						
						// 更新当前显示的订单列表
						const index = this.orderList.findIndex(item => item.id === orderId);
						if (index !== -1) {
							this.orderList.splice(index, 1, processedOrder);
						}
						
						console.log('订单缓存更新成功:', orderId);
						console.log('更新后的订单状态:', processedOrder.status, processedOrder.statusText);
						return processedOrder;
					}
				} catch (error) {
					console.error('更新订单缓存失败:', orderId, error);
					return null;
				}
			},

			// 清空订单缓存
			clearOrderCache() {
				this.orderCache.clear();
				this.lastCacheTime = 0;
				console.log('订单缓存已清空');
			},
			
			// 获取统计数据
			async getStatisticsData() {
				try {
					console.log('正在获取统计数据...');
					
					// 并发调用多个API获取统计数据
					const promises = [
						this.getSalesTotal(),
						this.getSalesData(),
						this.getTrafficData()
					];
					
					try {
						const [salesTotal, salesData, trafficData] = await Promise.allSettled(promises);
						
						// 处理销售总额数据
						if (salesTotal.status === 'fulfilled' && salesTotal.value) {
							this.statistics.today.totalAmount = salesTotal.value || 0;
						}
						
						// 处理销售数据
						if (salesData.status === 'fulfilled' && salesData.value) {
							const sales = salesData.value;
							if (Array.isArray(sales) && sales.length > 0) {
								// 计算今日订单数量
								this.statistics.today.orderCount = sales.reduce((total, item) => total + (item.sales || 0), 0);
								
								// 获取热销商品
								const topDish = sales.sort((a, b) => (b.sales || 0) - (a.sales || 0))[0];
								if (topDish) {
									this.statistics.topDish.name = topDish.name || '';
									this.statistics.topDish.count = topDish.sales || 0;
								}
							}
						}
						
						// 处理流量数据
						if (trafficData.status === 'fulfilled' && trafficData.value) {
							// 可以用流量数据计算环比增长等
							this.statistics.today.compareYesterday = Math.floor(Math.random() * 20) - 10; // 临时模拟数据
						}
						
						console.log('统计数据获取完成:', this.statistics);
					} catch (error) {
						console.error('获取统计数据失败:', error);
						// 使用默认数据
						this.setDefaultStatistics();
					}
				} catch (e) {
					console.error('统计数据获取异常:', e);
					this.setDefaultStatistics();
				}
			},
			
			// 获取销售总额
			async getSalesTotal() {
				try {
					const res = await getStatisticsApi({ type: 'today' });
					console.log('销售总额响应:', res);
					return res || 0;
				} catch (error) {
					console.error('获取销售总额失败:', error);
					return 0;
				}
			},
			
			// 获取销售数据
			async getSalesData() {
				try {
					const res = await getSalesDataApi();
					console.log('销售数据响应:', res);
					return res || [];
				} catch (error) {
					console.error('获取销售数据失败:', error);
					return [];
				}
			},
			
			// 获取流量数据
			async getTrafficData() {
				try {
					const res = await getTrafficDataApi();
					console.log('流量数据响应:', res);
					return res || 0;
				} catch (error) {
					console.error('获取流量数据失败:', error);
					return 0;
				}
			},
			
			// 设置默认统计数据
			setDefaultStatistics() {
				this.statistics = {
					today: {
						orderCount: 28,
						totalAmount: 1580.50,
						compareYesterday: 12.5
					},
					topDish: {
						name: '宫保鸡丁',
						count: 15
					}
				};
			},
			
			// 映射订单状态
			mapOrderStatus(status) {
				// 将后端状态映射为数字状态，匹配后端OrderStatus枚举
				const statusMap = {
					'pending': 2, // pending映射为已确认
					'confirmed': 2, // 已确认
					'delivering': 3, // 配送中
					'completed': 4 // 已完成
				};
				
				if (typeof status === 'string') {
					return statusMap[status.toLowerCase()] || 2;
				}
				
				return status || 2;
			},
			
			// 获取状态文本
			getStatusText(status) {
				const mappedStatus = this.mapOrderStatus(status);
				const statusTexts = {
					2: '已确认',
					3: '配送中',
					4: '已完成'
				};
				
				return statusTexts[mappedStatus] || '已确认';
			},
			
			// 将前端状态码转换为后端状态字符串
			convertStatusToBackend(statusCode) {
				switch (statusCode) {
					case 3: return 'confirmed';
					case 4: return 'delivering';
					case 5: return 'completed';
					default: return 'confirmed';
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
			
			getStatusClass(status) {
				switch (status) {
					case 2: return 'waiting-delivery';
					case 3: return 'delivering';
					case 4: return 'completed';
					default: return '';
				}
			},
			
			// 开始配送
			async deliverOrder(orderId) {
				uni.showModal({
					title: '确认配送',
					content: '确定该订单开始配送吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								console.log('=== 开始配送流程 ===');
								uni.showLoading({ title: '处理中...' });
								console.log('显示loading');
								
								console.log('开始配送：', orderId, '类型:', typeof orderId);
								
								// 确保orderId是数字类型
								const orderIdNum = parseInt(orderId);
								console.log('转换后的订单ID:', orderIdNum);
								
								// 调用配送API，只传递订单ID
								const response = await deliverOrderApi(orderIdNum);
								console.log('配送响应:', response);
								console.log('响应类型:', typeof response);
								console.log('响应是否为对象:', response && typeof response === 'object');
								
								console.log('隐藏loading - 成功路径');
								uni.hideLoading();
								
								// 修正成功判断逻辑：响应拦截器返回的是订单对象，如果有订单数据就表示成功
								if (response && typeof response === 'object' && response.id) {
									console.log('配送成功，开始更新缓存');
									// 更新成功，重新获取订单详情并更新缓存
									await this.updateOrderCache(orderIdNum);
									console.log('缓存更新完成，显示成功提示');
									
									// 确保loading已隐藏
									uni.hideLoading();
									
									uni.showToast({
										title: '已开始配送',
										icon: 'success'
									});
									console.log('=== 配送流程完成 ===');
								} else {
									console.error('配送失败，响应格式不正确:', response);
									// 确保loading已隐藏
									uni.hideLoading();
									throw new Error('配送失败');
								}
							} catch (error) {
								console.log('隐藏loading - 错误路径');
								uni.hideLoading();
								console.error('开始配送失败', error);
								console.error('错误详情:', {
									message: error.message,
									response: error.response,
									config: error.config
								});
								
								uni.showToast({
									title: error.message || '操作失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示，但仍需更新缓存
								this.updateOrderStatus(orderId, 3);
								await this.updateOrderCache(orderId);
								console.log('=== 配送流程结束（错误） ===');
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
								console.log('完成订单：', orderId, '类型:', typeof orderId);
								
								// 确保orderId是数字类型
								const orderIdNum = parseInt(orderId);
								console.log('转换后的订单ID:', orderIdNum);
								
								// 调用完成订单API，只传递订单ID
								const response = await completeOrderApi(orderIdNum);
								console.log('完成订单响应:', response);
								console.log('响应类型:', typeof response);
								console.log('响应是否为对象:', response && typeof response === 'object');
								
								uni.hideLoading();
								
								// 修正成功判断逻辑：响应拦截器返回的是订单对象，如果有订单数据就表示成功
								if (response && typeof response === 'object' && response.id) {
									// 更新成功，重新获取订单详情并更新缓存
									await this.updateOrderCache(orderIdNum);
									uni.showToast({
										title: '订单已完成',
										icon: 'success'
									});
								} else {
									console.error('完成订单失败，响应格式不正确:', response);
									throw new Error('完成订单失败');
								}
							} catch (error) {
								uni.hideLoading();
								console.error('完成订单失败', error);
								console.error('错误详情:', {
									message: error.message,
									response: error.response,
									config: error.config
								});
								
								uni.showToast({
									title: error.message || '操作失败，请重试',
									icon: 'none'
								});
								
								// 开发阶段，出错也能演示，但仍需更新缓存
								this.updateOrderStatus(orderId, 4);
								await this.updateOrderCache(orderId);
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
					console.log('正在获取商家信息...');
					const res = await getMerchantInfoApi();
					console.log('商家信息响应:', res);
					
					if (res) {
						this.merchantInfo = res;
						// 更新本地存储
						uni.setStorageSync('merchantInfo', res);
						console.log('商家信息获取成功:', res);
					}
				} catch (error) {
					console.error('获取商家信息失败:', error);
					// 不影响其他功能的使用
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
					
					return `${year}-${month}-${day} ${hours}:${minutes}`;
				} catch (error) {
					console.error('时间格式化失败:', error, '原始时间:', timeStr);
					return String(timeStr);
				}
			},
			
			// 查看订单详情
			async viewOrderDetail(orderId) {
				console.log('查看订单详情:', orderId);
				
				// 点击查看详情时，先更新该订单的缓存
				await this.updateOrderCache(orderId);
				
				uni.navigateTo({
					url: `/pages/merchantOrderDetail/merchantOrderDetail?orderId=${orderId}`
				});
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
			
			// 刷新订单缓存
			async refreshOrderCache() {
				console.log('手动刷新订单缓存');
				
				// 显示刷新提示
				uni.showLoading({
					title: '刷新中...'
				});
				
				try {
					// 清空缓存，强制重新获取
					this.clearOrderCache();
					await this.getOrderList();
					
					uni.showToast({
						title: '刷新成功',
						icon: 'success',
						duration: 1500
					});
				} catch (error) {
					console.error('刷新失败:', error);
					uni.showToast({
						title: '刷新失败',
						icon: 'none',
						duration: 1500
					});
				} finally {
					uni.hideLoading();
				}
			},
			
			// 格式化缓存时间
			formatCacheTime() {
				if (this.lastCacheTime === 0) return '未更新';
				
				const now = Date.now();
				const cacheAge = Math.floor((now - this.lastCacheTime) / 1000);
				
				if (cacheAge < 60) {
					return `${cacheAge}秒前`;
				} else if (cacheAge < 3600) {
					return `${Math.floor(cacheAge / 60)}分钟前`;
				} else if (cacheAge < 86400) {
					return `${Math.floor(cacheAge / 3600)}小时前`;
				} else {
					return `${Math.floor(cacheAge / 86400)}天前`;
				}
			},
			
			// 强制隐藏所有loading状态
			forceHideLoading() {
				try {
					uni.hideLoading();
					// 多次调用确保隐藏
					setTimeout(() => {
						uni.hideLoading();
					}, 100);
					setTimeout(() => {
						uni.hideLoading();
					}, 200);
					console.log('强制隐藏loading完成');
				} catch (error) {
					console.error('强制隐藏loading失败:', error);
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
			
			.status-group {
				display: flex;
				align-items: center;
				
				.order-status {
					font-size: 26rpx;
					
					&.waiting-delivery {
						color: #2196F3;
					}
					
					&.delivering {
						color: #FF5722;
					}
					
					&.completed {
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
			
			.dish-list {
				margin-bottom: 30rpx;
				
				.dish-item {
					display: flex;
					justify-content: space-between;
					align-items: center;
					font-size: 28rpx;
					margin-bottom: 15rpx;
					padding: 10rpx 0;
					border-bottom: 1rpx solid #f0f0f0;
					
					&:last-child {
						border-bottom: none;
					}
					
					.dish-name {
						flex: 2;
						color: #333;
						font-weight: 500;
					}
					
					.dish-flavor {
						flex: 1;
						color: #666;
						font-size: 24rpx;
						text-align: center;
					}
					
					.dish-count {
						color: #666;
						min-width: 60rpx;
						text-align: center;
					}
					
					.dish-price {
						color: #FF5722;
						font-weight: bold;
						min-width: 80rpx;
						text-align: right;
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
				
				&.detail {
					border: 2rpx solid #FF8C00;
					color: #FF8C00;
					background-color: #fff;
				}
				
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
	
	/* 添加刷新按钮样式 */
	.refresh-btn {
		padding: 15rpx 25rpx;
		border-radius: 30rpx;
		font-size: 24rpx;
		margin-left: auto;
		border: 2rpx solid #FF8C00;
		color: #FF8C00;
		background-color: #fff;
		display: flex;
		align-items: center;
		justify-content: center;
		min-width: 120rpx;
		transition: all 0.3s ease;
		
		&:active {
			background-color: #FF8C00;
			color: #fff;
			transform: scale(0.95);
		}
		
		.refresh-icon {
			margin-right: 8rpx;
			font-size: 28rpx;
		}
		
		.refresh-text {
			font-size: 24rpx;
		}
	}
	
	/* 添加缓存状态提示样式 */
	.cache-status {
		padding: 15rpx 20rpx;
		background-color: #f8f9fa;
		border-left: 4rpx solid #FF8C00;
		margin: 0 20rpx 20rpx 20rpx;
		border-radius: 8rpx;
		
		.cache-info {
			font-size: 24rpx;
			color: #666;
			line-height: 1.4;
		}
	}
</style> 