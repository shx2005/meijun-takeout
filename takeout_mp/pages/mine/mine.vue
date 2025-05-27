<template>
	<view class="mine-container">
		<!-- 未登录状态 -->
		<view class="user-section" v-if="!loggedIn">
			<view class="login-avatar">
				<image src="/static/images/user.png" mode="aspectFill" class="avatar-image"></image>
			</view>
			<view class="login-buttons">
				<button class="login-btn" @click="goToLogin">登录 / 注册</button>
			</view>
		</view>
		
		<!-- 已登录状态 -->
		<view class="user-section" v-else>
			<view class="user-info-top">
				<image class="avatar" :src="userInfo.avatar || '/static/images/user.png'" mode="aspectFill"></image>
				<view class="user-details">
					<view class="username">{{ userInfo.username || '用户' }}</view>
					<view class="phone">{{ userInfo.phone || '未绑定手机号' }}</view>
				</view>
				<view class="settings-icon" @click="goToSettings">
					<image src="/static/images/settings.png" mode="aspectFill"></image>
				</view>
			</view>
		</view>
		
		<!-- 订单状态区 -->
		<view class="order-status-section">
			<view class="section-title">我的订单</view>
			<view class="order-tabs">
				<view class="order-tab-item" @click="goToOrder(1)">
					<image src="/static/images/order-pay.png" mode="aspectFit"></image>
					<text>待支付</text>
				</view>
				<view class="order-tab-item" @click="goToOrder(2)">
					<image src="/static/images/order-delivery.png" mode="aspectFit"></image>
					<text>待配送</text>
				</view>
				<view class="order-tab-item" @click="goToOrder(3)">
					<image src="/static/images/order-receive.png" mode="aspectFit"></image>
					<text>待收货</text>
				</view>
				<view class="order-tab-item" @click="goToOrder(0)">
					<image src="/static/images/order-all.png" mode="aspectFit"></image>
					<text>全部订单</text>
				</view>
			</view>
		</view>
		
		<!-- 功能菜单 -->
		<view class="function-section">
			<view class="function-item" @click="goToAddressManage">
				<view class="function-icon">
					<image src="/static/images/address.png" mode="aspectFit"></image>
				</view>
				<view class="function-name">地址管理</view>
				<view class="function-arrow">></view>
			</view>
			
			<view class="function-item" @click="contactService">
				<view class="function-icon">
					<image src="/static/images/service.png" mode="aspectFit"></image>
				</view>
				<view class="function-name">联系客服</view>
				<view class="function-arrow">></view>
			</view>
			
			<view class="function-item" @click="goToAbout">
				<view class="function-icon">
					<image src="/static/images/about.png" mode="aspectFit"></image>
				</view>
				<view class="function-name">关于我们</view>
				<view class="function-arrow">></view>
			</view>
			
			<view class="function-item logout" v-if="loggedIn" @click="handleLogout">
				<view class="function-icon">
					<image src="/static/images/logout.png" mode="aspectFit"></image>
				</view>
				<view class="function-name">退出登录</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { getUserInfoApi, logoutApi } from '../../api/index.js';
	
	export default {
		data() {
			return {
				loggedIn: false,
				userInfo: {},
			}
		},
		onLoad() {
			// 页面加载时检查登录状态
			this.checkLoginStatus();
		},
		onShow() {
			// 每次页面显示时更新登录状态
			this.checkLoginStatus();
		},
		methods: {
			// 检查登录状态
			async checkLoginStatus() {
				const token = uni.getStorageSync('token');
				if (token) {
					this.loggedIn = true;
					// 尝试获取用户信息
					await this.getUserInfo();
				} else {
					this.loggedIn = false;
					this.userInfo = {};
				}
			},
			
			// 获取用户信息
			async getUserInfo() {
				try {
					const res = await getUserInfoApi();
					if (res && res.code === 0 && res.data) {
						this.userInfo = res.data;
					}
				} catch (error) {
					console.error('获取用户信息失败:', error);
				}
			},
			
			// 去登录页
			goToLogin() {
				uni.navigateTo({
					url: '/pages/login/login'
				});
			},
			
			// 去设置页
			goToSettings() {
				uni.navigateTo({
					url: '/pages/setting/setting'
				});
			},
			
			// 去订单页
			goToOrder(status) {
				if (!this.loggedIn) {
					return this.goToLogin();
				}
				uni.navigateTo({
					url: `/pages/order/order?status=${status}`
				});
			},
			
			// 去地址管理页
			goToAddressManage() {
				if (!this.loggedIn) {
					return this.goToLogin();
				}
				uni.navigateTo({
					url: '/pages/address/address'
				});
			},
			
			// 联系客服
			contactService() {
				// 可以是拨打电话或者跳转到客服页面
				uni.showModal({
					title: '联系客服',
					content: '客服电话: 400-123-4567',
					confirmText: '拨打',
					success: (res) => {
						if (res.confirm) {
							uni.makePhoneCall({
								phoneNumber: '4001234567'
							});
						}
					}
				});
			},
			
			// 关于我们
			goToAbout() {
				uni.navigateTo({
					url: '/pages/about/about'
				});
			},
			
			// 处理退出登录点击
			handleLogout() {
				uni.showModal({
					title: '提示',
					content: '确定要退出登录吗？',
					success: (res) => {
						if (res.confirm) {
							this.logout();
						}
					}
				});
			},
			
			// 退出登录
			async logout() {
				try {
					uni.showLoading({
						title: '退出登录中...'
					});
					
					// 调用退出登录API
					await logoutApi();
					
					// 清除用户信息和token
					uni.removeStorageSync('token');
					uni.removeStorageSync('originalToken');
					uni.removeStorageSync('userId');
					
					// 清除菜品相关的缓存数据，以便下次登录时获取最新数据
					uni.removeStorageSync('categoryData');
					uni.removeStorageSync('dishData');
					
					// 重置个人中心页面数据
					this.userInfo = {};
					this.loggedIn = false;
					
					uni.hideLoading();
					uni.showToast({
						title: '已退出登录',
						icon: 'success'
					});
					
				} catch (error) {
					console.error('退出登录失败:', error);
					uni.hideLoading();
					uni.showToast({
						title: '退出失败，请重试',
						icon: 'none'
					});
				}
			}
		}
	}
</script>

<style scoped>
.mine-container {
	background-color: #f5f5f5;
	min-height: 100vh;
}

.user-section {
	background: linear-gradient(135deg, #ffb300, #ffd54f);
	padding: 30rpx;
	border-radius: 0 0 30rpx 30rpx;
}

/* 未登录状态样式 */
.login-avatar {
	display: flex;
	justify-content: center;
	margin: 30rpx 0;
}

.avatar-image {
	width: 160rpx;
	height: 160rpx;
	border-radius: 80rpx;
	background-color: #ffffff;
}

.login-buttons {
	display: flex;
	justify-content: center;
	margin: 30rpx 0;
}

.login-btn {
	background-color: #ffffff;
	color: #ffb300;
	font-size: 30rpx;
	padding: 10rpx 40rpx;
	border-radius: 40rpx;
	border: none;
}

/* 已登录状态样式 */
.user-info-top {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
	margin-right: 30rpx;
	background-color: #ffffff;
}

.user-details {
	flex: 1;
}

.username {
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 10rpx;
}

.phone {
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.8);
}

.settings-icon {
	padding: 10rpx;
}

.settings-icon image {
	width: 40rpx;
	height: 40rpx;
}

/* 订单状态样式 */
.order-status-section {
	background-color: #ffffff;
	margin: 30rpx;
	border-radius: 20rpx;
	padding: 30rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	margin-bottom: 30rpx;
}

.order-tabs {
	display: flex;
	justify-content: space-between;
}

.order-tab-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	flex: 1;
}

.order-tab-item image {
	width: 60rpx;
	height: 60rpx;
	margin-bottom: 10rpx;
}

.order-tab-item text {
	font-size: 26rpx;
	color: #666;
}

/* 功能菜单样式 */
.function-section {
	background-color: #ffffff;
	margin: 30rpx;
	border-radius: 20rpx;
}

.function-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.function-icon {
	width: 50rpx;
	height: 50rpx;
	margin-right: 20rpx;
}

.function-icon image {
	width: 100%;
	height: 100%;
}

.function-name {
	flex: 1;
	font-size: 30rpx;
	color: #333;
}

.function-arrow {
	color: #ccc;
	font-size: 30rpx;
}

.logout {
	border-top: 20rpx solid #f5f5f5;
}

.logout .function-name {
	color: #ff5252;
}
</style> 