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
			
			<view class="form-item">
				<view class="radio-group">
					<view class="radio-item" @click="userType = '1'">
						<view class="radio-button" :class="{'checked': userType === '1'}"></view>
						<text class="radio-label">商家账号</text>
					</view>
					<view class="radio-item" @click="userType = '2'">
						<view class="radio-button" :class="{'checked': userType === '2'}"></view>
						<text class="radio-label">员工账号</text>
					</view>
				</view>
			</view>
			
			<button class="login-btn" @click="handleLogin" :disabled="loading">
				{{ loading ? '登录中...' : '确认登录' }}
			</button>
			
			<view class="debug-section">
				<view class="debug-toggle" @click="isDebugMode = !isDebugMode">
					{{ isDebugMode ? '关闭调试' : '开启调试' }}
				</view>
				
				<view class="debug-notice" v-if="isDebugMode">
					<text>调试模式已开启，点击下面按钮快速登录</text>
					<!-- 快捷登录 -->
					<!-- 已取消快捷登录功能 -->
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import { merchantLoginApi, employeeLoginApi } from '@/api/merchant';
	
	export default {
		data() {
			return {
				username: '',
				password: '',
				userType: '1', // 1: 商家, 2: 员工
				loading: false,
				isDebugMode: true // 默认启用调试模式
			}
		},
		methods: {
			validateInput() {
				if (!this.username) {
					uni.showToast({
						title: '请输入用户名',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.password) {
					uni.showToast({
						title: '请输入密码',
						icon: 'none'
					});
					return false;
				}
				
				return true;
			},
			
			async handleLogin() {
				if (!this.validateInput()) return;
				
				this.loading = true;
				
				try {
					let loginData;
					let res;
					
					// 根据用户类型选择不同的登录逻辑
					if (this.userType === '1') {
						// 商家登录：使用输入的用户名密码
						loginData = {
							username: this.username,
							password: this.password
						};
						res = await merchantLoginApi(loginData);
					} else {
						// 员工登录：分两步
						// 第一步：验证员工账号
						console.log('第一步：验证员工账号:', this.username);
						
						const employeeLoginData = {
							username: this.username,
							password: this.password
						};
						
						try {
							const employeeRes = await employeeLoginApi(employeeLoginData);
							console.log('员工验证响应:', employeeRes);
							
							// 检查员工验证是否成功
							if (employeeRes && (employeeRes.code === 0 || employeeRes.code === 200)) {
								console.log('员工验证成功，开始使用merchant1账号登录');
								
								// 第二步：使用merchant1账号进行商家登录
								const merchantLoginData = {
									username: 'merchant1',
									password: '123456'
								};
								
								res = await merchantLoginApi(merchantLoginData);
								console.log('merchant1登录响应:', res);
								
							} else {
								// 员工验证失败
								throw new Error(employeeRes?.msg || '员工账号验证失败');
							}
						} catch (employeeError) {
							console.error('员工账号验证失败:', employeeError);
							throw new Error('员工账号验证失败，请检查用户名和密码');
						}
					}
					
					console.log('最终登录响应:', res);
					
					if (res && (res.code === 0 || res.code === 200) && res.data) {
						// 获取token信息（都使用merchant的token）
						const token = res.data.token || res.token;
						
						if (token) {
							// 保存商家token（员工也使用商家token）
							uni.setStorageSync('merchantToken', token);
							
							// 员工登录后也完全使用merchant身份
							uni.setStorageSync('merchantUserType', 'merchant');
							
							// 保存商家信息（都使用merchant1的信息）
							const merchantInfo = res.data.merchantInfo || {
								id: res.data.id || res.id,
								name: res.data.name || res.name,
								username: res.data.username || res.username,
								uuid: res.data.uuid || res.uuid
							};
							
							// 如果是员工登录，只在merchantInfo中记录员工信息，但身份完全是merchant
							if (this.userType === '2') {
								merchantInfo.employeeName = this.username; // 记录员工姓名
								merchantInfo.isEmployee = true; // 标记为员工登录
								// 但userType、token、权限都是merchant的
							}
							
							uni.setStorageSync('merchantInfo', JSON.stringify(merchantInfo));
							
							const loginTypeText = this.userType === '1' ? '商家' : '员工';
							uni.showToast({
								title: `${loginTypeText}登录成功`,
								icon: 'success'
							});
							
							// 跳转到商家主页
							setTimeout(() => {
								uni.navigateTo({
									url: '/pages/merchantHome/merchantHome'
								});
							}, 1000);
							return;
						}
					}
					
					// 如果执行到这里，说明登录失败
					const errorMsg = this.userType === '1' ? 
						(res?.msg || '商家登录失败') : 
						'登录失败，请稍后重试';
					
					uni.showToast({
						title: errorMsg,
						icon: 'none'
					});
				} catch (error) {
					console.error('登录错误:', error);
					
					const errorMsg = this.userType === '1' ? 
						'商家登录失败，请稍后重试' : 
						error.message || '员工登录失败，请稍后重试';
					
					uni.showToast({
						title: errorMsg,
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},
			
			// 调试模式：快速登录
			quickLogin() {
				// 模拟商家信息
				const mockMerchantInfo = {
					id: 1,
					name: '测试商家',
					username: this.username || '测试商家',
					address: '上海市浦东新区张江高科技园区',
					phone: '17344402976',
					identity: this.userType === '1' ? 'MERCHANT' : 'EMPLOYEE'
				};
				
				// 模拟token
				const mockToken = 'mock_token_' + Date.now();
				
				// 保存到本地存储
				uni.setStorageSync('merchantToken', mockToken);
				uni.setStorageSync('merchantUserType', this.userType);
				uni.setStorageSync('merchantInfo', JSON.stringify(mockMerchantInfo));
				
				uni.showToast({
					title: '测试登录成功',
					icon: 'success'
				});
				
				// 跳转到商家主页
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/merchantHome/merchantHome'
					});
				}, 1000);
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
			
			.radio-group {
				display: flex;
				margin-top: 10rpx;
				
				.radio-item {
					display: flex;
					align-items: center;
					margin-right: 60rpx;
					
					.radio-button {
						width: 36rpx;
						height: 36rpx;
						border-radius: 50%;
						border: 2rpx solid #ddd;
						margin-right: 10rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						
						&.checked {
							border-color: #FF8C00;
							background-color: #fff;
							position: relative;
							
							&::after {
								content: '';
								width: 24rpx;
								height: 24rpx;
								border-radius: 50%;
								background-color: #FF8C00;
								position: absolute;
							}
						}
					}
					
					.radio-label {
						font-size: 28rpx;
						color: #333;
					}
				}
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
			
			&:disabled {
				opacity: 0.6;
			}
		}
		
		.debug-section {
			margin-top: 30rpx;
			
			.debug-toggle {
				text-align: center;
				font-size: 24rpx;
				color: #999;
				padding: 10rpx;
			}
			
			.debug-notice {
				margin-top: 20rpx;
				text-align: center;
				
				text {
					display: block;
					font-size: 24rpx;
					color: #999;
					margin-bottom: 15rpx;
				}
				
				.debug-btn {
					width: 80%;
					height: 70rpx;
					background-color: #ddd;
					color: #666;
					font-size: 26rpx;
					border-radius: 35rpx;
					margin: 0 auto;
					display: flex;
					align-items: center;
					justify-content: center;
				}
			}
		}
	}
</style> 