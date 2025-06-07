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
          <text class="coupon-amount" v-if="coupon.type === 'fixed'">¥{{ coupon.amount }}</text>
          <text class="coupon-amount" v-else>{{ (100 - coupon.amount) }}折</text>
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
        
        // 检查登录状态
        const token = uni.getStorageSync('token');
        if (!token) {
          uni.hideLoading();
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          return;
        }
        
        // 调用服务器API获取优惠券列表
        const response = await uni.request({
          url: 'http://localhost:8080/api/v1/coupons',
          method: 'GET',
          header: {
            'customerToken': token,
            'userType': '3',
            'Content-Type': 'application/json'
          }
        });
        
        uni.hideLoading();
        
        console.log('优惠券列表响应:', response);
        
        const res = response[1];
        if (res && res.statusCode === 200) {
          let result = res.data;
          
          // 检查是否为XML格式响应
          if (typeof result === 'string' && result.includes('<Result>')) {
            console.log('检测到XML格式优惠券响应，开始解析');
            result = this.parseXMLCouponResponse(result);
            console.log('XML解析后的结果:', result);
          }
          
          if (result && result.code === 200 && result.success === true && result.data) {
            // 处理优惠券数据
            this.couponList = result.data.map(coupon => ({
              id: coupon.id,
              name: coupon.name,
              amount: coupon.value, // API返回的是value字段
              minAmount: coupon.minAmount || 0,
              maxAmount: coupon.maxAmount || 0,
              startDate: this.formatDate(coupon.startTime),
              endDate: this.formatDate(coupon.endTime),
              description: coupon.description || '',
              type: coupon.type, // fixed 或 percentage
              userId: coupon.userId
            }));
            
            console.log('处理后的优惠券列表:', this.couponList);
          } else {
            this.couponList = [];
            console.log('没有可用的优惠券');
          }
        } else {
          this.couponList = [];
          uni.showToast({
            title: '获取优惠券失败',
            icon: 'none'
          });
        }
          
          // 如果有预选ID，确认它存在于列表中
          if (this.selectedCouponId) {
            const exists = this.couponList.some(coupon => coupon.id === this.selectedCouponId);
            if (!exists) {
              this.selectedCouponId = null;
            }
          }
      } catch (error) {
        uni.hideLoading();
        console.error('获取优惠券列表失败:', error);
        uni.showToast({
          title: '获取优惠券列表失败',
          icon: 'none'
        });
        this.couponList = [];
      }
    },

    // 解析XML格式的优惠券响应
    parseXMLCouponResponse(xmlString) {
      try {
        console.log('开始解析XML优惠券响应');
        
        // 提取基本信息
        const codeMatch = xmlString.match(/<code>(.*?)<\/code>/);
        const msgMatch = xmlString.match(/<msg>(.*?)<\/msg>/);
        const successMatch = xmlString.match(/<success>(.*?)<\/success>/);
        
        // 提取所有优惠券项
        const couponMatches = xmlString.match(/<data>(.*?)<\/data>/g);
        const coupons = [];
        
        if (couponMatches) {
          couponMatches.forEach(couponXml => {
            const idMatch = couponXml.match(/<id>(.*?)<\/id>/);
            const nameMatch = couponXml.match(/<name>(.*?)<\/name>/);
            const descriptionMatch = couponXml.match(/<description>(.*?)<\/description>/);
            const typeMatch = couponXml.match(/<type>(.*?)<\/type>/);
            const valueMatch = couponXml.match(/<value>(.*?)<\/value>/);
            const minAmountMatch = couponXml.match(/<minAmount>(.*?)<\/minAmount>/);
            const maxAmountMatch = couponXml.match(/<maxAmount>(.*?)<\/maxAmount>/);
            const startTimeMatch = couponXml.match(/<startTime>(.*?)<\/startTime>/);
            const endTimeMatch = couponXml.match(/<endTime>(.*?)<\/endTime>/);
            
            if (idMatch && nameMatch) {
              coupons.push({
                id: parseInt(idMatch[1]),
                name: nameMatch[1],
                description: descriptionMatch ? descriptionMatch[1] : '',
                type: typeMatch ? typeMatch[1] : 'fixed',
                value: valueMatch ? parseFloat(valueMatch[1]) : 0,
                minAmount: minAmountMatch ? parseFloat(minAmountMatch[1]) : 0,
                maxAmount: maxAmountMatch ? parseFloat(maxAmountMatch[1]) : 0,
                startTime: startTimeMatch ? startTimeMatch[1] : '',
                endTime: endTimeMatch ? endTimeMatch[1] : ''
              });
            }
          });
        }
        
        const result = {
          code: codeMatch ? parseInt(codeMatch[1]) : 200,
          msg: msgMatch ? msgMatch[1] : 'OK',
          data: coupons,
          success: successMatch ? successMatch[1] === 'true' : true
        };
        
        console.log('XML解析完成，结果:', result);
        return result;
      } catch (error) {
        console.error('解析XML优惠券响应失败:', error);
        return {
          code: 500,
          msg: 'XML解析失败',
          data: [],
          success: false
        };
      }
    },

    // 格式化日期
    formatDate(dateArray) {
      if (!dateArray || !Array.isArray(dateArray) || dateArray.length < 3) {
        return '';
      }
      
      const year = dateArray[0];
      const month = String(dateArray[1]).padStart(2, '0');
      const day = String(dateArray[2]).padStart(2, '0');
      
      return `${year}-${month}-${day}`;
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
    async confirmSelection() {
      if (this.selectedCoupon) {
        // 如果选择了优惠券，先验证优惠券
        try {
          uni.showLoading({ title: '验证优惠券...' });
          
          const isValid = await this.validateCoupon(this.selectedCoupon);
          
          uni.hideLoading();
          
          if (isValid) {
            // 验证成功，传递给上一页
            const pages = getCurrentPages();
            const prevPage = pages[pages.length - 2];
        prevPage.$vm.applyCoupon(this.selectedCoupon);
            
            uni.showToast({
              title: '优惠券验证成功',
              icon: 'success'
            });
            
            setTimeout(() => {
              uni.navigateBack();
            }, 1000);
          } else {
            // 验证失败，取消选择
            this.selectedCouponId = null;
            this.selectedCoupon = null;
          }
        } catch (error) {
          uni.hideLoading();
          console.error('优惠券验证失败:', error);
          uni.showToast({
            title: '优惠券验证失败',
            icon: 'none'
          });
        }
      } else {
        // 如果取消了选择，直接返回
        const pages = getCurrentPages();
        const prevPage = pages[pages.length - 2];
        prevPage.$vm.removeCoupon();
        
        uni.navigateBack();
      }
    },

    // 验证优惠券
    async validateCoupon(coupon) {
      try {
        const token = uni.getStorageSync('token');
        if (!token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          return false;
        }
        
        // 准备验证数据
        const validateData = {
          couponId: coupon.id,
          orderId: this.orderId,
          amount: this.orderAmount,
          payType: 1 // 1表示余额支付
        };
        
        console.log('优惠券验证请求数据:', validateData);
        
        // 调用优惠券验证API
        const response = await uni.request({
          url: 'http://localhost:8080/api/v1/coupons/validate',
          method: 'POST',
          header: {
            'customerToken': token,
            'userType': '3',
            'Content-Type': 'application/json'
          },
          data: validateData
        });
        
        console.log('优惠券验证响应:', response);
        
        const res = response[1];
        if (res && res.statusCode === 200) {
          let result = res.data;
          
          // 检查是否为XML格式响应
          if (typeof result === 'string' && result.includes('<Result>')) {
            console.log('检测到XML格式验证响应，开始解析');
            result = this.parseXMLValidateResponse(result);
            console.log('XML解析后的结果:', result);
          }
          
          if (result && result.code === 200 && result.success === true) {
            // 验证成功，更新优惠券信息
            if (result.data && typeof result.data.amount !== 'undefined') {
              // 将验证后的金额信息添加到优惠券对象中
              this.selectedCoupon.validatedAmount = result.data.amount;
              this.selectedCoupon.discountAmount = this.orderAmount - result.data.amount;
            }
            
            return true;
          } else {
            uni.showToast({
              title: result?.msg || '优惠券验证失败',
              icon: 'none'
            });
            return false;
          }
        } else {
          uni.showToast({
            title: '优惠券验证请求失败',
            icon: 'none'
          });
          return false;
        }
      } catch (error) {
        console.error('优惠券验证异常:', error);
        uni.showToast({
          title: '优惠券验证异常',
          icon: 'none'
        });
        return false;
      }
    },

    // 解析XML格式的验证响应
    parseXMLValidateResponse(xmlString) {
      try {
        console.log('开始解析XML验证响应');
        
        // 提取基本信息
        const codeMatch = xmlString.match(/<code>(.*?)<\/code>/);
        const msgMatch = xmlString.match(/<msg>(.*?)<\/msg>/);
        const successMatch = xmlString.match(/<success>(.*?)<\/success>/);
        
        // 提取验证结果数据
        const amountMatch = xmlString.match(/<amount>(.*?)<\/amount>/);
        const orderIdMatch = xmlString.match(/<orderId>(.*?)<\/orderId>/);
        const payTypeMatch = xmlString.match(/<payType>(.*?)<\/payType>/);
        
        const result = {
          code: codeMatch ? parseInt(codeMatch[1]) : 200,
          msg: msgMatch ? msgMatch[1] : 'OK',
          data: {
            amount: amountMatch ? parseFloat(amountMatch[1]) : this.orderAmount,
            orderId: orderIdMatch ? parseInt(orderIdMatch[1]) : this.orderId,
            payType: payTypeMatch ? parseInt(payTypeMatch[1]) : 1
          },
          success: successMatch ? successMatch[1] === 'true' : true
        };
        
        console.log('XML验证解析完成，结果:', result);
        return result;
      } catch (error) {
        console.error('解析XML验证响应失败:', error);
        return {
          code: 500,
          msg: 'XML解析失败',
          data: {},
          success: false
        };
      }
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