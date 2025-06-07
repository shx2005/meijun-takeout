// ajax.js

// 引入 uni-ajax 模块
import ajax from '@/uni_modules/u-ajax'

// 创建请求实例
const instance = ajax.create({
  // 初始配置
  baseURL: process.env.VUE_APP_BASE_URL || 'http://localhost:8080/' // 移除重复的/api/前缀
})

// 创建请求头的辅助函数
export const createHeaders = (token) => {
  return {
    'customerToken': token,
    'Accept': 'application/json',
    'userType': '3',
    'Content-Type': 'application/json'
  }
}

// 添加请求拦截器
instance.interceptors.request.use(
  config => {
    // 从本地存储获取token
    const token = uni.getStorageSync('token');
    console.log('请求拦截器中的token:', token);
    
    // 设置请求头
    config.header = {
      'customerToken': token,
      'Accept': 'application/json',
      'userType': '3',
      'Content-Type': 'application/json'
    };
    
    // 打印完整的请求信息，用于调试
    console.log('请求配置:', {
      url: config.url,
      method: config.method,
      headers: config.header,
      data: config.data
    });
    
    return config;
  },
  error => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
  }
);

// 添加响应拦截器
instance.interceptors.response.use(
  response => {
    // 对响应数据做些什么
    const res = response.data
    
    // 打印响应数据，帮助调试
    console.log('响应数据:', response.config.url, res)
    
    // 检查HTTP状态码
    if (response.statusCode >= 400) {
      console.error(`HTTP错误: ${response.statusCode}`, response)
      let errMsg = '请求失败'
      
      try {
        // 尝试解析错误响应
        if (typeof res === 'string' && res.includes('<Map>')) {
          // 处理XML格式的错误响应
          const errorMatch = res.match(/<e>(.*?)<\/error>/)
          if (errorMatch) {
            errMsg = errorMatch[1]
          }
        } else if (res && res.msg) {
          errMsg = res.msg
        }
      } catch (e) {
        console.error('解析错误响应失败:', e)
      }
      
      // 对401特殊处理
      if (response.statusCode === 401) {
        // 清除token
        uni.removeStorageSync('token')
        uni.removeStorageSync('originalToken')
        // 跳转到登录页
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/my/my'
          })
        }, 1000)
        return Promise.reject(new Error('登录已过期，请重新登录'))
      }
      
      return Promise.reject(new Error(errMsg))
    }
    
    // 判断返回的状态码
    if (res.code !== 0 && res.code !== 1 && res.code !== 200) {  // 接受多种成功状态码
      // 错误处理
      const errMsg = res.msg || '请求失败'
      
      // 未登录或token过期
      if (res.code === 401) {
        // 清除token
        uni.removeStorageSync('token')
        uni.removeStorageSync('originalToken')
        // 跳转到登录页
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/my/my'
          })
        }, 1000)
      }
      
      return Promise.reject(new Error(errMsg))
    }
    
    // 处理登录接口的特殊情况
    if (response.config && response.config.url && response.config.url.includes('auth/login')) {
      // 登录接口直接返回完整响应
      return res
    }
    
    // 其他接口返回data字段或整个响应
    return res.data || res
  },
  error => {
    if (error.response) {
      console.error('错误响应:', error.response)
      let errMsg = `服务器错误 (${error.response.statusCode})`
      
      try {
        // 尝试解析错误响应
        const res = error.response.data
        if (typeof res === 'string' && res.includes('<Map>')) {
          // 处理XML格式的错误响应
          const errorMatch = res.match(/<e>(.*?)<\/error>/)
          if (errorMatch) {
            errMsg = errorMatch[1]
          }
        } else if (res && res.msg) {
          errMsg = res.msg
        }
      } catch (e) {
        console.error('解析错误响应失败:', e)
      }
      
      uni.$showMsg(errMsg)
    } else if (error.errMsg) {
      // uni-app 错误格式
      console.error('错误信息:', error.errMsg)
      uni.$showMsg(error.errMsg || '网络请求失败')
    } else {
      console.error('未知错误:', error)
      uni.$showMsg('网络请求失败')
    }
    
    return Promise.reject(error)
  }
)

// 导出 create 创建后的实例
export default instance