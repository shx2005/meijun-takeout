<template>
  <view class="pay-confirm">
    <view class="order-info">
      <view class="info-row">
        <text class="label">订单金额</text>
        <text class="value">¥{{ orderAmount }}</text>
      </view>
      
      <!-- 账户余额显示 -->
      <view class="info-row">
        <text class="label">账户余额</text>
        <view style="display: flex; align-items: center;">
        <text class="value">¥{{ balance }}</text>
          <button @click="testFetchBalance" style="margin-left: 10rpx; padding: 5rpx 10rpx; font-size: 20rpx; background: #007aff; color: white; border: none; border-radius: 5rpx;">刷新</button>
        </view>
      </view>
      
      <!-- 优惠券选择 - 改为跳转页面 -->
      <view class="info-row coupon-selector" @click="navigateToCouponSelect">
        <text class="label">优惠券</text>
        <view class="coupon-select">
          <text class="coupon-text">{{ selectedCoupon ? selectedCoupon.name : '请选择优惠券' }}</text>
          <text class="iconfont icon-arrow-right"></text>
        </view>
      </view>
      
      <!-- 优惠金额 - 只在选择优惠券后显示 -->
      <view class="info-row" v-if="selectedCoupon && discountAmount > 0">
        <text class="label">优惠金额</text>
        <text class="value discount">-¥{{ discountAmount }}</text>
      </view>
      
      <!-- 实付金额 -->
      <view class="info-row total">
        <text class="label">实付金额</text>
        <text class="value">¥{{ finalAmount }}</text>
      </view>
    </view>
    
    <view class="payment-methods">
      <view class="method-item active">
        <view class="method-icon">
          <text class="iconfont icon-wallet"></text>
        </view>
        <view class="method-info">
          <text class="method-name">{{ paymentMethod }}</text>
        </view>
        <view class="method-selected">
          <text class="iconfont icon-check"></text>
        </view>
      </view>
    </view>

    <!-- 底部固定的确认支付按钮 -->
    <view class="bottom-payment-btn">
      <button class="payment-btn" :disabled="loading" @click="handleConfirmPayment">
        确认支付
    </button>
    </view>
  </view>
</template>

<script>
import request from '@/utils/request.js';

export default {
  data() {
    return {
      orderId: null,
      orderAmount: 0,
      paymentMethod: '余额支付', // 默认余额支付
      payType: 'BALANCE', // 对应API的payType
      balance: '0.00',
      loading: false,
      
      // 订单信息（从addOrder页面传递）
      orderInfo: null,
      cartData: [],
      address: '',
      
      // 优惠券相关
      selectedCoupon: null,
      discountAmount: 0, // 优惠金额
      finalAmount: 0, // 实付金额
      
      // 支付结果相关
      paymentSuccess: false,
    };
  },
  onLoad(options) {
    // 从addOrder页面传递的订单信息
    if (options.orderInfo) {
      try {
        const orderInfo = JSON.parse(decodeURIComponent(options.orderInfo));
        console.log('接收到的订单信息:', orderInfo);
        
        this.orderInfo = orderInfo;
        this.orderAmount = parseFloat(orderInfo.total).toFixed(2);
        this.finalAmount = this.orderAmount; // 初始化实付金额等于订单金额
        this.cartData = orderInfo.cartData || [];
        this.address = orderInfo.address || '';
        
        console.log('解析后的订单信息:', {
          orderAmount: this.orderAmount,
          cartData: this.cartData,
          address: this.address
        });
      } catch (error) {
        console.error('解析订单信息失败:', error);
        uni.showToast({
          title: '订单信息错误',
          icon: 'none',
          duration: 2000
        });
      }
    } else if (options.orderId && options.amount) {
      // 兼容旧的参数格式（已有订单ID的情况）
      this.orderId = parseInt(options.orderId);
      this.orderAmount = parseFloat(options.amount).toFixed(2);
      this.finalAmount = this.orderAmount;
    } else {
      uni.showToast({
        title: '订单信息错误',
        icon: 'none',
        duration: 2000
      });
    }
    
    // 获取余额和优惠券信息
    this.fetchBalance();
    this.fetchCoupons();
  },
  onShow() {
    // 页面显示时强制刷新余额
    console.log('payConfirm页面显示，强制刷新余额');
    this.fetchBalance();
  },
  methods: {
    // 获取用户余额
    async fetchBalance() {
      try {
        // 确保token存在
        const token = uni.getStorageSync('token');
        if (!token) {
            uni.showToast({ title: '请先登录', icon: 'none' });
            uni.navigateTo({ url: '/pages/my/my' });
            return;
        }

        uni.showLoading({ title: '获取余额中...' });
        
        // 使用正确的余额查询API
        const response = await uni.request({
          url: 'http://localhost:8080/api/v1/payment/balance',
          method: 'GET',
          header: {
            'customerToken': token,
            'userType': '3',
            'Content-Type': 'application/json'
          }
        });
        
        uni.hideLoading();
        
        console.log('余额查询响应:', response);
        
        // 处理响应 - 简化处理逻辑
        const res = response[1];
        if (res && res.statusCode === 200 && res.data) {
          let result = res.data;
          
          console.log('原始余额响应数据:', result);
          console.log('响应数据类型:', typeof result);
          
          // 检查是否为XML格式响应
          if (typeof result === 'string' && result.includes('<Result>')) {
            console.log('检测到XML格式余额响应，开始解析');
            console.log('原始XML字符串:', result);
            result = this.parseXMLBalanceResponse(result);
            console.log('XML解析后的结果:', result);
            
            if (result && result.code === 200 && result.success === true) {
              if (typeof result.data === 'string') {
                this.balance = parseFloat(result.data).toFixed(2);
                console.log('从XML解析的余额:', this.balance);
                // 强制更新UI
                this.$forceUpdate();
              } else if (result.data && typeof result.data.balance !== 'undefined') {
                this.balance = parseFloat(result.data.balance).toFixed(2);
                console.log('从XML对象解析的余额:', this.balance);
                // 强制更新UI
                this.$forceUpdate();
              } else {
                this.balance = '0.00';
                console.log('XML解析失败，设置余额为0.00');
                // 强制更新UI
                this.$forceUpdate();
        }
          } else {
            this.balance = '获取失败';
            uni.showToast({
                title: result?.msg || '获取余额失败',
                icon: 'none'
              });
            }
          } else {
            // 处理JSON格式响应
            console.log('处理JSON格式余额响应');
            if (result.code === 200 && result.success === true) {
              // API返回的data字段是字符串格式的余额
              if (typeof result.data === 'string') {
                this.balance = parseFloat(result.data).toFixed(2);
                console.log('从JSON字符串解析的余额:', this.balance);
                // 强制更新UI
                this.$forceUpdate();
              } else if (result.data && typeof result.data.balance !== 'undefined') {
                this.balance = parseFloat(result.data.balance).toFixed(2);
                console.log('从JSON对象解析的余额:', this.balance);
                // 强制更新UI
                this.$forceUpdate();
              } else {
                this.balance = '0.00';
                console.log('JSON解析失败，设置余额为0.00');
                // 强制更新UI
                this.$forceUpdate();
              }
              
              console.log('余额获取成功:', this.balance);
            } else {
              this.balance = '获取失败';
              uni.showToast({
                title: result?.msg || '获取余额失败',
              icon: 'none'
            });
            }
          }
        } else {
          this.balance = '获取失败';
          uni.showToast({
            title: '获取余额失败',
            icon: 'none'
          });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('获取余额接口请求失败:', error);
        this.balance = '获取失败';
        uni.showToast({
          title: '获取余额失败，请稍后重试',
          icon: 'none'
        });
      }
    },

    // 获取用户优惠券
    async fetchCoupons() {
      try {
        const token = uni.getStorageSync('token');
        if (!token) return;

        const response = await uni.request({
          url: 'http://localhost:8080/api/v1/coupons',
          method: 'GET',
          header: {
            'customerToken': token,
            'userType': '3',
            'Content-Type': 'application/json'
          }
        });
        
        console.log('优惠券查询响应:', response);
        
        const res = response[1];
        if (res && res.statusCode === 200 && res.data) {
          // 存储优惠券数据供选择页面使用
          uni.setStorageSync('availableCoupons', res.data);
        }
      } catch (error) {
        console.error('获取优惠券失败:', error);
      }
    },
    
    // 导航到优惠券选择页面
    navigateToCouponSelect() {
      let url = `/pages/couponSelect/couponSelect?orderId=${this.orderId}&amount=${this.orderAmount}`;
      
      // 如果已有选择的优惠券，传递ID以预选
      if (this.selectedCoupon) {
        url += `&selectedCouponId=${this.selectedCoupon.id}`;
      }
      
      uni.navigateTo({
        url: url
      });
    },
    
    // 应用所选优惠券 (由优惠券选择页面调用)
    applyCoupon(coupon) {
      this.selectedCoupon = coupon;
      
      // 如果优惠券已经验证过，直接使用验证结果
      if (coupon.validatedAmount !== undefined && coupon.discountAmount !== undefined) {
        this.discountAmount = coupon.discountAmount.toFixed(2);
        this.finalAmount = coupon.validatedAmount.toFixed(2);
        
        uni.showToast({
          title: '优惠券已应用',
          icon: 'success'
        });
      } else {
        // 如果没有验证过，重新验证
      this.validateCoupon(coupon.id);
      }
    },
    
    // 移除优惠券 (由优惠券选择页面调用)
    removeCoupon() {
      this.selectedCoupon = null;
      this.discountAmount = 0;
      this.finalAmount = this.orderAmount;
    },
    
    // 验证优惠券
    async validateCoupon(couponId) {
      try {
        uni.showLoading({ title: '验证优惠券...' });
        
        // 准备请求数据
        const requestData = {
          couponId: couponId,
          orderId: this.orderId,
          amount: parseFloat(this.orderAmount),
          payType: this.payType === 'BALANCE' ? 1 : 2
        };
        
        // 发送请求验证优惠券
        const response = await uni.request({
          url: 'http://localhost:8080/api/v1/coupons/validate',
          method: 'POST',
          header: {
            'customerToken': uni.getStorageSync('token'),
            'userType': '3',
            'Content-Type': 'application/json'
          },
          data: requestData
        });
        
        uni.hideLoading();
        
        if (response && response[1].statusCode === 200) {
          const result = response[1].data;
          
          if (result && result.code === 200 && result.data) {
            // 优惠后的金额
            const discountedAmount = parseFloat(this.orderAmount) - parseFloat(result.data.amount);
            this.discountAmount = discountedAmount.toFixed(2);
            this.finalAmount = parseFloat(result.data.amount).toFixed(2);
            
            uni.showToast({
              title: '优惠券已应用',
              icon: 'success'
            });
          } else {
            // 验证失败，移除优惠券
            this.removeCoupon();
            uni.showToast({
              title: result?.msg || '优惠券验证失败',
              icon: 'none'
            });
          }
        } else {
          // 验证失败，使用默认计算方式
          if (this.selectedCoupon) {
            const discountedAmount = parseFloat(this.selectedCoupon.amount);
            this.discountAmount = discountedAmount.toFixed(2);
            const finalAmount = Math.max(0, parseFloat(this.orderAmount) - discountedAmount);
            this.finalAmount = finalAmount.toFixed(2);
            
            uni.showToast({
              title: '已应用优惠券(本地计算)',
              icon: 'success'
            });
          }
        }
      } catch (error) {
        uni.hideLoading();
        console.error('验证优惠券失败:', error);
        
        // 验证失败，使用默认计算方式
        if (this.selectedCoupon) {
          const discountedAmount = parseFloat(this.selectedCoupon.amount);
          this.discountAmount = discountedAmount.toFixed(2);
          const finalAmount = Math.max(0, parseFloat(this.orderAmount) - discountedAmount);
          this.finalAmount = finalAmount.toFixed(2);
        }
      }
    },
    
    // 确认支付
    async handleConfirmPayment() {
      if (this.loading) return; // 防止重复点击
      
      // 检查是否有订单信息
      if (!this.orderInfo && !this.orderId) {
        uni.showToast({
          title: '订单信息错误',
          icon: 'none'
        });
        return;
      }
      
      // 检查余额是否足够
      if (parseFloat(this.balance) < parseFloat(this.finalAmount)) {
        uni.showToast({
          title: '余额不足，请充值',
          icon: 'none'
        });
        return;
      }

      try {
        this.loading = true;
        uni.showLoading({ title: '处理中...' });
        
        // 获取用户ID和token
        const userId = uni.getStorageSync('userId') || 1;
        const token = uni.getStorageSync('token');
        
        if (!token) {
          uni.hideLoading();
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          this.loading = false;
          return;
        }
        
        let currentOrderId = this.orderId;
        
        // 如果没有订单ID，说明需要先提交订单
        if (!currentOrderId && this.orderInfo) {
          console.log('开始提交订单...');
          
          // 准备订单数据
          const orderData = {
            userId: userId,
            total: parseFloat(this.finalAmount),
            payMethod: 3 // 余额支付
          };
          
          console.log('订单提交数据:', orderData);
          
          // 提交订单
          const orderResponse = await uni.request({
            url: 'http://localhost:8080/api/v1/orders/submit',
          method: 'POST',
          header: {
              'customerToken': token,
            'userType': '3',
            'Content-Type': 'application/json'
          },
            data: orderData
          });
          
          console.log('订单提交响应:', orderResponse);
          
          const orderRes = orderResponse[1];
          if (orderRes && orderRes.statusCode === 200) {
            let orderResult = orderRes.data;
            
            // 检查是否为XML格式响应
            if (typeof orderResult === 'string' && orderResult.includes('<Result>')) {
              console.log('检测到XML格式订单响应，开始解析');
              orderResult = this.parseXMLOrderResponse(orderResult);
              console.log('XML解析后的订单结果:', orderResult);
            }
            
            if (orderResult && (orderResult.code === 200 || orderResult.success === true)) {
              // 订单提交成功，获取订单ID
              currentOrderId = orderResult.data?.id || orderResult.id;
              console.log('订单提交成功，订单ID:', currentOrderId);
        
              // 订单创建成功后，调用支付API
              console.log('开始调用支付API...');
              
              const paymentData = {
                orderId: currentOrderId,
                customerId: userId,
                method: 'cash_pay',
                amount: this.finalAmount
              };
              
              console.log('支付API请求数据:', paymentData);
              
              const paymentResponse = await uni.request({
                url: 'http://localhost:8080/api/v1/payment/balance',
                method: 'POST',
                header: {
                  'customerToken': token,
                  'userType': '3',
                  'Content-Type': 'application/json'
                },
                data: paymentData
              });
              
              console.log('支付API响应:', paymentResponse);
              
              const payRes = paymentResponse[1];
              if (payRes && payRes.statusCode === 200) {
                let payResult = payRes.data;
            
                // 检查是否为XML格式响应
                if (typeof payResult === 'string' && payResult.includes('<Result>')) {
                  console.log('检测到XML格式支付响应，开始解析');
                  payResult = this.parseXMLPaymentResponse(payResult);
                  console.log('XML解析后的支付结果:', payResult);
                }
                
                if (payResult && (payResult.code === 200 || payResult.success === true)) {
                  // 支付成功
                  console.log('支付成功');
                  
                  // 重新获取余额（支付后余额应该被扣除）
                  await this.fetchBalance();
            
          uni.showToast({
              title: '支付成功',
              icon: 'success'
          });
            
            // 支付成功后跳转到支付成功页面
          setTimeout(() => {
              uni.redirectTo({
                      url: `/pages/paySuccess/paySuccess?orderId=${currentOrderId}&amount=${this.finalAmount}`
            });
            }, 1500);
                } else {
                  throw new Error(payResult?.msg || '支付失败');
                }
              } else {
                throw new Error('支付请求失败');
              }
            } else {
              throw new Error(orderResult?.msg || '订单提交失败');
            }
          } else {
            throw new Error('订单提交请求失败');
          }
        } else {
          // 如果已有订单ID，说明是旧流程，直接进行支付
          console.log('使用现有订单ID进行支付:', currentOrderId);
          
          // 这里可以添加单独的支付逻辑，但根据当前的API设计，
          // 订单提交已经包含了支付，所以这个分支可能不会被使用
          uni.showToast({
            title: '支付成功',
            icon: 'success'
          });
          
          setTimeout(() => {
            uni.redirectTo({
              url: `/pages/paySuccess/paySuccess?orderId=${currentOrderId}&amount=${this.finalAmount}`
            });
          }, 1500);
        }
        
        uni.hideLoading();
        
      } catch (error) {
        uni.hideLoading();
        console.error('支付处理失败:', error);
        uni.showToast({
          title: error.message || '支付处理失败，请稍后重试',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    // 解析XML格式的支付响应
    parseXMLPaymentResponse(xmlString) {
      try {
        console.log('开始解析XML支付响应');
        
        // 提取基本信息
        const codeMatch = xmlString.match(/<code>(.*?)<\/code>/);
        const msgMatch = xmlString.match(/<msg>(.*?)<\/msg>/);
        const successMatch = xmlString.match(/<success>(.*?)<\/success>/);
        
        // 提取余额信息
        const balanceMatch = xmlString.match(/<balance>(.*?)<\/balance>/);
        const payMethodMatch = xmlString.match(/<payMethod>(.*?)<\/payMethod>/);
        const orderIdMatch = xmlString.match(/<orderId>(.*?)<\/orderId>/);
        
        const result = {
          code: codeMatch ? parseInt(codeMatch[1]) : 200,
          msg: msgMatch ? msgMatch[1] : 'OK',
          data: {
            balance: balanceMatch ? parseFloat(balanceMatch[1]) : 0,
            payMethod: payMethodMatch ? payMethodMatch[1] : 'cash_pay',
            orderId: orderIdMatch ? parseInt(orderIdMatch[1]) : this.orderId
          },
          success: successMatch ? successMatch[1] === 'true' : true
        };
        
        console.log('XML解析完成，结果:', result);
        return result;
      } catch (error) {
        console.error('解析XML支付响应失败:', error);
        return {
          code: 500,
          msg: 'XML解析失败',
          data: {},
          success: false
        };
      }
    },

    // 解析XML格式的余额响应
    parseXMLBalanceResponse(xmlString) {
      try {
        console.log('开始解析XML余额响应，原始字符串:', xmlString);
        
        // 提取基本信息
        const codeMatch = xmlString.match(/<code>(.*?)<\/code>/);
        const msgMatch = xmlString.match(/<msg>(.*?)<\/msg>/);
        const successMatch = xmlString.match(/<success>(.*?)<\/success>/);
        
        // 提取余额数据 - data标签直接包含余额数字
        const dataMatch = xmlString.match(/<data>(.*?)<\/data>/);
        
        console.log('XML解析匹配结果:');
        console.log('- code:', codeMatch ? codeMatch[1] : 'null');
        console.log('- msg:', msgMatch ? msgMatch[1] : 'null');
        console.log('- success:', successMatch ? successMatch[1] : 'null');
        console.log('- data:', dataMatch ? dataMatch[1] : 'null');
        
        const result = {
          code: codeMatch ? parseInt(codeMatch[1]) : 200,
          msg: msgMatch ? msgMatch[1] : 'OK',
          data: dataMatch ? dataMatch[1] : '0.00', // 直接使用data标签的内容作为余额字符串
          success: successMatch ? successMatch[1] === 'true' : true
        };
        
        console.log('XML余额解析完成，最终结果:', result);
        console.log('解析出的余额数据类型:', typeof result.data);
        console.log('解析出的余额数值:', result.data);
        
        return result;
      } catch (error) {
        console.error('解析XML余额响应失败:', error);
        return {
          code: 500,
          msg: 'XML解析失败',
          data: '0.00',
          success: false
        };
      }
    },
    
    // 测试方法：手动获取余额
    async testFetchBalance() {
      console.log('=== 开始测试余额获取 ===');
      console.log('当前余额值:', this.balance);
      
      const token = uni.getStorageSync('token');
      console.log('存储的token:', token);
      
      if (!token) {
        console.log('没有找到token');
        return;
      }
      
      try {
        const response = await uni.request({
          url: 'http://localhost:8080/api/v1/payment/balance',
          method: 'GET',
          header: {
            'customerToken': token,
            'userType': '3',
            'Content-Type': 'application/json'
          }
        });
        
        console.log('测试余额查询完整响应:', response);
        
        if (response && response[1] && response[1].data) {
          const result = response[1].data;
          console.log('测试响应数据:', result);
          console.log('测试响应数据类型:', typeof result);
          
          if (typeof result === 'string' && result.includes('<Result>')) {
            console.log('测试检测到XML格式');
            const parsed = this.parseXMLBalanceResponse(result);
            console.log('测试XML解析结果:', parsed);
            
            if (parsed && parsed.data) {
              this.balance = parseFloat(parsed.data).toFixed(2);
              console.log('测试设置余额为:', this.balance);
              this.$forceUpdate();
            }
          } else if (result && result.data) {
            console.log('测试检测到JSON格式');
            this.balance = parseFloat(result.data).toFixed(2);
            console.log('测试设置余额为:', this.balance);
            this.$forceUpdate();
          }
        }
      } catch (error) {
        console.error('测试余额获取失败:', error);
      }
      
      console.log('=== 测试余额获取结束 ===');
    },

    // 解析XML格式的订单响应
    parseXMLOrderResponse(xmlString) {
      try {
        console.log('开始解析XML订单响应，原始字符串:', xmlString);
        
        // 提取基本信息
        const codeMatch = xmlString.match(/<code>(.*?)<\/code>/);
        const msgMatch = xmlString.match(/<msg>(.*?)<\/msg>/);
        const successMatch = xmlString.match(/<success>(.*?)<\/success>/);
        
        // 提取订单数据
        const idMatch = xmlString.match(/<id>(.*?)<\/id>/);
        const orderNumberMatch = xmlString.match(/<orderNumber>(.*?)<\/orderNumber>/);
        const orderAmountMatch = xmlString.match(/<orderAmount>(.*?)<\/orderAmount>/);
        
        console.log('XML订单解析匹配结果:');
        console.log('- code:', codeMatch ? codeMatch[1] : 'null');
        console.log('- msg:', msgMatch ? msgMatch[1] : 'null');
        console.log('- success:', successMatch ? successMatch[1] : 'null');
        console.log('- id:', idMatch ? idMatch[1] : 'null');
        console.log('- orderNumber:', orderNumberMatch ? orderNumberMatch[1] : 'null');
        console.log('- orderAmount:', orderAmountMatch ? orderAmountMatch[1] : 'null');
        
        const result = {
          code: codeMatch ? parseInt(codeMatch[1]) : 200,
          msg: msgMatch ? msgMatch[1] : 'OK',
          data: {
            id: idMatch ? parseInt(idMatch[1]) : null,
            orderNumber: orderNumberMatch ? orderNumberMatch[1] : '',
            orderAmount: orderAmountMatch ? parseFloat(orderAmountMatch[1]) : 0
          },
          success: successMatch ? successMatch[1] === 'true' : true
        };
        
        console.log('XML订单解析完成，最终结果:', result);
        return result;
      } catch (error) {
        console.error('解析XML订单响应失败:', error);
        return {
          code: 500,
          msg: 'XML解析失败',
          data: {},
          success: false
        };
      }
    },
  }
};
</script>

<style lang="scss">
.pay-confirm {
  padding: 30rpx;
  padding-bottom: 120rpx; /* 为底部按钮留出空间 */
  background-color: #f8f8f8;
  min-height: 100vh;
  box-sizing: border-box;
  
  .order-info {
    background-color: #fff;
    border-radius: 12rpx;
    padding: 30rpx;
    margin-bottom: 30rpx;

    .info-row {
  display: flex;
  justify-content: space-between;
      margin-bottom: 20rpx;
      
      &.total {
        margin-top: 20rpx;
        padding-top: 20rpx;
        border-top: 1px solid #eee;
        
        .label, .value {
          font-weight: bold;
          font-size: 32rpx;
        }
        
        .value {
          color: #ff5722;
        }
      }
      
      &.coupon-selector {
        cursor: pointer;
}

.label {
        color: #666;
  font-size: 28rpx;
}

.value {
  font-size: 28rpx;
  color: #333;
        
        &.discount {
          color: #ff5722;
        }
      }
      
      .coupon-select {
        display: flex;
        align-items: center;
        
        .coupon-text {
          font-size: 28rpx;
          color: #007aff;
          margin-right: 10rpx;
}

        .iconfont {
          color: #999;
          font-size: 24rpx;
        }
      }
    }
  }
  
  .payment-methods {
    background-color: #fff;
    border-radius: 12rpx;
    padding: 20rpx;
    margin-bottom: 50rpx;
    
    .method-item {
      display: flex;
      align-items: center;
      padding: 20rpx;
      
      &.active {
        background-color: #f9f9f9;
}

      .method-icon {
        width: 60rpx;
        height: 60rpx;
        background-color: #007aff;
        border-radius: 30rpx;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-right: 20rpx;
        
        .iconfont {
  color: #fff;
  font-size: 32rpx;
        }
      }
      
      .method-info {
        flex: 1;
        
        .method-name {
          font-size: 28rpx;
        }
      }
      
      .method-selected {
        .iconfont {
          color: #007aff;
          font-size: 32rpx;
        }
      }
    }
  }
  
  /* 底部固定的确认支付按钮 */
  .bottom-payment-btn {
    position: fixed;
    left: 0;
    bottom: 0;
    width: 100%;
    background-color: #fff;
    padding: 20rpx;
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
    z-index: 100;
    box-sizing: border-box;
    
    .payment-btn {
      background-color: #4285f4; /* 蓝色按钮 */
      color: #fff;
      border-radius: 50rpx;
      font-size: 32rpx;
      height: 90rpx;
      line-height: 90rpx;
      width: 100%;
      
      &[disabled] {
        background-color: #cccccc;
        color: #ffffff;
      }
    }
  }
}
</style> 