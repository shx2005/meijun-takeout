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
    this.loadCartFromLocalStorage();
  },
  onShow() {
    // 每次页面显示时重新从本地存储获取购物车数据
    this.loadCartFromLocalStorage();
  },
  methods: {
    // 从本地存储加载购物车数据
    loadCartFromLocalStorage() {
      try {
        this.loading = true;
        
        // 检查登录状态
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
        
        // 从本地存储获取购物车数据
        const cartItemsStr = uni.getStorageSync('cartItems');
        if (cartItemsStr) {
          const rawCartItems = JSON.parse(cartItemsStr);
          console.log('从本地存储加载的原始购物车数据:', rawCartItems);
          
          // 确保数据结构一致性 - 统一字段名称
          this.cartItems = rawCartItems.map(item => ({
            id: item.id,
            name: item.name || '菜品',
            price: item.price || 0,
            image: item.image || '/static/images/default-food.png',
            quantity: item.quantity || item.number || 1
          }));
          
          console.log('处理后的购物车数据:', this.cartItems);
        } else {
          // 没有本地数据，初始化空购物车
          this.cartItems = [];
          this.saveToLocalStorage();
        }
        
        // 计算总价
        this.calculateTotal();
      } catch (error) {
        console.error('加载本地购物车数据失败:', error);
        this.cartItems = [];
      } finally {
        this.loading = false;
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
        // 先更新本地数量，提升用户体验的响应速度
      item.quantity += 1;
      this.calculateTotal();
      this.saveToLocalStorage();
        
        // 获取token
        const token = uni.getStorageSync('token');
        if (!token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          return;
        }
        
        // 调用添加购物车API
        const res = await uni.request({
          url: 'http://localhost:8080/api/v1/cart/add',
          method: 'POST',
          header: {
            'customerToken': token,
            'Accept': 'application/json',
            'userType': '3',
            'Content-Type': 'application/json'
          },
          data: {
            itemId: item.id,
            quantity: 1  // 每次添加1个
          }
        });
        
        console.log('添加商品API响应:', res);
      } catch (error) {
        console.error('添加商品失败:', error);
        // 如果API调用失败，回滚本地状态
        item.quantity -= 1;
        this.calculateTotal();
        this.saveToLocalStorage();
        
        uni.showToast({
          title: '添加失败，请重试',
          icon: 'none'
        });
      }
    },

    // 减少商品数量
    async decreaseQuantity(item) {
      if (item.quantity <= 1) {
        // 如果只剩一个，询问是否从购物车移除
        uni.showModal({
          title: '提示',
          content: '是否从购物车移除该商品？',
          success: (res) => {
            if (res.confirm) {
              // 确认移除
              this.removeCartItem(item);
            }
          }
        });
      } else {
        try {
          // 先更新本地数量，提升用户体验的响应速度
        item.quantity -= 1;
        this.calculateTotal();
        this.saveToLocalStorage();
          
          // 获取token
          const token = uni.getStorageSync('token');
          if (!token) {
            uni.showToast({
              title: '请先登录',
              icon: 'none'
            });
            return;
          }
          
          // 调用减少购物车API
          const res = await uni.request({
            url: 'http://localhost:8080/api/v1/cart/sub',
            method: 'POST',
            header: {
              'customerToken': token,
              'Accept': 'application/json',
              'userType': '3',
              'Content-Type': 'application/json'
            },
            data: {
              itemId: item.id,
              quantity: 1  // 每次减少1个
            }
          });
          
          console.log('减少商品API响应:', res);
        } catch (error) {
          console.error('减少商品数量失败:', error);
          // 如果API调用失败，回滚本地状态
          item.quantity += 1;
          this.calculateTotal();
          this.saveToLocalStorage();
          
          uni.showToast({
            title: '操作失败，请重试',
            icon: 'none'
          });
        }
      }
    },

    // 从购物车移除商品
    async removeCartItem(item) {
      try {
        // 先从本地移除，提升用户体验的响应速度
      const index = this.cartItems.findIndex(cartItem => cartItem.id === item.id);
      if (index > -1) {
        // 删除数组中的项
        this.cartItems.splice(index, 1);
        this.calculateTotal();
        this.saveToLocalStorage();
        }
        
        // 获取token
        const token = uni.getStorageSync('token');
        if (!token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          return;
        }
        
        // 调用删除购物车API
        const res = await uni.request({
          url: 'http://localhost:8080/api/v1/cart/delete',
          method: 'DELETE',
          header: {
            'customerToken': token,
            'Accept': 'application/json',
            'userType': '3',
            'Content-Type': 'application/json'
          },
          data: {
            itemId: item.id
          }
        });
        
        console.log('删除商品API响应:', res);
        
        uni.showToast({
          title: '已移除商品',
          icon: 'success'
        });
      } catch (error) {
        console.error('删除商品失败:', error);
        uni.showToast({
          title: '删除失败，请重试',
          icon: 'none'
        });
      }
    },

    // 保存到本地存储
    saveToLocalStorage() {
      try {
        // 保存时统一转换为统一格式
        const dataToSave = this.cartItems.map(item => ({
          id: item.id,
          name: item.name,
          price: item.price,
          image: item.image,
          number: item.quantity // 使用number作为统一的数量字段
        }));
        
        uni.setStorageSync('cartItems', JSON.stringify(dataToSave));
        console.log('购物车数据已更新到本地存储:', dataToSave);
      } catch (error) {
        console.error('保存购物车到本地存储失败:', error);
      }
    },

    // 清空购物车
    async handleClearCart() {
      if (this.cartItems.length === 0) return;
      
      uni.showModal({
        title: '提示',
        content: '确定要清空购物车吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              // 先清空本地购物车
            this.cartItems = [];
            this.totalPrice = '0.00';
              this.saveToLocalStorage();
            
              // 获取token
              const token = uni.getStorageSync('token');
              if (!token) {
                uni.showToast({
                  title: '请先登录',
                  icon: 'none'
                });
                return;
              }
              
              // 调用清空购物车API
              await this.clearCartInServer(token);
            
            uni.showToast({
              title: '购物车已清空',
              icon: 'success'
            });
            } catch (error) {
              console.error('清空购物车失败:', error);
              uni.showToast({
                title: '操作失败，请重试',
                icon: 'none'
              });
            }
          }
        }
      });
    },
    
    // 清空服务器购物车
    async clearCartInServer(token) {
      try {
        // 获取购物车数据
        const res = await uni.request({
          url: 'http://localhost:8080/api/v1/cart',
          method: 'GET',
          header: {
            'customerToken': token,
            'userType': '3',
            'Content-Type': 'application/json'
          }
        });
        
        // 如果有数据，循环删除
        if (res && res[1].statusCode === 200 && res[1].data && res[1].data.data && res[1].data.data.items) {
          const items = res[1].data.data.items;
          for (const item of items) {
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
          }
        }
        
        console.log('服务器购物车已清空');
      } catch (error) {
        console.error('清空服务器购物车失败:', error);
        throw error;
      }
    },

    // 前往结算
    async handleCheckout() {
      if (this.cartItems.length === 0) {
        uni.showToast({
          title: '购物车为空',
          icon: 'none'
        });
        return;
      }
      
      try {
        // 显示加载
        uni.showLoading({
          title: '正在处理...'
        });
        
        // 获取token
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
        
        // 保存购物车到本地存储，用于结算页面显示
        console.log('保存购物车数据到本地存储，准备结算...');
        this.saveToLocalStorage();
        
        uni.hideLoading();
      
      // 跳转到下单页面
      uni.navigateTo({
        url: '/pages/addOrder/addOrder'
      });
      } catch (error) {
        uni.hideLoading();
        console.error('处理购物车失败:', error);
        uni.showToast({
          title: '处理失败，请重试',
          icon: 'none'
        });
      }
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