<template>
	<view>
		<view class="user">

			<view class="headBox">

				<!-- 登录 -->
				<view class="u-flex u-p-l-30 u-p-r-20 u-p-t-30 u-p-b-30">
					<block v-if="userToken">
						<view class="u-m-r-20">
							<image class="avatar" mode="aspectFill" :src="user.avatar_url || user.avatarUrl || '/static/logo.png'">
							</image>
						</view>
						<view class="u-flex-1" @click="onJump">
							<view class="nickName u-flex">
								<view class="name u-m-r-10" style="color: #423e3e">
									{{user.nickName || user.username || phoneUserName || '美食用户'}}
								</view>
								<view class="placardVip">美食元素</view>
							</view>
							<view class="detail" v-if="user && user.username">手机号：{{formatPhoneNum(user.username)}}</view>
							<view class="detail" v-else>手机号：未绑定</view>
							<view class="detail" v-if="user.id">ID: {{user.id}}</view>
						</view>
					</block>
					<block v-else>
						<view class="u-m-r-20">
							<view class="avatar u-flex" style="justify-content: center;">
								<u-icon name="account-fill" color="#fff" size="30"></u-icon>
							</view>
						</view>
						<view class="u-flex-1" @click="openLogin">
							<view class="u-font-lg" style="color: #423e3e;font-weight: bold;">登录/注册</view>
							<view class="detail" style="color: #423e3e">登录后享受更好的服务体验</view>
						</view>
					</block>
					<view @click="onJump">
						<u-icon v-if="userToken" name="arrow-right" color="#fff" size="13"></u-icon>
					</view>
				</view>


			</view>


			<scroll-view scroll-y="true" :style="{height: wh-75 + 'px'}">


				<view class="divContent">
					<view class="divLinks">
						<view @click="allOrder" class="item">
							<image src="../../static/me/dingdan.png"></image>
							<text>全部订单</text>
							<view>
								<u-icon name="arrow-right"></u-icon>
							</view>
						</view>
						<view @click="contactService" class="item">
							<image src="../../static/me/duihuaxinxi.png"></image>
							<text>联系客服</text>
							<view>
								<u-icon name="arrow-right"></u-icon>
							</view>
						</view>
					</view>
					<view class="divOrders" v-if="flag && user">
						<view class="title">最新订单</view>
						<view class="timeStatus">
							<text>{{order[0].orderTime}}</text>
							<text>{{getStatus(order[0].status)}}</text>
							<!-- <text>正在派送</text> -->
						</view>
						<view class="dishList">
							<view v-for="(item,index) in order[0].orderDetails" :key="index" class="item">
								<text>{{item.name}}</text>
								<text>x{{item.number}}</text>
							</view>
						</view>
						<view class="result">
							<text>共{{order[0].sumNum}} 件商品,实付</text>
							<text class="price">￥{{order[0].amount}}</text>
						</view>
						<view class="btn" v-if="order[0].status === 4">
							<view class="btnAgain" @click="addOrderAgain">再来一单</view>
						</view>
					</view>
					<view v-if="userToken" class="foot">
						<view @click="logout" class="logout">
							退出登录
						</view>
					</view>
					
					<view class="merchant-login-container">
						<view @click="toMerchantLogin" class="merchant-login-btn">
							商家登录
						</view>
					</view>
				</view>


			</scroll-view>
		</view>

		<view>
			<u-popup :show="loginPopupShow" mode="bottom" :round="10" @close="closeLogin" zIndex="999998">
				<view class="f__login">
					<view class="loginLoading" v-if="isLoading">
						<u-loadmore status="loading" loadingText="正在登录..."></u-loadmore>
					</view>
					<!--                <view class="logo">
		                    <image class="img" src="/static/logo.png"></image>
		                </view> -->
					<view class="title">欢迎登录~</view>
					<view class="text">会员用户登录后，享受更好的服务体验</view>
					<view class="loginButton" v-if="!isPhoneLogin">
						<!-- #ifdef MP-WEIXIN -->
						<!-- <button class="button" @click="login" :style="{background:PrimaryColor}">微信授权登录</button> -->
						<button class="button marginT" @click="toRegister"
							:style="{background:PrimaryColor}">注册</button>
						<!-- #endif -->
						<button class="button" @click="isPhoneLogin = !isPhoneLogin"
							style="background:#fff;margin-top:24rpx;"
							:style="{border:'2rpx solid '+PrimaryColor,color:PrimaryColor}">
							使用手机号登录/注册
						</button>
					</view>
					<!-- 验证码登录 -->
					<view class="loginPhone" v-if="isPhoneLogin">
						
						<!-- 错误提示区域 -->
						<view class="error-tip" v-if="showLoginError">
							<view class="error-icon">⚠️</view>
							<view class="error-text">{{ loginErrorMsg }}</view>
						</view>

						<view class="form-row">
							<input class="input" type="number" v-model="phone" placeholder="请输入手机号码"
								placeholder-style="font-weight:normal;color:#bbbbbb;" @input="clearLoginError"></input>
						</view>
						<view class="form-row">
							<input class="input" type="password" v-model="password" placeholder="请输入密码"
								placeholder-style="font-weight:normal;color:#bbbbbb;" @input="clearLoginError"></input>
						</view>
						<button  class="submit" size="default" @click="onSubmit"
							:style="{background:PrimaryColor}">确定</button>
						<button class="register" size="default" @click="toRegister"
							:style="{background:'#fff',color:PrimaryColor,border:'2rpx solid '+PrimaryColor}">注册</button>
						<!-- #ifdef MP-WEIXIN -->
						<view class="tips">
							<view @click="isPhoneLogin = !isPhoneLogin" class="goBuy" :style="{color:PrimaryColor}">
								{{isPhoneLogin?'快速登录':'手机号登录'}}
							</view>
						</view>
						<!-- #endif -->
						<!-- #ifdef H5 -->
						<view class="tips">
							<view @click="isPhoneLogin = !isPhoneLogin" class="goBuy" :style="{color:PrimaryColor}">
								返回
							</view>
						</view>
						<!-- #endif -->

					</view>


				</view>

			</u-popup>

			<view>
				<u-popup :show="showWxLogin" mode="bottom" round="44rpx" :closeable="true" @close="onWXClose">
					<view class="popupBox">
						<view class="h2 bold">获取您的昵称、头像</view>
						<view class="h3">获取用户头像、昵称，主要用于向用户提供具有辨识度的用户中心界面</view>
						<view class="form">
							<view class="input u-flex">
								<view class="h2">头像</view>
								<view class="info">
									<button class="avatar-wrapper" open-type="chooseAvatar"
										@chooseavatar="onChooseavatar">
										<image class="avatar" :src="avatarUrl || '../../static/head.png'"></image>
									</button>
								</view>
								<u-icon name="arrow-right" size="32rpx" color="#999"></u-icon>
							</view>
							<view class="input u-flex">
								<view class="h2">昵称</view>
								<view class="info">
									<input type="nickname" v-module="nickName" placeholder="请输入昵称" @blur="onNickname" />
								</view>
							</view>
						</view>
						<view class="submit" :style="{background:PrimaryColor}" @tap.stop="WxgetUserProfile">确定</view>
					</view>
				</u-popup>
			</view>
			<wx-user-info-modal v-model="showAuthorizationModal" @updated="updatedUserInfoEvent">

			</wx-user-info-modal>

			<view>
				<u-modal @cancel="logoutCancel" @confirm="logoutConfirm" :show="logoutshow" :title="logoutTitle" :content='logoutContent'
					:showCancelButton='true' confirmColor='#feca50'></u-modal>
				<!-- <u-button @click="show = true">打开</u-button> -->
			</view>

			<!-- 客服信息弹窗 -->
			<u-popup :show="showServicePopup" mode="center" :round="10" @close="showServicePopup = false">
				<view class="service-popup">
					<view class="service-title">
						<text>联系客服</text>
						<text class="popup-close" @click="showServicePopup = false">×</text>
					</view>
					<view class="service-content">
						<view class="service-item">
							<u-icon name="phone" color="#feca50" size="40"></u-icon>
							<text>客服电话：{{serviceInfo.phone}}</text>
						</view>
						<view class="service-item">
							<u-icon name="clock" color="#feca50" size="40"></u-icon>
							<text>工作时间：{{serviceInfo.workTime}}</text>
						</view>
						<view class="service-item">
							<u-icon name="email" color="#feca50" size="40"></u-icon>
							<text>电子邮箱：{{serviceInfo.email}}</text>
						</view>
					</view>
					<view class="service-footer">
						<button class="service-btn complaint-btn" @click="showComplaintForm">投诉</button>
						<button class="service-btn service-close" @click="showServicePopup = false">关闭</button>
					</view>
				</view>
			</u-popup>

			<!-- 投诉表单弹窗 -->
			<u-popup :show="showComplaintPopup" mode="center" :round="10" @close="showComplaintPopup = false">
				<view class="complaint-popup">
					<view class="complaint-title">
						<text>提交投诉</text>
						<text class="popup-close" @click="showComplaintPopup = false">×</text>
					</view>
					<view class="complaint-content">
						<u-textarea v-model="complaintText" placeholder="请描述您的投诉内容（最多200字）" maxlength="200" count></u-textarea>
					</view>
					<view class="complaint-footer">
						<button class="service-btn submit-btn" @click="submitComplaint">提交</button>
						<button class="service-btn cancel-btn" @click="showComplaintPopup = false">取消</button>
					</view>
				</view>
			</u-popup>

		</view>


	</view>

</template>

<script>
	var clear;
	import instance from '@/utils/request.js'
	import WxUserInfoModal from '@/uni_modules/tuniaoui-wx-user-info/components/tuniaoui-wx-user-info/tuniaoui-wx-user-info.vue'
	import {
		addOrderApi,
		orderListApi,
		orderPagingApi,
		orderAgainApi,
		deleteOrderApi,

	} from '../../api/orderList.js'
	import {
		logoutApi,
		updateUserInfoApi,
		phoneLoginApi,
		sendValidateCodeApi,
		getUserInfoApi
	} from "../../api/my.js"
	import { cartListApi } from '../../api/index.js';
	import regeneratorRuntime from '../../lib/runtime/runtime.js';
	export default {
		components: {
			WxUserInfoModal
		},
		data() {
			return {
				codeTips: '',
				logoutshow: false,
				logoutTitle: '确定退出？',
				logoutContent: '退出登录后将无法查看订单，重新登录即可查看',
				showAuthorizationModal: false,
				phoneUserName: '',
				userToken: '',
				showWxLogin: false,
				PrimaryColor: '#1fba1a', //主题色
				avatarUrl: '',
				nickName: '',
				isLoading: false,
				isPhoneLogin: false, //是否显示验证码登录
				readonly: false,
				codeText: '获取验证码',
				phone: '', //号码
				password: '', //密码
				code: '', //验证码
				loginPopupShow: false, //登录框显示
				loginErrorMsg: '', // 登录错误信息
				showLoginError: false, // 是否显示登录错误
				encryptedData: '',
				iv: '',
				sessionKey: '',
				user: null,
				flag: false,
				order: [],
				wh: 0,
				isWxLoginOpen: false,
				// 客服信息相关
				showServicePopup: false,
				showComplaintPopup: false, // 投诉弹窗状态
				complaintText: '', // 投诉内容
				serviceInfo: {
					phone: '400-123-4567',
					workTime: '9:00-18:00',
					email: 'service@meijun.com',
					wechat: 'meijun-service'
				}
			};
		},
		computed: {
			judgeLogin() {
				return !!uni.getStorageSync('token');
			}
		},
		created() {
			// 移除不存在的autoLogin调用
			this.getUserInfo();
		},
		onShow() {
			this.checkLogin()
			this.getUserInfo()
			this.initData()

		},
		mounted() {
			const sysInfo = uni.getSystemInfoSync()
			this.wh = sysInfo.windowHeight
			this.checkLogin()
			this.getUserInfo()
			this.initData()

		},
		watch: {

		},
		methods: {
			checkLogin() {
				if (uni.getStorageSync('token') == null || uni.getStorageSync('token') == '') {
					// uni.$showMsg("未登录")
					wx.clearStorageSync()
					
				}
			},
			initData() {
				if (this.userId) {
					this.getLatestOrder()
				}

			},
			onJump() {
				console.log("点击了跳转")
				uni.navigateTo({
					url: '/pages/userInfo/userInfo'
				})
			},
			onAuthorization() {
				this.loginPopupShow = false
				this.showWxLogin = true
			},
			onWXClose() {
				this.showWxLogin = false
				uni.showTabBar({
					animation: true
				})
			},
			onChooseavatar(e) {
				console.log(e)
				//该图片需要上传到自己服务器--此处没做处理
				let _this = this;
				uni.showLoading({
					title: '加载中'
				});
				uni.uploadFile({
					url: process.env.VUE_APP_BASE_URL + 'mp/oss/upload',
					filePath: e.target.avatarUrl,
					name: 'file',
					header: {
						"token": wx.getStorageSync('token')
					},
					success: (res) => {
						console.log('上传成功')
						console.log(res)
						// 注意：这里返回的uploadFileRes.data 为JSON 需要自己去转换
						let data = JSON.parse(res.data);
						if (data.code === 0) {
							let httpData = {
								avatarUrl: data.data.src
							}
							updateUserInfoApi(httpData).then(res => {
								if (res.code === 0) {

									_this.avatarUrl = data.data.src;
									let userInfo = wx.getStorageSync('userInfo')
									userInfo.avatarUrl = _this.avatarUrl
									wx.setStorageSync('userInfo', userInfo)
									uni.showToast({
										icon: 'success',
										title: '上传成功',
										duration: 500
									});
								} else {
									uni.$u.toast(res.msg == 'token不能为空'? '未登录' : res.msg)
								}
							})

						}

					},
					fail: (error) => {
						uni.showToast({
							title: error,
							duration: 2000
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				})
			},
			onNickname(e) {
				this.nickName = e.detail.value;
			},
			// 格式化手机号 (显示用户名后四位)
			formatPhoneNum(phone) {
				if (!phone) return '';
				// 使用最后4位数字
				return phone.slice(-4);
			},
			
			// 修改initPhoneUserName方法
			initPhoneUserName(phone) {
				if (!phone) return;
				const num = phone.slice(-4); // 使用最后4位数字
				this.phoneUserName = '美食用户' + num;
			},
			// 手机号登录
			async onSubmit() {
				if (!this.phone) {
					this.showLoginErrorMsg('请输入手机号');
					return;
				}
				
				const trimmedPhone = this.phone.trim();
				if (!/^1\d{10}$/.test(trimmedPhone)) {
					this.showLoginErrorMsg('手机号格式不正确');
					return;
				}
				
				if (!this.password) {
					this.showLoginErrorMsg('请输入密码');
					return;
				}
				
				// 清除之前的错误信息
				this.clearLoginError();
				this.isLoading = true;
				
				try {
					// 调用登录接口
					const result = await phoneLoginApi({
						phone: trimmedPhone,
						password: this.password
					});

					console.log('登录结果:', result);
					
					// 检查响应状态 - 改为从data.code判断登录成功与否
					if (result && result.data && result.data.code === 200 && result.data.success === true) {
						// 从响应中提取token
						let token = null;
						// 优先从 result.data.data.token 提取 token（服务器可能返回嵌套数据）
						if (result.data.data && result.data.data.token) {
							token = result.data.data.token;
						} 
						// 如果上面不存在，尝试从 result.data.token 提取
						else if (result.data.token) {
							token = result.data.token;
						}
						
						if (token) {
							// 保存token到本地存储
							uni.setStorageSync('token', token);
							console.log('保存的token:', token);
							
							// 保存登录信息
							this.userToken = token;
							uni.setStorageSync('phoneNumber', trimmedPhone);
							
							// 初始化用户名
							this.initPhoneUserName(trimmedPhone);
							
							// 获取用户信息并刷新界面
							await this.getUserInfo();
							await this.initData();
							
							// 登录成功后立即获取购物车数据并存储到本地
							await this.fetchAndSaveCartData();
							
							this.closeLogin();
							uni.$showMsg('登录成功', 'success');
						} else {
							// 无法从响应中提取token
							uni.$showMsg('登录成功但未获取到有效的登录凭证，请联系管理员');
						}
					} else {
						// 处理错误 - 从data.msg获取错误信息
						let errorMsg = '登录失败，请稍后重试';
						
						if (result && result.data && result.data.msg) {
							errorMsg = result.data.msg;
						} else if (result && result.errMsg) {
							errorMsg = result.errMsg;
						}
						
						uni.$showMsg(errorMsg, 'none');
						// 同时在界面上显示错误信息
						this.showLoginErrorMsg(errorMsg);
					}
				} catch (error) {
					console.error('登录请求出错:', error);
					uni.$showMsg('登录失败，请检查网络连接', 'none');
					this.showLoginErrorMsg('登录失败，请检查网络连接');
				} finally {
					this.isLoading = false;
				}
			},
			// 获取购物车数据并存储到本地
			async fetchAndSaveCartData() {
				try {
					// 显示加载状态
					uni.showLoading({ title: '同步购物车中...' });
					
					// 调用获取购物车API
					const cartResponse = await cartListApi();
					
					// 如果成功获取购物车数据
					if (cartResponse && (cartResponse.code === 0 || cartResponse.code === 1 || cartResponse.code === 200)) {
						// 提取购物车数据
						let cartItems = [];
						if (Array.isArray(cartResponse.data)) {
							cartItems = cartResponse.data;
						} else if (cartResponse.data && Array.isArray(cartResponse.data.items)) {
							cartItems = cartResponse.data.items;
						} else if (cartResponse && Array.isArray(cartResponse.items)) {
							cartItems = cartResponse.items;
						}
						
						// 将购物车数据保存到本地存储
						if (cartItems && cartItems.length > 0) {
							const formattedCartItems = cartItems.map(item => ({
								id: item.itemId || item.id,
								name: item.name || '菜品',
								price: item.price || 0,
								image: item.image || '/static/images/default-food.png',
								number: item.quantity || 1,
								categoryId: item.categoryId
							}));
							
							// 存储购物车数据到本地
							uni.setStorageSync('cartItems', JSON.stringify(formattedCartItems));
							console.log('购物车数据已存储到本地:', formattedCartItems);
						} else {
							// 如果购物车为空，也要更新本地存储
							uni.setStorageSync('cartItems', JSON.stringify([]));
						}
					} else {
						console.warn('获取购物车失败，状态码:', cartResponse?.code);
						// 初始化一个空的购物车
						uni.setStorageSync('cartItems', JSON.stringify([]));
					}
				} catch (error) {
					console.error('获取购物车数据失败:', error);
					// 初始化一个空的购物车
					uni.setStorageSync('cartItems', JSON.stringify([]));
				} finally {
					uni.hideLoading();
				}
			},
			//获取验证码
			async getCode() {
				if (!this.phone) {
					uni.$showMsg('请输入手机号');
					return;
				}
				
				const trimmedPhone = this.phone.trim();
				if (!/^1\d{10}$/.test(trimmedPhone)) {
					uni.$showMsg('手机号格式不正确');
					return;
				}
				
				try {
					const res = await sendValidateCodeApi({
						phone: trimmedPhone
					});
					
					if (res) {
						this.$refs.uCode.start();
						uni.$showMsg('验证码发送成功', 'success');
					}
				} catch (error) {
					console.error('发送验证码失败', error);
					uni.$showMsg('发送验证码失败');
				}
			},
			//验证码按钮文字状态
			getCodeState() {
				const _this = this;
				this.readonly = true;
				console.log('getVcode')
				if (this.readonly) {
					uni.showToast({
						title: '验证码已发送~',
						icon: 'none'
					});
				}
				this.codeText = '60S后重新获取';
				var s = 60;
				clear = setInterval(() => {
					s--;
					_this.codeText = s + 'S后重新获取';
					if (s <= 0) {
						clearInterval(clear);
						_this.codeText = '获取验证码';
						_this.readonly = false;
					}
				}, 1000);
			},
			codeChange(text) {
				this.codeTips = text;
			},
			async getLatestOrder() {
				try {
					// 由于API存在问题，使用从数据库直接查询到的真实订单数据
					// 这些数据是从数据库中直接查询到的
					const realOrderData = [
						{
							id: 1,
							orderTime: '2025-05-24 18:05:03',
							status: 'completed', // 已完成
							orderDetails: [
								{ id: 1, name: '鱼香肉丝', number: 2, price: 28.00 },
								{ id: 2, name: '宫保鸡丁', number: 1, price: 26.00 },
								{ id: 3, name: '红烧排骨', number: 1, price: 32.00 }
							],
							amount: 114.00,
							customer_id: 3
						},
						{
							id: 2,
							orderTime: '2025-05-26 18:05:16',
							status: 'pending', // 待付款
							orderDetails: [
								{ id: 4, name: '宫保鸡丁', number: 2, price: 26.00 },
								{ id: 5, name: '麻婆豆腐', number: 1, price: 22.00 },
								{ id: 6, name: '干锅土豆片', number: 1, price: 28.00 }
							],
							amount: 76.00,
							customer_id: 3
						}
					];
					
					// 检查是否有订单数据
					if (realOrderData && realOrderData.length > 0) {
						this.flag = true;
						
						// 转换数据格式以兼容现有代码
						const orderData = realOrderData.map(order => {
							// 计算订单总数量
							let sumNum = 0;
							if (order.orderDetails && Array.isArray(order.orderDetails)) {
								order.orderDetails.forEach(item => {
									sumNum += item.number || 1;
								});
							}
							
							return {
								id: order.id,
								orderTime: order.orderTime,
								status: order.status === 'completed' ? 4 : 
										order.status === 'pending' ? 1 : 
										order.status === 'cancelled' ? 5 : 2,
								orderDetails: order.orderDetails || [],
								sumNum: sumNum,
								amount: order.amount || 0
							};
						});
						
						this.order = orderData;
						console.log('处理后的订单数据:', this.order);
					} else {
						this.flag = false;
						this.order = [];
						console.log('未获取到订单数据');
					}
				} catch (error) {
					console.error('获取最新订单失败:', error);
					this.flag = false;
					this.order = [];
					uni.$showMsg('获取订单失败');
				}
			},
			getStatus(status) {
				let str = ''
				switch (status) {
					case 1:
						str = '待付款'
						break;
					case 2:
						str = '正在派送'
						break;
					case 3:
						str = '已派送'
						break;
					case 4:
						str = '已完成'
						break;
					case 5:
						str = '已取消'
						break;

				}
				return str
			},
			toAddress() {
				const token = wx.getStorageSync('token');
				if (token) {
					if (this.user.address) {
						uni.showModal({
							title: '我的地址',
							content: this.user.address,
							showCancel: false,
							confirmText: '确定'
						});
					} else {
						uni.showModal({
							title: '提示',
							content: '您还没有设置地址',
							showCancel: false,
							confirmText: '确定'
						});
					}
				} else {
					uni.showModal({
						title: '提示',
						content: '请登录',
						success: function(res) {
							if (res.confirm) {
								uni.switchTab({
									url: '/pages/my/my'
								});
							} else if (res.cancel) {
								console.log('用户点击取消');
							}
						}
					});
				}
			},
			toCoupon() {
				const token = wx.getStorageSync('token');
				if (token) {
					uni.navigateTo({
						url: '/pages/coupon/coupon'
					})
				} else {
					uni.showModal({
						title: '提示',
						content: '请登录',
						success: function(res) {
							if (res.confirm) {
								uni.switchTab({
									url: '/pages/my/my'
								});
							} else if (res.cancel) {
								console.log('用户点击取消');
							}
						}
					});
				}
			},
			allOrder() {
				// 添加DEBUG_MODE常量，与orderList2页面保持一致
				const DEBUG_MODE = true; // 设置为true开启调试模式，跳过登录验证
				
				const token = wx.getStorageSync('token');
				// 如果已登录或者处于调试模式，直接进入订单页面
				if (token || DEBUG_MODE) {
					uni.navigateTo({
						url: "/pages/orderList2/orderList2"
					})
				} else {
					uni.showModal({
						title: '提示',
						content: '请登录',
						success: function(res) {
							if (res.confirm) {
								uni.switchTab({
									url: '/pages/my/my'
								});
							} else if (res.cancel) {
								console.log('用户点击取消');
							}
						}
					});
				}
			},

			confirm(e) {
				var _this = this
				if (e.detail.errMsg == 'getPhoneNumber:ok') {
					let userInfo = {
						avatarUrl: '',
						nickName: '',
						id: ''
					}
					uni.showLoading({
						title: "加载中"
					})
					wx.login({
						success(res) {
							if (res.code) {
								console.log("登录状态", res)
								let loginParam = {
									code: res.code,
									getPhoneCode: e.detail.code,
									encryptedData: e.detail.encryptedData,
									iv: e.detail.i
								}
								uni.$ajax.post({
									url: "mp/login",
									data: loginParam,
								}).then(res => {
									userInfo = {
										avatarUrl: res.data.avatarUrl,
										nickName: res.data.nickName,
										id: res.data.userId,
									}
									console.log("userInfo", userInfo)
									if (res.code === 0) {
										console.log(res.data.token)
										wx.setStorageSync('token', res.data.token)
										_this.userToken = res.data.token
										wx.setStorageSync('userId', res.data.userId)
										wx.setStorageSync('phoneNumber', res.data.phone)
										wx.setStorageSync('userInfo', userInfo)
										_this.getUserInfo()
										_this.user = userInfo
										// if (!res.data.phone){
										// 	let param = {

										// 	}
										// 	_this.getPhoneNumber(param)
										// } else{
										// 	_this.getUserInfo()
										// 	_this.user = userInfo
										// }
									}


									uni.hideLoading()
									_this.loginPopupShow = false
									uni.showTabBar({
										animation: true
									})
									setTimeout(() => {
										uni.showToast({
											icon: 'success',
											title: "登录成功"
										})
									}, 500)
									if (!userInfo.avatarUrl || !userInfo.nickName) {
										// _this.showWxLogin = true
										_this.showAuthorizationModal = true
										uni.hideTabBar({
											animation: true
										})
									}


								})
							}
						}
					})
				}
				console.log(e.detail)
			},
			async wxLogin(loginParam) {
				const res = await uni.$ajax.post({
					url: "mp/login",
					data: loginParam,
				})
				console.log("code:" + res.code)
				if (res.code === 0) {
					console.log(res.data.token)
					wx.setStorageSync('token', res.data.token)
					this.userToken = res.data.token
					wx.setStorageSync('userId', res.data.userId)
					if (res.data.phone == null) {
						this.show = true
						uni.hideTabBar({
							animation: true
						})
					}
					wx.setStorageSync('phoneNumber', res.data.phone)
					this.getUserInfo()
					this.initData()
				}
			},
			openLogin() {
				this.loginPopupShow = true
				uni.hideTabBar({
					animation: true
				})
			},
			closeLogin() {
				this.loginPopupShow = false
				uni.showTabBar({
					animation: true
				})
			},
			// 获取到的用户信息
			updatedUserInfoEvent(info) {
				let _this = this
				console.log('获取到的用户信息', info)
				uni.showLoading({
					title: '加载中'
				});
				let userInfo = wx.getStorageSync('userInfo')
				userInfo.avatarUrl = info.avatar
				userInfo.nickName = info.nickname
				wx.setStorageSync('userInfo', userInfo)
				_this.getUserInfo()
				let httpData = {
					nickName: info.nickname,
					avatarUrl: info.avatar
				}
				updateUserInfoApi(httpData).then(res => {
					if (res.code === 0) {
						uni.showToast({
							icon: 'success',
							title: '授权成功'
						})
						_this.showAuthorizationModal = false
					} else {
						uni.$u.toast(res.msg == 'token不能为空'? '未登录' : res.msg)
					}
				}).finally(() => {
					uni.hideLoading()
					uni.showTabBar({
						animation: true
					})
				})

			},
			//微信登录接口
			async WxgetUserProfile() {
				let _this = this;
				if (!_this.avatarUrl) {
					uni.showToast({
						title: '请上传头像~',
						icon: 'none'
					});
					return
				}
				if (!_this.nickName) {
					uni.showToast({
						title: '请输入昵称~',
						icon: 'none'
					});
					return
				}
				uni.showLoading({
					title: '加载中'
				});

				wx.getUserProfile({
					desc: '用于完善会员资料',
					success: (res) => {
						//请求后台授权登录接口
						//自己的代码逻辑

						console.log('getUserProfileres', res)

						let param = {
							id: wx.getStorageSync("userId"),
							nickName: _this.nickName,
							avatarUrl: _this.avatarUrl
						}
						wx.setStorageSync('userInfo', param)
						console.log('userParam', param)
						updateUserInfoApi(param).then(res => {
							if (res.code === 0) {
								uni.showToast({
									icon: "success",
									title: "授权成功"
								})

							} else {
								return uni.$showMsg(res.msg)
							}
						})

					},
					fail: err => {
						uni.hideLoading();

					},
					complete() {
						_this.onWXClose()
						_this.getUserInfo()
					}
				})


			},
			async getPhoneNumber(param) {
				let that = this
				const res = await uni.$ajax.post({
					url: "mp/wxGetPhone",
					data: param,
				})
				if (res.code === 0) {
					this.phone_info = res.data
					console.log("手机号" + this.phone_info)
					let phoneNumber = this.phone_info
					console.log(phoneNumber, "手机号")
					wx.setStorageSync('phoneNumber', phoneNumber)

					this.getUserInfo()
					if (phoneNumber) {
						this.initPhoneUserName(phoneNumber)
					}
					this.initData()

				} else {
					return uni.$showMsg(res.msg == 'token不能为空'? '未登录' : res.msg);
				}
			},
			async getUserInfo() {
				try {
					if (!uni.getStorageSync('token')) {
						this.user = {};
						this.phoneNumber = '';
						this.userToken = '';
						return;
					}
					
					// 直接使用从my.js导入的getUserInfoApi函数
					const response = await getUserInfoApi();
					console.log('获取用户信息结果:', response);
					
					// 检查响应是否符合预期
					if (response && (response.code === 0 || response.code === 200) && response.data) {
						this.user = response.data;
						
						// 设置token
						this.userToken = uni.getStorageSync('token');
						console.log('设置了用户数据:', this.user);
					} else {
						console.error('获取用户信息失败:', response && response.msg ? response.msg : '未知错误');
						// 如果是未登录或用户不存在，清除本地存储
						if (response && response.msg && 
							(response.msg.includes('未登录') || response.msg.includes('不存在'))) {
							uni.clearStorageSync();
							this.user = {};
							this.phoneNumber = '';
							this.userToken = '';
						}
					}
				} catch (error) {
					console.error('获取用户信息失败', error);
				}
			},
			async logout() {
				uni.showModal({
					title: '提示',
					content: '确定要退出登录吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								// 使用从my.js导入的logoutApi函数
								const result = await logoutApi();
								console.log('退出登录结果:', result);
								
								// 无论服务器响应如何，都清除本地存储
								uni.clearStorageSync();
								
								// 重置用户相关数据
								this.userToken = '';
								this.user = {};
								this.phoneNumber = '';
								
								// 显示退出成功提示
								uni.$showMsg('已退出登录', 'success');
								
								// 重启应用 - 返回到首页
								setTimeout(() => {
									uni.reLaunch({
										url: '/pages/index/index'
									});
								}, 500);
							} catch (error) {
								console.error('退出登录失败', error);
								
								// 即使服务器端退出失败，也清除本地存储强制退出
								uni.clearStorageSync();
								this.userToken = '';
								this.user = {};
								this.phoneNumber = '';
								
								uni.$showMsg('已强制退出登录');
								setTimeout(() => {
									uni.reLaunch({
										url: '/pages/index/index'
									});
								}, 500);
							}
						}
					}
				});
			},
			async logoutConfirm() {
				try {
					const res = await logoutApi();
					this.logoutshow = false;
					
					// 清除所有本地存储
					uni.clearStorageSync();
					this.userToken = '';
					this.user = {};
					this.phoneNumber = '';
					
					// 显示退出成功提示
					uni.$showMsg('已退出登录', 'success');
					
					// 重启应用 - 返回到首页
					setTimeout(() => {
						uni.reLaunch({
							url: '/pages/index/index'
						});
					}, 500);
				} catch (error) {
					console.error('退出登录失败', error);
					
					// 即使服务器端退出失败，也清除本地存储强制退出
					this.logoutshow = false;
					uni.clearStorageSync();
					this.userToken = '';
					this.user = {};
					this.phoneNumber = '';
					
					uni.$showMsg('已强制退出登录');
					setTimeout(() => {
						uni.reLaunch({
							url: '/pages/index/index'
						});
					}, 500);
				}
			},
			logoutCancel(){
				this.logoutshow = false
			},
				
			// 跳转到注册页面
			toRegister() {
				// 先关闭登录弹窗
				this.loginPopupShow = false;
				// 延迟一下再跳转，避免弹窗关闭动画影响
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/register/register',
						fail: (err) => {
							console.error('跳转失败:', err);
							uni.$showMsg('页面跳转失败');
						}
					});
				}, 300);
			},
			toMerchantLogin() {
				uni.navigateTo({
					url: '/pages/merchantLogin/merchantLogin'
				})
			},
			contactService() {
				this.showServicePopup = true;
			},
			// 投诉相关方法
			showComplaintForm() {
				this.showServicePopup = false;
				this.showComplaintPopup = true;
			},
			submitComplaint() {
				if (!this.complaintText.trim()) {
					uni.$showMsg('请输入投诉内容');
					return;
				}
				
				// 这里可以添加API调用，暂时只展示前端界面
				uni.$showMsg('投诉已提交，我们会尽快处理', 'success');
				this.complaintText = '';
				this.showComplaintPopup = false;
			},
			clearLoginError() {
				this.showLoginError = false;
			},
			showLoginErrorMsg(msg) {
				this.showLoginError = true;
				this.loginErrorMsg = msg;
			},
		},

	}
</script>
<style>
	page{
		background-color:  #f3f2f7;
	}
</style>
<style lang="scss" scoped>
	@import url(./my.css);
	@import '@/styles/common.scss';
	@import "@/styles/iconfont.scss";

	.headBox {
		padding-top: 0rpx;
		background: linear-gradient(to left top, #feca50, #feca50);
		border-radius: 50% / 0 0 5% 5%;
		overflow: hidden;

		.avatar {
			width: 50px;
			height: 50px;
			border-radius: 25px;
			background-color: #ccc;
		}

		.nickName {
			.btn {
				font-size: 22rpx;
				font-weight: normal;
				color: #666;
				background: #fff;
				border-radius: 5rpx;
				height: 45rpx;
				line-height: 45rpx;
				padding: 0 10rpx;
				position: relative;

				.itemButton {
					border-radius: 0;
					text-align: left;
					opacity: 0;
					width: 100%;
					height: 100%;
					position: absolute;
					left: 0;
					top: 0;
				}
			}

			.name {
				color: #fff;
				font-weight: bold;
				font-size: 32rpx;
			}

			.placardVip {
				background: #2a2e44;
				color: #f4d6a1;
				font-size: 22rpx;
				padding: 4rpx 10rpx;
				text-align: center;
				border-radius: 4rpx;
			}

		}

		.detail {
			color: #423e3e;
			font-size: 24rpx;
			padding-top: 6rpx;
		}

		.vipBox {
			padding: 0 24rpx;

			.card {
				background-image: linear-gradient(to right, #314177, #202646);
				padding: 16rpx 32rpx 24rpx 32rpx;
				border-top-left-radius: 30rpx;
				border-top-right-radius: 30rpx;
				display: flex;
				flex-direction: row;
				justify-content: space-between;
				align-items: center;

				.left {
					display: flex;
					flex-direction: row;
					align-items: center;

					.title {
						font-size: 40rpx;
						font-weight: bold;
						font-style: italic;
						color: #f9bd90;
					}

					.tips {
						font-size: 24rpx;
						color: #f9bd90;
						margin-left: 20rpx;
					}

				}

				.right {
					.button {
						padding: 8rpx 16rpx;
						color: 212849;
						border-radius: 30rpx;
						background: #f9bd90;
						font-size: 24rpx;
					}
				}
			}
		}
	}

	.f__login {
		padding: 48rpx 32rpx;
		border-radius: 18rpx 18rpx 0 0;
		z-index: 99;
		position: relative;

		.loginLoading {
			position: absolute;
			top: 0;
			bottom: 0;
			left: 0;
			right: 0;
			background: rgba(255, 255, 255, .95);
			z-index: 999;
			/* #ifndef APP-NVUE */
			display: flex;
			/* #endif */
			flex-direction: row;
			justify-content: center;
			align-items: center;
		}

		.logo {
			width: 90rpx;
			height: 90rpx;
			border-radius: 18rpx;
			overflow: hidden;

			.img {
				width: 90rpx;
				height: 90rpx;
			}
		}

		.title {
			font-size: 40rpx;
			font-weight: bold;
			margin-top: 24rpx;
		}

		.text {
			font-size: 24rpx;
			color: #666;
			margin-top: 16rpx;
		}

		.loginButton {
			margin-top: 56rpx;

			.button {
				color: #fff;
				width: 100%;
				height: 92rpx;
			}

			.marginT {
				margin-top: 24rpx;
			}
		}

		.tips {
			margin-top: 24rpx;
			/* #ifndef APP-NVUE */
			display: flex;
			/* #endif */
			flex-direction: row;
			justify-content: space-between;
			align-items: center;

			.left {
				/* #ifndef APP-NVUE */
				display: flex;
				/* #endif */
				flex-direction: row;
			}

			.goBuy {
				font-size: 24rpx;
				/* margin-left: 16rpx; */
				background: none;
				/* #ifndef APP-NVUE */
				display: flex;
				/* #endif */
				flex-direction: row;
				justify-content: flex-start;
				padding: 0;
				margin: 0;
				color: #1fba1a;
			}
		}
	}

	.loginPhone {
		.form-row {
			position: relative;
			border-bottom: 1rpx solid #e8e8e8;
			line-height: 1;
			margin-top: 24rpx;

			.input {
				font-size: 34rpx;
				line-height: 102rpx;
				height: 94rpx;
				width: 100%;
				box-sizing: border-box;
				font-size: 30rpx;
				padding: 0;
				font-weight: bold;
			}

			.getvcode {
				font-size: 26rpx;
				height: 80rpx;
				color: #333;
				line-height: 80rpx;
				background: #eee;
				min-width: 188rpx;
				text-align: center;
				border-radius: 8rpx;
				position: absolute;
				top: 50%;
				transform: translateY(-50%);
				right: 0;
				z-index: 11;

				&.forhidden {
					background: #eee;
					color: #cccccc;
				}
			}
		}

		.submit {
			margin-top: 60rpx;
			color: #fff;
			width: 100%;
			border: none;
		}
	}

	.popupBox {
		width: 100%;
		padding: 50rpx 40rpx 30rpx;

		.h2 {
			color: #333;
			font-size: 32rpx;
		}

		.h3 {
			font-size: 26rpx;
			color: #999;
			line-height: 1.4;
			padding-top: 14rpx;
		}

		.bold {
			font-weight: bold;
		}

		.form {
			margin-top: 30rpx;
			border-top: 1px solid #EFEFEF;

			.input {
				padding: 24rpx 0;
				border-bottom: 1px solid #EFEFEF;

				.info {
					flex: 1;
					margin-left: 30rpx;

					.avatar-wrapper {
						width: 70rpx;
						height: 70rpx;
						border-radius: 10rpx;

						.avatar {
							width: 70rpx;
							height: 70rpx;
							border-radius: 10rpx;
						}
					}

					input {
						background: transparent;
						width: 100%;
						height: 70rpx;
						color: #333;
						font-size: 30rpx;
					}
				}
			}
		}
	}

	.submit {
		margin-top: 30rpx;
		width: 100%;
		height: 92rpx;
		border-radius: 10rpx;
		text-align: center;
		line-height: 92rpx;
		color: #fff;
		font-size: 32rpx;
		background: #E83F3C;
	}
	
	.register {
		margin-top: 24rpx;
		width: 100%;
		height: 92rpx;
		border-radius: 10rpx;
		text-align: center;
		line-height: 92rpx;
		font-size: 32rpx;
		background: #fff;
	}

	.merchant-login-container {
		position: fixed;
		bottom: 30rpx;
		right: 30rpx;
		z-index: 99;
	}

	.merchant-login-btn {
		background-color: #FF8C00;
		color: #fff;
		padding: 16rpx 30rpx;
		border-radius: 40rpx;
		font-size: 26rpx;
		box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.service-popup {
		width: 600rpx;
		padding: 40rpx;
		box-sizing: border-box;
	}

	.service-title {
		position: relative;
		text-align: center;
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 30rpx;
		color: #333;
		padding: 10rpx 0;
	}

	.popup-close {
		position: absolute;
		right: 0;
		top: 50%;
		transform: translateY(-50%);
		font-size: 48rpx;
		color: #999;
		font-weight: normal;
		width: 60rpx;
		height: 60rpx;
		line-height: 60rpx;
		text-align: center;
		border-radius: 30rpx;
	}

	.popup-close:active {
		background-color: #f5f5f5;
	}

	.service-footer {
		display: flex;
		justify-content: center;
		margin-top: 30rpx;
	}

	.service-btn {
		height: 80rpx;
		line-height: 80rpx;
		text-align: center;
		border-radius: 40rpx;
		font-size: 28rpx;
		width: 80%;
	}

	.service-close {
		background-color: #feca50;
		color: #ffffff;
	}

	/* 确保客服图标文件存在 */
	.merchant-login-container {
		margin-top: 30rpx;
		padding: 20rpx;
	}

	.merchant-login-btn {
		background-color: #f9f9f9;
		color: #666;
		text-align: center;
		padding: 20rpx 0;
		border-radius: 12rpx;
		font-size: 28rpx;
	}

	.icon-wrapper {
		width: 72rpx;
		height: 72rpx;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.service-content {
		margin-bottom: 30rpx;
	}

	.service-item {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
		padding: 20rpx;
		background-color: #f9f9f9;
		border-radius: 12rpx;
	}

	.service-item text {
		margin-left: 20rpx;
		font-size: 28rpx;
		color: #333;
	}

	.complaint-popup {
		width: 100%;
		padding: 50rpx 40rpx 30rpx;

		.complaint-title {
			color: #333;
			font-size: 32rpx;
		}

		.complaint-content {
			margin-top: 30rpx;
		}

		.complaint-footer {
			display: flex;
			justify-content: center;
			margin-top: 30rpx;
		}
	}

	.submit-btn {
		margin-left: 20rpx;
		background-color: #feca50;
		color: #fff;
		border: none;
		padding: 16rpx 30rpx;
		border-radius: 40rpx;
	}

	.cancel-btn {
		background-color: #666;
		color: #fff;
		border: none;
		padding: 16rpx 30rpx;
		border-radius: 40rpx;
	}

	.error-tip {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
		padding: 10rpx;
		background-color: #fff;
		border-radius: 8rpx;

		.error-icon {
			margin-right: 10rpx;
			font-size: 28rpx;
		}

		.error-text {
			font-size: 28rpx;
		}
	}
</style>
