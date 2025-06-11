<template>
	<view class="staff-management">
		<view class="header">
			<text class="title">员工管理</text>
		</view>
		
		<!-- 操作栏 -->
		<view class="action-bar">
			<view class="add-btn" @click="showAddStaffModal">
				<text class="add-icon">+</text>
				<text class="add-text">添加员工</text>
			</view>
		</view>
		
		<!-- 员工列表 -->
		<view class="staff-list">
			<view v-for="(staff, index) in staffList" :key="index" class="staff-item">
				<view class="staff-avatar">
					<image :src="staff.avatar_url || '/static/images/default-avatar.png'" mode="aspectFill"></image>
				</view>
				<view class="staff-info">
					<view class="staff-name">{{staff.name}}</view>
					<view class="staff-username">账号：{{staff.username}}</view>
					<view class="staff-phone">电话：{{staff.phoneNum || '未设置'}}</view>
					<view class="staff-status">
						<text :class="['status-text', staff.status === 'active' ? 'status-active' : 'status-inactive']">
							{{staff.status === 'active' ? '在职' : '离职'}}
						</text>
						<text class="identity-text">{{getIdentityText(staff.identity)}}</text>
					</view>
				</view>
				<view class="staff-actions">
					<view class="action-btn edit" @click="editStaff(staff)">编辑</view>
					<view class="action-btn delete" @click="deleteStaff(staff)">删除</view>
				</view>
			</view>
		</view>
		
		<!-- 添加/编辑员工弹窗 -->
		<view v-if="showAddModal" class="modal-overlay" @click="hideAddStaffModal">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">{{isEdit ? '编辑员工' : '添加员工'}}</text>
					<view class="close-btn" @click="hideAddStaffModal">×</view>
				</view>
				
				<view class="modal-body">
					<view class="form-item">
						<text class="label">姓名</text>
						<input v-model="staffForm.name" class="input" placeholder="请输入员工姓名" />
					</view>
					
					<view class="form-item">
						<text class="label">用户名</text>
						<input v-model="staffForm.username" class="input" placeholder="请输入用户名" />
					</view>
					
					<view class="form-item">
						<text class="label">密码</text>
						<input v-model="staffForm.password" class="input" type="password" placeholder="请输入密码" />
					</view>
					
					<view class="form-item">
						<text class="label">手机号</text>
						<input v-model="staffForm.phoneNum" class="input" placeholder="请输入手机号" />
					</view>
					
					<view class="form-item">
						<text class="label">性别</text>
						<view class="radio-group">
							<label class="radio-item">
								<radio :checked="staffForm.gender === '男'" @click="staffForm.gender = '男'" />
								<text>男</text>
							</label>
							<label class="radio-item">
								<radio :checked="staffForm.gender === '女'" @click="staffForm.gender = '女'" />
								<text>女</text>
							</label>
						</view>
					</view>
					
					<view class="form-item">
						<text class="label">身份证号</text>
						<input v-model="staffForm.idNumber" class="input" placeholder="请输入身份证号" />
					</view>
				</view>
				
				<view class="modal-footer">
					<view class="btn cancel" @click="hideAddStaffModal">取消</view>
					<view class="btn confirm" @click="saveStaff">{{isEdit ? '更新' : '添加'}}</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view v-if="staffList.length === 0" class="empty-container">
			<image class="empty-image" src="/static/images/empty-staff.png" mode="aspectFit"></image>
			<text class="empty-text">暂无员工，点击上方按钮添加</text>
		</view>
	</view>
</template>

<script>
	import { 
		getEmployeesApi,
		addEmployeeApi,
		updateEmployeeApi,
		deleteEmployeeApi
	} from '../../api/merchant.js';
	
	export default {
		data() {
			return {
				staffList: [],
				showAddModal: false,
				isEdit: false,
				staffForm: {
					id: 0,
					username: '',
					password: '',
					name: '',
					phoneNum: '',
					gender: '男',
					idNumber: '',
					merchantId: 0
				},
				loading: false
			}
		},
		onLoad() {
			this.getStaffList();
		},
		methods: {
			// 获取员工列表
			async getStaffList() {
				try {
					uni.showLoading({ title: '加载员工列表...' });
					
					const res = await getEmployeesApi();
					console.log('员工列表响应:', res);
					
					if (res && Array.isArray(res)) {
						this.staffList = res;
					} else if (res && res.records) {
						this.staffList = res.records;
					} else {
						this.staffList = [];
					}
				} catch (error) {
					console.error('获取员工列表失败:', error);
					uni.showToast({
						title: '获取员工列表失败',
						icon: 'none'
					});
					this.staffList = [];
				} finally {
					uni.hideLoading();
				}
			},
			
			// 显示添加员工弹窗
			showAddStaffModal() {
				this.isEdit = false;
				this.resetStaffForm();
				this.showAddModal = true;
			},
			
			// 隐藏添加员工弹窗
			hideAddStaffModal() {
				this.showAddModal = false;
				this.resetStaffForm();
			},
			
			// 重置表单
			resetStaffForm() {
				// 获取当前商家ID
				const merchantInfo = uni.getStorageSync('merchantInfo');
				let merchantId = 0;
				try {
					const info = typeof merchantInfo === 'string' ? JSON.parse(merchantInfo) : merchantInfo;
					merchantId = info?.id || 0;
				} catch (e) {
					console.error('解析商家信息失败:', e);
				}
				
				this.staffForm = {
					id: 0,
					username: '',
					password: '',
					name: '',
					phoneNum: '',
					gender: '男',
					idNumber: '',
					merchantId: merchantId
				};
			},
			
			// 编辑员工
			editStaff(staff) {
				this.isEdit = true;
				this.staffForm = {
					id: staff.id,
					username: staff.username,
					password: '', // 编辑时不显示原密码
					name: staff.name,
					phoneNum: staff.phoneNum || '',
					gender: staff.gender || '男',
					idNumber: staff.idNumber || '',
					merchantId: staff.merchantId || staff.merchant_id || 0
				};
				this.showAddModal = true;
			},
			
			// 保存员工
			async saveStaff() {
				// 表单验证
				if (!this.staffForm.name || !this.staffForm.name.trim()) {
					uni.showToast({
						title: '请输入员工姓名',
						icon: 'none'
					});
					return;
				}
				
				if (!this.staffForm.username || !this.staffForm.username.trim()) {
					uni.showToast({
						title: '请输入用户名',
						icon: 'none'
					});
					return;
				}
				
				if (!this.isEdit && (!this.staffForm.password || !this.staffForm.password.trim())) {
					uni.showToast({
						title: '请输入密码',
						icon: 'none'
					});
					return;
				}
				
				if (!this.staffForm.phoneNum || !this.staffForm.phoneNum.trim()) {
					uni.showToast({
						title: '请输入手机号',
						icon: 'none'
					});
					return;
				}
				
				// 手机号格式验证
				const phoneRegex = /^1[3-9]\d{9}$/;
				if (!phoneRegex.test(this.staffForm.phoneNum.trim())) {
					uni.showToast({
						title: '请输入正确的手机号',
						icon: 'none'
					});
					return;
				}
				
				try {
					uni.showLoading({ title: this.isEdit ? '更新中...' : '添加中...' });
					
					const staffData = { ...this.staffForm };
					
					// 如果是编辑且密码为空，则不传递密码字段
					if (this.isEdit && (!staffData.password || !staffData.password.trim())) {
						delete staffData.password;
					}
					
					console.log('提交的员工数据:', staffData);
					
					let response;
					if (this.isEdit) {
						response = await updateEmployeeApi(this.staffForm.id, staffData);
					} else {
						response = await addEmployeeApi(staffData);
					}
					
					console.log('保存员工响应:', response);
					
					uni.hideLoading();
					
					if (response && (response.id || response.success !== false)) {
						uni.showToast({
							title: this.isEdit ? '更新成功' : '添加成功',
							icon: 'success'
						});
						
						this.hideAddStaffModal();
						this.getStaffList(); // 重新加载列表
					} else {
						throw new Error(response?.msg || '操作失败');
					}
				} catch (error) {
					uni.hideLoading();
					console.error('保存员工失败:', error);
					uni.showToast({
						title: error.message || '操作失败，请重试',
						icon: 'none'
					});
				}
			},
			
			// 删除员工
			deleteStaff(staff) {
				uni.showModal({
					title: '确认删除',
					content: `确定要删除员工"${staff.name}"吗？此操作不可恢复。`,
					success: async (res) => {
						if (res.confirm) {
							try {
								uni.showLoading({ title: '删除中...' });
								
								const response = await deleteEmployeeApi(staff.id);
								console.log('删除员工响应:', response);
								
								uni.hideLoading();
								
								if (response && (response.success !== false || response.code === 0)) {
									uni.showToast({
										title: '删除成功',
										icon: 'success'
									});
									
									this.getStaffList(); // 重新加载列表
								} else {
									throw new Error(response?.msg || '删除失败');
								}
							} catch (error) {
								uni.hideLoading();
								console.error('删除员工失败:', error);
								uni.showToast({
									title: error.message || '删除失败，请重试',
									icon: 'none'
								});
							}
						}
					}
				});
			},
			
			// 获取身份文本
			getIdentityText(identity) {
				switch (identity) {
					case 'ADMIN': return '管理员';
					case 'STAFF': return '员工';
					case 'MANAGER': return '经理';
					default: return '员工';
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.staff-management {
		background-color: #f5f5f5;
		min-height: 100vh;
	}
	
	.header {
		background-color: #FF8C00;
		height: 90rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		
		.title {
			color: #fff;
			font-size: 36rpx;
			font-weight: bold;
		}
	}
	
	.action-bar {
		padding: 20rpx;
		background-color: #fff;
		margin-bottom: 20rpx;
		
		.add-btn {
			display: flex;
			align-items: center;
			justify-content: center;
			padding: 20rpx;
			background-color: #FF8C00;
			border-radius: 12rpx;
			color: #fff;
			
			.add-icon {
				font-size: 32rpx;
				margin-right: 10rpx;
			}
			
			.add-text {
				font-size: 28rpx;
			}
		}
	}
	
	.staff-list {
		padding: 0 20rpx;
	}
	
	.staff-item {
		display: flex;
		background-color: #fff;
		border-radius: 12rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.staff-avatar {
			width: 100rpx;
			height: 100rpx;
			border-radius: 50%;
			overflow: hidden;
			margin-right: 20rpx;
			
			image {
				width: 100%;
				height: 100%;
			}
		}
		
		.staff-info {
			flex: 1;
			
			.staff-name {
				font-size: 30rpx;
				color: #333;
				font-weight: bold;
				margin-bottom: 8rpx;
			}
			
			.staff-username {
				font-size: 24rpx;
				color: #666;
				margin-bottom: 6rpx;
			}
			
			.staff-phone {
				font-size: 24rpx;
				color: #666;
				margin-bottom: 8rpx;
			}
			
			.staff-status {
				display: flex;
				align-items: center;
				
				.status-text {
					font-size: 22rpx;
					padding: 4rpx 10rpx;
					border-radius: 10rpx;
					margin-right: 15rpx;
					
					&.status-active {
						background-color: #E8F5E8;
						color: #4CAF50;
					}
					
					&.status-inactive {
						background-color: #FFEBEE;
						color: #F44336;
					}
				}
				
				.identity-text {
					font-size: 22rpx;
					padding: 4rpx 10rpx;
					border-radius: 10rpx;
					background-color: #E3F2FD;
					color: #2196F3;
				}
			}
		}
		
		.staff-actions {
			display: flex;
			flex-direction: column;
			justify-content: center;
			
			.action-btn {
				padding: 8rpx 16rpx;
				border-radius: 16rpx;
				font-size: 22rpx;
				text-align: center;
				margin-bottom: 8rpx;
				
				&.edit {
					background-color: #2196F3;
					color: #fff;
				}
				
				&.delete {
					background-color: #F44336;
					color: #fff;
				}
			}
		}
	}
	
	.modal-overlay {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1000;
	}
	
	.modal-content {
		background-color: #fff;
		border-radius: 12rpx;
		width: 90%;
		max-height: 80vh;
		overflow-y: auto;
	}
	
	.modal-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 2rpx solid #f5f5f5;
		
		.modal-title {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
		}
		
		.close-btn {
			font-size: 40rpx;
			color: #999;
			width: 60rpx;
			height: 60rpx;
			display: flex;
			align-items: center;
			justify-content: center;
		}
	}
	
	.modal-body {
		padding: 30rpx;
	}
	
	.form-item {
		margin-bottom: 30rpx;
		
		.label {
			display: block;
			font-size: 28rpx;
			color: #333;
			margin-bottom: 10rpx;
		}
		
		.input {
			width: 100%;
			padding: 20rpx;
			border: 2rpx solid #e0e0e0;
			border-radius: 8rpx;
			font-size: 26rpx;
			box-sizing: border-box;
		}
		
		.radio-group {
			display: flex;
			
			.radio-item {
				display: flex;
				align-items: center;
				margin-right: 40rpx;
				
				radio {
					margin-right: 10rpx;
				}
				
				text {
					font-size: 26rpx;
					color: #333;
				}
			}
		}
	}
	
	.modal-footer {
		display: flex;
		padding: 30rpx;
		border-top: 2rpx solid #f5f5f5;
		
		.btn {
			flex: 1;
			padding: 20rpx;
			border-radius: 8rpx;
			text-align: center;
			font-size: 28rpx;
			
			&.cancel {
				background-color: #f5f5f5;
				color: #666;
				margin-right: 20rpx;
			}
			
			&.confirm {
				background-color: #FF8C00;
				color: #fff;
			}
		}
	}
	
	.empty-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;
		
		.empty-image {
			width: 200rpx;
			height: 200rpx;
			margin-bottom: 30rpx;
		}
		
		.empty-text {
			font-size: 28rpx;
			color: #999;
		}
	}
</style> 