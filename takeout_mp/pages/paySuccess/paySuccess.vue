<template>
	<view>
		<view class="pay_success">
			<view class="divContent">
				<image src="../../static/images/success.png" />
				<view class="divSuccess">下单成功</view>
				<view class="divDesc">预计{{orderInfo.expectedDeliveryTime || finishTime}}到达</view>
				<view class="divDesc1">后厨正在加紧制作中，请耐心等待~</view>
				
				<view class="btnView" @click="toOrderPage">查看订单</view>
				<view class="btnView btnReturn" @click="returnToIndex">返回首页</view>
			</view>
		</view>
	</view>
</template>

<script>
	import regeneratorRuntime, { async } from '../../lib/runtime/runtime';
	import { getOrderDetailApi } from '../../api/index';
	
	export default {
		data() {
			return {
				finishTime: '',
				orderId: null,
				orderInfo: {}
			};
		},
		onLoad(options) {
			console.log('=== paySuccess页面onLoad调试信息 ===');
			console.log('接收到的options:', options);
			console.log('options.orderId:', options.orderId);
			console.log('options.amount:', options.amount);
			
			// 获取订单ID
			if (options.orderId) {
				this.orderId = options.orderId;
				console.log('设置orderId为:', this.orderId);
				this.getOrderDetail();
			} else {
				console.warn('警告：未接收到订单ID参数');
			}
			
			console.log('=== paySuccess页面onLoad调试结束 ===');
			
			this.getFinishTime();
		},
		onUnload() {
			// 不自动跳转，由用户手动选择
		},
		methods: {
			// 查看订单详情页
			toOrderPage() {
				console.log('点击查看订单，orderId:', this.orderId);
				if (this.orderId) {
					// 有订单ID，跳转到订单详情页
					uni.navigateTo({
						url: '/pages/orderDetail/orderDetail?orderId=' + this.orderId,
						success: () => {
							console.log('成功跳转到订单详情页，订单ID:', this.orderId);
						},
						fail: (err) => {
							console.error('跳转到订单详情页失败:', err);
							// 如果跳转失败，则跳转到订单列表页
							uni.switchTab({
								url: '/pages/orderList/orderList'
							});
						}
					});
				} else {
					// 没有订单ID，跳转到订单列表页
					console.log('没有订单ID，跳转到订单列表页');
					uni.switchTab({
						url: '/pages/orderList/orderList'
					});
				}
			},
			
			// 返回首页
			returnToIndex() {
				uni.switchTab({
					url: '/pages/index/index'
				});
			},
			
			// 获取订单详情
			async getOrderDetail() {
				if (!this.orderId) return;
				
				try {
					const res = await getOrderDetailApi(this.orderId);
					
					if (res) {
						this.orderInfo = res;
					}
				} catch (error) {
					console.error('获取订单详情失败', error);
				}
			},
			
			// 获取送达时间
			getFinishTime() {
				let now = new Date();
				now.setMinutes(now.getMinutes() + 30); // 默认30分钟后送达
				let hour = now.getHours();
				let minute = now.getMinutes();
				
				if (hour.toString().length < 2) {
					hour = '0' + hour;
				}
				if (minute.toString().length < 2) {
					minute = '0' + minute;
				}
				
				this.finishTime = hour + ':' + minute;
			}
		}
	}
</script>

<style>
@import url(./paySuccess.css);

.btnReturn {
	margin-top: 20rpx;
	background-color: #f0f0f0;
	color: #333;
}
</style>
