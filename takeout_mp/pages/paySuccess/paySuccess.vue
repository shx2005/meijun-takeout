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
			// 获取订单ID
			if (options.orderId) {
				this.orderId = options.orderId;
				this.getOrderDetail();
			}
			
			this.getFinishTime();
		},
		onUnload() {
			// 不自动跳转，由用户手动选择
		},
		methods: {
			// 查看订单详情页
			toOrderPage() {
				if (this.orderId) {
					uni.navigateTo({
						url: '/pages/orderDetail/orderDetail?orderId=' + this.orderId
					});
				} else {
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
