// 登录调试辅助器

// 增强的登录函数
export function enhancedLogin(phone, password) {
  console.log('====== 增强登录调试 ======');
  console.log('登录请求参数:', { phone, password });
  
  return new Promise((resolve, reject) => {
    uni.request({
      url: 'http://localhost:8080/api/v1/auth/login',
      method: 'POST',
      header: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'userType': '3'
      },
      data: {
        username: phone,
        password: password,
        identity: "CUSTOMER"
      },
      success: (res) => {
        console.log('登录响应状态码:', res.statusCode);
        console.log('登录响应头:', JSON.stringify(res.header));
        console.log('登录响应数据:', JSON.stringify(res.data));
        
        if (res.statusCode === 200) {
          let token = null;
          
          // 提取token
          if (res.data && res.data.data && res.data.data.token) {
            token = res.data.data.token;
          } else if (res.data && res.data.token) {
            token = res.data.token;
          }
          
          if (token) {
            console.log('提取到token:', token);
            
            // 保存token
            uni.setStorageSync('token', token);
            
            // 验证保存是否成功
            const savedToken = uni.getStorageSync('token');
            console.log('保存后检查token:', savedToken);
            console.log('token是否匹配:', token === savedToken);
            
            // 立即发送测试请求验证token有效性
            testTokenAuth(token);
          } else {
            console.error('无法从响应中提取token');
          }
        }
        
        resolve(res);
      },
      fail: (err) => {
        console.error('登录请求失败:', err);
        reject(err);
      }
    });
  });
}

// 测试token是否有效
function testTokenAuth(token) {
  console.log('测试token有效性...');
  
  uni.request({
    url: 'http://localhost:8080/api/v1/user/info',
    method: 'GET',
    header: {
      'Authorization': `Bearer ${token}`,
      'Accept': 'application/json',
      'userType': '3',
      'Content-Type': 'application/json'
    },
    success: (res) => {
      console.log('token测试结果状态码:', res.statusCode);
      if (res.statusCode === 200) {
        console.log('token有效，可以正常访问API');
      } else if (res.statusCode === 401) {
        console.error('token无效，服务器返回401未授权');
        console.error('响应数据:', res.data);
      } else {
        console.warn('token测试结果异常:', res.statusCode);
      }
    },
    fail: (err) => {
      console.error('token测试请求失败:', err);
    }
  });
} 