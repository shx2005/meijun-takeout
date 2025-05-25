/**
 * 美颂外卖 - API客户端
 * 
 * 此文件演示如何使用token适配器正确调用后端API。
 * 将此文件与token_adapter.js一起集成到您的前端项目中。
 */

import axios from 'axios';
import tokenAdapter from './token_adapter';

// 创建axios实例
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'userType': '3'  // 根据实际需求设置
  }
});

// 应用token适配器
tokenAdapter.setup(apiClient);

// API函数
const API = {
  /**
   * 用户登录
   * @param {string} username - 用户名
   * @param {string} password - 密码
   * @param {string} identity - 身份类型
   * @returns {Promise}
   */
  login(username, password, identity = 'CUSTOMER') {
    return new Promise((resolve, reject) => {
      apiClient.post('/auth/login', { username, password, identity })
        .then(response => {
          if (response.data && response.data.code === 200) {
            // 保存token
            tokenAdapter.saveTokenFromResponse(response.data);
            resolve(response.data);
          } else {
            reject(new Error(response.data.msg || '登录失败'));
          }
        })
        .catch(error => {
          reject(error);
        });
    });
  },

  /**
   * 获取用户信息
   * @returns {Promise}
   */
  getUserInfo() {
    return apiClient.get('/user/info');
  },

  /**
   * 获取购物车内容
   * @returns {Promise}
   */
  getCart() {
    return apiClient.get('/cart');
  },

  /**
   * 添加商品到购物车
   * @param {number} itemId - 商品ID
   * @param {number} quantity - 数量
   * @param {string} itemType - 商品类型 (如 'DISH')
   * @returns {Promise}
   */
  addToCart(itemId, quantity = 1, itemType = 'DISH') {
    const userId = localStorage.getItem('userId') || 0;
    return apiClient.post('/cart/add', {
      itemId,
      quantity,
      userId: parseInt(userId),
      itemType
    });
  },

  /**
   * 获取菜品列表
   * @param {Object} params - 查询参数
   * @returns {Promise}
   */
  getDishes(params = {}) {
    return apiClient.get('/dishes/page', { params });
  }
};

// 测试函数
async function testAPI() {
  try {
    console.log('1. 测试登录...');
    const loginResult = await API.login('17344402975', '20050311', 'CUSTOMER');
    console.log('登录成功:', loginResult);

    console.log('\n2. 测试获取用户信息...');
    const userInfo = await API.getUserInfo();
    console.log('用户信息:', userInfo.data);

    console.log('\n3. 测试获取购物车...');
    const cart = await API.getCart();
    console.log('购物车:', cart.data);

    console.log('\n4. 测试添加商品到购物车...');
    const addResult = await API.addToCart(13, 1);
    console.log('添加结果:', addResult.data);

    console.log('\n5. 再次获取购物车...');
    const updatedCart = await API.getCart();
    console.log('更新后的购物车:', updatedCart.data);

  } catch (error) {
    console.error('测试出错:', error.message);
    if (error.response) {
      console.error('错误详情:', error.response.data);
    }
  }
}

// 如果需要在浏览器中直接运行测试
// window.testAPI = testAPI;

export default API; 