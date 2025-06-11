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
		
		<!-- 菜品列表 - 添加滑动功能 -->
		<scroll-view 
			class="dish-scroll-container"
			scroll-y="true"
			:scroll-top="scrollTop"
			:enable-back-to-top="true"
			:refresher-enabled="true"
			:refresher-triggered="refresherTriggered"
			@refresherrefresh="onRefresh"
			@scrolltolower="onScrollToLower"
			@scroll="onScroll"
			@touchstart="onTouchStart"
			@touchmove="onTouchMove"
			@touchend="onTouchEnd">
			
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
				
				<!-- 空状态 -->
				<view v-if="dishList.length === 0" class="empty-container">
					<image class="empty-image" src="/static/images/empty-dish.png" mode="aspectFit"></image>
					<text class="empty-text">暂无菜品，点击上方按钮添加</text>
				</view>
				
				<!-- 加载更多提示 -->
				<view v-if="hasMore && dishList.length > 0" class="load-more">
					<text class="load-more-text">{{loadingMore ? '加载中...' : '上拉加载更多'}}</text>
				</view>
				
				<!-- 没有更多数据提示 -->
				<view v-if="!hasMore && dishList.length > 0" class="no-more">
					<text class="no-more-text">没有更多数据了</text>
				</view>
			</view>
		</scroll-view>
		
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
		
		<!-- 回到顶部按钮 -->
		<view v-if="showBackToTop" class="back-to-top" @click="backToTop">
			<text class="back-to-top-icon">↑</text>
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
				loading: false,
				// 滑动相关数据
				scrollTop: 0,
				refresherTriggered: false,
				hasMore: true,
				loadingMore: false,
				currentPage: 1,
				pageSize: 20,
				showBackToTop: false,
				// 触摸相关数据
				touchStartY: 0,
				touchStartTime: 0,
				lastScrollTop: 0,
				scrollTimer: null
			}
		},
		onLoad() {
			this.getDishList();
			this.getCategories();
		},
		methods: {
			// 获取菜品列表 - 优化分页逻辑
			async getDishList(isLoadMore = false) {
				try {
					// 防止重复请求
					if (this.loading || (isLoadMore && this.loadingMore)) {
						console.log('正在加载中，跳过重复请求');
						return;
					}
					
					if (!isLoadMore) {
						// 首次加载或刷新
						uni.showLoading({ title: '加载菜品列表...' });
						this.loading = true;
						this.currentPage = 1;
						this.hasMore = true;
						this.dishList = []; // 清空现有数据
					} else {
						// 加载更多
						if (!this.hasMore) {
							console.log('没有更多数据了');
							return;
						}
						this.loadingMore = true;
					}
					
					console.log(`开始获取第${this.currentPage}页菜品数据，每页${this.pageSize}条`);
					
					const res = await getDishesApi({
						pageNum: this.currentPage,
						pageSize: this.pageSize
					});
					
					console.log(`第${this.currentPage}页菜品数据响应:`, res);
					
					let newDishes = [];
					let totalCount = 0;
					
					// 处理API返回的数据格式: {total, records, current, size, code}
					if (res && res.code === 200) {
						if (res.records && Array.isArray(res.records)) {
							newDishes = res.records;
							totalCount = res.total || 0;
							console.log(`从API获取到${newDishes.length}条菜品，总数${totalCount}`);
						} else {
							console.warn('API返回格式异常，records不是数组:', res);
							newDishes = [];
						}
					} else {
						console.error('API返回错误:', res);
						newDishes = [];
					}
					
					// 确保newDishes是数组
					if (!Array.isArray(newDishes)) {
						console.error('获取的菜品数据不是数组:', newDishes);
						newDishes = [];
					}
					
					if (isLoadMore) {
						// 加载更多时追加数据
						console.log(`追加${newDishes.length}条菜品到现有${this.dishList.length}条中`);
						this.dishList = [...this.dishList, ...newDishes];
						this.loadingMore = false;
					} else {
						// 首次加载或刷新时替换数据
						console.log(`替换菜品列表，共${newDishes.length}条`);
						this.dishList = newDishes;
						this.loading = false;
					}
					
					// 判断是否还有更多数据
					// 方法1: 根据当前已加载数量和总数判断
					const currentTotal = this.dishList.length;
					if (totalCount > 0 && currentTotal >= totalCount) {
						this.hasMore = false;
						console.log(`已加载全部数据: ${currentTotal}/${totalCount}`);
					}
					// 方法2: 根据返回数据量判断
					else if (newDishes.length < this.pageSize) {
						this.hasMore = false;
						console.log(`返回数据不足一页，没有更多数据: ${newDishes.length}/${this.pageSize}`);
					} 
					// 还有更多数据
					else {
						this.currentPage++;
						console.log(`还有更多数据，页码增加到${this.currentPage}`);
					}
					
					console.log(`当前菜品总数: ${this.dishList.length}, 服务器总数: ${totalCount}, 是否还有更多: ${this.hasMore}`);
					
				} catch (error) {
					console.error('获取菜品列表失败:', error);
					
					// 显示错误提示
					uni.showToast({
						title: '获取菜品列表失败',
						icon: 'none',
						duration: 2000
					});
					
					// 重置加载状态
					if (!isLoadMore) {
						this.loading = false;
						// 首次加载失败时，保持空数组
						if (this.dishList.length === 0) {
							this.dishList = [];
						}
					} else {
						this.loadingMore = false;
						// 加载更多失败时，不改变现有数据
					}
					
					// 如果是网络错误，可以保持hasMore为true，允许用户重试
					if (error.message && error.message.includes('网络')) {
						console.log('网络错误，保持hasMore为true以便重试');
					} else {
						this.hasMore = false;
					}
					
				} finally {
					// 确保加载状态被重置
					if (!isLoadMore) {
						this.loading = false;
						uni.hideLoading();
					} else {
						this.loadingMore = false;
					}
				}
			},
			
			// 获取分类列表
			async getCategories() {
				try {
					const res = await getCategoriesApi();
					console.log('分类列表响应:', res);
					
					// 处理分类数据格式: {code: 200, msg: "OK", data: Array(14), success: true}
					if (res && res.code === 200 && res.data && Array.isArray(res.data)) {
						this.categories = res.data;
						console.log(`获取到${this.categories.length}个分类`);
					} else if (res && Array.isArray(res)) {
						// 兼容直接返回数组的情况
						this.categories = res;
						console.log(`获取到${this.categories.length}个分类（数组格式）`);
					} else {
						console.warn('分类数据格式异常:', res);
						this.categories = [];
					}
				} catch (error) {
					console.error('获取分类列表失败:', error);
					this.categories = [];
				}
			},
			
			// 下拉刷新 - 重置所有状态
			async onRefresh() {
				console.log('开始下拉刷新');
				this.refresherTriggered = true;
				
				// 重置分页状态
				this.currentPage = 1;
				this.hasMore = true;
				this.loadingMore = false;
				
				try {
					await this.getDishList(false);
					console.log('下拉刷新完成');
				} catch (error) {
					console.error('下拉刷新失败:', error);
				} finally {
					this.refresherTriggered = false;
				}
			},
			
			// 上拉加载更多 - 优化触发条件
			async onScrollToLower() {
				console.log('触发上拉加载更多');
				console.log(`当前状态: hasMore=${this.hasMore}, loadingMore=${this.loadingMore}, loading=${this.loading}`);
				
				// 检查是否可以加载更多
				if (!this.hasMore) {
					console.log('没有更多数据，跳过加载');
					return;
				}
				
				if (this.loadingMore || this.loading) {
					console.log('正在加载中，跳过重复请求');
					return;
				}
				
				if (this.dishList.length === 0) {
					console.log('菜品列表为空，跳过加载更多');
					return;
				}
				
				console.log(`开始加载第${this.currentPage}页数据`);
				await this.getDishList(true);
			},
			
			// 滚动事件 - 优化性能
			onScroll(e) {
				// 节流处理，避免频繁更新
				if (this.scrollTimer) {
					clearTimeout(this.scrollTimer);
				}
				
				this.scrollTimer = setTimeout(() => {
					this.scrollTop = e.detail.scrollTop;
					this.showBackToTop = e.detail.scrollTop > 300;
					this.lastScrollTop = e.detail.scrollTop;
				}, 16); // 约60fps
			},
			
			// 触摸开始
			onTouchStart(e) {
				this.touchStartY = e.touches[0].clientY;
				this.touchStartTime = Date.now();
			},
			
			// 触摸移动
			onTouchMove(e) {
				// 可以在这里添加自定义的滑动逻辑
				const currentY = e.touches[0].clientY;
				const deltaY = currentY - this.touchStartY;
				
				// 如果是向下滑动且在顶部，可以触发刷新
				if (deltaY > 0 && this.scrollTop <= 0) {
					// 下拉刷新逻辑已由scroll-view的refresher处理
				}
			},
			
			// 触摸结束 - 优化快速滑动检测
			onTouchEnd(e) {
				const touchEndTime = Date.now();
				const touchDuration = touchEndTime - this.touchStartTime;
				
				// 快速滑动检测（滑动时间小于300ms且距离大于100px）
				if (touchDuration < 300) {
					const deltaY = e.changedTouches[0].clientY - this.touchStartY;
					
					// 快速向上滑动，滚动到底部
					if (deltaY < -100) {
						console.log('检测到快速向上滑动，滚动到底部');
						this.scrollToBottom();
					}
					// 快速向下滑动，回到顶部
					else if (deltaY > 100 && this.scrollTop > 0) {
						console.log('检测到快速向下滑动，回到顶部');
						this.backToTop();
					}
				}
			},
			
			// 回到顶部 - 平滑滚动
			backToTop() {
				console.log('回到顶部');
				// 使用动画滚动到顶部
				this.scrollTop = 0;
				
				// 如果需要更平滑的滚动效果，可以使用这种方式
				// let currentScrollTop = this.lastScrollTop;
				// const step = currentScrollTop / 10;
				// const scrollToTop = () => {
				// 	currentScrollTop -= step;
				// 	if (currentScrollTop <= 0) {
				// 		this.scrollTop = 0;
				// 	} else {
				// 		this.scrollTop = currentScrollTop;
				// 		setTimeout(scrollToTop, 16);
				// 	}
				// };
				// scrollToTop();
			},
			
			// 滚动到底部 - 智能计算位置
			scrollToBottom() {
				console.log('滚动到底部');
				// 更精确地计算底部位置
				const query = uni.createSelectorQuery().in(this);
				query.select('.dish-list').boundingClientRect(rect => {
					if (rect) {
						// 滚动到列表底部
						this.scrollTop = rect.height;
						console.log(`滚动到底部，高度: ${rect.height}`);
					} else {
						// 备用计算方法
						const estimatedItemHeight = 180; // 估算每个菜品项的高度
						const bottomPosition = this.dishList.length * estimatedItemHeight;
						this.scrollTop = bottomPosition;
						console.log(`使用估算方法滚动到底部，位置: ${bottomPosition}`);
					}
				}).exec();
			},
			
			// 重试加载 - 新增方法
			retryLoad() {
				console.log('重试加载菜品数据');
				this.hasMore = true;
				this.getDishList(false);
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
						// 刷新列表数据
						this.getDishList(false);
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
		},
		
		// 页面销毁时清理定时器
		beforeDestroy() {
			if (this.scrollTimer) {
				clearTimeout(this.scrollTimer);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.dish-management {
		background-color: #f5f5f5;
		min-height: 100vh;
		position: relative;
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
	
	// 滚动容器样式
	.dish-scroll-container {
		height: calc(100vh - 170rpx); // 减去header和action-bar的高度
		background-color: #f5f5f5;
	}
	
	.dish-list {
		padding: 0 20rpx 20rpx 20rpx;
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
	
	// 加载更多和没有更多数据的样式
	.load-more, .no-more {
		padding: 30rpx 0;
		text-align: center;
		
		.load-more-text, .no-more-text {
			font-size: 26rpx;
			color: #999;
		}
	}
	
	// 回到顶部按钮
	.back-to-top {
		position: fixed;
		right: 30rpx;
		bottom: 100rpx;
		width: 80rpx;
		height: 80rpx;
		background-color: #FF8C00;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 4rpx 12rpx rgba(255, 140, 0, 0.3);
		z-index: 999;
		
		.back-to-top-icon {
			color: #fff;
			font-size: 32rpx;
			font-weight: bold;
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