<template>
	<view class="merchant-login-container">
		<view class="login-header">
			<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
			<text class="title">商家管理系统</text>
		</view>
		
		<view class="login-form">
			<view class="form-item">
				<text class="label">用户名</text>
				<input type="text" v-model="username" placeholder="请输入商家用户名" class="input" />
			</view>
			
			<view class="form-item">
				<text class="label">密码</text>
				<input type="password" v-model="password" placeholder="请输入商家密码" class="input" />
			</view>
			
			<button class="login-btn" @click="handleLogin">确认登录</button>
			
			<view class="debug-notice">
				<text>调试模式：无需验证即可登录</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				username: '',
				password: ''
			}
		},
		methods: {
			handleLogin() {
				// 验证输入
				if (!this.username) {
					uni.showToast({
						title: '请输入用户名',
						icon: 'none'
					});
					return;
				}
				
				if (!this.password) {
					uni.showToast({
						title: '请输入密码',
						icon: 'none'
					});
					return;
				}
				
				// 调试模式：直接跳转到商家主页，无需验证
				uni.navigateTo({
					url: '/pages/merchantHome/merchantHome'
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.merchant-login-container {
		padding: 40rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		height: 100vh;
		background-color: #f5f5f5;
	}
	
	.login-header {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-top: 80rpx;
		margin-bottom: 80rpx;
		
		.logo {
			width: 160rpx;
			height: 160rpx;
			margin-bottom: 30rpx;
		}
		
		.title {
			font-size: 40rpx;
			font-weight: bold;
			color: #333;
		}
	}
	
	.login-form {
		width: 100%;
		background-color: #fff;
		border-radius: 12rpx;
		padding: 40rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
		
		.form-item {
			margin-bottom: 40rpx;
			
			.label {
				display: block;
				font-size: 28rpx;
				color: #333;
				margin-bottom: 20rpx;
			}
			
			.input {
				width: 100%;
				height: 90rpx;
				border: 2rpx solid #eee;
				border-radius: 8rpx;
				padding: 0 20rpx;
				font-size: 28rpx;
			}
		}
		
		.login-btn {
			width: 100%;
			height: 90rpx;
			background-color: #FF8C00;
			color: #fff;
			font-size: 32rpx;
			border-radius: 45rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-top: 60rpx;
		}
		
		.debug-notice {
			margin-top: 30rpx;
			text-align: center;
			
			text {
				font-size: 24rpx;
				color: #999;
			}
		}
	}
</style> 