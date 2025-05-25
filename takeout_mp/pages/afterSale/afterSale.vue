<template>
	<view class="after-sale">
		<view class="form-card">
			<view class="form-title">申请售后</view>
			
			<!-- 退款金额 -->
			<view class="form-item">
				<view class="item-label">
					<text class="required">*</text>
					<text>退款金额</text>
				</view>
				<view class="item-content">
					<text class="amount">￥{{ (orderAmount / 100).toFixed(2) }}</text>
					<text class="amount-desc">最多{{ (orderAmount / 100).toFixed(2) }}元</text>
				</view>
			</view>
			
			<!-- 退款原因 -->
			<view class="form-item">
				<view class="item-label">
					<text class="required">*</text>
					<text>退款原因</text>
				</view>
				<view class="item-content">
					<picker @change="onReasonChange" :value="reasonIndex" :range="reasonList">
						<view class="picker-content">
							<text>{{ reasonList[reasonIndex] }}</text>
							<u-icon name="arrow-right" size="28"></u-icon>
						</view>
					</picker>
				</view>
			</view>
			
			<!-- 问题描述 -->
			<view class="form-item">
				<view class="item-label">
					<text>问题描述</text>
				</view>
				<view class="item-content">
					<textarea 
						class="textarea" 
						v-model="description" 
						placeholder="请详细描述您遇到的问题，以便我们更好地为您服务"
						maxlength="200"
					></textarea>
					<view class="word-count">{{ description.length }}/200</view>
				</view>
			</view>
			
			<!-- 上传图片 -->
			<view class="form-item">
				<view class="item-label">
					<text>上传凭证</text>
				</view>
				<view class="item-content">
					<u-upload
						:file-list="fileList"
						@afterRead="afterRead"
						@delete="deletePic"
						name="3"
						multiple
						:max-count="3"
					></u-upload>
					<view class="upload-tips">最多上传3张图片，大小不超过5MB</view>
				</view>
			</view>
			
			<!-- 联系方式 -->
			<view class="form-item">
				<view class="item-label">
					<text class="required">*</text>
					<text>联系方式</text>
				</view>
				<view class="item-content">
					<input 
						class="input" 
						v-model="contactInfo" 
						placeholder="请输入手机号码"
						maxlength="11"
						type="number"
					/>
				</view>
			</view>
		</view>
		
		<!-- 提交按钮 -->
		<view class="submit-btn-area">
			<button class="submit-btn" @tap="submitAfterSale">提交申请</button>
		</view>
	</view>
</template>

<script>
	import regeneratorRuntime, {
		async
	} from '../../lib/runtime/runtime';
	import {
		applyAfterSaleApi
	} from '../../api/orderList.js';
	
	// 调试模式配置
	const DEBUG_MODE = true;
	
	export default {
		data() {
			return {
				orderId: '',
				orderAmount: 0, // 单位：分
				reasonList: [
					'请选择退款原因',
					'商品质量问题',
					'送错商品',
					'商品包装破损',
					'口味不佳',
					'份量不足',
					'商品缺少配件',
					'其他原因'
				],
				reasonIndex: 0,
				description: '',
				fileList: [],
				contactInfo: '',
				
				// 接口请求状态
				submitting: false
			}
		},
		onLoad(options) {
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
				
				if (options.amount) {
					this.orderAmount = parseInt(options.amount) || 0;
				}
				
				// 获取用户联系方式
				const userInfo = uni.getStorageSync('userInfo');
				if (userInfo && userInfo.phone) {
					this.contactInfo = userInfo.phone;
				}
			} else {
				uni.showToast({
					title: '参数错误',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateBack();
				}, 1500);
			}
		},
		methods: {
			// 选择退款原因
			onReasonChange(e) {
				this.reasonIndex = e.detail.value;
			},
			
			// 上传图片后回调
			afterRead(event) {
				// 获取上传的文件列表
				const { file } = event.detail;
				
				// 处理多文件上传
				const fileList = Array.isArray(file) ? file : [file];
				
				fileList.forEach(item => {
					// 在调试模式下，直接添加图片
					if (DEBUG_MODE) {
						this.fileList.push({
							url: item.url,
							status: 'success',
							message: '上传成功'
						});
						return;
					}
					
					// 在实际环境中，上传到服务器
					uni.uploadFile({
						url: 'https://your-server-url/upload', // 替换为实际上传地址
						filePath: item.url,
						name: 'file',
						formData: {
							orderId: this.orderId
						},
						success: (res) => {
							// 假设服务器返回的数据格式为 {code: 0, data: {url: 'xxx'}}
							const data = JSON.parse(res.data);
							if (data.code === 0) {
								this.fileList.push({
									url: data.data.url,
									status: 'success',
									message: '上传成功'
								});
							} else {
								uni.showToast({
									title: '图片上传失败',
									icon: 'none'
								});
							}
						},
						fail: () => {
							uni.showToast({
								title: '图片上传失败',
								icon: 'none'
							});
						}
					});
				});
			},
			
			// 删除图片
			deletePic(event) {
				this.fileList.splice(event.index, 1);
			},
			
			// 表单验证
			validateForm() {
				if (this.reasonIndex === 0) {
					uni.showToast({
						title: '请选择退款原因',
						icon: 'none'
					});
					return false;
				}
				
				if (!this.contactInfo) {
					uni.showToast({
						title: '请输入联系方式',
						icon: 'none'
					});
					return false;
				}
				
				// 简单验证手机号格式
				const phoneReg = /^1\d{10}$/;
				if (!phoneReg.test(this.contactInfo)) {
					uni.showToast({
						title: '请输入正确的手机号',
						icon: 'none'
					});
					return false;
				}
				
				return true;
			},
			
			// 提交售后申请
			async submitAfterSale() {
				// 防止重复提交
				if (this.submitting) {
					return;
				}
				
				// 表单验证
				if (!this.validateForm()) {
					return;
				}
				
				this.submitting = true;
				uni.showLoading({
					title: '提交中...'
				});
				
				try {
					// 构建请求参数
					const params = {
						orderId: this.orderId,
						reason: this.reasonList[this.reasonIndex],
						description: this.description,
						contactInfo: this.contactInfo,
						// 图片列表，实际接口可能需要图片URL列表
						images: this.fileList.map(item => item.url)
					};
					
					// 调试模式
					if (DEBUG_MODE) {
						setTimeout(() => {
							uni.hideLoading();
							this.submitting = false;
							
							uni.showToast({
								title: '申请提交成功',
								icon: 'success'
							});
							
							// 返回上一页
							setTimeout(() => {
								uni.navigateBack();
							}, 1500);
						}, 1000);
						return;
					}
					
					// 调用申请售后接口
					const res = await applyAfterSaleApi(params);
					
					uni.hideLoading();
					this.submitting = false;
					
					if (res.code === 0) {
						uni.showToast({
							title: '申请提交成功',
							icon: 'success'
						});
						
						// 返回上一页
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					} else {
						uni.showToast({
							title: res.msg || '申请提交失败',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('提交售后申请失败', error);
					uni.hideLoading();
					this.submitting = false;
					
					uni.showToast({
						title: '申请提交失败，请稍后再试',
						icon: 'none'
					});
				}
			}
		}
	}
</script>

<style>
	.after-sale {
		padding: 24rpx;
		background-color: #f8f8f8;
		min-height: 100vh;
	}
	
	.form-card {
		background-color: #fff;
		border-radius: 12rpx;
		padding: 24rpx;
		margin-bottom: 24rpx;
	}
	
	.form-title {
		font-size: 32rpx;
		font-weight: bold;
		padding-bottom: 24rpx;
		border-bottom: 1px solid #f5f5f5;
		margin-bottom: 24rpx;
	}
	
	.form-item {
		margin-bottom: 24rpx;
	}
	
	.item-label {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
		font-size: 28rpx;
		color: #333;
	}
	
	.required {
		color: #ff5050;
		margin-right: 8rpx;
	}
	
	.item-content {
		position: relative;
	}
	
	.amount {
		font-size: 36rpx;
		font-weight: bold;
		color: #ff5050;
	}
	
	.amount-desc {
		font-size: 24rpx;
		color: #999;
		margin-left: 16rpx;
	}
	
	.picker-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
		height: 80rpx;
		padding: 0 24rpx;
		background-color: #f9f9f9;
		border-radius: 8rpx;
	}
	
	.textarea {
		width: 100%;
		height: 200rpx;
		padding: 24rpx;
		background-color: #f9f9f9;
		border-radius: 8rpx;
		box-sizing: border-box;
		font-size: 28rpx;
	}
	
	.word-count {
		text-align: right;
		font-size: 24rpx;
		color: #999;
		margin-top: 8rpx;
	}
	
	.upload-tips {
		font-size: 24rpx;
		color: #999;
		margin-top: 16rpx;
	}
	
	.input {
		height: 80rpx;
		padding: 0 24rpx;
		background-color: #f9f9f9;
		border-radius: 8rpx;
		font-size: 28rpx;
	}
	
	.submit-btn-area {
		padding: 24rpx;
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background-color: #fff;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	}
	
	.submit-btn {
		width: 100%;
		height: 88rpx;
		line-height: 88rpx;
		background-color: #1fba1a;
		color: #fff;
		font-size: 32rpx;
		border-radius: 44rpx;
		border: none;
	}
</style> 