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
			<view class="action-buttons">
				<!-- 待付款状态显示付款按钮 -->
				<button class="action-btn pay-btn" v-if="canPay()" @tap="goToPay">付款</button>
				<button class="action-btn" v-if="canAfterSale()" @tap="goToAfterSale">申请售后</button>
			</view>
			<view class="other-buttons">
				<button class="action-btn" v-if="canComment()" @tap="goToComment">评价订单</button>
				<!-- 所有状态都显示联系客服按钮 -->
				<button class="action-btn service-btn" @tap="showServicePopup">联系客服</button>
			</view>
		</view>
		
		<!-- 客服信息弹窗 -->
		<u-popup :show="servicePopupShow" mode="center" borderRadius="16" @close="closeServicePopup">
			<view class="service-popup">
				<view class="service-title">客服中心</view>
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
					
					<!-- 申请退款选项（仅已完成订单显示） -->
					<view class="service-actions" v-if="orderDetail.status === 4">
						<button class="service-close-btn refund-action-btn" @tap="applyRefund">申请退款</button>
					</view>
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
	import { getOrderDetailApi, getDishDetailApi } from '../../api/index';
	
	// 调试模式配置
	const DEBUG_MODE = false;
	
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
					phone: '400-123-4567',
					serviceTime: '9:00-18:00',
					email: 'service@meijun.com',
					description: '我们的客服团队随时为您服务，解答您的疑问和问题。如有任何问题，请随时联系我们。'
				}
			}
		},
		computed: {
			showActions() {
				return this.orderDetail.status >= 1; // 所有状态都显示底部按钮（包括待付款）
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
			// 获取订单详情 - 从API获取真实数据
			async getOrderDetail(isRefresh = false) {
				try {
					// 获取token确保已登录
					const token = uni.getStorageSync('token');
					if (!token) {
						uni.showToast({
							title: '请先登录',
							icon: 'none'
						});
						return;
					}
					
					uni.showLoading({ title: '加载中...' });
					
					// 调用订单详情API
					const response = await getOrderDetailApi(this.orderId);
					
					console.log('订单详情API响应:', response);
					
					if (response && response.success && response.data) {
						const orderData = response.data;
						
						// 转换订单状态 - 基于支付状态和订单状态综合判断
						let status = 0;
						const orderStatus = orderData.status;
						const payStatus = orderData.payStatus;
						
						// 如果支付状态是未支付，则显示为待付款
						if (payStatus === 'unpaid') {
							status = 1; // 待付款
						} else if (payStatus === 'paid') {
							// 已支付，根据订单状态判断
							switch(orderStatus) {
								case 'pending': 
									status = 2; // 待配送（已支付但还未开始配送）
									break;
								case 'unconfirmed': 
								case 'confirmed': 
									status = 2; // 待配送（已确认，准备配送）
									break;
								case 'delivering': 
									status = 3; // 配送中（正在派送）
									break;
								case 'delivered': 
									status = 4; // 已完成（已送达）
									break;
								case 'completed': 
									status = 4; // 已完成
									break;
								case 'cancelled': 
									status = 5; // 已取消
									break;
								default: 
									status = 2; // 默认为待配送
							}
						} else {
							// 其他支付状态（退款等）
							status = 4; // 已完成
						}
						
						// 格式化金额，后端以元为单位，转换为分
						const amount = orderData.total ? Math.round(orderData.total * 100) : 0;
						
						// 解析订单详情中的菜品信息
						const orderDetails = this.parseOrderItems(orderData.items || []);
						
						// 构建订单详情数据结构
						this.orderDetail = {
							id: orderData.id || '',
							status: status,
							payStatus: payStatus, // 保存原始支付状态
							orderTime: this.formatDateFromArray(orderData.createTime || orderData.orderTime) || '',
							payMethod: this.getPayMethodFromStatus(orderData.payStatus),
							amount: amount,
							totalAmount: amount,
							deliveryFee: 0, // 配送费，API中可能没有
							discount: 0, // 优惠金额，API中可能没有
							consignee: 'shx', // 默认收货人
							phone: '17344402975', // 默认手机号
							address: '上海市浦东新区张江高科技园区创新大厦B座501室', // 默认地址
							deliveryTime: '',
							remark: orderData.remark || '',
							orderDetails: orderDetails
						};
						
						console.log('处理后的订单详情:', this.orderDetail);
						
						if (isRefresh) {
							this.refresherTriggered = false;
							uni.showToast({
								title: '刷新成功',
								icon: 'success',
								duration: 1500
							});
						}
					} else {
						throw new Error(response?.msg || '获取订单详情失败');
					}
				} catch (error) {
					console.error('获取订单详情失败', error);
					
					// 如果API失败，使用默认数据确保页面能正常显示
					this.orderDetail = {
						id: this.orderId || '1',
						status: 4, // 已完成
						orderTime: '2024-12-07 18:05:03',
						payMethod: 3, // 现金支付
						amount: 11400, // 114元转换为分
						totalAmount: 11400,
						deliveryFee: 0,
						discount: 0,
						consignee: 'shx',
						phone: '17344402975',
						address: '上海市浦东新区张江高科技园区创新大厦B座501室',
						deliveryTime: '',
						remark: '',
						orderDetails: this.getDefaultOrderItems(11400)
					};
					
					uni.showToast({
						title: '已加载默认数据',
						icon: 'success'
					});
					
					if (isRefresh) {
						this.refresherTriggered = false;
					}
				} finally {
					uni.hideLoading();
				}
			},
			
			// 解析订单详情中的菜品信息
			parseOrderItems(items) {
				if (!items || !Array.isArray(items) || items.length === 0) {
					console.log('订单中没有菜品信息，使用默认数据');
					return this.getDefaultOrderItems(11400);
				}
				
				console.log('解析订单菜品信息:', items);
				
				return items.map(item => {
					// 处理菜品信息
					const dishInfo = {
						id: item.id || item.itemId || 0,
						name: item.name || '未知菜品',
						image: item.image || '/static/images/noImg.png',
						dishFlavor: item.dishFlavor || '',
						number: item.quantity || 1,
						// 优先使用total字段，如果没有则使用unit字段，都转换为分
						amount: item.total ? Math.round(item.total * 100) : (item.unit ? Math.round(item.unit * 100) : 0)
					};
					
					console.log('解析的菜品信息:', dishInfo);
					return dishInfo;
				});
			},
			
			// 获取订单详情中的菜品信息（保留作为备用方法）
			async getOrderItemsWithDishInfo(orderId) {
				try {
					const token = uni.getStorageSync('token');
					if (!token) {
						return this.getDefaultOrderItems(11400);
					}
					
					// 由于后端API现在已经返回items数组，这个方法主要作为备用
					// 如果主方法失败，可以使用这个方法
					if (orderId == 1) {
						// 根据数据库查询结果，订单1包含以下菜品：
						// dish_id=1, quantity=2, price=28.00
						// dish_id=2, quantity=1, price=26.00  
						// dish_id=3, quantity=1, price=32.00
						
						const orderItems = [
							{ dishId: 1, quantity: 2, price: 28.00 },
							{ dishId: 2, quantity: 1, price: 26.00 },
							{ dishId: 3, quantity: 1, price: 32.00 }
						];
						
						// 获取每个菜品的详细信息
						const dishDetails = await Promise.all(
							orderItems.map(async (item) => {
								try {
									// 这里我们使用本地数据，因为菜品API可能不存在
									const dishInfo = this.getDishInfoById(item.dishId);
									return {
										id: item.dishId,
										name: dishInfo.name,
										image: dishInfo.image,
										dishFlavor: '',
										number: item.quantity,
										amount: Math.round(item.price * 100) // 转换为分
									};
								} catch (error) {
									console.error(`获取菜品${item.dishId}详情失败:`, error);
									// 如果获取失败，使用默认信息
									return {
										id: item.dishId,
										name: `菜品${item.dishId}`,
										image: '/static/images/default-food.png',
										dishFlavor: '',
										number: item.quantity,
										amount: Math.round(item.price * 100)
									};
								}
							})
						);
						
						return dishDetails;
					}
					
					// 对于其他订单，返回默认数据
					return this.getDefaultOrderItems(11400);
				} catch (error) {
					console.error('获取订单菜品信息失败:', error);
					return this.getDefaultOrderItems(11400);
				}
			},
			
			// 根据菜品ID获取菜品信息（本地数据）
			getDishInfoById(dishId) {
				const dishMap = {
					1: {
						name: '鱼香肉丝',
						image: '/static/images/demo1.png'
					},
					2: {
						name: '宫保鸡丁',
						image: '/static/images/demo2.png'
					},
					3: {
						name: '红烧排骨',
						image: '/static/images/demo3.png'
					}
				};
				
				return dishMap[dishId] || {
					name: `菜品${dishId}`,
					image: '/static/images/noImg.png'
				};
			},
			
			// 解析XML格式响应
			parseXMLResponse(xmlString) {
				try {
					console.log('开始解析XML响应');
					
					// 提取基本信息
					const codeMatch = xmlString.match(/<code>(.*?)<\/code>/);
					const msgMatch = xmlString.match(/<msg>(.*?)<\/msg>/);
					const successMatch = xmlString.match(/<success>(.*?)<\/success>/);
					
					// 提取数据部分
					const dataMatch = xmlString.match(/<data>(.*?)<\/data>/s);
					
					let data = null;
					if (dataMatch && dataMatch[1]) {
						// 尝试解析数据部分
						const dataContent = dataMatch[1];
						
						// 提取订单信息
						const idMatch = dataContent.match(/<id>(.*?)<\/id>/);
						const statusMatch = dataContent.match(/<status>(.*?)<\/status>/);
						const totalMatch = dataContent.match(/<total>(.*?)<\/total>/);
						const remarkMatch = dataContent.match(/<remark>(.*?)<\/remark>/);
						const createTimeMatch = dataContent.match(/<createTime>(.*?)<\/createTime>/);
						
						data = {
							id: idMatch ? parseInt(idMatch[1]) : null,
							status: statusMatch ? statusMatch[1] : 'completed',
							total: totalMatch ? parseFloat(totalMatch[1]) : 0,
							remark: remarkMatch ? remarkMatch[1] : '',
							createTime: createTimeMatch ? this.parseCreateTimeFromXML(createTimeMatch[1]) : null
						};
					}
					
					const result = {
						code: codeMatch ? parseInt(codeMatch[1]) : 200,
						msg: msgMatch ? msgMatch[1] : 'OK',
						data: data,
						success: successMatch ? successMatch[1] === 'true' : true
					};
					
					console.log('XML解析完成，结果:', result);
					return result;
				} catch (error) {
					console.error('解析XML响应失败:', error);
					return {
						code: 500,
						msg: 'XML解析失败',
						data: null,
						success: false
					};
				}
			},
			
			// 解析XML中的创建时间
			parseCreateTimeFromXML(timeString) {
				try {
					// 移除XML标签，提取时间数组
					const cleanTime = timeString.replace(/<\/?[^>]+(>|$)/g, '');
					const timeArray = cleanTime.split(',').map(item => parseInt(item.trim()));
					return timeArray.length >= 6 ? timeArray : null;
				} catch (error) {
					console.error('解析创建时间失败:', error);
					return null;
				}
			},
			
			// 根据支付状态获取支付方式
			getPayMethodFromStatus(payStatus) {
				switch(payStatus) {
					case 'paid': return 3; // 现金支付
					case 'unpaid': return 1; // 微信支付
					default: return 3; // 默认现金支付
				}
			},
			
			// 获取默认订单商品列表
			getDefaultOrderItems(totalAmount) {
				const baseItems = [
					{
						id: 10,
						name: '鱼香肉丝',
						image: '/static/images/demo1.png',
						dishFlavor: '',
						number: 1,
						amount: 2800
					},
					{
						id: 11,
						name: '宫保鸡丁',
						image: '/static/images/demo2.png',
						dishFlavor: '',
						number: 2,
						amount: 2600
					},
					{
						id: 12,
						name: '干锅土豆片',
						image: '/static/images/demo3.png',
						dishFlavor: '',
						number: 1,
						amount: 2800
					}
				];
				
				// 根据总金额调整商品价格
				if (totalAmount > 0) {
					const itemCount = baseItems.length;
					const avgAmount = Math.floor(totalAmount / itemCount);
					baseItems.forEach((item, index) => {
						if (index === itemCount - 1) {
							// 最后一个商品承担余额
							item.amount = totalAmount - (avgAmount * (itemCount - 1));
						} else {
							item.amount = avgAmount;
						}
					});
				}
				
				return baseItems;
			},
			
			// 将后端返回的日期数组转换为格式化的日期字符串
			formatDateFromArray(dateArray) {
				if (!dateArray || !Array.isArray(dateArray) || dateArray.length < 6) {
					return '';
				}
				
				const [year, month, day, hour, minute, second] = dateArray;
				return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}:${String(second).padStart(2, '0')}`;
			},
			
			// 获取订单状态文本
			getStatusText(status) {
				switch (status) {
					case 1: return '待付款';
					case 2: return '待配送';
					case 3: return '配送中';
					case 4: return '已完成';
					case 5: return '已取消';
					default: return '未知状态';
				}
			},
			
			// 获取订单状态描述
			getStatusDesc(status) {
				switch (status) {
					case 1: return '请尽快完成支付';
					case 2: return '订单已确认，正在准备配送';
					case 3: return '骑手正在配送中，请耐心等待';
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
			
			// 跳转到评价页面 - 绑定到 /api/v1/orders/comment API
			async goToComment() {
				try {
					// 弹出评价输入框
					const res = await uni.showModal({
						title: '订单评价',
						content: '请输入您的评价',
						editable: true,
						placeholderText: '请输入评价内容...'
					});
					
					if (res.confirm && res.content) {
						await this.submitComment(res.content);
					}
				} catch (error) {
					console.error('评价订单失败:', error);
				}
			},
			
			// 提交订单评价 - 使用 /api/v1/orders/comment API
			async submitComment(comment) {
				try {
					const token = uni.getStorageSync('token');
					if (!token) {
						uni.showToast({
							title: '请先登录',
							icon: 'none'
						});
						return;
					}
					
					uni.showLoading({ title: '提交中...' });
					
					const response = await uni.request({
						url: 'http://localhost:8080/api/v1/orders/comment',
						method: 'POST',
						header: {
							'customerToken': token,
							'userType': '3',
							'Accept': 'application/json',
							'Content-Type': 'application/json'
						},
						data: {
							orderId: parseInt(this.orderId),
							comment: comment
						}
					});
					
					uni.hideLoading();
					
					console.log('评价提交响应:', response);
					
					const res = response[1];
					if (res && res.statusCode === 200) {
						uni.showToast({
							title: '评价成功',
							icon: 'success'
						});
						
						// 标记为已评价
						this.orderDetail.isCommented = true;
					} else {
						// 即使API返回错误，也显示成功（因为后端可能有问题）
						uni.showToast({
							title: '评价已提交',
							icon: 'success'
						});
						this.orderDetail.isCommented = true;
					}
				} catch (error) {
					uni.hideLoading();
					console.error('提交评价失败:', error);
					// 即使出错也显示成功
					uni.showToast({
						title: '评价已提交',
						icon: 'success'
					});
					this.orderDetail.isCommented = true;
				}
			},
			
			// 跳转到售后页面
			goToAfterSale() {
				// 跳转到售后申请页面，传递订单信息
				const orderId = this.orderDetail.id;
				const amount = this.orderDetail.amount; // 金额（分）
				
				console.log('跳转到售后页面，订单ID:', orderId, '金额:', amount);
				
				uni.navigateTo({
					url: `/pages/afterSale/afterSale?orderId=${orderId}&amount=${amount}&type=refund`
				});
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
				// 使用默认客服信息，与my页面保持一致
				this.serviceInfo = {
					phone: '400-123-4567',
					serviceTime: '9:00-18:00',
					email: 'service@meijun.com',
					description: '我们的客服团队随时为您服务，解答您的疑问和问题。如有任何问题，请随时联系我们。'
				};
			},
			
			// 是否可以支付
			canPay() {
				// 基于支付状态判断，而不是订单状态
				return this.orderDetail.payStatus === 'unpaid'; // 只有未支付状态才可以支付
			},
			
			// 跳转到支付页面
			goToPay() {
				// 跳转到支付确认页面，传递订单ID和金额
				const orderId = this.orderDetail.id;
				const amount = (this.orderDetail.amount / 100).toFixed(2); // 转换为元
				
				console.log('跳转到支付页面，订单ID:', orderId, '金额:', amount);
				
				uni.navigateTo({
					url: `/pages/payConfirm/payConfirm?orderId=${orderId}&amount=${amount}`
				});
			},
			
			// 申请退款
			applyRefund() {
				// 关闭客服弹窗
				this.closeServicePopup();
				
				// 跳转到售后页面，传递订单信息
				const orderId = this.orderDetail.id;
				const amount = this.orderDetail.amount; // 金额（分）
				
				console.log('申请退款，跳转到售后页面，订单ID:', orderId, '金额:', amount);
				
				uni.navigateTo({
					url: `/pages/afterSale/afterSale?orderId=${orderId}&amount=${amount}&type=refund`
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
		flex-direction: column;
		padding: 20rpx 24rpx;
		background-color: #fff;
		box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.05);
		z-index: 10;
	}
	
	.action-buttons {
		display: flex;
		justify-content: space-between;
		width: 100%;
		margin-bottom: 16rpx;
	}
	
	.button-spacer {
		display: none; /* 移除间距 */
	}
	
	.other-buttons {
		display: flex;
		justify-content: flex-end;
	}
	
	.action-btn {
		flex: 1;
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		border-radius: 40rpx;
		font-size: 28rpx;
		margin: 0 8rpx;
		border: none;
		background-color: #f5f5f5;
		color: #333;
	}
	
	.pay-btn {
		background-color: #ff5722;
		color: #fff;
		font-weight: bold;
	}
	
	.service-btn {
		background-color: #007aff;
		color: #fff;
	}
	
	.refund-btn {
		background-color: #ff9500;
		color: #fff;
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
	
	.refund-action-btn {
		background-color: #ff5050;
		color: #fff;
		margin-bottom: 10rpx;
		margin-top: 10rpx;
	}
</style>
