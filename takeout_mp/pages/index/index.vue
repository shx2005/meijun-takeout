<template>
	<view class="container">
		<!-- 顶部区域 -->
		<view class="header">
			<view class="header-title">美食元素</view>
		</view>
		
		<!-- 店铺信息区域 -->
		<view class="shop-info">
			<view class="shop-detail">
				<view class="shop-location">
					<image src="/static/index_image/dingwei.png" mode="aspectFit" />
					<text>距离1.5km</text>
				</view>
				<view class="shop-delivery">
					<image src="/static/index_image/qiandai.png" mode="aspectFit" />
					<text>配送费2元</text>
				</view>
				<view class="shop-time">
					<image src="/static/index_image/shijian.png" mode="aspectFit" />
					<text>预计时长12min</text>
				</view>
			</view>
			<view class="shop-desc">简介: "有滋有味的生活来自于对美食的品味和享受"</view>
		</view>
		
		<!-- 页面标签页 -->
		<view class="tab-container" v-if="tabs && tabs.length > 0">
			<me-tabs v-model="tabIndex" :tabs="tabs" :tab-width="130"></me-tabs>
		</view>
		
		<!-- 主体内容区域 -->
		<view class="main-content" v-if="tabIndex === 0">
			<!-- 左侧分类导航 -->
			<view class="category-sidebar">
				<scroll-view scroll-y class="category-scroll">
					<block v-if="!categoryList || categoryList.length === 0">
						<view 
							v-for="(item, index) in ['家常菜', '盖饭', '米饭', '特色菜', '干锅', '家常菜系列', '汤菜', '素菜系列', '套餐']" 
							:key="index"
							:class="['category-item', activeType === index ? 'active' : '']"
							@click="categoryClick(index)">
							{{ item }}
						</view>
					</block>
					<block v-else>
						<view 
							v-for="(item, index) in categoryList" 
							:key="index"
							:class="['category-item', activeType === index ? 'active' : '']"
							@click="categoryClick(index, item.id, item.type)">
							{{ item.name }}
						</view>
					</block>
				</scroll-view>
			</view>
			
			<!-- 右侧菜品列表 - 修改为分类展示 -->
			<scroll-view 
				scroll-y 
				class="dish-container" 
				id="dishContainer" 
				:scroll-into-view="scrollIntoViewId"
				@scroll="onDishScroll">
				<block v-for="(category, catIndex) in categoryList" :key="catIndex">
					<view :id="'category-'+category.id" class="category-title" :data-category-id="category.id">{{ category.name }}</view>
					
					<view class="dish-group">
						<!-- 该分类下没有菜品时显示提示 -->
						<view class="no-dish-tip" v-if="getDishesForCategory(category.id).length === 0">
							暂无菜品
							</view>
						
						<!-- 该分类下的菜品列表 -->
						<view 
							class="dish-item" 
							v-for="(item, index) in getDishesForCategory(category.id)" 
							:key="index"
							@click="dishDetails(item)">
							<image class="dish-image" :src="getImageSrc(item)" mode="aspectFill" @error="handleImageError($event, item)"></image>
							<view class="dish-content">
								<view class="dish-info">
									<view class="dish-name">{{ item.name }}</view>
									<view class="dish-desc" v-if="item.description">{{ item.description }}</view>
									<view class="dish-sales">月售 {{ item.sales || item.sale || 0 }}</view>
								</view>
								<view class="dish-action">
									<view class="dish-price">
										<text class="price-symbol">￥</text>
										<text class="price-value">{{ (item.price).toFixed(2) }}</text>
								</view>
									<view class="dish-controls">
										<view class="subtract-button" v-if="getItemCount(item.id) >= 1" @click.stop.prevent="subtractCart(item)">
											<image src="../../static/images/subtract.png"></image>
								</view>
										<view class="dish-count" v-if="getItemCount(item.id) >= 1">{{ getItemCount(item.id) }}</view>
										<view class="flavor-button" v-if="item.flavors && item.flavors.length > 0 && !getItemCount(item.id)"
											@click.stop.prevent="chooseFlavorClick(item)">选择规格</view>
										<view class="add-button" v-else @click.stop.prevent="addCart(item,$event)">
											<image src="../../static/images/add.png"></image>
								</view>
							</view>
							</view>
							</view>
						</view>
					</view>
				</block>
				<view class="bottom-space"></view>
			</scroll-view>
							</view>
		
		<!-- 评价页面内容 -->
		<view class="main-content" v-if="tabIndex === 1">
			<view class="review-container">
				<view class="review-header">
					<view class="review-title">顾客评价</view>
					<view class="review-stats">
						<text class="review-score">4.8</text>
						<text class="review-count">共 {{commentList.length}} 条评价</text>
								</view>
									</view>
				
				<view class="review-list">
					<view v-if="commentList.length === 0" class="no-reviews">
						<image src="/static/images/no-review.png" mode="aspectFit" class="no-review-image"></image>
						<text>暂无评价</text>
									</view>
					<view v-else class="review-item" v-for="(item, index) in commentList" :key="index">
						<view class="review-user">
							<image class="user-avatar" src="/static/images/avatar.png" mode="aspectFill"></image>
							<view class="user-info">
								<view class="user-name">顾客{{index+1}}</view>
								<view class="review-time">{{formatDate(item.createTime)}}</view>
								</view>
							</view>
						<view class="review-content">{{item.comment || item.content}}</view>
						<view class="review-order-info">
							<view class="review-order-id">订单号: {{item.orderId}}</view>
						</view>
					</view>
		</view>
			</view>
		</view>
		
		<!-- 商家页面内容 (删除门店信息按钮) -->
		<view class="main-content" v-if="tabIndex === 2">
			<view class="merchant-container">
				<view class="merchant-header">
					<image class="merchant-logo" src="/static/images/logo.png" mode="aspectFill"></image>
					<view class="merchant-info">
						<view class="merchant-name">美食元素餐厅</view>
						<view class="merchant-desc">营业时间: 10:00-22:00</view>
					</view>
				</view>
				
				<view class="merchant-details">
					<view class="merchant-section">
						<view class="section-title">商家介绍</view>
						<view class="section-content">
							"美食元素"是一家致力于为顾客提供高品质、健康美食的餐厅。我们坚持选用新鲜食材，菜品种类丰富，包括多种家常菜、特色菜和创新菜品。我们的厨师团队经验丰富，每一道菜品都精心制作，保证口味独特、营养均衡。
						</view>
					</view>
					
					<view class="merchant-section">
						<view class="section-title">联系方式</view>
						<view class="section-content">
							<view class="contact-item">
								<image src="/static/images/phone.png" mode="aspectFit"></image>
								<text>电话：13800138000</text>
						</view>
							<view class="contact-item">
								<image src="/static/images/location.png" mode="aspectFit"></image>
								<text>地址：上海市浦东新区张江高科技园区</text>
						</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 浮动购物车图标 -->
		<view class="floating-cart" @click="goToCart">
			<view class="cart-icon-container">
				<text class="cart-emoji">🛒</text>
				<view class="floating-cart-badge" v-if="cartCount > 0">{{ cartCount }}</view>
			</view>
		</view>
		
		<!-- 底部购物车 -->
		<view class="cart-bar">
			<view class="cart-left">
				<view class="cart-icon-wrapper" @click="goToCart">
					<text class="cart-emoji">🛒</text>
					<view class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</view>
				</view>
				<view class="cart-price">
				<text>￥{{ totalPrice || '0.00' }}</text>
			</view>
			</view>
			<view class="cart-button" :class="{'cart-button-active': cartCount > 0}" @click="submitOrder">
				<text class="cart-emoji-small">🛒</text>
				去结算{{ cartCount > 0 ? `(${cartCount})` : '' }}
			</view>
		</view>
	</view>
</template>

<script>
	import {
		cartListApi,
		categoryListApi,
		dishListApi,
		setmealListApi,
		clearCartApi,
		updateCartApi,
		setMealDishDetailsApi,
		addCartApi,
		submitOrderCommentApi
	} from '../../api/index';
	
	export default {
		data() {
			return {
				tabIndex: 0,
				tabs: ['点餐', '评价', '商家'],
				activeType: 0,
				categoryList: [],
				dishList: [],
				totalPrice: '0.00',
				cartCount: 0,
				cartItems: [], // 购物车本地存储项
				allDishes: [], // 所有菜品数据
				scrollIntoViewId: '', // 用于控制右侧滚动位置
				scrollLock: false, // 防止连续触发滚动事件
				categoryPositions: [], // 存储分类位置信息
				commentList: [] // 存储评价数据
			}
		},
		onLoad() {
			// 首次加载时初始化空页面
			this.categoryList = [];
			this.allDishes = [];
			this.cartItems = [];
			this.cartCount = 0;
			this.totalPrice = '0.00';
			
			// 检查是否已登录
			const token = uni.getStorageSync('token');
			if (token) {
				// 已登录，加载数据
				this.init();
				this.loadCartFromServer();
			}
		},
		onShow() {
			// 每次页面显示时重新从服务器获取购物车数据
			const token = uni.getStorageSync('token');
			if (token) {
				this.loadCartFromServer();
			}
		},
		mounted() {
			// 初始化后获取分类位置信息
			setTimeout(() => {
				this.getCategoryPositions();
			}, 1000);
		},
		watch: {
			tabIndex(newVal) {
				// 当切换到评价页面时，加载评价数据
				if (newVal === 1) {
					this.getComments();
				}
			}
		},
		methods: {
			async init() {
				try {
					// 检查登录状态
					const token = uni.getStorageSync('token');
					if (!token) {
						console.log('用户未登录，不初始化数据');
						// 清空数据并确保是正确的数据类型
						this.categoryList = [];
						this.allDishes = [];
						this.cartItems = [];
						this.cartCount = 0;
						this.totalPrice = '0.00';
						return;
					}
					
					// 确保数据类型初始化正确
					if (!Array.isArray(this.allDishes)) this.allDishes = [];
					if (!Array.isArray(this.categoryList)) this.categoryList = [];
					if (!Array.isArray(this.cartItems)) this.cartItems = [];
					
					// 优先从本地缓存加载数据
					const loadFromCache = () => {
						// 尝试从本地缓存加载分类数据
						try {
							const cachedCategoryData = uni.getStorageSync('categoryData');
							if (cachedCategoryData) {
								const parsedData = JSON.parse(cachedCategoryData);
								if (parsedData && Array.isArray(parsedData.data)) {
									console.log('使用本地缓存的分类数据');
									this.categoryList = parsedData.data;
									// 按sort字段排序
									this.categoryList.sort((a, b) => a.sort - b.sort);
								}
							}
						} catch (e) {
							console.error('从本地缓存加载分类数据失败:', e);
						}
						
						// 尝试从本地缓存加载菜品数据
						try {
							const cachedDishData = uni.getStorageSync('allDishes');
							if (cachedDishData) {
								const parsedData = JSON.parse(cachedDishData);
								if (parsedData && Array.isArray(parsedData)) {
									console.log('使用本地缓存的菜品数据');
									this.allDishes = parsedData;
								}
							}
						} catch (e) {
							console.error('从本地缓存加载菜品数据失败:', e);
						}
						
						// 如果没有缓存数据，使用备用数据
						if (!this.categoryList || this.categoryList.length === 0) {
							console.log('使用备用分类数据');
							this.categoryList = [
								{ id: 1, type: 1, name: '家常菜', sort: 1, status: 1 },
								{ id: 2, type: 1, name: '盖饭', sort: 2, status: 1 },
								{ id: 3, type: 1, name: '米饭', sort: 3, status: 1 },
								{ id: 4, type: 1, name: '特色菜', sort: 4, status: 1 },
								{ id: 5, type: 1, name: '干锅', sort: 5, status: 1 },
								{ id: 6, type: 1, name: '家常菜系列', sort: 6, status: 1 },
								{ id: 7, type: 1, name: '汤菜', sort: 7, status: 1 },
								{ id: 8, type: 1, name: '素菜系列', sort: 8, status: 1 },
								{ id: 9, type: 2, name: '套餐', sort: 9, status: 1 }
							];
							// 保存到本地缓存
							uni.setStorageSync('categoryData', JSON.stringify({
								data: this.categoryList,
								timestamp: Date.now()
							}));
						}
						
						if (!this.allDishes || this.allDishes.length === 0) {
							console.log('使用备用菜品数据');
							this.allDishes = [
					{
						id: 1,
									name: "鱼香肉丝",
									categoryId: 1,
									price: 28.00,
									image: "/static/images/dish1.jpg",
									description: "主料：猪肉、胡萝卜、青椒、木耳",
									status: 1,
									sale: 128
					},
					{
						id: 2,
									name: "宫保鸡丁",
									categoryId: 1,
									price: 26.00,
									image: "/static/images/dish2.jpg",
									description: "主料：鸡胸肉、花生米、黄瓜、胡萝卜",
									status: 1,
									sale: 105
					},
					{
						id: 3,
									name: "红烧排骨",
									categoryId: 1,
									price: 32.00,
									image: "/static/images/dish3.jpg",
									description: "主料：猪排骨、土豆、胡萝卜",
									status: 1,
									sale: 98
								},
								{
									id: 4,
									name: "麻婆豆腐",
									categoryId: 2,
									price: 22.00,
									image: "/static/images/dish4.jpg",
									description: "主料：豆腐、肉末、豆瓣酱",
									status: 1,
									sale: 85
								},
								{
									id: 5,
									name: "干锅土豆片",
									categoryId: 5,
									price: 28.00,
									image: "/static/images/dish5.jpg",
									description: "主料：土豆、辣椒、木耳、肉片",
									status: 1,
									sale: 75
								}
							];
							// 保存到本地缓存
							uni.setStorageSync('allDishes', JSON.stringify(this.allDishes));
						}
					};
					
					// 先尝试从本地缓存加载数据
					loadFromCache();
					
					// 然后尝试从服务器获取最新数据
					try {
						// 并行加载分类和菜品数据
						await Promise.all([
							this.loadCategoryData().catch(err => {
								console.error("加载分类数据失败:", err);
								// 已经从缓存加载了数据，不需要额外处理
							}),
							this.loadDishData().catch(err => {
								console.error("加载菜品数据失败:", err);
								// 已经从缓存加载了数据，不需要额外处理
							})
						]);
					} catch (error) {
						console.error("从服务器加载数据失败，使用本地缓存", error);
						// 已经从缓存加载了数据，不需要额外处理
					}
					
				} catch (error) {
					console.error("初始化数据失败", error);
					uni.$showMsg('获取数据失败，已使用本地数据');
				}
			},
			
			// 更新菜品显示 - 确保数据一致
			updateDishDisplay() {
				// 检查登录状态
				const token = uni.getStorageSync('token');
				if (!token) {
					console.log('用户未登录，不更新菜品显示');
					this.allDishes = [];
					this.categoryList = [];
					return;
				}
				
				// 如果没有分类数据，使用备用数据
				if (!this.categoryList || this.categoryList.length === 0) {
					this.categoryList = [
						{ id: 1, type: 1, name: '家常菜', sort: 1, status: 1 },
						{ id: 2, type: 1, name: '盖饭', sort: 2, status: 1 },
						{ id: 3, type: 1, name: '米饭', sort: 3, status: 1 },
						{ id: 4, type: 1, name: '特色菜', sort: 4, status: 1 },
						{ id: 5, type: 1, name: '干锅', sort: 5, status: 1 },
						{ id: 6, type: 1, name: '家常菜系列', sort: 6, status: 1 },
						{ id: 7, type: 1, name: '汤菜', sort: 7, status: 1 },
						{ id: 8, type: 1, name: '素菜系列', sort: 8, status: 1 },
						{ id: 9, type: 2, name: '套餐', sort: 9, status: 1 }
					];
				}
				
				// 按sort字段排序
				this.categoryList.sort((a, b) => a.sort - b.sort);
				
				// 如果没有菜品数据，尝试从本地存储获取
				if (!this.allDishes || !Array.isArray(this.allDishes) || this.allDishes.length === 0) {
					try {
						const storedDishes = uni.getStorageSync('allDishes');
						if (storedDishes) {
							const parsedDishes = JSON.parse(storedDishes);
							// 确保解析后的数据是数组
							this.allDishes = Array.isArray(parsedDishes) ? parsedDishes : [];
						} else {
							// 没有本地存储数据
							this.allDishes = [];
						}
					} catch (e) {
						console.error('从本地存储获取菜品数据失败', e);
						this.allDishes = [];
					}
				}
				
				// 如果还是没有菜品数据，使用备用数据
				if (!this.allDishes || !Array.isArray(this.allDishes) || this.allDishes.length === 0) {
					console.log('使用备用菜品数据');
					this.allDishes = [
						{
							id: 1,
							name: "鱼香肉丝",
							categoryId: 1,
							price: 28.00,
							image: "/static/images/dish1.jpg",
							description: "主料：猪肉、胡萝卜、青椒、木耳",
							status: 1,
							sale: 128
						},
						{
							id: 2,
							name: "宫保鸡丁",
							categoryId: 1,
							price: 26.00,
							image: "/static/images/dish2.jpg",
							description: "主料：鸡胸肉、花生米、黄瓜、胡萝卜",
							status: 1,
							sale: 105
						},
						{
							id: 3,
							name: "红烧排骨",
							categoryId: 1,
							price: 32.00,
							image: "/static/images/dish3.jpg",
							description: "主料：猪排骨、土豆、胡萝卜",
							status: 1,
							sale: 98
						}
					];
					// 将备用数据保存到本地存储
					uni.setStorageSync('allDishes', JSON.stringify(this.allDishes));
				}
			},
			
			// 加载分类数据
			async loadCategoryData() {
				try {
					// 检查登录状态
					const token = uni.getStorageSync('token');
					if (!token) {
						console.log('用户未登录，不加载分类数据');
						return;
					}
					
					// 先检查是否有登录时预加载的分类数据
					const cachedCategoryData = uni.getStorageSync('categoryData');
					if (cachedCategoryData) {
						try {
							const parsedData = JSON.parse(cachedCategoryData);
							// 检查数据是否在24小时内（86400000毫秒）
							if (parsedData.timestamp && Date.now() - parsedData.timestamp < 86400000) {
								console.log('使用预加载的菜品分类数据');
								this.categoryList = parsedData.data;
								// 数据有效，直接返回
								return;
							}
						} catch (e) {
							console.error('解析预加载的菜品分类数据失败:', e);
						}
					}
					
					// 如果没有预加载数据或数据已过期，则继续请求API
					console.log('尝试从API获取分类数据');
					const res = await categoryListApi({});
					console.log('分类数据API响应:', res);
					// 修复：处理多种可能的响应格式
					if (res) {
						// 尝试提取分类数据，处理不同的响应结构
						let categoryData = null;
						if (res.code === 0 && res.data) {
							categoryData = res.data;
						} else if (res.code === 200 && res.data) {
							categoryData = res.data;
						} else if (Array.isArray(res)) {
							categoryData = res;
						} else if (typeof res === 'object' && Object.keys(res).length > 0) {
							// 可能直接返回数据对象
							categoryData = res;
						}
						
						if (categoryData) {
							this.categoryList = categoryData;
							// 将数据保存到本地存储
							uni.setStorageSync('categoryData', JSON.stringify({
								data: categoryData,
								timestamp: Date.now()
							}));
							// 按sort字段排序
							this.categoryList.sort((a, b) => a.sort - b.sort);
							return;
						}
					}
					
					// 如果API请求失败或无法提取数据，尝试本地备份
					const response = await uni.request({
						url: '/category_data.json',
						method: 'GET'
					});
					if (response && response[1].data) {
						this.categoryList = response[1].data;
					} else {
						// 使用硬编码的备用数据
						this.categoryList = [
							{ id: 1, type: 1, name: '家常菜', sort: 1, status: 1 },
							{ id: 2, type: 1, name: '盖饭', sort: 2, status: 1 },
							{ id: 3, type: 1, name: '米饭', sort: 3, status: 1 },
							{ id: 4, type: 1, name: '特色菜', sort: 4, status: 1 },
							{ id: 5, type: 1, name: '干锅', sort: 5, status: 1 },
							{ id: 6, type: 1, name: '家常菜系列', sort: 6, status: 1 },
							{ id: 7, type: 1, name: '汤菜', sort: 7, status: 1 },
							{ id: 8, type: 1, name: '素菜系列', sort: 8, status: 1 },
							{ id: 9, type: 2, name: '套餐', sort: 9, status: 1 }
						];
					}
				} catch (error) {
					console.error("加载分类数据失败:", error);
					// 使用硬编码的备用数据
					this.categoryList = [
						{ id: 1, type: 1, name: '家常菜', sort: 1, status: 1 },
						{ id: 2, type: 1, name: '盖饭', sort: 2, status: 1 },
						{ id: 3, type: 1, name: '米饭', sort: 3, status: 1 },
						{ id: 4, type: 1, name: '特色菜', sort: 4, status: 1 },
						{ id: 5, type: 1, name: '干锅', sort: 5, status: 1 },
						{ id: 6, type: 1, name: '家常菜系列', sort: 6, status: 1 },
						{ id: 7, type: 1, name: '汤菜', sort: 7, status: 1 },
						{ id: 8, type: 1, name: '素菜系列', sort: 8, status: 1 },
						{ id: 9, type: 2, name: '套餐', sort: 9, status: 1 }
					];
				}
				
				// 按sort字段排序
				if (this.categoryList && this.categoryList.length > 0) {
					this.categoryList.sort((a, b) => a.sort - b.sort);
				}
			},
			
			// 加载菜品数据
			async loadDishData() {
				try {
					// 检查登录状态
					const token = uni.getStorageSync('token');
					if (!token) {
						console.log('用户未登录，不加载菜品数据');
						// 确保初始值为空数组
						this.allDishes = [];
						return;
					}
					
					// 先检查是否有登录时预加载的菜品数据
					const cachedDishData = uni.getStorageSync('dishData');
					if (cachedDishData) {
						try {
							const parsedData = JSON.parse(cachedDishData);
							// 检查数据是否在24小时内（86400000毫秒）
							if (parsedData.timestamp && Date.now() - parsedData.timestamp < 86400000) {
								console.log('使用预加载的菜品列表数据');
								// 确保数据是数组
								this.allDishes = Array.isArray(parsedData.data) ? parsedData.data : [];
								// 数据有效，直接返回
								return;
							}
						} catch (e) {
							console.error('解析预加载的菜品列表数据失败:', e);
							this.allDishes = [];
						}
					}
					
					// 如果没有预加载数据或数据已过期，则继续请求API
					console.log('尝试从API获取菜品数据');
					// 尝试从服务器获取菜品数据
					const res = await dishListApi({
						pageNum: 1,      // 显式指定页码
						pageSize: 20     // 显式指定每页20条数据
					});
					console.log('菜品数据API响应:', res);
					
					// 检查API响应结构
					if (res && res.code === 200 && res.records) {
						// 新的API结构: {total, records, current, size, code}
						console.log('解析新API结构，records数组长度:', res.records.length);
						this.allDishes = res.records.map(item => {
							// 处理可能的字段名不一致问题
							const name = item.n || item.name;
							const categoryId = this.getCategoryIdByName(name);
							
							return {
								id: item.id,
								name: name,
								categoryId: categoryId,
								price: item.price,
								image: item.image || '/static/images/noImg.png',
								description: item.description || '',
								status: item.status,
								sale: item.sales || item.sale || 0
							};
						});
						console.log('解析后的菜品数据:', this.allDishes);
					} else if (res && res.code === 0 && res.data) {
						// 旧的API结构
						console.log('解析旧API结构');
						this.allDishes = Array.isArray(res.data) ? res.data.map(item => {
							const name = item.n || item.name;
							const categoryId = this.getCategoryIdByName(name);
							
							return {
								...item,
								name: name,
								categoryId: categoryId
							};
						}) : [];
					} else if (Array.isArray(res)) {
						// 直接返回数组
						console.log('API直接返回数组');
						this.allDishes = res.map(item => {
							const name = item.n || item.name;
							const categoryId = this.getCategoryIdByName(name);
							
							return {
								...item,
								name: name,
								categoryId: categoryId
							};
						});
					} else if (res && typeof res === 'object') {
						// 尝试从任何对象结构中提取数据
						console.log('尝试从复杂对象中提取数据');
						let dishes = [];
						
						if (res.records && Array.isArray(res.records)) {
							dishes = res.records;
						} else if (res.data && Array.isArray(res.data)) {
							dishes = res.data;
						} else if (res.dishes && Array.isArray(res.dishes)) {
							dishes = res.dishes;
						}
						
						this.allDishes = dishes.map(item => {
							const name = item.n || item.name;
							const categoryId = this.getCategoryIdByName(name);
							
							return {
								...item,
								name: name,
								categoryId: categoryId
							};
						});
					}
					
					// 将所有菜品数据保存到本地存储
					if (this.allDishes && this.allDishes.length > 0) {
						console.log(`成功获取${this.allDishes.length}个菜品，保存到本地存储`);
						uni.setStorageSync('allDishes', JSON.stringify(this.allDishes));
					} else {
						console.warn('API未返回有效的菜品数据，使用备用数据');
						// 使用备用数据
						this.allDishes = [
							{
								id: 1,
								name: "鱼香肉丝",
								categoryId: 1,
								price: 28.00,
								image: "/static/images/noImg.png",
								description: "主料：猪肉、胡萝卜、青椒、木耳",
								status: 1,
								sale: 128
							},
							{
								id: 2,
								name: "宫保鸡丁",
								categoryId: 1,
								price: 26.00,
								image: "/static/images/noImg.png",
								description: "主料：鸡胸肉、花生米、黄瓜、胡萝卜",
								status: 1,
								sale: 105
							},
							{
								id: 3,
								name: "红烧排骨",
								categoryId: 1,
								price: 32.00,
								image: "/static/images/noImg.png",
								description: "主料：猪排骨、土豆、胡萝卜",
								status: 1,
								sale: 98
							},
							{
								id: 4,
								name: "麻婆豆腐",
								categoryId: 2,
								price: 22.00,
								image: "/static/images/noImg.png",
								description: "主料：豆腐、肉末、豆瓣酱",
								status: 1,
								sale: 85
							},
							{
								id: 5,
								name: "干锅土豆片",
								categoryId: 5,
								price: 28.00,
								image: "/static/images/noImg.png",
								description: "主料：土豆、辣椒、木耳、肉片",
								status: 1,
								sale: 75
							}
						];
						// 将备用数据保存到本地存储
						uni.setStorageSync('allDishes', JSON.stringify(this.allDishes));
					}
				} catch (error) {
					console.error("加载菜品数据失败:", error);
					// 使用备用数据
					this.allDishes = [
						{
							id: 1,
							name: "鱼香肉丝",
							categoryId: 1,
							price: 28.00,
							image: "/static/images/noImg.png",
							description: "主料：猪肉、胡萝卜、青椒、木耳",
							status: 1,
							sale: 128
						},
						{
							id: 2,
							name: "宫保鸡丁",
							categoryId: 1,
							price: 26.00,
							image: "/static/images/noImg.png",
							description: "主料：鸡胸肉、花生米、黄瓜、胡萝卜",
							status: 1,
							sale: 105
						}
					];
					// 将备用数据保存到本地存储
					uni.setStorageSync('allDishes', JSON.stringify(this.allDishes));
				}
			},
			
			// 根据菜品名称获取分类ID
			getCategoryIdByName(name) {
				if (!name) return 1;
				
				// 家常菜: 鱼香肉丝、宫保鸡丁、红烧排骨、回锅肉
				if (name.includes('鱼香肉丝') || name.includes('宫保鸡丁') || 
					name.includes('红烧排骨') || name.includes('回锅肉')) {
					return 1;
				}
				
				// 盖饭: 麻婆豆腐
				if (name.includes('麻婆豆腐')) {
					return 2;
				}
				
				// 米饭: 白米饭
				if (name.includes('米饭')) {
					return 3;
				}
				
				// 特色菜: 水煮肉片
				if (name.includes('水煮肉片')) {
					return 4;
				}
				
				// 干锅: 干锅土豆片
				if (name.includes('干锅')) {
					return 5;
				}
				
				// 汤菜: 番茄蛋花汤
				if (name.includes('汤')) {
					return 7;
				}
				
				// 素菜系列: 蒜蓉蒸茄子、青椒土豆丝、干煸四季豆
				if (name.includes('茄子') || name.includes('土豆丝') || name.includes('四季豆')) {
					return 8;
				}
				
				// 默认分类为家常菜
				return 1;
			},
			
			// 按分类id获取该分类下的所有菜品
			getDishesForCategory(categoryId) {
				// 确保 allDishes 是一个数组
				if (!this.allDishes || !Array.isArray(this.allDishes)) {
					console.error('allDishes 不是数组:', this.allDishes);
					return [];
				}
				
				// 过滤出该分类下的菜品
				const dishes = this.allDishes.filter(dish => {
					// 如果菜品已有分类ID且匹配，直接返回true
					if (dish.categoryId && dish.categoryId === categoryId) {
						return true;
					}
					
					// 否则，根据名称判断分类
					const dishCategoryId = this.getCategoryIdByName(dish.name);
					// 更新菜品的分类ID
					dish.categoryId = dishCategoryId;
					return dishCategoryId === categoryId;
				});
				
				console.log(`分类ID ${categoryId} 下有 ${dishes.length} 个菜品`);
				return dishes;
			},
			
			// 获取所有分类标题的位置信息
			getCategoryPositions() {
				const query = uni.createSelectorQuery().in(this);
				query.selectAll('.category-title').boundingClientRect(rects => {
					if (!rects || rects.length === 0) return;
					
					// 存储每个分类的位置信息
					this.categoryPositions = rects.map(rect => {
						return {
							id: parseInt(rect.dataset.categoryId),
							top: rect.top
						};
					});
				}).exec();
			},
			
			// 监听右侧滚动，同步左侧菜单选中状态
			onDishScroll(e) {
				// 防止频繁触发
				if (this.scrollLock) return;
				this.scrollLock = true;
				
				// 延迟执行，降低频率
				setTimeout(() => {
					this.scrollLock = false;
					
					// 重新获取分类位置
					this.getCategoryPositions();
					
					// 计算当前应该选中哪个分类
					if (this.categoryPositions && this.categoryPositions.length > 0) {
						// 找到第一个在可视区域内的分类
						const currentInView = this.categoryPositions.find(item => item.top > 100);
						
						if (currentInView) {
							// 找到该分类在左侧菜单的索引
							const index = this.categoryList.findIndex(cat => cat.id === currentInView.id);
							if (index !== -1 && index !== this.activeType) {
								// 更新选中分类
				this.activeType = index;
							}
						}
				}
				}, 200);
			},
			
			// 分类点击事件 - 滚动到对应分类区域
			categoryClick(index, id, type) {
				this.activeType = index;
				
				// 滚动到对应的分类区域
				this.scrollIntoViewId = `category-${id}`;
                
                // 防止连续触发滚动事件
                this.scrollLock = true;
                setTimeout(() => {
                    this.scrollLock = false;
                }, 500);
			},
			
			// 从服务器加载购物车数据
			async loadCartFromServer() {
				try {
					// 检查登录状态
					const token = uni.getStorageSync('token');
					if (!token) {
						console.log('用户未登录，不加载购物车数据');
						this.cartItems = [];
						this.cartCount = 0;
						this.totalPrice = '0.00';
						return;
					}
					
					// 从服务器获取购物车数据
					const response = await uni.request({
						url: 'http://localhost:8080/api/v1/cart',
						method: 'GET',
						header: {
							'customerToken': token,
							'userType': '3',
							'Content-Type': 'application/json'
						}
					});
					
					console.log('从服务器获取购物车数据响应:', response);
					
					// 解析响应数据
					const res = response[1];
					console.log('解析后的响应对象:', res);
					
					if (res && res.statusCode === 200 && res.data) {
						let result = res.data;
						console.log('响应数据内容:', result);
						
						// 检查是否为XML格式的响应
						if (typeof result === 'string' && result.includes('<Result>')) {
							console.log('检测到XML格式响应，开始解析');
							result = this.parseXMLCartResponse(result);
							console.log('XML解析后的结果:', result);
						}
						
						// 处理JSON格式或解析后的数据
						if (result && result.code === 200 && result.data) {
							console.log('购物车数据:', result.data);
							console.log('购物车items:', result.data.items);
							
							if (result.data.items && Array.isArray(result.data.items)) {
								// 将服务器数据转换为前端格式
								this.cartItems = result.data.items.map(item => {
									console.log('处理购物车项:', item);
									return {
										id: item.itemId || item.id,
										name: item.name,
										price: item.price,
										image: item.image || '/static/images/noImg.png',
										number: item.quantity || 1,
										categoryId: item.categoryId
									};
								});
								console.log('从服务器加载购物车数据成功，共', this.cartItems.length, '项:', this.cartItems);
					} else {
								// 服务器返回空购物车
						this.cartItems = [];
								console.log('服务器返回空购物车数据 - items为空或不是数组');
							}
						} else {
							// 服务器返回错误或无效数据
							console.log('服务器返回无效数据，code:', result?.code);
							this.cartItems = [];
						}
					} else {
						// 获取失败，初始化为空
						console.log('获取服务器购物车数据失败，状态码:', res?.statusCode, '数据:', res?.data);
						this.cartItems = [];
					}
					
					// 计算购物车总数和总价
					this.calculateCartTotals();
					console.log('计算后的购物车总数:', this.cartCount, '总价:', this.totalPrice);
				} catch (e) {
					console.error("从服务器获取购物车数据失败", e);
					this.cartItems = [];
					this.cartCount = 0;
					this.totalPrice = '0.00';
				}
			},
			
			// 解析XML格式的购物车响应
			parseXMLCartResponse(xmlString) {
				try {
					console.log('开始解析XML购物车响应');
					
					// 提取基本信息
					const codeMatch = xmlString.match(/<code>(.*?)<\/code>/);
					const msgMatch = xmlString.match(/<msg>(.*?)<\/msg>/);
					const successMatch = xmlString.match(/<success>(.*?)<\/success>/);
					const totalMatch = xmlString.match(/<total>(.*?)<\/total>/);
					
					// 提取所有购物车项
					const itemsMatches = xmlString.match(/<items>(.*?)<\/items>/g);
					const items = [];
					
					if (itemsMatches) {
						itemsMatches.forEach(itemXml => {
							const idMatch = itemXml.match(/<id>(.*?)<\/id>/);
							const nameMatch = itemXml.match(/<name>(.*?)<\/name>/);
							const cartIdMatch = itemXml.match(/<cartId>(.*?)<\/cartId>/);
							const userIdMatch = itemXml.match(/<userId>(.*?)<\/userId>/);
							const itemIdMatch = itemXml.match(/<itemId>(.*?)<\/itemId>/);
							const itemTypeMatch = itemXml.match(/<itemType>(.*?)<\/itemType>/);
							const quantityMatch = itemXml.match(/<quantity>(.*?)<\/quantity>/);
							const priceMatch = itemXml.match(/<price>(.*?)<\/price>/);
							const itemTotalMatch = itemXml.match(/<total>(.*?)<\/total>/);
							
							if (idMatch && nameMatch && itemIdMatch) {
								items.push({
									id: parseInt(idMatch[1]),
									name: nameMatch[1],
									cartId: cartIdMatch ? parseInt(cartIdMatch[1]) : null,
									userId: userIdMatch ? parseInt(userIdMatch[1]) : null,
									itemId: parseInt(itemIdMatch[1]),
									itemType: itemTypeMatch ? itemTypeMatch[1] : 'dish',
									quantity: quantityMatch ? parseInt(quantityMatch[1]) : 1,
									price: priceMatch ? parseFloat(priceMatch[1]) : 0,
									total: itemTotalMatch ? parseFloat(itemTotalMatch[1]) : 0
								});
							}
						});
					}
					
					const result = {
						code: codeMatch ? parseInt(codeMatch[1]) : 200,
						msg: msgMatch ? msgMatch[1] : 'OK',
						data: {
							id: null,
							userId: 1,
							items: items,
							total: totalMatch ? parseFloat(totalMatch[1]) : 0
						},
						success: successMatch ? successMatch[1] === 'true' : true
					};
					
					console.log('XML解析完成，结果:', result);
					return result;
				} catch (error) {
					console.error('解析XML购物车响应失败:', error);
					return {
						code: 500,
						msg: 'XML解析失败',
						data: { items: [] },
						success: false
					};
				}
			},
			
			// 计算购物车总数和总价
			calculateCartTotals() {
						let count = 0;
						let price = 0;
				
				this.cartItems.forEach(item => {
							count += item.number;
							price += item.number * item.price;
						});
				
						this.cartCount = count;
				this.totalPrice = price.toFixed(2);
			},
			
			// 获取特定菜品在购物车中的数量
			getItemCount(dishId) {
				const cartItem = this.cartItems.find(item => item.id === dishId);
				return cartItem ? cartItem.number : 0;
			},
			
			// 添加菜品到购物车
			async addCart(item, event) {
				try {
					console.log('开始添加商品到购物车:', item.name, 'ID:', item.id);
					
					// 检查登录状态
					const token = uni.getStorageSync('token');
					if (!token) {
						console.log('用户未登录，token为空');
						uni.$showMsg('请先登录');
						return;
					}
					console.log('用户token:', token);
					
					// 获取用户ID
					const userId = uni.getStorageSync('userId') || 1;
					console.log('用户ID:', userId);
					
					const requestData = {
						userId: userId,
						itemId: item.id,
						itemType: 'dish', // 默认为菜品类型
						quantity: 1
					};
					console.log('请求数据:', requestData);
					
					// 直接调用服务器API添加商品
					const response = await uni.request({
						url: 'http://localhost:8080/api/v1/cart/add',
						method: 'POST',
						header: {
							'customerToken': token,
							'Accept': 'application/json',
							'userType': '3',
							'Content-Type': 'application/json'
						},
						data: requestData
					});
					
					console.log('添加购物车API响应:', response);
					
					// 检查响应
					const res = response[1];
					console.log('添加购物车解析后的响应对象:', res);
					
					if (res && res.statusCode === 200) {
						console.log('添加购物车响应数据:', res.data);
						
						// 检查响应数据结构
						if (res.data && (res.data.success === true || res.data.code === 200)) {
							console.log('添加购物车成功，开始重新加载购物车数据');
							// 添加成功，重新从服务器获取购物车数据
							await this.loadCartFromServer();
					
							// 显示成功提示
					uni.$showMsg('已添加到购物车');
						} else {
							console.log('添加购物车失败，响应数据:', res.data);
							throw new Error('添加失败');
						}
					} else {
						console.log('添加购物车请求失败，状态码:', res?.statusCode);
						throw new Error('添加失败');
					}
				} catch (e) {
					console.error("添加到购物车失败", e);
					uni.$showMsg('添加失败，请重试');
				}
			},
			
			// 从购物车中减少菜品
			async subtractCart(item) {
				try {
					console.log('开始减少商品数量:', item.name, 'ID:', item.id);
					
					// 检查登录状态
					const token = uni.getStorageSync('token');
					if (!token) {
						uni.$showMsg('请先登录');
						return;
					}
					
					// 获取当前商品在购物车中的信息
					const currentItem = this.cartItems.find(cartItem => cartItem.id === item.id);
					if (!currentItem) {
						console.log('商品不在本地购物车中，商品ID:', item.id);
						console.log('当前购物车项:', this.cartItems);
						uni.$showMsg('商品不在购物车中');
						return;
					}
					
					console.log('找到购物车项:', currentItem);
					
					// 找到购物车中对应的项目ID（cartItemId）
					// 需要重新获取购物车数据来获取正确的cartItemId
					const cartResponse = await uni.request({
						url: 'http://localhost:8080/api/v1/cart',
						method: 'GET',
						header: {
							'customerToken': token,
							'userType': '3',
							'Content-Type': 'application/json'
						}
					});
					
					console.log('获取购物车响应:', cartResponse);
					
					let cartItemId = null;
					if (cartResponse && cartResponse[1].statusCode === 200 && cartResponse[1].data) {
						let cartData = cartResponse[1].data;
						
						// 检查是否为XML格式的响应
						if (typeof cartData === 'string' && cartData.includes('<Result>')) {
							console.log('检测到XML格式响应，开始解析');
							cartData = this.parseXMLCartResponse(cartData);
							console.log('XML解析后的结果:', cartData);
						}
						
						// 查找对应的购物车项
						if (cartData && cartData.data && cartData.data.items) {
							const cartItem = cartData.data.items.find(ci => ci.itemId === item.id);
							if (cartItem) {
								cartItemId = cartItem.id; // 这是购物车项的ID
								console.log('找到购物车项ID:', cartItemId);
					} else {
								console.log('在服务器购物车中未找到商品，itemId:', item.id);
								console.log('服务器购物车项:', cartData.data.items);
							}
						}
					}
					
					if (!cartItemId) {
						console.log('找不到购物车项ID');
						uni.$showMsg('找不到购物车项');
						return;
					}
					
					if (currentItem.number <= 1) {
						// 如果只剩一个，询问是否移除
						uni.showModal({
							title: '提示',
							content: '是否从购物车移除该商品？',
							success: async (res) => {
								if (res.confirm) {
									// 确认移除，调用删除API
									await this.removeFromCartById(cartItemId);
								}
							}
						});
						return;
					}
					
					console.log('开始删除购物车项，cartItemId:', cartItemId);
					
					// 先删除该购物车项
					await uni.request({
						url: `http://localhost:8080/api/v1/cart/delete?cartItemId=${cartItemId}`,
						method: 'DELETE',
						header: {
							'customerToken': token,
							'Accept': 'application/json',
							'userType': '3',
							'Content-Type': 'application/json'
						}
					});
						
					// 如果还有剩余数量，重新添加
					if (currentItem.number > 1) {
						const userId = uni.getStorageSync('userId') || 1;
						console.log('重新添加商品，数量:', currentItem.number - 1);
						
						await uni.request({
							url: 'http://localhost:8080/api/v1/cart/add',
							method: 'POST',
							header: {
								'customerToken': token,
								'Accept': 'application/json',
								'userType': '3',
								'Content-Type': 'application/json'
							},
							data: {
								userId: userId,
								itemId: item.id,
								itemType: 'dish',
								quantity: currentItem.number - 1
							}
						});
					}
					
					// 重新从服务器获取购物车数据
					await this.loadCartFromServer();
					
					uni.$showMsg('已减少');
				} catch (e) {
					console.error("从购物车移除失败", e);
					uni.$showMsg('操作失败，请重试');
				}
			},
			
			// 从购物车中完全移除商品
			async removeFromCart(item) {
				try {
					console.log('开始移除商品:', item.name, 'ID:', item.id);
					
					const token = uni.getStorageSync('token');
					if (!token) {
						uni.$showMsg('请先登录');
						return;
					}
					
					// 获取购物车数据来找到正确的cartItemId
					const cartResponse = await uni.request({
						url: 'http://localhost:8080/api/v1/cart',
						method: 'GET',
						header: {
							'customerToken': token,
							'userType': '3',
							'Content-Type': 'application/json'
						}
					});
					
					console.log('获取购物车响应:', cartResponse);
					
					let cartItemId = null;
					if (cartResponse && cartResponse[1].statusCode === 200 && cartResponse[1].data) {
						let cartData = cartResponse[1].data;
						
						// 检查是否为XML格式的响应
						if (typeof cartData === 'string' && cartData.includes('<Result>')) {
							console.log('检测到XML格式响应，开始解析');
							cartData = this.parseXMLCartResponse(cartData);
							console.log('XML解析后的结果:', cartData);
						}
						
						// 查找对应的购物车项
						if (cartData && cartData.data && cartData.data.items) {
							const cartItem = cartData.data.items.find(ci => ci.itemId === item.id);
							if (cartItem) {
								cartItemId = cartItem.id;
								console.log('找到购物车项ID:', cartItemId);
							} else {
								console.log('在服务器购物车中未找到商品，itemId:', item.id);
							}
						}
					}
					
					if (!cartItemId) {
						console.log('找不到购物车项ID');
						uni.$showMsg('找不到购物车项');
						return;
					}
					
					await this.removeFromCartById(cartItemId);
				} catch (error) {
					console.error('删除商品失败:', error);
					uni.$showMsg('删除失败，请重试');
				}
			},
			
			// 根据cartItemId删除购物车项
			async removeFromCartById(cartItemId) {
				try {
					const token = uni.getStorageSync('token');
					
					// 调用删除API
					const response = await uni.request({
						url: `http://localhost:8080/api/v1/cart/delete?cartItemId=${cartItemId}`,
						method: 'DELETE',
						header: {
							'customerToken': token,
							'Accept': 'application/json',
							'userType': '3',
							'Content-Type': 'application/json'
						}
					});
					
					console.log('删除购物车API响应:', response);
					
					// 重新从服务器获取购物车数据
					await this.loadCartFromServer();
					
					uni.$showMsg('已移除商品');
				} catch (error) {
					console.error('删除购物车项失败:', error);
					uni.$showMsg('删除失败，请重试');
				}
			},
			
			// 跳转到菜品详情页
			dishDetails(dish) {
				uni.navigateTo({
					url: `/pages/dishDetail/dishDetail?id=${dish.id}`
				});
			},
			
			// 前往购物车页面或结算页面
			goToCart() {
				if (this.cartCount > 0) {
						uni.navigateTo({
							url: '/pages/cart/cart'
					});
				} else {
					uni.$showMsg('购物车是空的哦~');
				}
			},
			
			// 提交订单
			submitOrder() {
				if (this.cartCount > 0) {
					uni.navigateTo({
							url: '/pages/cart/cart'
					});
				} else {
					uni.$showMsg('请先选择菜品');
				}
			},
			
			// 获取订单评价数据
			async getComments() {
				try {
					// 模拟调用API获取评价数据
					const commentData = [
						{
							id: 1,
							orderId: 10001,
							comment: '菜品味道很好，服务也很周到',
							createTime: '2023-12-01 12:30:45'
						},
						{
							id: 2,
							orderId: 10002,
							comment: '红烧排骨很入味，汤也很好喝',
							createTime: '2023-12-02 18:22:31'
						},
						{
							id: 3,
							orderId: 10003,
							comment: '干锅土豆片非常好吃，下次还会再点',
							createTime: '2023-12-03 19:15:27'
						},
						{
							id: 4,
							orderId: 10004,
							comment: '糖醋排骨的味道很不错',
							createTime: '2023-12-04 13:45:18'
						}
					];
					
					// 设置评价数据
					this.commentList = commentData;
					
				} catch (error) {
					console.error('获取评价数据失败：', error);
					uni.showToast({
						title: '获取评价数据失败',
						icon: 'none'
					});
				}
			},
			
			// 日期格式化工具
			formatDate(dateStr) {
				if (!dateStr) return '';
				
				try {
					const date = new Date(dateStr);
					return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
				} catch (e) {
					return dateStr;
				}
			},
			
			// 处理图片加载错误
			handleImageError(event, item) {
				console.error('图片加载失败:', item.name, item.image);
				// 将图片源替换为本地占位图
				item.image = '/static/images/noImg.png';
			},
			
			// 获取图片源
			getImageSrc(item) {
				// 检查是否是本地图片路径
				if (item.image && (item.image.startsWith('/static/images/dish') || item.image.includes('dish'))) {
					// 本地菜品图片可能不存在，直接使用占位图
					return '/static/images/noImg.png';
				}
				// 返回原始图片路径或默认占位图
				return item.image || '/static/images/noImg.png';
			},
			
			// 调试方法：显示当前分类和菜品数据
			debugShowData() {
				console.log('==== 调试信息 ====');
				console.log('分类数据:', this.categoryList);
				console.log('菜品总数:', this.allDishes.length);
				
				// 检查每个分类下的菜品数量
				if (this.categoryList && this.categoryList.length > 0) {
					this.categoryList.forEach(category => {
						const dishes = this.getDishesForCategory(category.id);
						console.log(`分类 ${category.name}(ID:${category.id}) 下有 ${dishes.length} 个菜品`);
					});
				}
				
				console.log('==== 调试结束 ====');
			}
		}
	}
</script>

<style scoped>
.container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f8f8f8;
}

/* 顶部区域样式 */
.header {
	background: linear-gradient(135deg, #ffb300, #ffd54f);
	border-radius: 0 0 30rpx 30rpx;
	height: 160rpx;
	display: flex;
	align-items: flex-end;
	padding-bottom: 20rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.header-title {
	width: 100%;
	text-align: center;
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	letter-spacing: 2rpx;
	padding-bottom: 20rpx;
}

/* 店铺信息区域 */
.shop-info {
	background-color: #fff;
	border-radius: 16rpx;
	margin: 20rpx;
	padding: 24rpx;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.shop-detail {
	display: flex;
	margin-bottom: 16rpx;
	flex-wrap: wrap;
}

.shop-location, .shop-delivery, .shop-time {
	display: flex;
	align-items: center;
	margin-right: 24rpx;
	margin-bottom: 10rpx;
}

.shop-location image, .shop-delivery image, .shop-time image {
	width: 32rpx;
	height: 32rpx;
	margin-right: 8rpx;
}

.shop-location text, .shop-delivery text, .shop-time text {
	font-size: 24rpx;
	color: #666;
}

.shop-desc {
	font-size: 26rpx;
	color: #999;
	line-height: 1.5;
}

/* 标签页样式 */
.tab-container {
	background-color: #fff;
	border-bottom: 1rpx solid #eee;
}

/* 主体内容区域 */
.main-content {
	display: flex;
	flex: 1;
	overflow: hidden;
}

/* 左侧分类导航 */
.category-sidebar {
	width: 160rpx;
	background-color: #f5f5f5;
	height: 100%;
	overflow: hidden;
}

.category-scroll {
	height: 100%;
}

.category-item {
	padding: 30rpx 10rpx;
	text-align: center;
	font-size: 24rpx;
	color: #666;
	border-left: 6rpx solid transparent;
	position: relative;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.category-item.active {
	color: #ffb300;
	font-weight: bold;
	background-color: #fff;
	border-left-color: #ffb300;
}

/* 菜品分组样式 */
.dish-group {
	margin-bottom: 20rpx;
}

/* 暂无菜品提示 */
.no-dish-tip {
	padding: 30rpx 0;
	text-align: center;
	color: #999;
	font-size: 24rpx;
}

/* 右侧菜品列表 */
.dish-container {
	flex: 1;
	padding: 20rpx;
	background-color: #fff;
}

.dish-item {
	display: flex;
	margin-bottom: 30rpx;
	padding-bottom: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.dish-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}

.dish-content {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.dish-info {
	flex: 1;
}

.dish-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 10rpx;
}

.dish-desc {
	font-size: 24rpx;
	color: #999;
	line-height: 1.5;
	margin-bottom: 8rpx;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.dish-sales {
	font-size: 22rpx;
	color: #bbb;
}

.dish-action {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 16rpx;
}

.dish-price {
	display: flex;
	align-items: baseline;
}

.price-symbol {
	font-size: 24rpx;
	color: #ff5722;
}

.price-value {
	font-size: 32rpx;
	color: #ff5722;
	font-weight: bold;
}

.dish-controls {
	display: flex;
	align-items: center;
}

.add-button, .subtract-button {
	width: 50rpx;
	height: 50rpx;
	border-radius: 50%;
	display: flex;
	justify-content: center;
	align-items: center;
	background-color: #ffb300;
	box-shadow: 0 2rpx 6rpx rgba(255, 179, 0, 0.3);
}

.add-button image, .subtract-button image {
	width: 60%;
	height: 60%;
}

.subtract-button {
	background-color: #fff;
	border: 1rpx solid #ddd;
}

.dish-count {
	margin: 0 20rpx;
	font-size: 28rpx;
	color: #333;
	min-width: 40rpx;
	text-align: center;
}

.flavor-button {
	background-color: #fff5e6;
	color: #ffb300;
	border: 1rpx solid #ffb300;
	border-radius: 30rpx;
	padding: 8rpx 20rpx;
	font-size: 24rpx;
}

.bottom-space {
	height: 120rpx;
}

/* 浮动购物车图标 */
.floating-cart {
	position: fixed;
	right: 30rpx;
	bottom: 160rpx;
	z-index: 99;
}

.cart-icon-container {
	width: 90rpx;
	height: 90rpx;
	background-color: #feca50;
	border-radius: 50%;
	display: flex;
	justify-content: center;
	align-items: center;
	position: relative;
	box-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.2);
}

.cart-emoji {
	font-size: 50rpx;
}

.cart-emoji-small {
	font-size: 40rpx;
	margin-right: 10rpx;
}

.floating-cart-badge {
	position: absolute;
	top: -12rpx;
	right: -12rpx;
	background-color: #ff5252;
	color: #ffffff;
	border-radius: 50%;
	width: 40rpx;
	height: 40rpx;
	font-size: 24rpx;
	display: flex;
	justify-content: center;
	align-items: center;
}

/* 底部购物车 */
.cart-bar {
	position: fixed;
	left: 0;
	bottom: 0;
	width: 100%;
	height: 100rpx;
	background-color: #ffffff;
	display: flex;
	align-items: center;
	padding: 0 20rpx;
	box-sizing: border-box;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
	z-index: 10;
}

.cart-left {
	flex: 1;
	display: flex;
	align-items: center;
}

.cart-icon-wrapper {
	width: 80rpx;
	height: 80rpx;
	background-color: #feca50;
	border-radius: 50%;
	display: flex;
	justify-content: center;
	align-items: center;
	position: relative;
	margin-right: 20rpx;
}

.cart-price {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.cart-button {
	background-color: #ddd;
	color: #fff;
	border-radius: 30rpx;
	padding: 16rpx 30rpx;
	font-size: 28rpx;
	font-weight: bold;
}

.cart-button-active {
	background-color: #ffb300;
	box-shadow: 0 4rpx 8rpx rgba(255, 179, 0, 0.3);
}

.cart-button-icon {
	width: 24rpx;
	height: 24rpx;
	margin-right: 10rpx;
}

/* 评价页面样式 */
.review-container {
	background-color: #fff;
	padding: 20rpx;
	height: 100%;
	overflow-y: auto;
}

.review-header {
	padding: 20rpx 0;
	border-bottom: 1rpx solid #eee;
	margin-bottom: 20rpx;
}

.review-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.review-stats {
	margin-top: 10rpx;
	display: flex;
	align-items: center;
}

.review-score {
	font-size: 36rpx;
	color: #ff5722;
	font-weight: bold;
	margin-right: 10rpx;
}

.review-count {
	font-size: 24rpx;
	color: #999;
}

.no-reviews {
	padding: 60rpx 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	color: #999;
	font-size: 28rpx;
}

.no-review-image {
	width: 200rpx;
	height: 200rpx;
	margin-bottom: 20rpx;
}

.review-item {
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f5f5f5;
}

.review-user {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.user-avatar {
	width: 64rpx;
	height: 64rpx;
	border-radius: 50%;
	margin-right: 16rpx;
}

.user-info {
	flex: 1;
}

.user-name {
	font-size: 28rpx;
	color: #333;
	font-weight: 500;
}

.review-time {
	font-size: 22rpx;
	color: #999;
	margin-top: 4rpx;
}

.review-content {
	font-size: 28rpx;
	color: #333;
	line-height: 1.6;
	margin-bottom: 16rpx;
}

.review-order-info {
	font-size: 24rpx;
	color: #999;
}

/* 商家页面样式 */
.merchant-container {
	background-color: #fff;
	padding: 20rpx;
	height: 100%;
	overflow-y: auto;
}

.merchant-header {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #eee;
}

.merchant-logo {
	width: 120rpx;
	height: 120rpx;
	border-radius: 8rpx;
	margin-right: 20rpx;
}

.merchant-info {
	flex: 1;
}

.merchant-name {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
}

.merchant-desc {
	font-size: 26rpx;
	color: #666;
}

.merchant-details {
	padding: 20rpx 0;
}

.merchant-section {
	margin-bottom: 30rpx;
}

.section-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 16rpx;
	position: relative;
	padding-left: 20rpx;
}

.section-title::before {
	content: '';
	position: absolute;
	left: 0;
	top: 50%;
	transform: translateY(-50%);
	height: 30rpx;
	width: 6rpx;
	background-color: #ffb300;
	border-radius: 3rpx;
}

.section-content {
	font-size: 28rpx;
	color: #666;
	line-height: 1.6;
}

.contact-item {
	display: flex;
	align-items: center;
	margin-bottom: 16rpx;
}

.contact-item image {
	width: 36rpx;
	height: 36rpx;
	margin-right: 10rpx;
}

.cart-button-icon {
	width: 36rpx;
	height: 36rpx;
	margin-right: 10rpx;
}

/* 分类标题样式 */
.category-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	padding: 20rpx 0 10rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
	margin-bottom: 10rpx;
}
</style>
