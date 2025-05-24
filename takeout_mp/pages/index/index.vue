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
			
			<!-- 右侧菜品列表 -->
			<scroll-view scroll-y class="dish-container">
				<block v-if="!dishList || dishList.length === 0">
					<view class="dish-item" v-for="(item, index) in testDishes" :key="index" @click="dishDetails(item)">
						<image class="dish-image" :src="item.image || '../../static/images/noImg.png'" mode="aspectFill"></image>
						<view class="dish-content">
							<view class="dish-info">
								<view class="dish-name">{{ item.name }}</view>
								<view class="dish-desc" v-if="item.description">{{ item.description }}</view>
								<view class="dish-sales">月售 {{ item.sales || 0 }}</view>
							</view>
							<view class="dish-action">
								<view class="dish-price">
									<text class="price-symbol">￥</text>
									<text class="price-value">{{ (item.price/100).toFixed(2) }}</text>
								</view>
								<view class="add-button" @click.stop="addCart(item,$event)">
									<image src="../../static/images/add.png"></image>
								</view>
							</view>
						</view>
					</view>
				</block>
				<block v-else>
					<view class="dish-item" v-for="(item, index) in dishList" :key="index" @click="dishDetails(item)">
						<image class="dish-image" :src="item.image || '../../static/images/noImg.png'" mode="aspectFill"></image>
						<view class="dish-content">
							<view class="dish-info">
								<view class="dish-name">{{ item.name }}</view>
								<view class="dish-desc" v-if="item.description">{{ item.description }}</view>
								<view class="dish-sales">月售 {{ item.sales || 0 }}</view>
							</view>
							<view class="dish-action">
								<view class="dish-price">
									<text class="price-symbol">￥</text>
									<text class="price-value">{{ (item.price/100).toFixed(2) }}</text>
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
				categoryList: [
					{ id: 1, name: '家常菜', type: 1 },
					{ id: 2, name: '盖饭', type: 1 },
					{ id: 3, name: '米饭', type: 1 },
					{ id: 4, name: '特色菜', type: 1 },
					{ id: 5, name: '干锅', type: 1 },
					{ id: 6, name: '家常菜系列', type: 1 },
					{ id: 7, name: '汤菜', type: 1 },
					{ id: 8, name: '素菜系列', type: 1 },
					{ id: 9, name: '套餐', type: 2 }
				],
				dishList: [],
				totalPrice: '0.00',
				cartCount: 0,
				cartItems: [], // 购物车本地存储项
				
				// 占位菜品数据
				testDishes: [
					{
						id: 1,
						name: "鱼香肉丝套餐",
						description: "主料：猪肉、胡萝卜、青椒、木耳，配米饭一份",
						image: "../../static/images/dish1.jpg",
						price: 2800,
						sales: 128,
						categoryId: 1
					},
					{
						id: 2,
						name: "宫保鸡丁套餐",
						description: "主料：鸡胸肉、花生米、黄瓜、胡萝卜，配米饭一份",
						image: "../../static/images/dish2.jpg",
						price: 2600,
						sales: 105,
						categoryId: 1
					},
					{
						id: 3,
						name: "红烧排骨套餐",
						description: "主料：猪排骨、土豆、胡萝卜，配米饭一份",
						image: "../../static/images/dish3.jpg",
						price: 3200,
						sales: 98,
						categoryId: 1
					},
					{
						id: 4,
						name: "麻婆豆腐套餐",
						description: "主料：豆腐、肉末、豆瓣酱，配米饭一份",
						image: "../../static/images/dish4.jpg",
						price: 2200,
						sales: 85,
						categoryId: 2
					},
					{
						id: 5,
						name: "干锅土豆片",
						description: "主料：土豆、辣椒、木耳、肉片",
						image: "../../static/images/dish5.jpg",
						price: 2800,
						sales: 75,
						categoryId: 5
					},
					{
						id: 6,
						name: "水煮肉片",
						description: "主料：猪肉、豆芽、白菜",
						image: "../../static/images/dish6.jpg",
						price: 3200,
						sales: 65,
						categoryId: 4
					},
					{
						id: 7,
						name: "蒜蓉蒸茄子",
						description: "主料：茄子、蒜蓉",
						image: "../../static/images/dish7.jpg",
						price: 1800,
						sales: 55,
						categoryId: 8
					},
					{
						id: 8,
						name: "白米饭",
						description: "精选东北大米",
						image: "../../static/images/dish8.jpg",
						price: 200,
						sales: 200,
						categoryId: 3
					},
					{
						id: 9,
						name: "番茄蛋花汤",
						description: "主料：番茄、鸡蛋",
						image: "../../static/images/dish9.jpg",
						price: 1000,
						sales: 45,
						categoryId: 7
					}
				],
				
				// 模拟API返回的所有菜品
				allDishes: []
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
		methods: {
			async init() {
				try {
					// 模拟获取菜品分类API
					// const categoryRes = await this.fetchCategories();
					// 使用本地数据代替API调用
					const categoryRes = {
						code: 0,
						data: this.categoryList
					};
					
					if (categoryRes && categoryRes.code === 0) {
						this.categoryList = categoryRes.data;
						
						// 模拟获取所有菜品API
						// const allDishesRes = await this.fetchAllDishes();
						// 使用本地数据代替API调用
						const allDishesRes = {
							code: 0,
							data: this.testDishes
						};
						
						if (allDishesRes && allDishesRes.code === 0) {
							this.allDishes = allDishesRes.data;
							
							// 默认选中第一个分类
						if (this.categoryList.length > 0) {
							this.categoryClick(0, this.categoryList[0].id, this.categoryList[0].type);
							}
						}
					}
				} catch (error) {
					console.error("初始化数据失败", error);
					uni.$showMsg('获取分类失败，请重试');
				}
			},
			
			// 模拟获取菜品分类API
			async fetchCategories() {
				// 实际项目中应该调用真实API
				// return await categoryListApi({});
				
				// 模拟API返回
				return {
					code: 0,
					data: this.categoryList
				};
			},
			
			// 模拟获取所有菜品API
			async fetchAllDishes() {
				// 实际项目中应该调用真实API
				// return await dishListApi({});
				
				// 模拟API返回
				return {
					code: 0,
					data: this.testDishes
				};
			},
			
			async categoryClick(index, id, type) {
				this.activeType = index;
				
				try {
					// 根据分类ID过滤菜品
					if (type === 1) { // 菜品
						this.dishList = this.allDishes.filter(dish => dish.categoryId === id);
					} else { // 套餐
						// 这里可以模拟套餐数据，或者使用同样的测试数据
						this.dishList = this.allDishes.filter(dish => dish.categoryId === id);
					}
				} catch (error) {
					console.error("获取菜品失败", error);
					uni.$showMsg('获取菜品失败，请重试');
					this.dishList = [];
				}
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
				this.totalPrice = (price / 100).toFixed(2);
			},
			
			// 获取特定菜品在购物车中的数量
			getItemCount(dishId) {
				const cartItem = this.cartItems.find(item => item.id === dishId);
				return cartItem ? cartItem.number : 0;
			},
			
			// 添加菜品到购物车
			async addCart(item, event) {
				try {
					// 查找购物车中是否已有该菜品
					const index = this.cartItems.findIndex(cartItem => cartItem.id === item.id);
					
					if (index !== -1) {
						// 已存在，数量+1
						this.cartItems[index].number += 1;
					} else {
						// 不存在，添加新项
						this.cartItems.push({
							id: item.id,
							name: item.name,
							image: item.image,
							price: item.price,
							number: 1
						});
					}
					
					// 保存到本地存储
					this.saveCartToStorage();
					// 重新计算总数和总价
					this.calculateCartTotals();
					
					// 显示提示
					uni.$showMsg('已加入购物车');
				} catch (error) {
					console.error('加入购物车失败', error);
					uni.$showMsg('加入购物车失败，请重试');
				}
			},
			
			// 从购物车减少菜品
			async subtractCart(item) {
				try {
					// 查找购物车中的菜品
					const index = this.cartItems.findIndex(cartItem => cartItem.id === item.id);
					
					if (index !== -1) {
						if (this.cartItems[index].number > 1) {
							// 数量大于1，减少1
							this.cartItems[index].number -= 1;
						} else {
							// 数量为1，移除项
							this.cartItems.splice(index, 1);
						}
						
						// 保存到本地存储
						this.saveCartToStorage();
						// 重新计算总数和总价
						this.calculateCartTotals();
					}
				} catch (error) {
					console.error('减少购物车失败', error);
					uni.$showMsg('操作失败，请重试');
				}
			},
			
			chooseFlavorClick(item) {
				uni.$showMsg('选择规格功能开发中');
			},
			
			submitOrder() {
				if (this.cartCount <= 0) {
					uni.$showMsg('请先选择菜品');
					return;
				}
				uni.navigateTo({
					url: '/pages/addOrder/addOrder'
				});
			},
			
			dishDetails(item) {
				uni.$showMsg('菜品详情功能开发中');
			},
			
			goToCart() {
				if (this.cartCount <= 0) {
					uni.$showMsg('购物车为空');
					return;
				}
				uni.navigateTo({
					url: '/pages/addOrder/addOrder'
				});
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
