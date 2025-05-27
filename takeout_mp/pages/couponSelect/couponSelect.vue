<template>
  <view class="coupon-select">
    <view class="page-header">
      <text class="page-title">选择优惠券</text>
    </view>
    
    <view class="coupon-list">
      <view v-if="couponList.length === 0" class="no-coupon">
        <text>暂无可用优惠券</text>
      </view>
      <view 
        v-for="(coupon, index) in couponList" 
        :key="index" 
        class="coupon-item"
        :class="{ 'selected': selectedCouponId === coupon.id }"
        @click="selectCoupon(coupon)"
      >
        <view class="coupon-left">
          <text class="coupon-amount">¥{{ coupon.amount }}</text>
          <text class="coupon-condition" v-if="coupon.minAmount > 0">满{{ coupon.minAmount }}元可用</text>
          <text class="coupon-condition" v-else>无门槛</text>
        </view>
        <view class="coupon-right">
          <text class="coupon-name">{{ coupon.name }}</text>
          <text class="coupon-date">{{ coupon.startDate }} - {{ coupon.endDate }}</text>
          <text class="coupon-desc">{{ coupon.description }}</text>
        </view>
        <view class="coupon-select-icon" v-if="selectedCouponId === coupon.id">
          <text class="iconfont icon-check"></text>
        </view>
      </view>
    </view>
    
    <view class="bottom-btn-container">
      <button class="confirm-btn" @click="confirmSelection">确认选择</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      orderId: null,
      orderAmount: 0,
      couponList: [],
      selectedCouponId: null,
      selectedCoupon: null
    };
  },
  onLoad(options) {
    // 获取传递过来的订单信息
    if (options.orderId && options.amount) {
      this.orderId = parseInt(options.orderId);
      this.orderAmount = parseFloat(options.amount);
      
      // 如果有预选的优惠券ID
      if (options.selectedCouponId) {
        this.selectedCouponId = parseInt(options.selectedCouponId);
      }
    }
    
    // 获取优惠券列表
    this.fetchCoupons();
  },
  methods: {
    // 获取可用优惠券列表
    async fetchCoupons() {
      try {
        uni.showLoading({ title: '获取优惠券...' });
        
        // 这里应该调用获取优惠券列表的API
        // 目前使用模拟数据
        setTimeout(() => {
          uni.hideLoading();
          
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
          
          // 如果有预选ID，确认它存在于列表中
          if (this.selectedCouponId) {
            const exists = this.couponList.some(coupon => coupon.id === this.selectedCouponId);
            if (!exists) {
              this.selectedCouponId = null;
            }
          }
        }, 500);
      } catch (error) {
        uni.hideLoading();
        console.error('获取优惠券列表失败:', error);
        uni.showToast({
          title: '获取优惠券列表失败',
          icon: 'none'
        });
      }
    },
    
    // 选择优惠券
    selectCoupon(coupon) {
      if (this.selectedCouponId === coupon.id) {
        // 如果已选中，则取消选择
        this.selectedCouponId = null;
        this.selectedCoupon = null;
      } else {
        // 优惠券条件检查
        if (coupon.minAmount > this.orderAmount) {
          uni.showToast({
            title: `订单金额不满${coupon.minAmount}元，无法使用此优惠券`,
            icon: 'none'
          });
          return;
        }
        
        this.selectedCouponId = coupon.id;
        this.selectedCoupon = coupon;
      }
    },
    
    // 确认选择
    confirmSelection() {
      const pages = getCurrentPages();
      const prevPage = pages[pages.length - 2]; // 获取上一个页面
      
      // 将选择结果传递回上一页
      if (this.selectedCoupon) {
        // 如果选择了优惠券
        prevPage.$vm.applyCoupon(this.selectedCoupon);
      } else {
        // 如果取消了选择
        prevPage.$vm.removeCoupon();
      }
      
      // 返回上一页
      uni.navigateBack();
    }
  }
}
</script>

<style lang="scss">
.coupon-select {
  padding: 30rpx;
  padding-bottom: 120rpx; /* 为底部按钮留出空间 */
  background-color: #f8f8f8;
  min-height: 100vh;
  box-sizing: border-box;
  
  .page-header {
    padding: 20rpx 0;
    margin-bottom: 30rpx;
    
    .page-title {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
    }
  }
  
  .coupon-list {
    .no-coupon {
      padding: 40rpx 0;
      text-align: center;
      color: #999;
      background-color: #fff;
      border-radius: 12rpx;
    }
    
    .coupon-item {
      display: flex;
      padding: 20rpx;
      background-color: #fff;
      border-radius: 12rpx;
      margin-bottom: 20rpx;
      position: relative;
      
      &.selected {
        background-color: #ecf5ff;
        border: 2rpx solid #4285f4;
      }
      
      .coupon-left {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        padding: 0 20rpx;
        border-right: 1px dashed #ddd;
        min-width: 150rpx;
        
        .coupon-amount {
          font-size: 40rpx;
          color: #ff5722;
          font-weight: bold;
        }
        
        .coupon-condition {
          font-size: 22rpx;
          color: #999;
          margin-top: 10rpx;
        }
      }
      
      .coupon-right {
        flex: 1;
        padding: 0 20rpx;
        display: flex;
        flex-direction: column;
        justify-content: center;
        
        .coupon-name {
          font-size: 28rpx;
          margin-bottom: 10rpx;
        }
        
        .coupon-date, .coupon-desc {
          font-size: 22rpx;
          color: #999;
          margin-top: 6rpx;
        }
      }
      
      .coupon-select-icon {
        position: absolute;
        right: 20rpx;
        bottom: 20rpx;
        
        .iconfont {
          color: #4285f4;
          font-size: 32rpx;
        }
      }
    }
  }
  
  .bottom-btn-container {
    position: fixed;
    left: 0;
    bottom: 0;
    width: 100%;
    background-color: #fff;
    padding: 20rpx;
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
    z-index: 100;
    box-sizing: border-box;
    
    .confirm-btn {
      background-color: #4285f4;
      color: #fff;
      border-radius: 50rpx;
      font-size: 32rpx;
      height: 90rpx;
      line-height: 90rpx;
      width: 100%;
    }
  }
}
</style> 