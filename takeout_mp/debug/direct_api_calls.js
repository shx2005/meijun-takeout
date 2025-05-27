现在可以直接修改API调用

// 直接API调用示例
// 使用这些函数代替api/index.js中的函数

// 获取菜品分页
export function dishListApi(params) {
  // 从本地存储获取token
  const token = uni.getStorageSync('token');
  console.log('获取菜品分页时的token:', token);
  
  // 用于调试的请求头
  const headers = {
    'Authorization': `Bearer ${token}`,
    'Accept': 'application/json',
    'userType': '3',
    'Content-Type': 'application/json'
  };
  
  // 打印完整请求信息
  console.log('请求URL:', 'http://localhost:8080/api/v1/dishes/page');
  console.log('请求方法:', 'GET');
  console.log('请求头:', JSON.stringify(headers));
  
  // 使用uni.request直接发送请求
  return new Promise((resolve, reject) => {
    uni.request({
      url: 'http://localhost:8080/api/v1/dishes/page',
      method: 'GET',
      header: headers,
      data: params || {},
      success: (res) => {
        console.log('菜品分页响应状态码:', res.statusCode);
        
        if (res.statusCode === 200) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          console.error('认证失败，检查token是否有效');
          
          // 提示用户登录
          uni.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none',
            duration: 2000
          });
          
          // 延迟跳转到登录页
          setTimeout(() => {
            uni.navigateTo({
              url: '/pages/my/my'
            });
          }, 2000);
          
          reject(res);
        } else {
          console.error('获取菜品分页失败:', res.statusCode, res.data);
          reject(res);
        }
      },
      fail: (err) => {
        console.error('请求菜品分页接口失败:', err);
        reject(err);
      }
    });
  });
}

// 获取菜品分类
export function categoryListApi(params) {
  // 从本地存储获取token
  const token = uni.getStorageSync('token');
  console.log('获取菜品分类时的token:', token);
  
  // 用于调试的请求头
  const headers = {
    'Authorization': `Bearer ${token}`,
    'Accept': 'application/json',
    'userType': '3',
    'Content-Type': 'application/json'
  };
  
  // 打印完整请求信息
  console.log('请求URL:', 'http://localhost:8080/api/v1/dishes/categories');
  console.log('请求方法:', 'GET');
  console.log('请求头:', JSON.stringify(headers));
  
  // 使用uni.request直接发送请求
  return new Promise((resolve, reject) => {
    uni.request({
      url: 'http://localhost:8080/api/v1/dishes/categories',
      method: 'GET',
      header: headers,
      data: params || {},
      success: (res) => {
        console.log('菜品分类响应状态码:', res.statusCode);
        
        if (res.statusCode === 200) {
          resolve(res.data);
        } else if (res.statusCode === 401) {
          console.error('认证失败，检查token是否有效');
          
          // 提示用户登录
          uni.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none',
            duration: 2000
          });
          
          // 延迟跳转到登录页
          setTimeout(() => {
            uni.navigateTo({
              url: '/pages/my/my'
            });
          }, 2000);
          
          reject(res);
        } else {
          console.error('获取菜品分类失败:', res.statusCode, res.data);
          reject(res);
        }
      },
      fail: (err) => {
        console.error('请求菜品分类接口失败:', err);
        reject(err);
      }
    });
  });
}
