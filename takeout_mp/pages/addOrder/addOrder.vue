<template>
	<view>
		<view class="add_order">
			<view class="divContent">
				<view class="divAddress">
					<view @click="toAddressPage">
						<view class="address">
							<view v-if="address == null">
								<text style="color: gainsboro;font-size: 36rpx">请选择收货地址</text>

							</view>
							<view v-else>
								{{address.detail}}
							</view>
							<view class="icon">
								<u-icon name="arrow-right"></u-icon>
							</view>
						</view>

						<view class="name">
							<text
								v-if="address != null">{{address.consignee}}{{address.gender === 1 ? '女士':'先生'}}</text>
							<text>{{address.phone}}</text>


						</view>

					</view>
					<view class="divSplit"></view>
					<view class="divFinishTime">预计{{finishTime}}送达</view>
				</view>
				<view class="order">
					<view class="title">订单明细</view>
					<view class="divSplit"></view>
					<view class="itemList">
						<view class="item" v-for="(item,index) in cartData" :key="index">
							<view class="u-image1">
								<u-image width="128rpx" height="128rpx" :src="item.image">

									<image src="../../static/images/noImg.png" />

								</u-image>
							</view>

							<view class="desc">
								<view class="name">{{item.name}}</view>
								<view class="numPrice">
									<text class="num">x{{item.number}}</text>
									<view class="price">
										<text class="spanMoney">￥</text>{{item.amount * item.number}}
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
				<view class="note">
					<view class="title">备注</view>
					<u-textarea v-model="note" placeholder="请输入您需要备注的信息" maxlength="50" count border="bottom">
					</u-textarea>
					<van-field v-model="note" rows="2" autosize type="textarea" maxlength="50" placeholder="请输入您需要备注的信息"
						show-word-limit />
				</view>
			</view>
			<view class="divCart">
				<view class="imgCart" v-if="!cartData || cartData.length<1"></view>
				<view class="imgCartActive" v-else></view>

				<view :class="{divGoodsNum:1===1, moreGoods:cartData && cartData.length > 99}"
					v-if="cartData && cartData.length > 0">{{ goodsNum }}</view>
				<view class="divNum">
					<text>￥</text>
					<text>{{goodsPrice}}</text>
				</view>
				<view class="divPrice"></view>
				<view class="cartData.length > 0 ? 'btnSubmitActive' : ''" @click="goToPaySuccess">去支付</view>
			</view>
		</view>
	</view>
</template>

<script>
	import WebsocketHeartbeat from 'websocket-heartbeat-miniprogram';
	import '../../api/index.js'
	import regeneratorRuntime, {
		async
	} from '../../lib/runtime/runtime';
	import {
		cartListApi,
		categoryListApi,
		dishListApi,
		setmealListApi,
		clearCartApi,
		updateCartApi,
		setMealDishDetailsApi,
		addCartApi,
	} from '../../api/index';
	import {
		getDefaultAddressApi,
		addressListApi
	} from '../../api/address.js'
	import {
		addOrderApi
	} from '../../api/addOrder.js'

	export default {
		data() {
			return {
				timeout: 10000, // 30秒一次心跳
				timeoutObj: null, // 心跳心跳倒计时
				serverTimeoutObj: null, // 心跳倒计时
				timeoutNum: null, // 断开 重连倒计时
				lockReconnect: false, // 防止
				imageUrl: '',
				address: null,
				finishTime: '', //送达时间
				cartData: [],
				note: '', //备注信息,
				websocket: null,
				loading: false
			}
		},
		onShow() {
			// 获取上一页传递的地址
			let pages = getCurrentPages();
			let currPage = pages[pages.length - 1];
			
			if (currPage.data && currPage.data.address) {
				this.address = currPage.data.address;
			} else {
				this.getDefaultAddress();
			}
			
			this.initData();
		},
		onUnload() {
			console.log("onUnload")
			try {
				if (this.websocket) {
					this.websocket.close()
				}
			} catch (e) {
				console.log('Error closing WebSocket:', e);
			}
		},
		computed: {
			goodsNum() {
				let num = 0
				this.cartData.forEach(item => {
					num += item.number
				})
				if (num < 99) {
					return num
				} else {
					return '99+'
				}
			},
			goodsPrice() {
				let price = 0;
				this.cartData.forEach(item => {
					price += item.number * item.amount;
				});
				return price.toFixed(2);
			}
		},
		created() {
			this.initData()
			this.getFinishTime()
			// 可选择是否启用WebSocket
			// this.initWebSocket()
		},
		mounted() {},
		methods: {
			initWebSocket() {
				let token = uni.getStorageSync("token")
				let _this = this
				WebsocketHeartbeat({
					miniprogram: wx,
					connectSocketParams: {
						url:  process.env.WEBSOCKET_BASE_URL + 'mp/websocket?token=' + token
						// url:  "ws://localhost:8081/api/mp/websocket?token=" + token
					}
				}).then(task =>{
					_this.websocket = task
					task.onOpen = (e) => {//钩子函数
					            console.log('open');
					        };
					        task.onClose = (e) => {//钩子函数
					            console.log('close');
					        };
					        task.onError = e => {//钩子函数
					            console.log('onError：', e);
					        };
					        task.onMessage = data => {//钩子函数
					            console.log('onMessage', data);
					        };
					        task.onReconnect = () => {//钩子函数
					            console.log('reconnect...');
					        };
					        task.socketTask.onOpen(e => {//原生实例注册函数，重连后丢失
					            console.log('socketTask open');
					        });
					        task.socketTask.onMessage(data => {//原生实例注册函数，重连后丢失
					            console.log('socketTask data');
					        });
				})
			},
			initData() {
				//获取默认的地址
				this.getDefaultAddress()
				//获取购物车的商品
				this.getCartData()
			},
			//获取默认地址
			async getDefaultAddress() {
				try {
					const res = await getDefaultAddressApi()
					if (res.code === 0) {
						console.log("res",res.data)
						this.address = res.data
					} else {
						// 如果没有默认地址，尝试获取地址列表
						const addressList = await addressListApi()
						if (addressList && addressList.length > 0) {
							this.address = addressList[0]
						}
					}
				} catch (error) {
					console.error('获取默认地址失败', error)
				}
			},
			//获取送达时间
			getFinishTime() {
				let now = new Date()
				let hour = now.getHours() + 1
				let minute = now.getMinutes()
				if (hour.toString().length < 2) {
					hour = '0' + hour
				}
				if (minute.toString().length < 2) {
					minute = '0' + minute
				}
				this.finishTime = hour + ':' + minute
			},
			toAddressPage() {
				uni.navigateTo({
					url: '/pages/address/address'
				})
			},
			//获取购物车数据
			async getCartData() {
				try {
					const res = await cartListApi();
					
					if (res) {
						this.cartData = res.items || [];
						
						if (this.cartData.length === 0) {
							uni.showModal({
								title: '提示',
								content: '购物车为空，是否返回点餐页面？',
								success: function(res) {
									if (res.confirm) {
										uni.switchTab({
											url: '/pages/index/index'
										});
									}
								}
							});
						}
					} else {
						uni.$showMsg('获取购物车失败');
					}
				} catch (error) {
					console.error('获取购物车数据失败', error);
					uni.$showMsg('获取购物车失败，请检查网络');
				}
			},
			async goToPaySuccess() {
				if (!this.address) {
					uni.$showMsg('请选择收货地址');
					return;
				}
				
				if (this.cartData.length === 0) {
					uni.$showMsg('购物车为空，请先添加商品');
					return;
				}
				
				if (this.loading) return;
				this.loading = true;
				
				try {
					const orderData = {
						addressBookId: this.address.id,
						remark: this.note,
						payMethod: 1, // 默认微信支付
						expectedDeliveryTime: this.finishTime
					};
					
					const res = await uni.$ajax.post({
						url: 'api/v1/orders/submit',
						data: orderData
					});
					
					if (res) {
						// 清空购物车
						await clearCartApi();
						
						// 跳转到支付成功页面
						uni.navigateTo({
							url: '/pages/paySuccess/paySuccess?orderId=' + res.id
						});
					}
				} catch (error) {
					console.error('提交订单失败', error);
					uni.$showMsg('提交订单失败，请重试');
				} finally {
					this.loading = false;
				}
			},
		}
	}
</script>

<style>
	@import url(./addOrder.css);
</style>
<style>
	page {
		background-color: #f3f2f7;
	}
</style>
