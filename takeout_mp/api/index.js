import regeneratorRuntime, { async } from '../lib/runtime/runtime';
import request from '../utils/request'

// 购物车相关接口
export const addCartApi = (data) => {
	return request({
		url: '/v1/cart',
		method: 'post',
		data
	})
}

export const updateCartApi = (data) => {
	return request({
		url: '/v1/cart',
		method: 'put',
		data
	})
}

export const clearCartApi = () => {
	return request({
		url: '/v1/cart',
		method: 'delete'
	})
}

export const cartListApi = (params) => {
	return request({
		url: '/v1/cart',
		method: 'get',
		params
	})
}

// 菜品相关接口
export function dishListApi(params) {
	// 从本地存储获取token
	const token = uni.getStorageSync('token');
	console.log('获取菜品分页时的token:', token);
	
	// 用于调试的请求头
	const headers = {
	  'tokenName': token,
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
	  'tokenName': token,
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

// 用户相关接口
export const loginApi = (data) => {
	return uni.$ajax.post({
		url: "v1/auth/login",
		data: data
	})
}

export const registerApi = (data) => {
	return uni.$ajax.post({
		url: "register",
		data: data
	})
}

export const logoutApi = () => {
	return uni.$ajax.post({
		url: "v1/auth/logout"
	})
}

export const getUserInfoApi = () => {
	// 从本地存储获取token
	const token = uni.getStorageSync('token');
	console.log('获取用户信息时的token:', token);
	
	// 使用uni.request手动发送请求，确保包含正确的头
	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://localhost:8080/api/v1/user/info',
			method: 'GET',
			header: {
				'tokenName': token,
				'Accept': 'application/json',
				'userType': '3'
			},
			success: (res) => {
				console.log('用户信息接口响应:', res);
				if (res.statusCode === 200) {
					resolve(res.data);
				} else {
					reject(res);
				}
			},
			fail: (err) => {
				console.error('请求用户信息接口失败:', err);
				reject(err);
			}
		});
	});
}

// 订单相关接口
export const getOrdersApi = (data) => {
	return uni.$ajax.get({
		url: "v1/orders/page",
		data: { ...data }
	})
}

export const getOrderDetailApi = (orderId) => {
	return uni.$ajax.get({
		url: `v1/orders/${orderId}`,
	})
}

export const submitOrderCommentApi = (data) => {
	return uni.$ajax.post({
		url: "v1/orders/comment",
		data: data
	})
}

export const setMealDishDetailsApi = (id) => {
	return request({
		url: `/v1/setmeals/${id}`,
		method: 'get'
	})
}

export const setmealListApi = (params) => {
	return request({
		url: '/v1/setmeals',
		method: 'get',
		params
	})
}

export const recommendationsApi = () => {
	return uni.$ajax.get({
		url: 'v1/dishes/recommendations'
	})
}
