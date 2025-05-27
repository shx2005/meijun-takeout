/**
 * 购物车本地存储工具库
 * 提供统一的购物车数据操作接口
 */

const CART_STORAGE_KEY = 'cartItems';

/**
 * 从本地存储获取购物车数据
 * @returns {Array} 购物车数据数组，如果不存在则返回空数组
 */
export function getCartFromStorage() {
  try {
    const cartData = uni.getStorageSync(CART_STORAGE_KEY);
    if (cartData) {
      return JSON.parse(cartData);
    }
  } catch (error) {
    console.error('读取购物车数据失败:', error);
  }
  return [];
}

/**
 * 保存购物车数据到本地存储
 * @param {Array} cartItems 购物车数据数组
 */
export function saveCartToStorage(cartItems) {
  try {
    uni.setStorageSync(CART_STORAGE_KEY, JSON.stringify(cartItems || []));
    return true;
  } catch (error) {
    console.error('保存购物车数据失败:', error);
    return false;
  }
}

/**
 * 清空本地存储中的购物车数据
 */
export function clearCartStorage() {
  try {
    uni.removeStorageSync(CART_STORAGE_KEY);
    return true;
  } catch (error) {
    console.error('清空购物车数据失败:', error);
    return false;
  }
}

/**
 * 添加商品到本地购物车
 * @param {Object} item 商品对象
 * @returns {Array} 更新后的购物车数据
 */
export function addToLocalCart(item) {
  try {
    if (!item || !item.id) {
      console.error('添加到购物车的商品无效');
      return [];
    }
    
    const cartItems = getCartFromStorage();
    const existingIndex = cartItems.findIndex(cartItem => cartItem.id === item.id);
    
    if (existingIndex >= 0) {
      // 已存在，增加数量
      cartItems[existingIndex].number = (cartItems[existingIndex].number || 1) + 1;
    } else {
      // 不存在，添加新项
      cartItems.push({
        id: item.id,
        name: item.name || '商品',
        price: item.price || 0,
        image: item.image || '/static/images/default-food.png',
        number: item.quantity || item.number || 1,
        categoryId: item.categoryId
      });
    }
    
    saveCartToStorage(cartItems);
    return cartItems;
  } catch (error) {
    console.error('添加商品到本地购物车失败:', error);
    return getCartFromStorage();
  }
}

/**
 * 从本地购物车中移除商品
 * @param {number|string} itemId 商品ID
 * @returns {Array} 更新后的购物车数据
 */
export function removeFromLocalCart(itemId) {
  try {
    if (!itemId) {
      console.error('要移除的商品ID无效');
      return [];
    }
    
    const cartItems = getCartFromStorage();
    const filteredItems = cartItems.filter(item => item.id !== itemId);
    
    saveCartToStorage(filteredItems);
    return filteredItems;
  } catch (error) {
    console.error('从本地购物车移除商品失败:', error);
    return getCartFromStorage();
  }
}

/**
 * 计算购物车总数和总价
 * @returns {Object} 包含总数和总价的对象 {count, price}
 */
export function calculateCartTotals() {
  try {
    const cartItems = getCartFromStorage();
    let count = 0;
    let price = 0;
    
    cartItems.forEach(item => {
      const itemNumber = item.number || item.quantity || 0;
      count += itemNumber;
      price += itemNumber * (item.price || 0);
    });
    
    return {
      count,
      price: price.toFixed(2)
    };
  } catch (error) {
    console.error('计算购物车总数和总价失败:', error);
    return { count: 0, price: '0.00' };
  }
}

export default {
  getCartFromStorage,
  saveCartToStorage,
  clearCartStorage,
  addToLocalCart,
  removeFromLocalCart,
  calculateCartTotals
}; 