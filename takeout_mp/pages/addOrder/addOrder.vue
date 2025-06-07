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
							<scroll-view v-else scroll-x="true" class="address-scroll-view">
								<view class="address-content">
								{{address.detail}}
							</view>
							</scroll-view>
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

					// 先检查响应是否成功
					if (res && res[1].statusCode === 200) {
						// 处理XML格式的响应
						const responseData = res[1].data;
						
						// 检查是否为XML格式的字符串
						if (typeof responseData === 'string' && responseData.includes('<Result>')) {
							console.log('解析XML格式的用户信息');
							
							// 从XML中提取地址
							const addressMatch = responseData.match(/<address>(.*?)<\/address>/);
							const nameMatch = responseData.match(/<name>(.*?)<\/name>/);
							const usernameMatch = responseData.match(/<username>(.*?)<\/username>/);
							const phoneMatch = responseData.match(/<phoneNum>(.*?)<\/phoneNum>/);
							const genderMatch = responseData.match(/<gender>(.*?)<\/gender>/);
							
							if (addressMatch && addressMatch[1]) {
								const address = addressMatch[1];
								console.log("从XML中获取到地址:", address);
								
								// 构建地址对象
								this.address = {
									detail: address,
									consignee: nameMatch && nameMatch[1] ? nameMatch[1] : (usernameMatch && usernameMatch[1] ? usernameMatch[1] : ''),
									phone: phoneMatch && phoneMatch[1] ? phoneMatch[1] : (usernameMatch && usernameMatch[1] ? usernameMatch[1] : ''),
									gender: genderMatch && genderMatch[1] === '男' ? 0 : 1
								};
								
								console.log("构建的地址对象:", this.address);
								return;
							}
						} 
						// 处理JSON格式的响应
						else if (res[1].data && res[1].data.data) {
						const userData = res[1].data.data;
							if (userData.address) {
								console.log("从JSON中获取到地址:", userData.address);
						
						// 从用户信息中提取必要数据构建地址对象
						this.address = {
							detail: userData.address, // 地址详情
							consignee: userData.name || userData.username || '', // 收货人姓名
							phone: userData.phoneNum || userData.username || '', // 联系电话
							gender: userData.gender === '男' ? 0 : 1 // 性别
						};
						
						console.log("构建的地址对象:", this.address);
								return;
							}
						}
					}
					
					// 如果没有成功获取地址
						console.log("用户信息中未找到地址或获取失败");
						this.address = null; // 地址为空时提示用户选择地址
						
					// 显示提示信息
						uni.showToast({
							title: '未设置地址，请从个人信息页添加地址',
							icon: 'none',
							duration: 2000
						});
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
					
					// 检查登录状态
					const token = uni.getStorageSync('token');
					if (!token) {
						uni.hideLoading();
						uni.showToast({
							title: '请先登录',
							icon: 'none'
						});
						setTimeout(() => {
							uni.navigateTo({
								url: '/pages/my/my'
							});
						}, 1500);
						return;
					}
					
					// 从服务器获取购物车数据
					const res = await uni.request({
						url: 'http://localhost:8080/api/v1/cart',
						method: 'GET',
						header: {
							'customerToken': token,
							'userType': '3',
							'Content-Type': 'application/json'
						}
					});
					
					console.log('服务器购物车数据响应:', res);
					
					// 检查响应是否成功且有数据
					const response = res[1];
					if (response && response.statusCode === 200 && response.data) {
						let result = response.data;
						
						// 检查是否为XML格式的响应
						if (typeof result === 'string' && result.includes('<Result>')) {
							console.log('检测到XML格式购物车响应，开始解析');
							result = this.parseXMLCartResponse(result);
							console.log('XML解析后的结果:', result);
						}
						
						if (result && result.code === 200 && result.data && result.data.items && result.data.items.length > 0) {
							// 使用服务器返回的购物车数据
							this.cartData = result.data.items.map(item => ({
								id: item.itemId || item.id,
								name: item.name || '菜品',
								image: item.image || '/static/images/default-food.png',
								number: item.quantity || 1,
								amount: item.price || 0
							}));
							
							console.log('从服务器获取的购物车数据:', this.cartData);
						} else {
							// 购物车为空
							this.cartData = [];
							console.log('购物车为空');
						}
					} else {
						// 获取失败
						console.error('获取购物车数据失败:', response);
						this.cartData = [];
					}
					
					// 隐藏加载状态
					uni.hideLoading();
					
					// 如果没有数据，提示用户
					if (!this.cartData || this.cartData.length === 0) {
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
					uni.showToast({
						title: '获取购物车失败，请重试',
						icon: 'none'
					});
					this.cartData = [];
				}
			},

			// 解析XML格式的购物车响应
			parseXMLCartResponse(xmlString) {
				try {
					console.log('开始解析XML购物车响应');
					
					// 提取基本信息
					const codeMatch = xmlString.match(/<code>(.*?)<\/code>/);
					const msgMatch = xmlString.match(/<msg>(.*?)<\/msg>/);
					const successMatch = xmlString.match(/<success>(.*?)<\/success>/);
					const totalMatch = xmlString.match(/<total>(.*?)<\/total>/);
					
					// 提取所有购物车项
					const itemsMatches = xmlString.match(/<items>(.*?)<\/items>/g);
					const items = [];
					
					if (itemsMatches) {
						itemsMatches.forEach(itemXml => {
							const idMatch = itemXml.match(/<id>(.*?)<\/id>/);
							const nameMatch = itemXml.match(/<name>(.*?)<\/name>/);
							const cartIdMatch = itemXml.match(/<cartId>(.*?)<\/cartId>/);
							const userIdMatch = itemXml.match(/<userId>(.*?)<\/userId>/);
							const itemIdMatch = itemXml.match(/<itemId>(.*?)<\/itemId>/);
							const itemTypeMatch = itemXml.match(/<itemType>(.*?)<\/itemType>/);
							const quantityMatch = itemXml.match(/<quantity>(.*?)<\/quantity>/);
							const priceMatch = itemXml.match(/<price>(.*?)<\/price>/);
							const itemTotalMatch = itemXml.match(/<total>(.*?)<\/total>/);
							
							if (idMatch && nameMatch && itemIdMatch) {
								items.push({
									id: parseInt(idMatch[1]),
									name: nameMatch[1],
									cartId: cartIdMatch ? parseInt(cartIdMatch[1]) : null,
									userId: userIdMatch ? parseInt(userIdMatch[1]) : null,
									itemId: parseInt(itemIdMatch[1]),
									itemType: itemTypeMatch ? itemTypeMatch[1] : 'dish',
									quantity: quantityMatch ? parseInt(quantityMatch[1]) : 1,
									price: priceMatch ? parseFloat(priceMatch[1]) : 0,
									total: itemTotalMatch ? parseFloat(itemTotalMatch[1]) : 0
								});
							}
						});
					}
					
					const result = {
						code: codeMatch ? parseInt(codeMatch[1]) : 200,
						msg: msgMatch ? msgMatch[1] : 'OK',
						data: {
							id: null,
							userId: 1,
							items: items,
							total: totalMatch ? parseFloat(totalMatch[1]) : 0
						},
						success: successMatch ? successMatch[1] === 'true' : true
					};
					
					console.log('XML解析完成，结果:', result);
					return result;
				} catch (error) {
					console.error('解析XML购物车响应失败:', error);
					return {
						code: 500,
						msg: 'XML解析失败',
						data: { items: [] },
						success: false
					};
				}
			},
			async goToPaySuccess() {
				if (!this.address) {
					uni.showToast({
						title: '请选择收货地址',
						icon: 'none'
					});
					return;
				}
				
				if (!this.cartData || this.cartData.length === 0) {
					// 如果购物车为空，再次尝试获取购物车数据
					await this.getCartData();
					
					// 再次检查购物车是否为空
					if (!this.cartData || this.cartData.length === 0) {
						uni.showToast({
							title: '购物车为空，请先添加商品',
							icon: 'none'
						});
						return;
					}
				}
				
				if (this.loading) return;
				this.loading = true;
				
				try {
					// 获取token
					const token = uni.getStorageSync('token');
					if (!token) {
						uni.showToast({
							title: '请先登录',
							icon: 'none'
						});
						this.loading = false;
						return;
					}
					
					// 计算总价
					const totalPrice = this.cartData.reduce((sum, item) => {
						return sum + (item.number * item.amount);
					}, 0);
					
					// 准备订单数据 - 使用用户指定的简化参数格式
					const orderData = {
						userId: 1,
						total: totalPrice
					};
					
					console.log('准备提交订单:', orderData);
					
					// 提交订单
					const response = await uni.request({
						url: 'http://localhost:8080/api/v1/orders/submit',
						method: 'POST',
						header: {
							'customerToken': token,
							'Accept': 'application/json',
							'userType': '3',
							'Content-Type': 'application/json'
						},
						data: orderData
					});
					
					console.log('订单提交响应:', response);
					
					// 检查响应状态
					const res = response[1];
					if (res && res.statusCode === 200) {
						const result = res.data;
					
						if (result && (result.code === 0 || result.code === 200 || result.success)) {
							// 订单提交成功
							const orderId = result.data?.id || result.id || '';
							const orderNumber = result.data?.orderNumber || '';
							
							// 跳转到支付确认页面，传递订单信息
							const orderInfo = {
								orderId: orderId,
								orderNumber: orderNumber,
								total: totalPrice
							};
							
							uni.navigateTo({
								url: '/pages/payConfirm/payConfirm?orderInfo=' + encodeURIComponent(JSON.stringify(orderInfo))
							});
							
							uni.showToast({
								title: '订单提交成功',
								icon: 'success'
							});
						} else {
							uni.showToast({
								title: result?.msg || '订单提交失败',
								icon: 'none'
							});
						}
					} else {
						uni.showToast({
							title: '订单提交失败',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('提交订单失败', error);
					uni.showToast({
						title: '提交订单失败，请重试',
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},
			// 清空服务器购物车
			async clearCartInServer(token) {
				try {
					// 先获取购物车数据
					const res = await uni.request({
						url: 'http://localhost:8080/api/v1/cart',
						method: 'GET',
						header: {
							'customerToken': token,
							'userType': '3',
							'Content-Type': 'application/json'
						}
					});
					
					// 如果有数据，循环删除每个商品
					if (res && res[1].statusCode === 200 && res[1].data && res[1].data.data && res[1].data.data.items) {
						const items = res[1].data.data.items;
						for (const item of items) {
							// 删除购物车中的商品
							await uni.request({
								url: 'http://localhost:8080/api/v1/cart/delete',
								method: 'DELETE',
								header: {
									'customerToken': token,
									'Accept': 'application/json',
									'userType': '3',
									'Content-Type': 'application/json'
								},
								data: {
									itemId: item.itemId || item.id
								}
							});
							console.log(`删除购物车商品: ${item.itemId || item.id}`);
						}
					}
					
					console.log('清空服务器购物车完成');
				} catch (error) {
					console.error('清空购物车失败:', error);
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
	
	.address-scroll-view {
		width: 100%;
		white-space: nowrap;
	}
	
	.address-content {
		display: inline-block;
		font-size: 28rpx;
		color: #333;
		padding: 10rpx 0;
	}
</style>
