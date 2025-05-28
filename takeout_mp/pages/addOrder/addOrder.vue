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
				<view :class="cartData.length > 0 ? 'btnSubmitActive' : ''" @click="goToPaySuccess">去支付</view>
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
					console.log("从用户信息接口获取地址...");
					
					// 获取token
					const token = uni.getStorageSync('token');
					if (!token) {
						console.log("未登录，无法获取地址");
						uni.showToast({
							title: '请先登录',
							icon: 'none',
							duration: 2000
						});
						return;
					}
					
					// 使用uni.request直接调用API
					const res = await uni.request({
						url: 'http://localhost:8080/api/v1/user/info',
						method: 'GET',
						header: {
							'customerToken': token,
							'userType': '3',
							'Content-Type': 'application/json'
						}
					});

					console.log("用户信息响应:", res);

					// 检查是否成功获取到用户信息和地址
					if (res && res[1].statusCode === 200 && res[1].data && res[1].data.data && res[1].data.data.address) {
						const userData = res[1].data.data;
						console.log("从用户信息中获取到地址:", userData.address);
						
						// 从用户信息中提取必要数据构建地址对象
						this.address = {
							detail: userData.address, // 地址详情
							consignee: userData.name || userData.username || '', // 收货人姓名
							phone: userData.phoneNum || userData.username || '', // 联系电话
							gender: userData.gender === '男' ? 0 : 1 // 性别
						};
						
						console.log("构建的地址对象:", this.address);
					} else {
						console.log("用户信息中未找到地址或获取失败");
						this.address = null; // 地址为空时提示用户选择地址
						
						// 可以显示提示信息
						uni.showToast({
							title: '未设置地址，请从个人信息页添加地址',
							icon: 'none',
							duration: 2000
						});
					}
				} catch (error) {
					console.error('获取地址失败:', error);
					this.address = null;
					
					// 显示错误信息
					uni.showToast({
						title: '获取地址失败，请重试',
						icon: 'none',
						duration: 2000
					});
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
					// 显示加载状态
					uni.showLoading({ title: '加载中...' });
					
					// 从本地存储获取购物车数据
					const cartItemsStr = uni.getStorageSync('cartItems');
					if (cartItemsStr) {
						const cartItems = JSON.parse(cartItemsStr);
						// 映射购物车数据到所需格式
						this.cartData = cartItems.map(item => ({
							id: item.id,
							name: item.name || '菜品',
							image: item.image || '/static/images/default-food.png',
							number: item.quantity || 1,
							amount: item.price || 0
						}));
						
						console.log('从本地存储加载购物车数据:', this.cartData);
					} else {
						this.cartData = [];
					}
					
					// 隐藏加载状态
					uni.hideLoading();
					
					// 检查购物车是否为空
					if (this.cartData.length === 0) {
						uni.showModal({
							title: '提示',
							content: '购物车为空，是否返回点餐页面？',
							success: function(modalRes) {
								if (modalRes.confirm) {
									uni.switchTab({
										url: '/pages/index/index'
									});
								}
							}
						});
					}
				} catch (error) {
					console.error('获取购物车数据失败:', error);
					uni.hideLoading();
					uni.$showMsg('获取购物车失败，请重试');
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
					// 准备订单数据
					const orderData = {
						address: this.address.detail,
						consignee: this.address.consignee,
						phone: this.address.phone,
						remark: this.note,
						payMethod: 1, // 默认微信支付
						orderTime: new Date().toISOString(),
						status: "PENDING", // 待付款状态
						items: this.cartData.map(item => ({
							itemId: item.id,
							name: item.name,
							image: item.image,
							quantity: item.number,
							unit: item.amount,
							total: item.amount * item.number
						})),
						total: this.goodsPrice
					};
					
					// 提交订单数据
					const res = await uni.$ajax.post({
						url: '/api/v1/orders/submit',
						data: orderData,
						header: {
							'Content-Type': 'application/json'
						}
					});
					
					if (res && (res.code === 0 || res.code === 1 || res.code === 200)) {
						// 清空本地购物车
						uni.removeStorageSync('cartItems');
						
						// 跳转到支付成功页面
						uni.navigateTo({
							url: '/pages/payConfirm/payConfirm?orderId=' + (res.id || res.data?.id || '')
						});
						
						uni.showToast({
							title: '订单提交成功',
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: res?.msg || '订单提交失败',
							icon: 'none'
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
