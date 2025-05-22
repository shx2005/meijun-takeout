// ajax.js

// 引入 uni-ajax 模块
import ajax from '@/uni_modules/u-ajax'

// 创建请求实例
const instance = ajax.create({
  // 初始配置
  baseURL: 'http://localhost:8080' // 默认后端接口地址
})

// 添加请求拦截器
instance.interceptors.request.use(
  config => {
    // 在发送请求前做些什么
    const token = uni.getStorageSync('token')
    if (token) {
      config.header['token'] = token
      config.header['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 添加响应拦截器
instance.interceptors.response.use(
  response => {
    // 对响应数据做些什么
    const res = response.data
    
    // 判断返回的状态码
    if (res.code !== 1) {
      // 错误处理
      uni.$showMsg(res.msg || '请求失败')
      
      // 未登录或token过期
      if (res.code === 401) {
        // 清除token
        uni.removeStorageSync('token')
        // 跳转到登录页
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/login/login'
          })
        }, 1500)
      }
      
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    
    return res.data
  },
  error => {
    // 对响应错误做些什么
    uni.$showMsg('网络请求失败')
    return Promise.reject(error)
  }
)

// 导出 create 创建后的实例
export default instance
