<template>
	<view class="register-container">
		<view class="register-header">
			<view class="title">注册账号</view>
			<view class="subtitle">欢迎加入美食外卖</view>
		</view>
		
		<view class="register-form">
			<view class="form-item">
				<input 
					type="number" 
					v-model="phone" 
					placeholder="请输入手机号"
					placeholder-style="color: #999;"
					maxlength="11"
				/>
			</view>
			
			<view class="form-item">
				<input 
					type="password" 
					v-model="password" 
					placeholder="请输入密码"
					placeholder-style="color: #999;"
				/>
			</view>
			
			<view class="form-item">
				<input 
					type="password" 
					v-model="confirmPassword" 
					placeholder="请确认密码"
					placeholder-style="color: #999;"
				/>
			</view>
			
			<button class="register-btn" @click="handleRegister" :disabled="isLoading">
				{{ isLoading ? '注册中...' : '注册' }}
			</button>
			
			<view class="login-link" @click="goToLogin">
				已有账号？去登录
			</view>
		</view>
	</view>
</template>

<script>
import { registerApi } from '@/api/index.js'

export default {
	data() {
		return {
			phone: '',
			password: '',
			confirmPassword: '',
			isLoading: false
		}
	},
	methods: {
		// 处理注册
		async handleRegister() {
			// 表单验证
			if (!this.phone) {
				uni.$showMsg('请输入手机号');
				return;
			}
			
			if (!/^1\d{10}$/.test(this.phone)) {
				uni.$showMsg('手机号格式不正确');
				return;
			}
			
			if (!this.password) {
				uni.$showMsg('请输入密码');
				return;
			}
			
			if (this.password.length < 6) {
				uni.$showMsg('密码长度不能少于6位');
				return;
			}
			
			if (this.password !== this.confirmPassword) {
				uni.$showMsg('两次输入的密码不一致');
				return;
			}
			
			this.isLoading = true;
			
			try {
				const registerParams = {
					username: this.phone,
					password: this.password,
					identity: 3,
					openid: this.phone
				};
				console.log('注册参数:', registerParams);
				const result = await registerApi(registerParams);
				console.log('注册结果:', result);
				if (result && result.code === 0) {
					uni.$showMsg('注册成功', 'success');
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				} else {
					let errorMsg = '注册失败，请重试';
					if (result && result.msg) {
						errorMsg = result.msg;
					} else if (result && result.data && typeof result.data === 'string') {
						errorMsg = result.data;
					}
					uni.showModal({
						title: '注册失败',
						content: errorMsg,
						showCancel: false
					});
				}
			} catch (error) {
				console.error('注册失败:', error);
				let errorMsg = '注册失败，请重试';
				if (error.message) {
					errorMsg = error.message;
				} else if (error.errMsg) {
					errorMsg = error.errMsg;
				}
				uni.showModal({
					title: '注册异常',
					content: errorMsg,
					showCancel: false
				});
			} finally {
				this.isLoading = false;
			}
		},
		
		// 跳转到登录页
		goToLogin() {
			uni.navigateBack();
		}
	}
}
</script>

<style lang="scss" scoped>
.register-container {
	min-height: 100vh;
	background-color: #fff;
	padding: 40rpx;
}

.register-header {
	margin-top: 60rpx;
	margin-bottom: 80rpx;
	
	.title {
		font-size: 48rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
	
	.subtitle {
		font-size: 28rpx;
		color: #999;
	}
}

.register-form {
	.form-item {
		margin-bottom: 40rpx;
		
		input {
			width: 100%;
			height: 90rpx;
			border-bottom: 2rpx solid #eee;
			font-size: 32rpx;
			color: #333;
			padding: 0 20rpx;
		}
	}
	
	.register-btn {
		width: 100%;
		height: 90rpx;
		background: #feca50;
		color: #333;
		font-size: 32rpx;
		border-radius: 45rpx;
		margin-top: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		
		&:active {
			opacity: 0.8;
		}
		
		&[disabled] {
			opacity: 0.6;
			background: #ccc;
		}
	}
	
	.login-link {
		text-align: center;
		font-size: 28rpx;
		color: #666;
		margin-top: 40rpx;
		
		&:active {
			opacity: 0.8;
		}
	}
}
</style> 