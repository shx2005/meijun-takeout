<template>
	<view class="category-container">
		<view class="header">
			<view class="back-btn" @tap="goBack">
				<u-icon name="arrow-left" size="40" color="#333"></u-icon>
			</view>
			<view class="title">{{ categoryName }}</view>
		</view>
		
		<view class="dishes-list">
			<view
				v-for="(item, index) in dishesList"
				:key="index"
				class="dish-item"
				@tap="goToDishDetail(item)"
			>
				<image class="dish-image" :src="item.imageUrl" mode="aspectFill"></image>
				<view class="dish-info">
					<text class="dish-name">{{ item.name }}</text>
					<text class="dish-desc">{{ item.description }}</text>
					<text class="dish-sold">已售 {{ item.sales }}</text>
					<view class="dish-bottom">
						<text class="dish-price">￥{{ (item.price / 100).toFixed(2) }}</text>
						<view class="add-btn" @tap.stop="addToCart(item)">
							<u-icon name="plus" color="#fff" size="24"></u-icon>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-if="dishesList.length === 0">
			<u-empty
				mode="data"
				icon="http://cdn.uviewui.com/uview/empty/data.png"
				text="暂无菜品"
			></u-empty>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				categoryId: '',
				categoryName: '分类菜品',
				dishesList: [
					{
						id: 201,
						name: '麻婆豆腐',
						description: '麻辣鲜香，入口即化',
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
						price: 1880,
						sales: 456
					},
					{
						id: 202,
						name: '回锅肉',
						description: '肥而不腻，香气四溢',
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
						price: 2580,
						sales: 389
					},
					{
						id: 203,
						name: '水煮肉片',
						description: '麻辣鲜香，肉质鲜嫩',
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
						price: 2880,
						sales: 321
					},
					{
						id: 204,
						name: '糖醋里脊',
						description: '外酥里嫩，酸甜可口',
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
						price: 2680,
						sales: 287
					},
					{
						id: 205,
						name: '宫保鸡丁',
						description: '酥香爽辣，经典川菜',
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
						price: 2280,
						sales: 498
					},
					{
						id: 206,
						name: '鱼香肉丝',
						description: '开胃下饭，家常菜代表',
						imageUrl: 'https://img.yzcdn.cn/vant/cat.jpeg',
						price: 1980,
						sales: 452
					}
				]
			};
		},
		onLoad(options) {
			if (options.id) {
				this.categoryId = options.id;
				
				// 如果传入了分类名称，则使用传入的名称
				if (options.name) {
					this.categoryName = options.name;
				}
				
				// 这里可以根据分类ID加载对应的菜品列表
				// this.loadDishesByCategory();
			}
		},
		methods: {
			// 加载分类菜品
			loadDishesByCategory() {
				// 实际项目中应该调用API获取菜品列表
				// 这里使用模拟数据
				console.log('加载分类' + this.categoryId + '的菜品');
			},
			
			// 返回上一页
			goBack() {
				uni.navigateBack();
			},
			
			// 跳转到菜品详情页
			goToDishDetail(item) {
				uni.navigateTo({
					url: `/pages/dishDetail/dishDetail?id=${item.id}`
				});
			},
			
			// 添加到购物车
			addToCart(item) {
				// 实际项目中应该调用添加购物车的方法
				uni.showToast({
					title: '已添加到购物车',
					icon: 'success'
				});
			}
		}
	}
</script>

<style>
	.category-container {
		padding-top: 100rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}
	
	.header {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		height: 88rpx;
		background-color: #fff;
		display: flex;
		align-items: center;
		padding: 0 30rpx;
		z-index: 100;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.back-btn {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.title {
		flex: 1;
		text-align: center;
		font-size: 32rpx;
		color: #333;
		font-weight: bold;
		margin-right: 60rpx; /* 为了标题居中 */
	}
	
	.dishes-list {
		padding: 20rpx;
	}
	
	.dish-item {
		display: flex;
		padding: 24rpx;
		background-color: #fff;
		margin-bottom: 20rpx;
		border-radius: 12rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.dish-image {
		width: 180rpx;
		height: 180rpx;
		border-radius: 8rpx;
	}
	
	.dish-info {
		flex: 1;
		margin-left: 20rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}
	
	.dish-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}
	
	.dish-desc {
		font-size: 26rpx;
		color: #999;
		margin-bottom: 10rpx;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}
	
	.dish-sold {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 10rpx;
	}
	
	.dish-bottom {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}
	
	.dish-price {
		font-size: 32rpx;
		color: #ff5050;
		font-weight: bold;
	}
	
	.add-btn {
		width: 50rpx;
		height: 50rpx;
		background-color: #ff9500;
		border-radius: 50%;
		display: flex;
		justify-content: center;
		align-items: center;
	}
	
	.empty-state {
		padding-top: 200rpx;
	}
</style> 