/**
 * 美颂外卖 - Token适配器
 * 
 * 此文件为前端适配后端token验证机制的临时解决方案。
 * 由于前端与后端使用不同结构的JWT token，此适配器将在前端发送请求前
 * 进行token格式转换和请求头适配。
 */

// 示例使用方法（基于axios拦截器）:
// 在前端项目的请求拦截器中添加此代码

const tokenAdapter = {
  /**
   * 设置默认token键名和请求头
   */
  setup(axiosInstance) {
    // 添加请求拦截器
    axiosInstance.interceptors.request.use(
      config => {
        // 从本地存储获取token
        const token = localStorage.getItem('token');
        
        if (token) {
          // 添加正确的请求头
          config.headers['token'] = token;
          
          // 注意：根据后端实际要求，可能需要以下格式之一
          // config.headers['Authorization'] = `Bearer ${token}`;
          // config.headers['Token'] = token;
          // config.headers['tokenName'] = token;
        }
        return config;
      },
      error => {
        return Promise.reject(error);
      }
    );
    
    return axiosInstance;
  },
  
  /**
   * 验证token是否有效
   * @param {string} token - JWT token
   * @returns {boolean} - 是否有效
   */
  isValidToken(token) {
    if (!token) return false;
    
    try {
      // 简单验证token格式（分成三段，用.分隔）
      const parts = token.split('.');
      if (parts.length !== 3) return false;
      
      // 解析payload（第二部分）
      const payload = JSON.parse(atob(parts[1]));
      
      // 检查是否过期
      if (payload.exp && payload.exp < Date.now() / 1000) {
        return false;
      }
      
      // 验证必要字段是否存在
      return !!payload.customerID;
      
    } catch (error) {
      console.error('Token验证错误:', error);
      return false;
    }
  },
  
  /**
   * 从登录响应中提取token并保存
   * @param {Object} loginResponse - 登录API响应对象
   */
  saveTokenFromResponse(loginResponse) {
    try {
      if (loginResponse && loginResponse.data && loginResponse.data.token) {
        const token = loginResponse.data.token;
        localStorage.setItem('token', token);
        
        // 可以解析token保存用户信息
        const payload = this.decodeToken(token);
        if (payload && payload.customerID) {
          localStorage.setItem('userId', payload.customerID);
        }
        
        return true;
      }
    } catch (error) {
      console.error('保存token失败:', error);
    }
    return false;
  },
  
  /**
   * 解码JWT token
   * @param {string} token - JWT token
   */
  decodeToken(token) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Token解码失败:', error);
      return null;
    }
  }
};

export default tokenAdapter; 