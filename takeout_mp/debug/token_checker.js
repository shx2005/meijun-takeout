// token调试工具

export function checkToken() {
  console.log('======== TOKEN DEBUG ========');
  const token = uni.getStorageSync('token');
  console.log('当前token:', token);
  
  if (!token) {
    console.error('未找到token!');
    return false;
  }
  
  console.log('token长度:', token.length);
  console.log('=========================');
  
  return token;
}

// 手动设置测试token
export function setTestToken() {
  const testToken = 'eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MywidXNlcm5hbWUiOiIxNzM0NDQwMjk3NSIsImlkZW50aXR5IjoiQ1VTVE9NRVIiLCJleHAiOjE3NDgyMzYwNjB9.0P1A_qRBXwi2xEHPKgwKP_uZbp1HXHcQbgmYda4g3J4';
  uni.setStorageSync('token', testToken);
  console.log('已设置测试token:', testToken);
  return testToken;
}

// 发送测试请求
export function testRequest(url) {
  const token = uni.getStorageSync('token');
  console.log('发送测试请求使用token:', token);
  
  return new Promise((resolve, reject) => {
    uni.request({
      url: url,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${token}`,
        'Accept': 'application/json',
        'userType': '3'
      },
      success: (res) => {
        console.log('测试请求响应状态:', res.statusCode);
        console.log('测试请求响应数据:', res.data);
        resolve(res);
      },
      fail: (err) => {
        console.error('测试请求失败:', err);
        reject(err);
      }
    });
  });
} 