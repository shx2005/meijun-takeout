<template>
	<view class="after-sale">
		<scroll-view scroll-y="true" class="after-sale-scroll" :style="{height: scrollHeight + 'px'}">
			<view class="form-card">
				<view class="form-title">申请售后</view>
				
				<!-- 订单信息 -->
				<view class="order-info-card">
					<view class="order-item">
						<text class="label">订单号：</text>
						<text class="value">{{ orderId }}</text>
					</view>
					<view class="order-item" v-if="orderAmount > 0">
						<text class="label">订单金额：</text>
						<text class="value">￥{{ (orderAmount / 100).toFixed(2) }}</text>
					</view>
				</view>
				
				<!-- 售后类型 -->
				<view class="form-item">
					<view class="item-label">
						<text class="required">*</text>
						<text>售后类型</text>
					</view>
					<view class="item-content">
						<picker @change="onTypeChange" :value="typeIndex" :range="typeList" range-key="label">
							<view class="picker-content">
								<text>{{ typeList[typeIndex].label }}</text>
								<u-icon name="arrow-right" size="28"></u-icon>
							</view>
						</picker>
					</view>
				</view>
				
				<!-- 退款金额（仅退款类型显示） -->
				<view class="form-item" v-if="typeList[typeIndex].value === 'refund'">
					<view class="item-label">
						<text class="required">*</text>
						<text>退款金额</text>
					</view>
					<view class="item-content">
						<input 
							class="input" 
							type="digit" 
							v-model="refundAmount" 
							placeholder="请输入退款金额"
							@input="onRefundAmountChange"
						/>
						<text class="amount-desc">最多{{ (orderAmount / 100).toFixed(2) }}元</text>
					</view>
				</view>
				
				<!-- 售后原因 -->
				<view class="form-item">
					<view class="item-label">
						<text class="required">*</text>
						<text>售后原因</text>
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
						<text class="required">*</text>
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
				
				<!-- 提交按钮 -->
				<view class="submit-btn-area">
					<button class="submit-btn" @tap="submitAfterSale" :disabled="submitting">
						{{ submitting ? '提交中...' : '提交申请' }}
					</button>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import regeneratorRuntime, {
		async
	} from '../../lib/runtime/runtime';
	import {
		saveAfterSaleApi,
		AfterSaleType,
		AfterSaleStatus
	} from '../../api/afterSale.js';
	
	// 调试模式配置
	const DEBUG_MODE = false;
	
	export default {
		data() {
			return {
				scrollHeight: 0, // 滚动区域高度
				orderId: '',
				orderAmount: 0, // 单位：分
				refundAmount: '', // 退款金额，用户输入，单位：元
				
				// 售后类型
				typeList: [
					{ label: '退款', value: 'refund' },
					{ label: '换货', value: 'replace' },
					{ label: '其他', value: 'other' }
				],
				typeIndex: 0,
				
				// 售后原因
				reasonList: [
					'请选择售后原因',
					'商品质量问题',
					'送错商品',
					'商品包装破损',
					'口味不佳',
					'份量不足',
					'商品缺少配件',
					'配送延误',
					'服务态度差',
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
			console.log('售后页面接收参数:', options);
			
			// 获取系统信息设置滚动区域高度
			const systemInfo = uni.getSystemInfoSync();
			this.scrollHeight = systemInfo.windowHeight;
			
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
					// 默认退款金额为订单金额
					this.refundAmount = (this.orderAmount / 100).toFixed(2);
				}
				
				// 获取用户联系方式
				const userInfo = uni.getStorageSync('userInfo');
				if (userInfo && userInfo.phoneNum) {
					this.contactInfo = userInfo.phoneNum;
				}
				
				// 如果传入了售后类型，设置默认类型
				if (options.type) {
					const typeIndex = this.typeList.findIndex(item => item.value === options.type);
					if (typeIndex !== -1) {
						this.typeIndex = typeIndex;
					}
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
			// 选择售后类型
			onTypeChange(e) {
				this.typeIndex = e.detail.value;
				// 如果不是退款类型，清空退款金额
				if (this.typeList[this.typeIndex].value !== 'refund') {
					this.refundAmount = '';
				} else {
					// 如果是退款类型，设置默认退款金额
					this.refundAmount = (this.orderAmount / 100).toFixed(2);
				}
			},
			
			// 选择售后原因
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
			
			// 监听退款金额变化
			onRefundAmountChange(e) {
				// 限制只能输入数字和小数点
				let value = e.target.value;
				// 清除非数字和小数点
				value = value.replace(/[^\d.]/g, '');
				// 确保只有一个小数点
				const parts = value.split('.');
				if (parts.length > 2) {
					value = parts[0] + '.' + parts.slice(1).join('');
				}
				// 限制小数点后两位
				if (parts.length > 1 && parts[1].length > 2) {
					value = parts[0] + '.' + parts[1].substring(0, 2);
				}
				
				this.refundAmount = value;
			},
			
			// 表单验证
			validateForm() {
				if (this.reasonIndex === 0) {
					uni.showToast({
						title: '请选择售后原因',
						icon: 'none'
					});
					return false;
				}
				
				// 如果是退款类型，验证退款金额
				if (this.typeList[this.typeIndex].value === 'refund') {
					if (!this.refundAmount || parseFloat(this.refundAmount) <= 0) {
						uni.showToast({
							title: '请输入有效的退款金额',
							icon: 'none'
						});
						return false;
					}
					
					// 检查退款金额是否超出订单总金额
					const refundAmountCents = Math.round(parseFloat(this.refundAmount) * 100);
					if (refundAmountCents > this.orderAmount) {
						uni.showToast({
							title: `退款金额不能超过${(this.orderAmount / 100).toFixed(2)}元`,
							icon: 'none'
						});
						return false;
					}
				}
				
				if (!this.description.trim()) {
					uni.showToast({
						title: '请填写问题描述',
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
					// 获取用户ID
					const userInfo = uni.getStorageSync('userInfo');
					const userId = userInfo?.id || 1;
					
					// 构建请求参数，符合后端期望的格式
					const afterSaleData = {
						id: 0, // 新建时为0
						orderId: parseInt(this.orderId),
						userId: userId,
						type: this.typeList[this.typeIndex].value,
						reason: this.reasonList[this.reasonIndex],
						content: this.description.trim(),
						status: AfterSaleStatus.PENDING,
						createTime: new Date().toISOString(),
						updateTime: new Date().toISOString()
					};
					
					console.log('提交售后申请数据:', afterSaleData);
					
					// 调用保存售后接口
					const res = await saveAfterSaleApi(afterSaleData);
					
					uni.hideLoading();
					this.submitting = false;
					
					console.log('售后申请响应:', res);
					
					if (res && (res.code === 200 || res.success === true)) {
						// 申请成功后，将订单ID存储到本地
						this.saveAfterSaleOrderId(parseInt(this.orderId), userId);
						
						uni.showToast({
							title: '申请提交成功',
							icon: 'success'
						});
						
						// 跳转到售后中心页面
						setTimeout(() => {
							uni.redirectTo({
								url: '/pages/afterSaleCenter/afterSaleCenter'
							});
						}, 1500);
					} else {
						uni.showToast({
							title: res?.msg || '申请提交失败',
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
			},
			
			// 保存售后订单ID到本地存储
			saveAfterSaleOrderId(orderId, userId) {
				try {
					// 构建完整的售后申请信息
					const afterSaleInfo = {
						id: Date.now(), // 使用时间戳作为临时ID
						orderId: orderId,
						userId: userId,
						type: this.typeList[this.typeIndex].value,
						reason: this.reasonList[this.reasonIndex],
						content: this.description.trim(),
						status: 'pending', // 默认状态为待处理
						createTime: new Date().toISOString(),
						updateTime: new Date().toISOString(),
						orderAmount: this.orderAmount // 保存订单金额
					};
					
					// 获取现有的售后申请列表
					const storageKey = `afterSaleList_${userId}`;
					let afterSaleList = uni.getStorageSync(storageKey);
					
					// 如果没有数据或数据格式不正确，初始化为空数组
					if (!afterSaleList || !Array.isArray(afterSaleList)) {
						afterSaleList = [];
					}
					
					// 检查订单ID是否已存在，避免重复
					const existingIndex = afterSaleList.findIndex(item => item.orderId === orderId);
					if (existingIndex !== -1) {
						// 如果已存在，更新信息
						afterSaleList[existingIndex] = afterSaleInfo;
					} else {
						// 如果不存在，添加新记录
						afterSaleList.push(afterSaleInfo);
					}
					
					// 保存到本地存储
					uni.setStorageSync(storageKey, afterSaleList);
					console.log('已保存售后申请信息:', afterSaleInfo);
					console.log('当前售后列表:', afterSaleList);
				} catch (error) {
					console.error('保存售后申请信息失败:', error);
				}
			}
		}
	}
</script>

<style>
	.after-sale {
		width: 100%;
		height: 100vh;
		background-color: #f8f8f8;
	}
	
	.after-sale-scroll {
		width: 100%;
	}
	
	.form-card {
		background: #fff;
		border-radius: 12rpx;
		padding: 32rpx;
		margin: 24rpx;
		margin-bottom: 40rpx;
	}
	
	.form-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 32rpx;
		text-align: center;
	}
	
	.order-info-card {
		background: #f8f9fa;
		border-radius: 8rpx;
		padding: 24rpx;
		margin-bottom: 32rpx;
	}
	
	.order-item {
		display: flex;
		justify-content: space-between;
		margin-bottom: 12rpx;
	}
	
	.order-item:last-child {
		margin-bottom: 0;
	}
	
	.order-item .label {
		color: #666;
		font-size: 28rpx;
	}
	
	.order-item .value {
		color: #333;
		font-size: 28rpx;
		font-weight: 500;
	}
	
	.form-item {
		margin-bottom: 32rpx;
	}
	
	.item-label {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
		font-size: 28rpx;
		color: #333;
		font-weight: 500;
	}
	
	.required {
		color: #ff4757;
		margin-right: 8rpx;
	}
	
	.item-content {
		position: relative;
	}
	
	.picker-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 24rpx;
		background: #f8f9fa;
		border-radius: 8rpx;
		border: 1rpx solid #e9ecef;
		font-size: 28rpx;
		color: #333;
	}
	
	.input {
		width: 100%;
		padding: 24rpx;
		background: #f8f9fa;
		border-radius: 8rpx;
		border: 1rpx solid #e9ecef;
		font-size: 28rpx;
		color: #333;
	}
	
	.amount-desc {
		position: absolute;
		right: 24rpx;
		top: 50%;
		transform: translateY(-50%);
		font-size: 24rpx;
		color: #999;
	}
	
	.textarea {
		width: 100%;
		min-height: 200rpx;
		padding: 24rpx;
		background: #f8f9fa;
		border-radius: 8rpx;
		border: 1rpx solid #e9ecef;
		font-size: 28rpx;
		color: #333;
		line-height: 1.5;
	}
	
	.word-count {
		text-align: right;
		margin-top: 12rpx;
		font-size: 24rpx;
		color: #999;
	}
	
	.upload-tips {
		margin-top: 16rpx;
		font-size: 24rpx;
		color: #999;
	}
	
	.submit-btn-area {
		padding: 32rpx 0 40rpx 0;
	}
	
	.submit-btn {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(to right, #ff6b6b, #ff5722);
		color: #fff;
		border-radius: 44rpx;
		font-size: 32rpx;
		font-weight: bold;
		border: none;
		display: flex;
		align-items: center;
		justify-content: center;
	}
	
	.submit-btn[disabled] {
		background: #ccc;
		color: #999;
	}
</style> 