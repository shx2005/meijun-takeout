<template>
  <view class="container">
    <view class="header">
      <text class="title">授权测试页面</text>
    </view>
    
    <view class="content">
      <view class="section">
        <text class="section-title">当前Token</text>
        <text class="token-text">{{ token || '未设置' }}</text>
      </view>
      
      <view class="section">
        <text class="section-title">登录测试</text>
        <input class="input" v-model="phone" placeholder="手机号" />
        <input class="input" v-model="password" placeholder="密码" password />
        <button class="btn" @click="handleLogin">登录</button>
      </view>
      
      <view class="section">
        <text class="section-title">请求测试</text>
        <view class="btn-group">
          <button class="btn" @click="testUserInfo">获取用户信息</button>
          <button class="btn" @click="testDishPage">获取菜品分页</button>
          <button class="btn" @click="testCategories">获取菜品分类</button>
        </view>
      </view>
      
      <view class="section">
        <text class="section-title">Token操作</text>
        <view class="btn-group">
          <button class="btn" @click="checkToken">检查Token</button>
          <button class="btn" @click="setTestToken">设置测试Token</button>
          <button class="btn" @click="clearToken">清除Token</button>
        </view>
      </view>
      
      <view class="section">
        <text class="section-title">请求结果</text>
        <view class="result-box">
          <text class="result-text">{{ result }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { enhancedLogin } from './login_debug.js';
import { checkToken, setTestToken, testRequest } from './token_checker.js';

export default {
  data() {
    return {
      token: '',
      phone: '17344402975',
      password: '20050311',
      result: '请点击按钮测试...'
    }
  },
  onLoad() {
    this.refreshToken();
  },
  methods: {
    refreshToken() {
      this.token = uni.getStorageSync('token') || '';
    },
    
    handleLogin() {
      this.result = '登录中...';
      
      enhancedLogin(this.phone, this.password)
        .then(res => {
          this.refreshToken();
          this.result = '登录响应: ' + JSON.stringify(res.data);
        })
        .catch(err => {
          this.result = '登录失败: ' + JSON.stringify(err);
        });
    },
    
    testUserInfo() {
      this.result = '请求用户信息中...';
      
      testRequest('http://localhost:8080/api/v1/user/info')
        .then(res => {
          this.result = '用户信息响应: ' + JSON.stringify(res.data);
        })
        .catch(err => {
          this.result = '请求失败: ' + JSON.stringify(err);
        });
    },
    
    testDishPage() {
      this.result = '请求菜品分页中...';
      
      testRequest('http://localhost:8080/api/v1/dishes/page')
        .then(res => {
          this.result = '菜品分页响应: ' + JSON.stringify(res.data);
        })
        .catch(err => {
          this.result = '请求失败: ' + JSON.stringify(err);
        });
    },
    
    testCategories() {
      this.result = '请求菜品分类中...';
      
      testRequest('http://localhost:8080/api/v1/dishes/categories')
        .then(res => {
          this.result = '菜品分类响应: ' + JSON.stringify(res.data);
        })
        .catch(err => {
          this.result = '请求失败: ' + JSON.stringify(err);
        });
    },
    
    checkToken() {
      const token = checkToken();
      this.refreshToken();
      this.result = token ? '有效Token' : '无效Token';
    },
    
    setTestToken() {
      setTestToken();
      this.refreshToken();
      this.result = '已设置测试Token';
    },
    
    clearToken() {
      uni.removeStorageSync('token');
      this.refreshToken();
      this.result = '已清除Token';
    }
  }
}
</script>

<style>
.container {
  display: flex;
  flex-direction: column;
  padding: 20rpx;
  background-color: #f8f8f8;
  min-height: 100vh;
}

.header {
  background-color: #ffb74d;
  padding: 30rpx;
  border-radius: 10rpx;
  margin-bottom: 20rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.content {
  flex: 1;
}

.section {
  background-color: #fff;
  padding: 20rpx;
  border-radius: 10rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
  display: block;
}

.token-text {
  font-size: 26rpx;
  color: #666;
  word-break: break-all;
}

.input {
  height: 80rpx;
  border: 1px solid #ddd;
  border-radius: 8rpx;
  margin-bottom: 20rpx;
  padding: 0 20rpx;
  font-size: 28rpx;
}

.btn {
  background-color: #ff9800;
  color: #fff;
  margin-bottom: 20rpx;
}

.btn-group {
  display: flex;
  flex-direction: column;
}

.result-box {
  border: 1px solid #eee;
  padding: 20rpx;
  border-radius: 8rpx;
  background-color: #f9f9f9;
  max-height: 400rpx;
  overflow-y: auto;
}

.result-text {
  font-size: 24rpx;
  color: #333;
  word-break: break-all;
}
</style> 