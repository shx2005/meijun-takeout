// 修复认证问题的测试代码

// 固定一个已知可用的token格式
export function simulateLogin() {
  const testToken = 'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MywidXNlcm5hbWUiOiIxNzM0NDQwMjk3NSIsImlkZW50aXR5IjoiQ1VTVE9NRVIiLCJleHAiOjE3NDgyMzYwNjB9.0P1A_qRBXwi2xEHPKgwKP_uZbp1HXHcQbgmYda4g3J4';
  
  // 设置到本地存储
  uni.setStorageSync('token', testToken);
  console.log('模拟登录成功，设置测试token');
  
  // 返回token用于测试
  return testToken;
}

// 使用固定token直接请求菜品分页
export function directDishPageRequest() {
  const token = simulateLogin();
  
  return new Promise((resolve, reject) => {
    uni.request({
      url: 'http://localhost:8080/api/v1/dishes/page',
      method: 'GET',
      header: {
        'tokenName': token,
        'Accept': 'application/json',
        'userType': '3'
      },
      success: (res) => {
        console.log('菜品分页响应:', res);
        resolve(res);
      },
      fail: (err) => {
        console.error('请求失败:', err);
        reject(err);
      }
    });
  });
}

// 将接收到的token解析为可读格式
export function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('解析JWT失败:', error);
    return null;
  }
} 