<template>
  <view class="cart-container">
    <!-- 顶部标题 -->
    <view class="header">
      <text class="title">购物车</text>
      <text class="clear" @click="handleClearCart" v-if="cartItems.length > 0">清空</text>
    </view>

    <!-- 购物车列表 -->
    <view class="cart-list" v-if="cartItems.length > 0">
      <view class="cart-item" v-for="(item, index) in cartItems" :key="index">
        <image class="item-image" :src="item.image || '/static/images/default-food.png'" mode="aspectFill"></image>
        <view class="item-info">
          <text class="item-name">{{ item.name }}</text>
          <text class="item-price">¥{{ item.price }}</text>
        </view>
        <view class="item-actions">
          <view class="quantity-control">
            <text class="quantity-btn" @click="decreaseQuantity(item)">-</text>
            <text class="quantity-value">{{ item.quantity }}</text>
            <text class="quantity-btn" @click="increaseQuantity(item)">+</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 空购物车状态 -->
    <view class="empty-cart" v-else>
      <image src="/static/images/empty-cart.png" mode="aspectFit" class="empty-image"></image>
      <text class="empty-text">购物车空空如也</text>
      <button class="go-shopping-btn" @click="goShopping">去购物</button>
    </view>

    <!-- 底部结算栏 -->
    <view class="checkout-bar" v-if="cartItems.length > 0">
      <view class="total-price">
        <text>合计：</text>
        <text class="price">¥{{ totalPrice }}</text>
      </view>
      <button class="checkout-btn" @click="handleCheckout">去结算</button>
    </view>
  </view>
</template>

<script>
import { cartListApi, updateCartApi, clearCartApi, addCartApi } from '@/api/index.js';

export default {
  data() {
    return {
      cartItems: [],
      totalPrice: 0,
      loading: false
    };
  },
  onLoad() {
    // 页面加载时获取购物车数据
    this.fetchCartData();
  },
  onShow() {
    // 每次页面显示时重新获取购物车数据
    this.fetchCartData();
  },
  methods: {
    // 获取购物车数据
    async fetchCartData() {
      try {
        this.loading = true;
        const token = uni.getStorageSync('token');
        
        if (!token) {
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

        // 先从本地存储获取购物车数据
        const localCart = uni.getStorageSync('cartItems');
        if (localCart) {
          try {
            this.cartItems = JSON.parse(localCart);
            this.calculateTotal();
            console.log('从本地存储加载购物车数据成功:', this.cartItems);
            this.loading = false;
            return; // 如果成功从本地加载，就不再请求API
          } catch (parseError) {
            console.error('解析本地购物车数据失败:', parseError);
            // 解析失败继续执行后续代码尝试API获取
          }
        }
        
        // 本地存储没有购物车数据或解析失败，尝试从API获取
        console.log('本地没有有效的购物车数据，从API获取');
        uni.showLoading({ title: '加载中...' });
        
        // 调用购物车列表API
        try {
          const response = await cartListApi();
          uni.hideLoading();
          
          if (response && response.code === 0 && response.data) {
            let cartData = [];
            
            // 提取购物车数据
            if (Array.isArray(response.data)) {
              cartData = response.data;
            } else if (response.data && Array.isArray(response.data.items)) {
              cartData = response.data.items;
            } else if (response && Array.isArray(response.items)) {
              cartData = response.items;
            }
            
            // 格式化购物车数据
            this.cartItems = cartData.map(item => ({
              id: item.itemId || item.id,
              name: item.name || '菜品',
              price: item.price || 0,
              image: item.image || '/static/images/default-food.png',
              quantity: item.quantity || item.number || 1
            }));
            
            // 保存到本地存储
            uni.setStorageSync('cartItems', JSON.stringify(this.cartItems));
            this.calculateTotal();
          } else {
            uni.showToast({
              title: '获取购物车数据失败',
              icon: 'none'
            });
          }
        } catch (apiError) {
          console.error('API获取购物车数据失败:', apiError);
          uni.hideLoading();
          uni.showToast({
            title: '获取购物车数据失败',
            icon: 'none'
          });
        }
      } catch (error) {
        console.error('购物车数据加载过程出错:', error);
      } finally {
        this.loading = false;
        uni.hideLoading();
      }
    },

    // 计算总价
    calculateTotal() {
      this.totalPrice = this.cartItems.reduce((sum, item) => {
        return sum + (item.price * item.quantity);
      }, 0).toFixed(2);
    },

    // 增加商品数量
    async increaseQuantity(item) {
      try {
        item.quantity += 1;
        this.calculateTotal();
        
        // 更新购物车API
        await this.updateCartItem(item);
      } catch (error) {
        console.error('增加商品数量失败:', error);
        // 恢复原来的数量
        item.quantity -= 1;
        this.calculateTotal();
      }
    },

    // 减少商品数量
    async decreaseQuantity(item) {
      if (item.quantity <= 1) {
        // 如果只剩一个，询问是否从购物车移除
        uni.showModal({
          title: '提示',
          content: '是否从购物车移除该商品？',
          success: async (res) => {
            if (res.confirm) {
              // 确认移除
              await this.removeCartItem(item);
            }
          }
        });
      } else {
        try {
          item.quantity -= 1;
          this.calculateTotal();
          
          // 更新购物车API
          await this.updateCartItem(item);
        } catch (error) {
          console.error('减少商品数量失败:', error);
          // 恢复原来的数量
          item.quantity += 1;
          this.calculateTotal();
        }
      }
    },

    // 更新购物车商品
    async updateCartItem(item) {
      try {
        const data = {
          id: item.id,
          quantity: item.quantity
        };
        
        await updateCartApi(data);
        
        // 更新本地存储
        this.saveToLocalStorage();
      } catch (error) {
        console.error('更新购物车失败:', error);
        uni.showToast({
          title: '更新购物车失败',
          icon: 'none'
        });
      }
    },

    // 从购物车移除商品
    async removeCartItem(item) {
      try {
        const index = this.cartItems.findIndex(cartItem => cartItem.id === item.id);
        if (index > -1) {
          // 删除数组中的项
          this.cartItems.splice(index, 1);
          this.calculateTotal();
          
          // 更新服务器
          try {
            await this.updateCartItem({
              id: item.id,
              quantity: 0 // 设为0表示删除
            });
          } catch (apiError) {
            console.error('服务器移除商品失败:', apiError);
            // 即使API失败也继续更新本地存储
          }
          
          // 更新本地存储
          this.saveToLocalStorage();
          
          uni.showToast({
            title: '已移除商品',
            icon: 'success'
          });
        }
      } catch (error) {
        console.error('移除商品失败:', error);
        uni.showToast({
          title: '移除商品失败',
          icon: 'none'
        });
      }
    },

    // 保存到本地存储
    saveToLocalStorage() {
      try {
        uni.setStorageSync('cartItems', JSON.stringify(this.cartItems));
        console.log('购物车数据已更新到本地存储');
      } catch (error) {
        console.error('保存购物车到本地存储失败:', error);
      }
    },

    // 清空购物车
    handleClearCart() {
      if (this.cartItems.length === 0) return;
      
      uni.showModal({
        title: '提示',
        content: '确定要清空购物车吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await clearCartApi();
              this.cartItems = [];
              this.totalPrice = '0.00';
              
              // 清除本地存储
              uni.removeStorageSync('cartItems');
              
              uni.showToast({
                title: '购物车已清空',
                icon: 'success'
              });
            } catch (error) {
              console.error('清空购物车失败:', error);
              
              // 即使API失败，也清空本地购物车
              this.cartItems = [];
              this.totalPrice = '0.00';
              uni.removeStorageSync('cartItems');
              
              uni.showToast({
                title: 'API清空失败，已清空本地购物车',
                icon: 'none'
              });
            }
          }
        }
      });
    },

    // 前往结算
    handleCheckout() {
      if (this.cartItems.length === 0) {
        uni.showToast({
          title: '购物车为空',
          icon: 'none'
        });
        return;
      }
      
      // 跳转到下单页面
      uni.navigateTo({
        url: '/pages/addOrder/addOrder'
      });
    },

    // 去购物
    goShopping() {
      uni.switchTab({
        url: '/pages/index/index'
      });
    }
  }
};
</script>

<style lang="scss">
.cart-container {
  min-height: 100vh;
  background-color: #f8f8f8;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 30rpx 30rpx 20rpx;
  background-color: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.clear {
  font-size: 26rpx;
  color: #999;
}

.cart-list {
  flex: 1;
  padding: 20rpx;
}

.cart-item {
  display: flex;
  padding: 20rpx;
  background-color: #fff;
  border-radius: 15rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.item-image {
  width: 160rpx;
  height: 160rpx;
  border-radius: 8rpx;
  background-color: #f5f5f5;
}

.item-info {
  flex: 1;
  padding: 0 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-name {
  font-size: 30rpx;
  color: #333;
  margin-bottom: 10rpx;
}

.item-price {
  font-size: 32rpx;
  color: #ff5722;
  font-weight: bold;
}

.item-actions {
  display: flex;
  align-items: center;
}

.quantity-control {
  display: flex;
  align-items: center;
}

.quantity-btn {
  width: 60rpx;
  height: 60rpx;
  line-height: 60rpx;
  text-align: center;
  border-radius: 50%;
  background-color: #f5f5f5;
  color: #333;
  font-size: 36rpx;
}

.quantity-value {
  width: 80rpx;
  text-align: center;
  font-size: 28rpx;
}

.empty-cart {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding-top: 100rpx;
}

.empty-image {
  width: 240rpx;
  height: 240rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 30rpx;
  color: #999;
  margin-bottom: 40rpx;
}

.go-shopping-btn {
  width: 240rpx;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  background-color: #4285f4;
  color: #fff;
  border-radius: 40rpx;
  font-size: 28rpx;
}

.checkout-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 30rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.total-price {
  font-size: 30rpx;
  color: #333;
}

.price {
  font-size: 34rpx;
  color: #ff5722;
  font-weight: bold;
}

.checkout-btn {
  width: 240rpx;
  height: 70rpx;
  line-height: 70rpx;
  text-align: center;
  background-color: #4285f4;
  color: #fff;
  border-radius: 35rpx;
  font-size: 28rpx;
}
</style> 