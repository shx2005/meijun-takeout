// ajax.js

// 引入 uni-ajax 模块
import ajax from '@/uni_modules/u-ajax'

// 创建请求实例
const instance = ajax.create({
  // 初始配置
  baseURL: process.env.VUE_APP_BASE_URL || 'http://localhost:8080/api/' // 还原/api/前缀
})
