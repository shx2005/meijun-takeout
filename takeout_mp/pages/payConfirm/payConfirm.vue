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
        <text class="value">¥{{ balance }}</text>
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
      
      // 优惠券相关
      selectedCoupon: null,
      discountAmount: 0, // 优惠金额
      finalAmount: 0, // 实付金额
      
      // 支付结果相关
      paymentSuccess: false,
    };
  },
  onLoad(options) {
    // onLoad中获取路由参数
    if (options.orderId && options.amount) {
      this.orderId = parseInt(options.orderId);
      this.orderAmount = parseFloat(options.amount).toFixed(2);
      this.finalAmount = this.orderAmount; // 初始化实付金额等于订单金额
    } else {
      uni.showToast({
        title: '订单信息错误',
        icon: 'none',
        duration: 2000
      });
    }
    this.fetchBalance();
  },
  methods: {
    // 获取用户余额
    async fetchBalance() {
      try {
        // 确保token存在
        if (!uni.getStorageSync('token')) {
            uni.showToast({ title: '请先登录', icon: 'none' });
            // 跳转到登录页
            uni.navigateTo({ url: '/pages/my/my' });
            return;
        }

        uni.showLoading({ title: '获取余额中...' });
        
        // 使用 uni.request 调用余额API
        const response = await uni.request({
          url: 'http://localhost:8080/api/v1/user/balance',
          method: 'GET',
          header: {
            'tokenName': uni.getStorageSync('token'),
            'userType': '3',
            'Content-Type': 'application/json'
          }
        });
        
        uni.hideLoading();
        
        // 处理响应
        if (response && response[1].statusCode === 200) {
          const res = response[1].data;
          if (res && res.code === 0) {
            if (res.data && typeof res.data.balance !== 'undefined') {
          this.balance = parseFloat(res.data.balance).toFixed(2);
            } else if (typeof res.balance !== 'undefined') {
          this.balance = parseFloat(res.balance).toFixed(2);
        }
          } else {
            this.balance = '获取失败';
            uni.showToast({
              title: res.message || '获取余额失败',
              icon: 'none'
            });
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
      
      // 验证优惠券
      this.validateCoupon(coupon.id);
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
      
      if (!this.orderId) {
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
        uni.showLoading({ title: '支付处理中...' });
        
        // 调用确认支付API
        const paymentResponse = await uni.request({
          url: 'http://localhost:8080/api/v1/payment/balance',
          method: 'POST',
          header: {
            'tokenName': uni.getStorageSync('token'),
            'userType': '3',
            'Content-Type': 'application/json'
          },
          data: {
            orderId: this.orderId,
            amount: parseFloat(this.finalAmount),
            payType: this.payType
          }
        });
        
        uni.hideLoading();
        
        // 处理支付结果
        if (paymentResponse && paymentResponse[1].statusCode === 200) {
          const paymentResult = paymentResponse[1].data;
          
          if (paymentResult && paymentResult.code === 0) {
            // 支付成功
            this.paymentSuccess = true;
            
            // 更新余额显示
            if (paymentResult.data && typeof paymentResult.data.balance !== 'undefined') {
              this.balance = parseFloat(paymentResult.data.balance).toFixed(2);
            }
            
          uni.showToast({
              title: '支付成功',
              icon: 'success'
          });
            
            // 支付成功后跳转到支付成功页面
          setTimeout(() => {
              uni.redirectTo({
                url: `/pages/paySuccess/paySuccess?orderId=${this.orderId}&amount=${this.finalAmount}`
            });
            }, 1500);
          } else {
            uni.showToast({
              title: paymentResult.message || '支付失败，请重试',
              icon: 'none'
            });
          }
        } else {
          uni.showToast({
            title: '支付请求失败',
            icon: 'none'
          });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('支付处理失败:', error);
        uni.showToast({
          title: '支付处理失败，请稍后重试',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    }
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