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
// 导入正确的API函数
import {
	cartListApi as getCartApi,
	addCartApi as addToCartApi,
	updateCartApi as updateCartItemApi,
	clearCartApi as clearAllCartApi
} from '../../api/index';

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
    // 从服务器获取购物车数据
    async fetchCartData() {
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
        
        // 直接使用uni.request调用购物车API
        const response = await uni.request({
          url: 'http://localhost:8080/api/v1/cart',
          method: 'GET',
          header: {
            'customerToken': token,
            'userType': '3',
            'Content-Type': 'application/json'
          }
        });
        
        console.log('购物车数据响应:', response);
        
        // 修复响应数据解析 - uni.request返回的是数组，第二个元素是响应对象
        const res = response[1];
        if (res && res.statusCode === 200 && res.data) {
          let result = res.data;
          
          // 检查是否为XML格式的响应
          if (typeof result === 'string' && result.includes('<Result>')) {
            console.log('检测到XML格式响应，开始解析');
            result = this.parseXMLCartResponse(result);
            console.log('XML解析后的结果:', result);
          }
          
          // 检查返回的数据结构
          if (result && result.code === 200 && result.data && result.data.items) {
            console.log('使用服务器返回的购物车数据');
            this.cartItems = result.data.items.map(item => ({
              id: item.itemId || item.id, // 菜品ID，用于前端显示匹配
              itemId: item.itemId, // 菜品ID
              cartItemId: item.id, // 购物车项ID，用于删除和更新操作
              name: item.name,
              price: item.price,
              image: item.image || '/static/images/default-food.png',
              quantity: item.quantity || item.number || 1, // 数量
              categoryId: item.categoryId
            }));
          } else {
            // 服务器返回空购物车或无效数据
            console.log('服务器返回空购物车数据');
            this.cartItems = [];
          }
        } else {
          // 获取失败，初始化为空
          console.log('获取服务器购物车数据失败，状态码:', res?.statusCode);
          this.cartItems = [];
        }
        
        // 计算总价
        this.calculateTotal();
      } catch (error) {
        console.error('获取购物车数据失败:', error);
        // 出错时初始化为空
        this.cartItems = [];
        this.calculateTotal();
      } finally {
        this.loading = false;
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

    // 计算总价
    calculateTotal() {
      this.totalPrice = this.cartItems.reduce((sum, item) => {
        return sum + (item.price * item.quantity);
      }, 0).toFixed(2);
    },

    // 增加商品数量
    async increaseQuantity(item) {
      try {
        console.log('增加商品数量:', item.name);
        
        const token = uni.getStorageSync('token');
        if (!token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          return;
        }
        
        // 获取用户ID
        const userId = uni.getStorageSync('userId') || 1;
        
        // 调用添加购物车API
        const response = await uni.request({
          url: 'http://localhost:8080/api/v1/cart/add',
          method: 'POST',
          header: {
            'customerToken': token,
            'Accept': 'application/json',
            'userType': '3',
            'Content-Type': 'application/json'
          },
          data: {
            userId: userId,
            itemId: item.itemId || item.id,
            itemType: 'dish',
            quantity: 1
          }
        });
        
        console.log('添加购物车API结果:', response);
        
        // 修复响应解析
        const res = response[1];
        if (res && res.statusCode === 200 && res.data && res.data.success) {
          // 添加成功，重新从服务器获取购物车数据
          await this.fetchCartData();
          
          // 显示成功提示
          uni.showToast({
            title: '已添加',
            icon: 'success',
            duration: 1000
          });
        } else {
          throw new Error('添加失败');
        }
        
      } catch (error) {
        console.error('增加商品数量失败:', error);
        uni.showToast({
          title: '操作失败，请重试',
          icon: 'none'
        });
      }
    },

    // 减少商品数量
    async decreaseQuantity(item) {
      try {
        console.log('减少商品数量:', item.name);
        
        if (item.quantity <= 1) {
          // 如果只剩一个，询问是否从购物车移除
          uni.showModal({
            title: '提示',
            content: '是否从购物车移除该商品？',
            success: async (res) => {
              if (res.confirm) {
                // 确认移除，直接调用删除API
                await this.removeCartItem(item);
              }
            }
          });
          return;
        }
        
        const token = uni.getStorageSync('token');
        if (!token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          return;
        }
        
        // 先删除该购物车项
        await uni.request({
          url: `http://localhost:8080/api/v1/cart/delete?cartItemId=${item.cartItemId}`,
          method: 'DELETE',
          header: {
            'customerToken': token,
            'Accept': 'application/json',
            'userType': '3',
            'Content-Type': 'application/json'
          }
        });
        
        // 如果还有剩余数量，重新添加
        if (item.quantity > 1) {
          const userId = uni.getStorageSync('userId') || 1;
          await uni.request({
            url: 'http://localhost:8080/api/v1/cart/add',
            method: 'POST',
            header: {
              'customerToken': token,
              'Accept': 'application/json',
              'userType': '3',
              'Content-Type': 'application/json'
            },
            data: {
              userId: userId,
              itemId: item.itemId || item.id,
              itemType: 'dish',
              quantity: item.quantity - 1
            }
          });
        }
        
        // 重新获取购物车数据
        await this.fetchCartData();
        
        uni.showToast({
          title: '已减少',
          icon: 'success',
          duration: 1000
        });
        
      } catch (error) {
        console.error('减少商品数量失败:', error);
        uni.showToast({
          title: '操作失败，请重试',
          icon: 'none'
        });
      }
    },

    // 从购物车移除商品
    async removeCartItem(item) {
      try {
        console.log('移除商品:', item.name);
        
        const token = uni.getStorageSync('token');
        if (!token) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          });
          return;
        }
        
        // 调用删除API，使用cartItemId
        const response = await uni.request({
          url: `http://localhost:8080/api/v1/cart/delete?cartItemId=${item.cartItemId}`,
          method: 'DELETE',
          header: {
            'customerToken': token,
            'Accept': 'application/json',
            'userType': '3',
            'Content-Type': 'application/json'
          }
        });
        
        console.log('删除购物车API结果:', response);
        
        // 重新从服务器获取购物车数据
        await this.fetchCartData();
        
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

    // 清空购物车
    async handleClearCart() {
      if (this.cartItems.length === 0) return;
      
      uni.showModal({
        title: '提示',
        content: '确定要清空购物车吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              const token = uni.getStorageSync('token');
              if (!token) {
                uni.showToast({
                  title: '请先登录',
                  icon: 'none'
                });
                return;
              }
              
              // 遍历删除所有项，使用cartItemId
              for (const item of this.cartItems) {
                await uni.request({
                  url: `http://localhost:8080/api/v1/cart/delete?cartItemId=${item.cartItemId}`,
                  method: 'DELETE',
                  header: {
                    'customerToken': token,
                    'Accept': 'application/json',
                    'userType': '3',
                    'Content-Type': 'application/json'
                  }
                });
              }
              
              // 重新获取购物车数据
              await this.fetchCartData();
            
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