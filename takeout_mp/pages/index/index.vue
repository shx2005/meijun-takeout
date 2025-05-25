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
		<view class="main-content">
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
				<block v-for="(category, catIndex) in categoryList" :key="'cat_'+catIndex">
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
							:key="'dish_'+item.id"
							@click="dishDetails(item)">
							<image class="dish-image" :src="item.image || '../../static/images/noImg.png'" mode="aspectFill"></image>
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
		
		<!-- 浮动购物车图标 -->
		<view class="floating-cart" @click="goToCart">
			<image src="/static/index_image/cart.png" class="floating-cart-icon"></image>
			<view class="floating-cart-badge" v-if="cartCount > 0">{{ cartCount }}</view>
		</view>
		
		<!-- 底部购物车 -->
		<view class="cart-bar">
			<view class="cart-left">
				<view class="cart-icon-wrapper" @click="goToCart">
					<image src="/static/index_image/cart.png" class="cart-icon"></image>
					<view class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</view>
				</view>
				<view class="cart-price">
				<text>￥{{ totalPrice || '0.00' }}</text>
				</view>
			</view>
			<view class="cart-button" :class="{'cart-button-active': cartCount > 0}" @click="submitOrder">
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
		addCartApi
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
				categoryPositions: [] // 存储分类位置信息
			}
		},
		onLoad() {
			this.init();
			this.loadCartFromStorage();
		},
		onShow() {
			this.loadCartFromStorage();
			this.calculateCartTotals();
		},
		mounted() {
			// 初始化后获取分类位置信息
			setTimeout(() => {
				this.getCategoryPositions();
			}, 1000);
		},
		methods: {
			async init() {
				try {
					// 加载分类数据
					await this.loadCategoryData();
					
					// 加载菜品数据
					await this.loadDishData();
				} catch (error) {
					console.error("初始化数据失败", error);
					uni.$showMsg('获取数据失败，请重试');
				}
			},
			
			// 加载分类数据
			async loadCategoryData() {
				try {
					// 尝试从服务器获取分类数据
					const res = await categoryListApi({});
					if (res && res.code === 0 && res.data) {
						this.categoryList = res.data;
					} else {
						// 如果服务器获取失败，使用本地JSON数据作为备用
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
				this.categoryList.sort((a, b) => a.sort - b.sort);
			},
			
			// 加载菜品数据
			async loadDishData() {
				try {
					// 尝试从服务器获取菜品数据
					const res = await dishListApi({});
					if (res && res.code === 0 && res.data) {
						this.allDishes = res.data;
					} else {
						// 如果服务器获取失败，使用本地JSON数据作为备用
						const response = await uni.request({
							url: '/dish_data.json',
							method: 'GET'
						});
						if (response && response[1].data) {
							this.allDishes = response[1].data;
						} else {
							// 使用硬编码的备用数据
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
								// 更多备用数据...
							];
						}
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
						},
						{
							id: 6,
							name: "水煮肉片",
							categoryId: 4,
							price: 32.00,
							image: "/static/images/dish6.jpg",
							description: "主料：猪肉、豆芽、白菜",
							status: 1,
							sale: 65
						},
						{
							id: 7,
							name: "蒜蓉蒸茄子",
							categoryId: 8,
							price: 18.00,
							image: "/static/images/dish7.jpg",
							description: "主料：茄子、蒜蓉",
							status: 1,
							sale: 55
						},
						{
							id: 8,
							name: "白米饭",
							categoryId: 3,
							price: 2.00,
							image: "/static/images/dish8.jpg",
							description: "精选东北大米",
							status: 1,
							sale: 200
						},
						{
							id: 9,
							name: "番茄蛋花汤",
							categoryId: 7,
							price: 15.00,
							image: "/static/images/dish9.jpg",
							description: "主料：番茄、鸡蛋",
							status: 1,
							sale: 60
						}
					];
				}
			},
			
			// 按分类id获取该分类下的所有菜品
			getDishesForCategory(categoryId) {
				return this.allDishes.filter(dish => dish.categoryId === categoryId);
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
			
			// 从本地存储加载购物车数据
			loadCartFromStorage() {
				try {
					const cartData = uni.getStorageSync('cartItems');
					if (cartData) {
						this.cartItems = JSON.parse(cartData);
					} else {
						this.cartItems = [];
					}
					this.calculateCartTotals();
				} catch (e) {
					console.error("读取购物车数据失败", e);
					this.cartItems = [];
					this.cartCount = 0;
					this.totalPrice = '0.00';
				}
			},
			
			// 保存购物车到本地存储
			saveCartToStorage() {
				try {
					uni.setStorageSync('cartItems', JSON.stringify(this.cartItems));
				} catch (e) {
					console.error("保存购物车数据失败", e);
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
			addCart(item, event) {
				try {
					// 查找购物车中是否已有该菜品
					const index = this.cartItems.findIndex(cartItem => cartItem.id === item.id);
					
					if (index >= 0) {
						// 已有该菜品，增加数量
						this.cartItems[index].number += 1;
					} else {
						// 没有该菜品，添加新项
						this.cartItems.push({
							id: item.id,
							name: item.name,
							price: item.price,
							image: item.image,
							number: 1,
							categoryId: item.categoryId
						});
					}
					
					// 保存到本地存储并更新总计
					this.saveCartToStorage();
					this.calculateCartTotals();
					
					// 可以添加添加成功的视觉反馈
					uni.$showMsg('已添加到购物车');
				} catch (e) {
					console.error("添加到购物车失败", e);
					uni.$showMsg('添加失败，请重试');
				}
			},
			
			// 从购物车中减少菜品
			subtractCart(item) {
				try {
					const index = this.cartItems.findIndex(cartItem => cartItem.id === item.id);
					
					if (index >= 0) {
						if (this.cartItems[index].number > 1) {
							// 数量大于1，减少数量
							this.cartItems[index].number -= 1;
						} else {
							// 数量为1，从购物车移除
							this.cartItems.splice(index, 1);
						}
						
						// 保存到本地存储并更新总计
						this.saveCartToStorage();
						this.calculateCartTotals();
					}
				} catch (e) {
					console.error("从购物车移除失败", e);
					uni.$showMsg('操作失败，请重试');
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
						url: '/pages/payConfirm/payConfirm'
					});
				} else {
					uni.$showMsg('购物车是空的哦~');
				}
			},
			
			// 提交订单
			submitOrder() {
				if (this.cartCount > 0) {
					uni.navigateTo({
						url: '/pages/payConfirm/payConfirm'
					});
				} else {
					uni.$showMsg('请先选择菜品');
				}
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
}

.category-scroll {
	height: 100%;
}

.category-item {
	padding: 30rpx 20rpx;
	text-align: center;
	font-size: 26rpx;
	color: #666;
	border-left: 6rpx solid transparent;
	position: relative;
}

.category-item.active {
	color: #ffb300;
	font-weight: bold;
	background-color: #fff;
	border-left-color: #ffb300;
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
	width: 90rpx;
	height: 90rpx;
	background-color: #fff;
	border-radius: 50%;
	box-shadow: 0 2rpx 20rpx rgba(0, 0, 0, 0.15);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 99;
}

.floating-cart-icon {
	width: 60rpx;
	height: 60rpx;
}

.floating-cart-badge {
	position: absolute;
	top: -10rpx;
	right: -10rpx;
	background-color: #ff5722;
	color: #fff;
	font-size: 20rpx;
	min-width: 36rpx;
	height: 36rpx;
	border-radius: 18rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: bold;
}

/* 底部购物车 */
.cart-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 100rpx;
	background-color: #fff;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 30rpx;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	z-index: 100;
}

.cart-left {
	display: flex;
	align-items: center;
}

.cart-icon-wrapper {
	position: relative;
	margin-right: 20rpx;
}

.cart-icon {
	width: 60rpx;
	height: 60rpx;
}

.cart-badge {
	position: absolute;
	top: -10rpx;
	right: -10rpx;
	background-color: #ff5722;
	color: #fff;
	font-size: 20rpx;
	min-width: 36rpx;
	height: 36rpx;
	border-radius: 18rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: bold;
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
</style>
