<template>
	<view class="dish-management">
		<view class="header">
			<text class="title">菜品管理</text>
		</view>
		
		<!-- 操作栏 -->
		<view class="action-bar">
			<view class="add-btn" @click="showAddDishModal">
				<text class="add-icon">+</text>
				<text class="add-text">添加菜品</text>
			</view>
		</view>
		
		<!-- 菜品列表 -->
		<view class="dish-list">
			<view v-for="(dish, index) in dishList" :key="index" class="dish-item">
				<view class="dish-image">
					<image :src="dish.image || '/static/images/default-dish.png'" mode="aspectFill"></image>
				</view>
				<view class="dish-info">
					<view class="dish-name">{{dish.name}}</view>
					<view class="dish-description">{{dish.description || '暂无描述'}}</view>
					<view class="dish-price">¥{{dish.price}}</view>
					<view class="dish-status">
						<text :class="['status-text', dish.status === 1 ? 'status-active' : 'status-inactive']">
							{{dish.status === 1 ? '上架' : '下架'}}
						</text>
					</view>
				</view>
				<view class="dish-actions">
					<view class="action-btn edit" @click="editDish(dish)">编辑</view>
					<view class="action-btn toggle" @click="toggleDishStatus(dish)">
						{{dish.status === 1 ? '下架' : '上架'}}
					</view>
				</view>
			</view>
		</view>
		
		<!-- 添加菜品弹窗 -->
		<view v-if="showAddModal" class="modal-overlay" @click="hideAddDishModal">
			<view class="modal-content" @click.stop>
				<view class="modal-header">
					<text class="modal-title">{{isEdit ? '编辑菜品' : '添加菜品'}}</text>
					<view class="close-btn" @click="hideAddDishModal">×</view>
				</view>
				
				<view class="modal-body">
					<view class="form-item">
						<text class="label">菜品名称</text>
						<input v-model="dishForm.name" class="input" placeholder="请输入菜品名称" />
					</view>
					
					<view class="form-item">
						<text class="label">分类ID</text>
						<input v-model.number="dishForm.categoryId" class="input" type="number" placeholder="请输入分类ID" />
					</view>
					
					<view class="form-item">
						<text class="label">价格</text>
						<input v-model.number="dishForm.price" class="input" type="digit" placeholder="请输入价格" />
					</view>
					
					<view class="form-item">
						<text class="label">图片URL</text>
						<input v-model="dishForm.image" class="input" placeholder="请输入图片URL" />
					</view>
					
					<view class="form-item">
						<text class="label">描述</text>
						<textarea v-model="dishForm.description" class="textarea" placeholder="请输入菜品描述"></textarea>
					</view>
					
					<view class="form-item">
						<text class="label">状态</text>
						<view class="radio-group">
							<label class="radio-item">
								<radio :checked="dishForm.status === 1" @click="dishForm.status = 1" />
								<text>上架</text>
							</label>
							<label class="radio-item">
								<radio :checked="dishForm.status === 0" @click="dishForm.status = 0" />
								<text>下架</text>
							</label>
						</view>
					</view>
				</view>
				
				<view class="modal-footer">
					<view class="btn cancel" @click="hideAddDishModal">取消</view>
					<view class="btn confirm" @click="saveDish">{{isEdit ? '更新' : '添加'}}</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view v-if="dishList.length === 0" class="empty-container">
			<image class="empty-image" src="/static/images/empty-dish.png" mode="aspectFit"></image>
			<text class="empty-text">暂无菜品，点击上方按钮添加</text>
		</view>
	</view>
</template>

<script>
	import { 
		getDishesApi,
		getCategoriesApi,
		addDishApi,
		updateDishApi,
		changeDishStatusApi
	} from '../../api/merchant.js';
	
	export default {
		data() {
			return {
				dishList: [],
				categories: [],
				showAddModal: false,
				isEdit: false,
				dishForm: {
					id: 0,
					name: '',
					categoryId: 0,
					price: 0,
					image: '',
					description: '',
					status: 1
				},
				loading: false
			}
		},
		onLoad() {
			this.getDishList();
			this.getCategories();
		},
		methods: {
			// 获取菜品列表
			async getDishList() {
				try {
					uni.showLoading({ title: '加载菜品列表...' });
					
					const res = await getDishesApi({
						page: 1,
						size: 100
					});
					
					console.log('菜品列表响应:', res);
					
					if (res && Array.isArray(res)) {
						this.dishList = res;
					} else if (res && res.records) {
						this.dishList = res.records;
					} else {
						this.dishList = [];
					}
				} catch (error) {
					console.error('获取菜品列表失败:', error);
					uni.showToast({
						title: '获取菜品列表失败',
						icon: 'none'
					});
					this.dishList = [];
				} finally {
					uni.hideLoading();
				}
			},
			
			// 获取分类列表
			async getCategories() {
				try {
					const res = await getCategoriesApi();
					console.log('分类列表响应:', res);
					
					if (res && Array.isArray(res)) {
						this.categories = res;
					} else {
						this.categories = [];
					}
				} catch (error) {
					console.error('获取分类列表失败:', error);
					this.categories = [];
				}
			},
			
			// 显示添加菜品弹窗
			showAddDishModal() {
				this.isEdit = false;
				this.resetDishForm();
				this.showAddModal = true;
			},
			
			// 隐藏添加菜品弹窗
			hideAddDishModal() {
				this.showAddModal = false;
				this.resetDishForm();
			},
			
			// 重置表单
			resetDishForm() {
				this.dishForm = {
					id: 0,
					name: '',
					categoryId: 0,
					price: 0,
					image: '',
					description: '',
					status: 1
				};
			},
			
			// 编辑菜品
			editDish(dish) {
				this.isEdit = true;
				this.dishForm = {
					id: dish.id,
					name: dish.name,
					categoryId: dish.categoryId,
					price: dish.price,
					image: dish.image || '',
					description: dish.description || '',
					status: dish.status
				};
				this.showAddModal = true;
			},
			
			// 保存菜品
			async saveDish() {
				// 表单验证
				if (!this.dishForm.name.trim()) {
					uni.showToast({
						title: '请输入菜品名称',
						icon: 'none'
					});
					return;
				}
				
				if (!this.dishForm.categoryId || this.dishForm.categoryId <= 0) {
					uni.showToast({
						title: '请输入有效的分类ID',
						icon: 'none'
					});
					return;
				}
				
				if (!this.dishForm.price || this.dishForm.price <= 0) {
					uni.showToast({
						title: '请输入有效的价格',
						icon: 'none'
					});
					return;
				}
				
				try {
					uni.showLoading({ title: this.isEdit ? '更新中...' : '添加中...' });
					
					// 简化数据结构，不包含flavors
					const dishData = {
						id: this.dishForm.id || 0,
						name: this.dishForm.name,
						categoryId: this.dishForm.categoryId,
						price: this.dishForm.price,
						image: this.dishForm.image || '',
						description: this.dishForm.description || '',
						status: this.dishForm.status || 1
					};
					
					console.log('提交的菜品数据:', dishData);
					
					let response;
					if (this.isEdit) {
						response = await updateDishApi(this.dishForm.id, dishData);
					} else {
						response = await addDishApi(dishData);
					}
					
					console.log('保存菜品响应:', response);
					
					uni.hideLoading();
					
					if (response && (response.code === 200 || response.success === true)) {
						uni.showToast({
							title: this.isEdit ? '更新成功' : '添加成功',
							icon: 'success'
						});
						
						this.hideAddDishModal();
						this.getDishList(); // 重新加载列表
					} else {
						throw new Error(response?.msg || '操作失败');
					}
				} catch (error) {
					uni.hideLoading();
					console.error('保存菜品失败:', error);
					uni.showToast({
						title: error.message || '操作失败，请重试',
						icon: 'none'
					});
				}
			},
			
			// 切换菜品状态
			async toggleDishStatus(dish) {
				try {
					uni.showLoading({ title: '更新状态中...' });
					
					const newStatus = dish.status === 1 ? 0 : 1;
					const response = await changeDishStatusApi(dish.id, newStatus);
					
					console.log('切换状态响应:', response);
					
					uni.hideLoading();
					
					if (response && response.success !== false) {
						dish.status = newStatus;
						uni.showToast({
							title: newStatus === 1 ? '已上架' : '已下架',
							icon: 'success'
						});
					} else {
						throw new Error(response?.msg || '状态更新失败');
					}
				} catch (error) {
					uni.hideLoading();
					console.error('切换状态失败:', error);
					uni.showToast({
						title: error.message || '状态更新失败',
						icon: 'none'
					});
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.dish-management {
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
	
	.dish-list {
		padding: 0 20rpx;
	}
	
	.dish-item {
		display: flex;
		background-color: #fff;
		border-radius: 12rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		
		.dish-image {
			width: 120rpx;
			height: 120rpx;
			border-radius: 8rpx;
			overflow: hidden;
			margin-right: 20rpx;
			
			image {
				width: 100%;
				height: 100%;
			}
		}
		
		.dish-info {
			flex: 1;
			
			.dish-name {
				font-size: 30rpx;
				color: #333;
				font-weight: bold;
				margin-bottom: 10rpx;
			}
			
			.dish-description {
				font-size: 24rpx;
				color: #666;
				margin-bottom: 10rpx;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}
			
			.dish-price {
				font-size: 28rpx;
				color: #FF5722;
				font-weight: bold;
				margin-bottom: 10rpx;
			}
			
			.dish-status {
				.status-text {
					font-size: 24rpx;
					padding: 4rpx 12rpx;
					border-radius: 12rpx;
					
					&.status-active {
						background-color: #E8F5E8;
						color: #4CAF50;
					}
					
					&.status-inactive {
						background-color: #FFEBEE;
						color: #F44336;
					}
				}
			}
		}
		
		.dish-actions {
			display: flex;
			flex-direction: column;
			justify-content: center;
			
			.action-btn {
				padding: 10rpx 20rpx;
				border-radius: 20rpx;
				font-size: 24rpx;
				text-align: center;
				margin-bottom: 10rpx;
				
				&.edit {
					background-color: #2196F3;
					color: #fff;
				}
				
				&.toggle {
					background-color: #FF9800;
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
		
		.input, .textarea {
			width: 100%;
			padding: 20rpx;
			border: 2rpx solid #e0e0e0;
			border-radius: 8rpx;
			font-size: 26rpx;
			box-sizing: border-box;
		}
		
		.textarea {
			height: 120rpx;
			resize: none;
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