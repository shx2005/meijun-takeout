// ajax.js

// 引入 uni-ajax 模块
import ajax from '@/uni_modules/u-ajax'

// 创建请求实例
const instance = ajax.create({
  // 初始配置
  baseURL: process.env.VUE_APP_BASE_URL || 'http://localhost:8080/api/' // 还原/api/前缀
})

// 添加请求拦截器
instance.interceptors.request.use(
  config => {
    // 在发送请求前做些什么
    const token = uni.getStorageSync('originalToken') || uni.getStorageSync('token')
    console.log('发送请求时的token:', token)
    
    if (token) {
      // 直接使用原始token，不再进行JSON解析和Base64编码
      // 根据后端要求设置token请求头
      config.header['token'] = token
      
      // 移除不需要的头部
      delete config.header['tokenName']
      delete config.header['Authorization']
    }
    
    // 添加用户类型标识
    config.header['userType'] = '3' // 3 代表 CUSTOMER
    // 明确指定请求和响应都用 JSON - 强制要求服务器返回JSON
    config.header['Accept'] = 'application/json'
    config.header['X-Requested-With'] = 'XMLHttpRequest' // 明确表明这是AJAX请求
    
    // 设置Content-Type头
    if (config.method && (config.method.toUpperCase() === 'POST' || config.method.toUpperCase() === 'PUT')) {
      config.header['Content-Type'] = 'application/json;charset=UTF-8'
    } else {
      config.header['Content-Type'] = 'application/json'
    }
    
    // 打印完整请求信息，帮助调试
    console.log('完整请求URL:', config.url);
    console.log('请求方法:', config.method);
    console.log('请求头:', JSON.stringify(config.header));
    
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

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
    // 对响应错误做些什么
    console.error('请求错误:', error)
    
    // 检查是否有响应数据
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
