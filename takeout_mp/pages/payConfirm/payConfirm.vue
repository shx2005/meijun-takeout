<template>
  <view class="confirm-payment-container">
    <view class="info-section">
      <view class="info-item">
        <text class="label">订单金额：</text>
        <text class="value amount">￥{{ orderAmount }}</text>
      </view>
      
      <!-- 优惠券选择区域 -->
      <view class="info-item coupon-section" @tap="showCouponSelector">
        <text class="label">优惠券：</text>
        <view class="coupon-value">
          <text v-if="selectedCoupon" class="value coupon">{{ selectedCoupon.name }}</text>
          <text v-else class="value coupon-placeholder">选择优惠券</text>
          <u-icon name="arrow-right" size="28"></u-icon>
        </view>
      </view>
      
      <!-- 优惠后金额 -->
      <view class="info-item" v-if="discountAmount > 0">
        <text class="label">优惠金额：</text>
        <text class="value discount">-￥{{ discountAmount }}</text>
      </view>
      
      <view class="info-item">
        <text class="label">实付金额：</text>
        <text class="value final-amount">￥{{ finalAmount }}</text>
      </view>
      
      <view class="info-item">
        <text class="label">支付方式：</text>
        <text class="value">{{ paymentMethod }}</text>
      </view>
      
      <view class="info-item">
        <text class="label">当前余额：</text>
        <text class="value">￥{{ balance }}</text>
      </view>
    </view>

    <button class="confirm-button" @tap="handleConfirmPayment" :disabled="loading || !orderId">
      {{ loading ? '支付中...' : '确认支付' }}
    </button>
    
    <!-- 优惠券选择弹窗 -->
    <u-popup :show="showCouponPopup" mode="bottom" @close="closeCouponPopup" round="10" closeable>
      <view class="coupon-popup">
        <view class="popup-title">选择优惠券</view>
        <scroll-view scroll-y class="coupon-list">
          <!-- 无优惠券时显示 -->
          <view class="no-coupon" v-if="couponList.length === 0">
            <text>暂无可用优惠券</text>
          </view>
          
          <!-- 优惠券列表 -->
          <view 
            v-for="(coupon, index) in couponList" 
            :key="index" 
            class="coupon-item"
            :class="{'selected': selectedCoupon && selectedCoupon.id === coupon.id}"
            @tap="selectCoupon(coupon)"
          >
            <view class="coupon-left">
              <text class="coupon-amount">￥{{ coupon.amount }}</text>
              <text class="coupon-condition" v-if="coupon.minAmount > 0">满{{ coupon.minAmount }}元可用</text>
              <text class="coupon-condition" v-else>无门槛</text>
            </view>
            <view class="coupon-right">
              <text class="coupon-name">{{ coupon.name }}</text>
              <text class="coupon-date">{{ coupon.startDate }} - {{ coupon.endDate }}</text>
              <text class="coupon-desc">{{ coupon.description }}</text>
            </view>
            <view class="coupon-select" v-if="selectedCoupon && selectedCoupon.id === coupon.id">
              <u-icon name="checkmark-circle" color="#feca50" size="40"></u-icon>
            </view>
          </view>
        </scroll-view>
        
        <view class="popup-actions">
          <button class="popup-btn cancel" @tap="closeCouponPopup">取消</button>
          <button class="popup-btn confirm" @tap="confirmCouponSelection">确定</button>
        </view>
      </view>
    </u-popup>
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
      showCouponPopup: false,
      couponList: [],
      selectedCoupon: null,
      tempSelectedCoupon: null, // 临时选择，确认后才赋值给selectedCoupon
      discountAmount: 0, // 优惠金额
      finalAmount: 0, // 实付金额
    };
  },
  onLoad(options) {
    // onLoad中获取路由参数
    if (options.orderId && options.amount) {
      this.orderId = options.orderId;
      this.orderAmount = parseFloat(options.amount).toFixed(2);
      this.finalAmount = this.orderAmount; // 初始化实付金额等于订单金额
    } else {
      uni.showToast({
        title: '订单信息错误',
        icon: 'none',
        duration: 2000
      });
      // 可以选择返回上一页或跳转到订单列表
      // uni.navigateBack(); 
    }
    this.fetchBalance();
    this.fetchCoupons(); // 获取优惠券列表
  },
  methods: {
    async fetchBalance() {
      try {
        // uni.getStorageSync('token') 确保token存在
        if (!uni.getStorageSync('token')) {
            uni.showToast({ title: '请先登录', icon: 'none' });
            // 跳转到登录页
            uni.navigateTo({ url: '/pages/my/my' });
            return;
        }
        const res = await request({
          url: '/api/v1/user/balance', // 根据api.md
          method: 'GET'
        });
        if (res && res.data && typeof res.data.balance !== 'undefined') { // 假设API返回 {code: 0, message: "", data: {balance: 100.00}}
          this.balance = parseFloat(res.data.balance).toFixed(2);
        } else if (res && res.code === 0 && typeof res.balance !== 'undefined') { // 兼容直接返回 {code:0, message:"", balance: 100.00} 的情况
          this.balance = parseFloat(res.balance).toFixed(2);
        }
         else {
          console.error("获取余额失败: ", res);
          this.balance = '获取失败';
          uni.showToast({
            title: res.msg || '获取余额失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('获取余额接口请求失败:', error);
        this.balance = '获取失败';
        uni.showToast({
          title: '获取余额失败，请稍后重试',
          icon: 'none'
        });
      }
    },
    
    // 获取可用优惠券列表
    async fetchCoupons() {
      try {
        // 这里应该调用获取优惠券列表的API
        // 由于API文档中没有提供获取优惠券列表的接口，这里使用模拟数据
        // 实际项目中应该替换为真实API调用
        
        // 模拟数据
        setTimeout(() => {
          this.couponList = [
            {
              id: 1,
              name: '新人优惠券',
              amount: 5,
              minAmount: 20,
              startDate: '2023-06-01',
              endDate: '2023-12-31',
              description: '新用户专享优惠'
            },
            {
              id: 2,
              name: '满减优惠券',
              amount: 10,
              minAmount: 50,
              startDate: '2023-06-01',
              endDate: '2023-12-31',
              description: '满50元减10元'
            },
            {
              id: 3,
              name: '无门槛优惠券',
              amount: 3,
              minAmount: 0,
              startDate: '2023-06-01',
              endDate: '2023-12-31',
              description: '任意消费可用'
            }
          ];
        }, 500);
      } catch (error) {
        console.error('获取优惠券列表失败:', error);
        uni.showToast({
          title: '获取优惠券列表失败',
          icon: 'none'
        });
      }
    },
    
    // 显示优惠券选择弹窗
    showCouponSelector() {
      this.tempSelectedCoupon = this.selectedCoupon;
      this.showCouponPopup = true;
    },
    
    // 关闭优惠券选择弹窗
    closeCouponPopup() {
      this.showCouponPopup = false;
    },
    
    // 选择优惠券
    selectCoupon(coupon) {
      // 检查优惠券是否可用（订单金额是否满足最低消费）
      if (parseFloat(this.orderAmount) < coupon.minAmount) {
        uni.showToast({
          title: `订单金额不满${coupon.minAmount}元，无法使用该优惠券`,
          icon: 'none'
        });
        return;
      }
      
      this.tempSelectedCoupon = coupon;
    },
    
    // 确认选择优惠券
    async confirmCouponSelection() {
      this.selectedCoupon = this.tempSelectedCoupon;
      this.showCouponPopup = false;
      
      if (this.selectedCoupon) {
        try {
          // 调用优惠券计算接口
          const params = {
            couponId: this.selectedCoupon.id,
            orderId: this.orderId,
            amount: this.orderAmount,
            payType: this.payType
          };
          
          // 实际项目中应该调用真实API
          // const res = await request({
          //   url: '/api/v1/coupons/calculate',
          //   method: 'POST',
          //   data: params
          // });
          
          // 模拟API响应
          const res = {
            code: 0,
            message: 'success',
            data: {
              amount: (parseFloat(this.orderAmount) - this.selectedCoupon.amount).toFixed(2)
            }
          };
          
          if (res && res.code === 0 && res.data) {
            this.discountAmount = this.selectedCoupon.amount.toFixed(2);
            this.finalAmount = res.data.amount;
          } else {
            uni.showToast({
              title: res.message || '计算优惠失败',
              icon: 'none'
            });
          }
        } catch (error) {
          console.error('计算优惠失败:', error);
          uni.showToast({
            title: '计算优惠失败，请重试',
            icon: 'none'
          });
        }
      } else {
        // 如果取消选择优惠券，恢复原价
        this.discountAmount = 0;
        this.finalAmount = this.orderAmount;
      }
    },
    
    async handleConfirmPayment() {
      if (!this.orderId) {
        uni.showToast({ title: '订单ID缺失', icon: 'none' });
        return;
      }
      
      // 检查余额是否足够支付实际金额
      if (parseFloat(this.balance) < parseFloat(this.finalAmount)) {
        uni.showToast({ title: '余额不足，请充值', icon: 'none' });
        return;
      }

      this.loading = true;
      try {
        let params = {
          orderId: this.orderId,
          amount: this.finalAmount,
          payType: this.payType 
        };
        
        // 如果选择了优惠券，先进行优惠券验证
        if (this.selectedCoupon) {
          const couponParams = {
            couponId: this.selectedCoupon.id,
            orderId: this.orderId,
            amount: this.orderAmount,
            payType: this.payType
          };
          
          try {
            // 实际项目中应该调用真实API
            // const validateRes = await request({
            //   url: '/api/v1/coupons/validate',
            //   method: 'POST',
            //   data: couponParams
            // });
            
            // 模拟API响应
            const validateRes = {
              code: 0,
              message: 'success',
              data: {
                amount: this.finalAmount,
                orderId: this.orderId,
                payType: this.payType
              }
            };
            
            if (!(validateRes && validateRes.code === 0)) {
              uni.showToast({
                title: validateRes.message || '优惠券验证失败',
                icon: 'none'
              });
              this.loading = false;
              return;
            }
            
            // 更新支付参数
            params = validateRes.data;
          } catch (error) {
            console.error('优惠券验证失败:', error);
            uni.showToast({
              title: '优惠券验证失败，请重试',
              icon: 'none'
            });
            this.loading = false;
            return;
          }
        }
        
        console.log("支付参数：", params)
        const res = await request({
          url: '/api/v1/payment/balance', // 根据api.md
          method: 'POST',
          data: params
        });

        console.log("支付结果：", res)

        if (res && (res.code === 0 || res.code === 1 || res.code === 200)) { // 根据request.js的成功码判断
          uni.showToast({
            title: '支付成功！',
            icon: 'success',
            duration: 2000
          });
          // 支付成功后，可以跳转到支付成功页或订单详情页
          setTimeout(() => {
            uni.redirectTo({ // 使用redirectTo避免返回确认支付页
              url: `/pages/paySuccess/paySuccess?orderId=${this.orderId}&amount=${this.finalAmount}` // 假设有支付成功页
            });
          }, 2000);
        } else {
          uni.showToast({
            title: res.msg || '支付失败',
            icon: 'none',
            duration: 2000
          });
        }
      } catch (error) {
        console.error('支付接口请求失败:', error);
        let errMsg = '支付失败，请稍后重试';
        if(error && error.message){
            errMsg = error.message; // 使用拦截器中封装的错误信息
        }
        uni.showToast({
          title: errMsg,
          icon: 'none',
          duration: 2000
        });
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.confirm-payment-container {
  padding: 30rpx;
  background-color: #f9f9f9;
  min-height: 100vh;
  box-sizing: border-box;
}

.info-section {
  background-color: #ffffff;
  padding: 20rpx 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  margin-bottom: 40rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25rpx 0;
  border-bottom: 1rpx solid #eee;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 28rpx;
  color: #666;
}

.value {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
}

.amount {
  color: #ff5050;
  font-size: 32rpx;
  font-weight: bold;
}

.discount {
  color: #ff5050;
  font-size: 28rpx;
}

.final-amount {
  color: #ff5050;
  font-size: 36rpx;
  font-weight: bold;
}

.coupon-section {
  cursor: pointer;
}

.coupon-value {
  display: flex;
  align-items: center;
}

.coupon {
  color: #feca50;
  margin-right: 10rpx;
}

.coupon-placeholder {
  color: #999;
  margin-right: 10rpx;
}

.confirm-button {
  background-color: #feca50; /* 主题色 */
  color: #fff;
  font-size: 32rpx;
  border-radius: 50rpx;
  padding: 20rpx 0;
  text-align: center;
  box-shadow: 0 4rpx 12rpx rgba(254, 202, 80, 0.4);
  margin-top: 50rpx;
  border: none;
}

.confirm-button:active {
  background-color: #eAA_A0; /* 主题色深一点 */
}

.confirm-button[disabled] {
  background-color: #ccc;
  color: #999;
  box-shadow: none;
}

/* 优惠券弹窗样式 */
.coupon-popup {
  padding: 30rpx;
  max-height: 70vh;
}

.popup-title {
  font-size: 32rpx;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30rpx;
}

.coupon-list {
  max-height: 50vh;
}

.no-coupon {
  text-align: center;
  padding: 50rpx 0;
  color: #999;
}

.coupon-item {
  display: flex;
  padding: 20rpx;
  border-radius: 12rpx;
  background: #fff;
  margin-bottom: 20rpx;
  position: relative;
  border: 1rpx solid #eee;
}

.coupon-item.selected {
  border-color: #feca50;
  background-color: rgba(254, 202, 80, 0.05);
}

.coupon-left {
  width: 180rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-right: 1rpx dashed #eee;
  padding-right: 20rpx;
}

.coupon-amount {
  color: #ff5050;
  font-size: 40rpx;
  font-weight: bold;
}

.coupon-condition {
  font-size: 22rpx;
  color: #999;
  margin-top: 10rpx;
}

.coupon-right {
  flex: 1;
  padding-left: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.coupon-name {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 10rpx;
}

.coupon-date {
  font-size: 22rpx;
  color: #999;
  margin-bottom: 10rpx;
}

.coupon-desc {
  font-size: 24rpx;
  color: #666;
}

.coupon-select {
  position: absolute;
  right: 20rpx;
  top: 50%;
  transform: translateY(-50%);
}

.popup-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 30rpx;
}

.popup-btn {
  flex: 1;
  padding: 20rpx 0;
  text-align: center;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.popup-btn.cancel {
  background-color: #f5f5f5;
  color: #666;
  margin-right: 20rpx;
}

.popup-btn.confirm {
  background-color: #feca50;
  color: #fff;
}
</style> 