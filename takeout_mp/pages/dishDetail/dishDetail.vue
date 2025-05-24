<template>
	<view class="dish-detail-container">
		<!-- 返回按钮 -->
		<view class="back-btn" @tap="goBack">
			<u-icon name="arrow-left" color="#fff" size="40"></u-icon>
		</view>
		
		<!-- 菜品图片 -->
		<view class="dish-image-wrap">
			<image class="dish-image" :src="dishInfo.imageUrl" mode="aspectFill"></image>
		</view>
		
		<!-- 菜品信息 -->
		<view class="dish-info-card">
			<view class="dish-name">{{ dishInfo.name }}</view>
			<view class="dish-stats">
				<text class="sold">月售 {{ dishInfo.sales }}</text>
				<text class="rating">好评率 {{ dishInfo.favorRate || '98' }}%</text>
			</view>
			<view class="dish-price">
				<text class="price">￥{{ (dishInfo.price / 100).toFixed(2) }}</text>
				<text class="original-price" v-if="dishInfo.originalPrice">￥{{ (dishInfo.originalPrice / 100).toFixed(2) }}</text>
			</view>
		</view>
		
		<!-- 菜品口味选择 -->
		<view class="section-card" v-if="dishInfo.flavors && dishInfo.flavors.length > 0">
			<view class="section-title">口味选择</view>
			<view class="flavor-list">
				<view 
					v-for="(item, index) in dishInfo.flavors" 
					:key="index"
					:class="['flavor-item', { active: selectedFlavor === item }]"
					@tap="selectFlavor(item)"
				>
					{{ item }}
				</view>
			</view>
		</view>
		
		<!-- 菜品详情 -->
		<view class="section-card">
			<view class="section-title">菜品详情</view>
			<view class="detail-info">
				<view class="detail-item">
					<text class="label">菜品简介</text>
					<text class="content">{{ dishInfo.description || '暂无简介' }}</text>
				</view>
				<view class="detail-item">
					<text class="label">推荐指数</text>
					<view class="stars">
						<u-icon 
							v-for="i in 5" 
							:key="i" 
							name="star-fill" 
							:color="i <= dishInfo.stars ? '#ff9500' : '#ddd'" 
							size="36"
						></u-icon>
					</view>
				</view>
				<view class="detail-item">
					<text class="label">材料</text>
					<text class="content">{{ dishInfo.ingredients || '暂无信息' }}</text>
				</view>
			</view>
		</view>
		
		<!-- 评价 -->
		<view class="section-card">
			<view class="section-title">
				<text>用户评价</text>
				<text class="view-all" @tap="viewAllComments">查看全部</text>
			</view>
			<view class="comments-list" v-if="dishInfo.comments && dishInfo.comments.length > 0">
				<view class="comment-item" v-for="(item, index) in dishInfo.comments.slice(0, 2)" :key="index">
					<view class="user-info">
						<image class="avatar" :src="item.avatar || 'https://img.yzcdn.cn/vant/cat.jpeg'" mode="aspectFill"></image>
						<view class="user-detail">
							<text class="username">{{ item.username }}</text>
							<text class="comment-time">{{ item.time }}</text>
						</view>
					</view>
					<view class="comment-content">{{ item.content }}</view>
				</view>
			</view>
			<view class="empty-comment" v-else>
				<u-empty mode="comment" text="暂无评价"></u-empty>
			</view>
		</view>
		
		<!-- 推荐菜品 -->
		<view class="section-card">
			<view class="section-title">推荐菜品</view>
			<scroll-view scroll-x class="recommend-scroll" show-scrollbar="false">
				<view class="recommend-list">
					<view 
						class="recommend-item" 
						v-for="(item, index) in recommendDishes" 
						:key="index"
						@tap="goToDishDetail(item)"
					>
						<image class="recommend-image" :src="item.imageUrl" mode="aspectFill"></image>
						<text class="recommend-name">{{ item.name }}</text>
						<text class="recommend-price">￥{{ (item.price / 100).toFixed(2) }}</text>
					</view>
				</view>
			</scroll-view>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<view class="cart-btn" @tap="goToCart">
				<u-icon name="shopping-cart" size="40" color="#333"></u-icon>
				<view class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</view>
			</view>
			<view class="order-btns">
				<button class="add-to-cart-btn" @tap="addToCart">加入购物车</button>
				<button class="buy-now-btn" @tap="buyNow">立即购买</button>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				dishId: '',
				dishInfo: {
					id: '',
					name: '加载中...',
					price: 0,
					sales: 0,
					imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
					description: '',
					stars: 5,
					ingredients: '',
					flavors: ['不辣', '微辣', '中辣', '特辣'],
					comments: []
				},
				selectedFlavor: '',
				cartCount: 2,
				recommendDishes: [
					{
						id: '101',
						name: '黄焖鸡米饭',
						price: 1680,
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg'
					},
					{
						id: '102',
						name: '宫保鸡丁',
						price: 2280,
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg'
					},
					{
						id: '103',
						name: '鱼香肉丝',
						price: 1980,
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg'
					},
					{
						id: '104',
						name: '麻婆豆腐',
						price: 1480,
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg'
					}
				]
			}
		},
		onLoad(options) {
			if (options.id) {
				this.dishId = options.id;
				this.getDishDetail();
			} else {
				uni.showToast({
					title: '菜品ID无效',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateBack();
				}, 1500);
			}
		},
		methods: {
			// 获取菜品详情
			getDishDetail() {
				// 这里是模拟数据，实际项目中应该调用API获取
				setTimeout(() => {
					// 使用测试数据
					const testData = {
						id: this.dishId,
						name: '宫保鸡丁',
						price: 2280,
						originalPrice: 2880,
						sales: 498,
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
						description: '宫保鸡丁，是一道闻名中外的特色传统名菜。鲁菜、川菜、贵州菜中都有这道菜，但各地的做法和味道有所不同。',
						ingredients: '鸡胸肉、黄瓜、胡萝卜、花生米、干辣椒、花椒、姜、蒜等',
						stars: 5,
						favorRate: 98,
						flavors: ['不辣', '微辣', '中辣', '特辣'],
						comments: [
							{
								username: '用户1001',
								avatar: 'https://img.yzcdn.cn/vant/cat.jpeg',
								content: '超级好吃，辣度刚刚好！下次还会再来点！',
								time: '2024-06-01 12:30'
							},
							{
								username: '用户1002',
								avatar: 'https://img.yzcdn.cn/vant/cat.jpeg',
								content: '份量足，味道佳，下次会再来的！',
								time: '2024-05-30 18:45'
							}
						]
					};
					
					this.dishInfo = testData;
					// 默认选择第一个口味
					if (this.dishInfo.flavors && this.dishInfo.flavors.length > 0) {
						this.selectedFlavor = this.dishInfo.flavors[0];
					}
				}, 500);
			},
			
			// 返回上一页
			goBack() {
				uni.navigateBack();
			},
			
			// 选择口味
			selectFlavor(flavor) {
				this.selectedFlavor = flavor;
			},
			
			// 查看全部评价
			viewAllComments() {
				uni.navigateTo({
					url: `/pages/comments/comments?dishId=${this.dishId}`
				});
			},
			
			// 查看其他菜品详情
			goToDishDetail(item) {
				if (item.id === this.dishId) return; // 如果是当前菜品，则不跳转
				
				uni.redirectTo({
					url: `/pages/dishDetail/dishDetail?id=${item.id}`
				});
			},
			
			// 前往购物车
			goToCart() {
				uni.switchTab({
					url: '/pages/index/index'
				});
			},
			
			// 加入购物车
			addToCart() {
				// 这里是模拟功能，实际项目中应该调用API
				this.cartCount++;
				uni.showToast({
					title: '已加入购物车',
					icon: 'success'
				});
			},
			
			// 立即购买
			buyNow() {
				// 这里是模拟功能，实际项目中应该跳转到结算页面
				uni.navigateTo({
					url: '/pages/addOrder/addOrder'
				});
			}
		}
	}
</script>

<style>
	.dish-detail-container {
		background-color: #f8f8f8;
		min-height: 100vh;
		padding-bottom: 120rpx; /* 为底部操作栏留出空间 */
	}
	
	.back-btn {
		position: fixed;
		left: 30rpx;
		top: 60rpx;
		width: 80rpx;
		height: 80rpx;
		background-color: rgba(0, 0, 0, 0.3);
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
		z-index: 100;
	}
	
	.dish-image-wrap {
		width: 100%;
		height: 750rpx;
	}
	
	.dish-image {
		width: 100%;
		height: 100%;
	}
	
	.dish-info-card {
		margin: -60rpx 30rpx 0;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 16rpx;
		position: relative;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
	}
	
	.dish-name {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 16rpx;
	}
	
	.dish-stats {
		display: flex;
		font-size: 26rpx;
		color: #999;
		margin-bottom: 16rpx;
	}
	
	.sold {
		margin-right: 30rpx;
	}
	
	.dish-price {
		display: flex;
		align-items: center;
	}
	
	.price {
		font-size: 40rpx;
		font-weight: bold;
		color: #ff5050;
		margin-right: 16rpx;
	}
	
	.original-price {
		font-size: 28rpx;
		color: #999;
		text-decoration: line-through;
	}
	
	.section-card {
		margin: 30rpx;
		padding: 30rpx;
		background-color: #fff;
		border-radius: 16rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
	}
	
	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
		display: flex;
		justify-content: space-between;
	}
	
	.view-all {
		font-size: 28rpx;
		color: #ff9500;
		font-weight: normal;
	}
	
	.flavor-list {
		display: flex;
		flex-wrap: wrap;
	}
	
	.flavor-item {
		min-width: 120rpx;
		height: 70rpx;
		line-height: 70rpx;
		text-align: center;
		background-color: #f5f5f5;
		border-radius: 35rpx;
		font-size: 28rpx;
		color: #333;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
		padding: 0 24rpx;
	}
	
	.flavor-item.active {
		background-color: #fff7e6;
		color: #ff9500;
		border: 1px solid #ff9500;
	}
	
	.detail-info {
		padding: 10rpx 0;
	}
	
	.detail-item {
		margin-bottom: 24rpx;
	}
	
	.detail-item:last-child {
		margin-bottom: 0;
	}
	
	.label {
		font-size: 28rpx;
		color: #999;
		margin-bottom: 10rpx;
		display: block;
	}
	
	.content {
		font-size: 30rpx;
		color: #333;
		line-height: 1.6;
	}
	
	.stars {
		display: flex;
	}
	
	.comments-list {
		padding: 10rpx 0;
	}
	
	.comment-item {
		margin-bottom: 24rpx;
		padding-bottom: 24rpx;
		border-bottom: 1px solid #f2f2f2;
	}
	
	.comment-item:last-child {
		margin-bottom: 0;
		padding-bottom: 0;
		border-bottom: none;
	}
	
	.user-info {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
	}
	
	.avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		margin-right: 16rpx;
	}
	
	.user-detail {
		flex: 1;
	}
	
	.username {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 6rpx;
		display: block;
	}
	
	.comment-time {
		font-size: 24rpx;
		color: #999;
	}
	
	.comment-content {
		font-size: 28rpx;
		color: #333;
		line-height: 1.6;
	}
	
	.empty-comment {
		padding: 40rpx 0;
	}
	
	.recommend-scroll {
		width: 100%;
	}
	
	.recommend-list {
		display: flex;
		padding: 10rpx 0;
	}
	
	.recommend-item {
		width: 200rpx;
		margin-right: 24rpx;
	}
	
	.recommend-image {
		width: 200rpx;
		height: 200rpx;
		border-radius: 8rpx;
		margin-bottom: 10rpx;
	}
	
	.recommend-name {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 6rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		display: block;
	}
	
	.recommend-price {
		font-size: 28rpx;
		color: #ff5050;
		font-weight: bold;
	}
	
	.bottom-bar {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		height: 100rpx;
		background-color: #fff;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
		z-index: 100;
	}
	
	.cart-btn {
		width: 100rpx;
		height: 60rpx;
		position: relative;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.cart-badge {
		position: absolute;
		top: -10rpx;
		right: 10rpx;
		background-color: #ff5050;
		color: #fff;
		font-size: 20rpx;
		width: 36rpx;
		height: 36rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.order-btns {
		flex: 1;
		display: flex;
		justify-content: space-between;
	}
	
	.add-to-cart-btn, .buy-now-btn {
		height: 80rpx;
		line-height: 80rpx;
		border-radius: 40rpx;
		text-align: center;
		font-size: 28rpx;
		margin: 0 10rpx;
	}
	
	.add-to-cart-btn {
		background-color: #fff7e6;
		color: #ff9500;
		flex: 1;
		border: 1px solid #ff9500;
	}
	
	.buy-now-btn {
		background-color: #ff9500;
		color: #fff;
		flex: 1;
		border: none;
	}
</style> 