<template>
	<view>
		<view class="add_order">
			<view class="divContent">
				<view class="divAddress">
					<view>
						<view class="address">
							<view v-if="address == null">
								<text style="color: gainsboro;font-size: 36rpx">请选择收货地址</text>
							</view>
							<view v-else>
								{{address.detail}}
							</view>
						</view>

						<view class="name">
							<text
								v-if="address != null && address.consignee">{{address.consignee}}{{address.gender === 1 ? '女士':'先生'}}</text>
							<text v-if="address != null && address.phone">{{address.phone}}</text>
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
	import WebsocketHeartbeat from '@/node_modules/websocket-heartbeat-miniprogram';
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
	import request from '@/utils/request.js';

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
						url:  process.env.WEBSOCKET_BASE_URL + 'mp/websocket?token=' + token || "ws://localhost:8080/api/mp/websocket?token=" + token
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
			//获取默认地址 (现在主要从用户信息获取，再尝试地址列表)
			async getDefaultAddress() {
				try {
					console.log("尝试从用户信息接口获取地址...");
					const userInfoRes = await request({ 
						url: '/api/v1/user/info',
						method: 'GET'
					});

					if (userInfoRes && (userInfoRes.code === 0 || userInfoRes.code === 1 || userInfoRes.code === 200) && userInfoRes.data && userInfoRes.data.address) {
						console.log("从用户信息中获取到地址:", userInfoRes.data.address);
						this.address = {
							detail: userInfoRes.data.address, // 主要显示这个
							consignee: userInfoRes.data.name || (userInfoRes.data.nickName || ''), // 尝试用用户名或昵称
							phone: userInfoRes.data.phone || '',   // 尝试用用户电话
							// id: undefined, // 明确这个地址不是来自地址簿，没有id
						};
						return; // 成功获取到用户地址，直接返回
					}

					// 如果用户信息中没有地址，或者接口调用有问题，尝试从地址列表获取
					console.log("用户信息中未找到地址或获取失败，尝试从地址列表获取...");
					const addressListRes = await addressListApi(); // 确保 addressListApi 已导入
					// 检查 addressListRes 的具体结构，API文档中未明确，常见结构为 res.data 是数组或 res 直接是数组
					let addresses = [];
					if (addressListRes) {
						if (Array.isArray(addressListRes.data) && addressListRes.data.length > 0) {
							addresses = addressListRes.data;
						} else if (Array.isArray(addressListRes) && addressListRes.length > 0) { // 兼容直接返回数组的情况
							addresses = addressListRes;
						}
					}

					if (addresses.length > 0) {
						console.log("从地址列表获取到地址:", addresses[0]);
						this.address = addresses[0]; // 使用地址列表的第一个
					} else {
						console.log("地址列表也为空或获取失败");
						this.address = null; // 都没有获取到，则为null
					}

				} catch (error) {
					console.error('获取地址过程中发生错误（可能是用户信息接口或地址列表接口）:', error);
					// 即使上面出错，也尝试一下地址列表作为最终手段，以防万一
					try {
						console.log("捕获异常后，最后尝试从地址列表获取...");
						const addressListRes = await addressListApi();
						let addresses = [];
						if (addressListRes) {
							if (Array.isArray(addressListRes.data) && addressListRes.data.length > 0) {
								addresses = addressListRes.data;
							} else if (Array.isArray(addressListRes) && addressListRes.length > 0) {
								addresses = addressListRes;
							}
						}
						if (addresses.length > 0) {
							this.address = addresses[0];
						} else {
							this.address = null;
						}
					} catch (finalFallbackError) {
						console.error("最终尝试从地址列表获取也失败:", finalFallbackError);
						this.address = null; // 最终确保为null
					}
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
			/* toAddressPage() { // 方法已不再需要，可以删除或注释掉
				uni.navigateTo({
					url: '/pages/address/address'
				})
			}, */
			//获取购物车数据
			async getCartData() {
				try {
					// const res = await cartListApi(); // 旧的API调用，将被替换
					console.log("尝试调用新的 getCart 接口...");
					const res = await request({ // 使用全局的 request 实例
						url: '/api/v1/cart',     // 假设这是新的获取购物车接口URL
						method: 'GET'
					});
					
					// 根据新的接口响应结构处理数据
					// 假设成功时 res.code 为 0, 1 或 200, 且 res.data 是 CartItem 数组
					if (res && (res.code === 0 || res.code === 1 || res.code === 200) && Array.isArray(res.data)) {
						this.cartData = res.data;
						console.log("新接口获取购物车数据成功:", this.cartData);

						// 如果后端返回的是 { code: ..., data: { items: [...] } } 结构
						// else if (res && (res.code === 0 || res.code === 1 || res.code === 200) && res.data && Array.isArray(res.data.items)) {
						//   this.cartData = res.data.items;
						//   console.log("新接口获取购物车数据成功 (items 字段):", this.cartData);
						// }
						
						if (this.cartData.length === 0) {
							uni.showModal({
								title: '提示',
								content: '购物车为空，是否返回点餐页面？',
								success: function(modalRes) {
									if (modalRes.confirm) {
										uni.switchTab({
											url: '/pages/index/index' // 假设首页是点餐页
										});
									}
								}
							});
						}
					} else {
						console.error("新接口获取购物车数据失败或格式不正确:", res);
						uni.$showMsg(res.msg || '获取购物车失败');
						this.cartData = []; // 清空旧数据以防显示错误
					}
				} catch (error) {
					console.error('调用新 getCart 接口失败:', error);
					uni.$showMsg('获取购物车失败，请检查网络');
					this.cartData = [];
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
						url: 'v1/orders/submit',
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
