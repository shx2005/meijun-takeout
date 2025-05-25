import regeneratorRuntime, { async } from '../lib/runtime/runtime';
import request from '../utils/request'

// 购物车相关接口
/**
 * 添加商品到购物车
 * @param {Object} data 商品数据，包含 itemId、quantity 等信息
 * @returns {Promise} 返回添加结果
 */
export const addCartApi = (data) => {
	// 更新本地存储购物车
	try {
		// 获取原有购物车数据
		let cartItems = [];
		const cartData = uni.getStorageSync('cartItems');
		if (cartData) {
			cartItems = JSON.parse(cartData);
		}
		
		// 查找是否已存在该商品
		const index = cartItems.findIndex(item => item.id === data.itemId);
		
		if (index > -1) {
			// 已存在则增加数量
			cartItems[index].number = cartItems[index].number + (data.quantity || 1);
		} else {
			// 不存在则添加新商品
			const dishInfo = getDishInfo(data.itemId);
			cartItems.push({
				id: data.itemId,
				name: dishInfo.name || '菜品',
				price: dishInfo.price || 0,
				image: dishInfo.image || '/static/images/default-food.png',
				number: data.quantity || 1,
				categoryId: dishInfo.categoryId
			});
		}
		
		// 保存到本地存储
		uni.setStorageSync('cartItems', JSON.stringify(cartItems));
		console.log('本地购物车更新成功:', cartItems);
	} catch (error) {
		console.error('更新本地购物车失败:', error);
	}
	
	// 尝试调用API - 使用直接发送请求的方式
	return new Promise((resolve) => {
		// 获取token
		const token = uni.getStorageSync('originalToken') || uni.getStorageSync('token');
		
		uni.request({
			url: process.env.VUE_APP_BASE_URL + 'v1/cart/add',
			method: 'POST',
			header: {
				'tokenName': token,
				'Accept': 'application/json',
				'userType': '3',
				'Content-Type': 'application/json',
				'X-Requested-With': 'XMLHttpRequest'
			},
			data: data,
			success: (res) => {
				console.log('添加购物车API响应:', res);
				if (res.statusCode >= 200 && res.statusCode < 300) {
					// 请求成功
					resolve({
						code: 0,
						data: null,
						msg: '添加成功'
					});
				} else {
					console.error('添加购物车API请求失败:', res);
					// 返回成功，因为本地购物车已更新
					resolve({
						code: 0,
						data: null,
						msg: '添加成功(仅本地)'
					});
				}
			},
			fail: (err) => {
				console.error('添加购物车API请求错误:', err);
				// 返回成功，因为本地购物车已更新
				resolve({
					code: 0,
					data: null,
					msg: '添加成功(仅本地)'
				});
			}
		});
	});
}

// 辅助函数：根据商品ID获取商品信息
function getDishInfo(itemId) {
	// 尝试从本地缓存获取所有菜品数据
	try {
		const allDishes = uni.getStorageSync('allDishes');
		if (allDishes) {
			const dishes = JSON.parse(allDishes);
			const dish = dishes.find(item => item.id === itemId);
			if (dish) {
				return dish;
			}
		}
	} catch (error) {
		console.error('从本地存储获取菜品信息失败:', error);
	}
	
	// 找不到指定菜品，返回默认值
	return {
		name: '菜品',
		price: 0,
		image: '/static/images/default-food.png'
	};
}

/**
 * 更新购物车商品信息
 * @param {Object} data 商品数据，包含 id、quantity 等信息
 * @returns {Promise} 返回更新结果
 */
export const updateCartApi = (data) => {
	// 先更新本地存储
	try {
		let cartItems = [];
		const cartData = uni.getStorageSync('cartItems');
		if (cartData) {
			cartItems = JSON.parse(cartData);
		}
		
		// 查找要更新的商品
		const index = cartItems.findIndex(item => item.id === data.id);
		
		if (index > -1) {
			if (data.quantity <= 0) {
				// 数量为0，从购物车中移除该商品
				cartItems.splice(index, 1);
			} else {
				// 更新商品数量
				cartItems[index].number = data.quantity;
			}
			
			// 保存更新后的购物车
			uni.setStorageSync('cartItems', JSON.stringify(cartItems));
			console.log('本地购物车更新成功:', cartItems);
		}
	} catch (error) {
		console.error('更新本地购物车失败:', error);
	}
	
	// 使用uni.request直接发送请求
	return new Promise((resolve) => {
		// 获取token
		const token = uni.getStorageSync('originalToken') || uni.getStorageSync('token');
		
		uni.request({
			url: process.env.VUE_APP_BASE_URL + 'v1/cart/update',
			method: 'PUT',
			header: {
				'tokenName': token,
				'Accept': 'application/json',
				'userType': '3',
				'Content-Type': 'application/json',
				'X-Requested-With': 'XMLHttpRequest'
			},
			data: data,
			success: (res) => {
				console.log('更新购物车API响应:', res);
				if (res.statusCode >= 200 && res.statusCode < 300) {
					// 请求成功
					resolve({
						code: 0,
						data: null,
						msg: '更新成功'
					});
				} else {
					console.error('更新购物车API请求失败:', res);
					// 返回成功，因为本地购物车已更新
					resolve({
						code: 0,
						data: null,
						msg: '更新成功(仅本地)'
					});
				}
			},
			fail: (err) => {
				console.error('更新购物车API请求错误:', err);
				// 返回成功，因为本地购物车已更新
				resolve({
					code: 0,
					data: null,
					msg: '更新成功(仅本地)'
				});
			}
		});
	});
}

/**
 * 清空购物车
 * @returns {Promise} 返回清空结果
 */
export const clearCartApi = () => {
	// 先清空本地存储
	try {
		uni.removeStorageSync('cartItems');
		console.log('本地购物车已清空');
	} catch (error) {
		console.error('清空本地购物车失败:', error);
	}
	
	// 使用uni.request直接发送请求
	return new Promise((resolve) => {
		// 获取token
		const token = uni.getStorageSync('token');
		
		uni.request({
			url: process.env.VUE_APP_BASE_URL + 'v1/cart/delete',
			method: 'DELETE',
			header: {
				'tokenName': token,
				'Accept': 'application/json',
				'userType': '3',
				'Content-Type': 'application/json',
				'X-Requested-With': 'XMLHttpRequest'
			},
			success: (res) => {
				console.log('清空购物车API响应:', res);
				if (res.statusCode >= 200 && res.statusCode < 300) {
					// 请求成功
					resolve({
						code: 0,
						data: null,
						msg: '购物车已清空'
					});
				} else {
					console.error('清空购物车API请求失败:', res);
					// 返回成功，因为本地购物车已清空
					resolve({
						code: 0,
						data: null,
						msg: '购物车已清空(仅本地)'
					});
				}
			},
			fail: (err) => {
				console.error('清空购物车API请求错误:', err);
				// 返回成功，因为本地购物车已清空
				resolve({
					code: 0,
					data: null,
					msg: '购物车已清空(仅本地)'
				});
			}
		});
	});
}

/**
 * 获取购物车列表
 * @param {Object} params 查询参数
 * @returns {Promise} 返回购物车数据
 */
export const cartListApi = (params) => {
	// 从本地存储获取token
	const token = uni.getStorageSync('token');
	console.log('获取购物车时的token:', token);
	
	// 用于请求的headers
	const headers = {
		'tokenName': token,
		'Accept': 'application/json',
		'userType': '3',
		'Content-Type': 'application/json'
	};
	
	// 使用uni.request直接发送请求
	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://localhost:8080/api/v1/cart',
			method: 'GET',
			header: headers,
			data: params || {},
			success: (res) => {
				console.log('购物车响应状态码:', res.statusCode);
				
				if (res.statusCode === 200) {
					// 保存到本地存储以便离线使用
					if (res.data && res.data.data) {
						uni.setStorageSync('cartItems', JSON.stringify(res.data.data));
					}
					resolve(res.data);
				} else if (res.statusCode === 401) {
					console.error('认证失败，检查token是否有效');
					reject(res);
				} else {
					console.error('获取购物车失败:', res.statusCode, res.data);
					reject(res);
				}
			},
			fail: (err) => {
				console.error('请求购物车接口失败:', err);
				reject(err);
			}
		});
	});
}

// 菜品相关接口
export function dishListApi(params) {
	// 从本地存储获取token
	const token = uni.getStorageSync('token');
	console.log('获取菜品分页时的token:', token);
	
	// 用于请求的headers
	const headers = {
		'tokenName': token,
		'Accept': 'application/json',
		'userType': '3',
		'Content-Type': 'application/json'
	};
	
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
	
	// 用于请求的headers
	const headers = {
		'tokenName': token,
		'Accept': 'application/json',
		'userType': '3',
		'Content-Type': 'application/json'
	};
	
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

// 获取推荐菜品
export const recommendationsApi = () => {
	// 从本地存储获取token
	const token = uni.getStorageSync('token');
	console.log('获取推荐菜品时的token:', token);
	
	// 用于请求的headers
	const headers = {
		'tokenName': token,
		'Accept': 'application/json',
		'userType': '3',
		'Content-Type': 'application/json'
	};
	
	// 使用uni.request直接发送请求
	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://localhost:8080/api/v1/dishes/recommendations',
			method: 'GET',
			header: headers,
			success: (res) => {
				console.log('推荐菜品响应状态码:', res.statusCode);
				
				if (res.statusCode === 200) {
					resolve(res.data);
				} else if (res.statusCode === 401) {
					console.error('认证失败，检查token是否有效');
					reject(res);
				} else {
					console.error('获取推荐菜品失败:', res.statusCode, res.data);
					reject(res);
				}
			},
			fail: (err) => {
				console.error('请求推荐菜品接口失败:', err);
				reject(err);
			}
		});
	});
}

// 简单的Base64编码函数
function base64Encode(str) {
	// 创建一个对字符串进行编码的字符表
	const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
	let output = '';
	
	// 处理UTF-8编码
	str = encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, 
		function(match, p1) {
			return String.fromCharCode('0x' + p1);
	});
	
	let i = 0;
	while (i < str.length) {
		let chr1 = str.charCodeAt(i++);
		let chr2 = i < str.length ? str.charCodeAt(i++) : 0;
		let chr3 = i < str.length ? str.charCodeAt(i++) : 0;
		
		let enc1 = chr1 >> 2;
		let enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
		let enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
		let enc4 = chr3 & 63;
		
		if (isNaN(chr2)) {
			enc3 = enc4 = 64;
		} else if (isNaN(chr3)) {
			enc4 = 64;
		}
		
		output += chars.charAt(enc1) + chars.charAt(enc2) + chars.charAt(enc3) + chars.charAt(enc4);
	}
	
	return output;
}

// 用户相关接口
export const loginApi = (data) => {
	// 转换输入参数的格式
	const requestData = {
		username: data.phone, // 将phone作为username发送
		password: data.password,
		identity: 'CUSTOMER' // 添加身份标识
	};
	
	return new Promise((resolve, reject) => {
		// 使用uni.request直接发送请求
		uni.request({
			url: 'http://localhost:8080/api/v1/auth/login',
			method: 'POST',
			header: {
				'Accept': 'application/json',
				'Content-Type': 'application/json',
				'userType': '3'
			},
			data: requestData,
			success: (res) => {
				console.log('登录API响应:', res);
				if (res.statusCode === 200) {
					// 请求成功，处理响应
					const responseData = res.data;
					
					// 获取token
					const token = responseData.token || (responseData.data && responseData.data.token);
					
					if (token) {
						// 保存token到本地存储
						uni.setStorageSync('token', token);
						
						// 获取用户ID和用户名
						const userId = responseData.id || (responseData.data && responseData.data.id) || '';
						const username = responseData.username || (responseData.data && responseData.data.username) || '';
						
						console.log('登录成功，保存的token:', token);
						
						// 登录成功后，预加载所有必需数据
						console.log('开始预加载数据...');
						
						// 1. 预加载菜品分类
						preloadCategoryData(token);
						
						// 2. 预加载菜品列表
						preloadDishData(token);
						
						// 3. 预加载推荐菜品
						preloadRecommendations(token);
						
						// 4. 预加载购物车数据
						preloadCartData(token);
						
						// 5. 预加载用户信息
						preloadUserInfo(token);
						
						// 6. 预加载订单历史
						preloadOrderHistory(token);
						
						// 返回成功响应
						resolve({
							code: 0,
							data: {
								token: token,
								userId: userId,
								username: username
							},
							msg: 'success'
						});
					} else {
						// 缺失token
						reject(new Error('登录响应缺少token信息'));
					}
				} else {
					// 登录失败
					console.error('登录失败:', res);
					reject(new Error('登录失败'));
				}
			},
			fail: (err) => {
				console.error('登录请求错误:', err);
				reject(err);
			}
		});
	});
}

// 预加载菜品分类数据
function preloadCategoryData(token) {
	// 使用获取到的token请求菜品分类数据
	uni.request({
		url: 'http://localhost:8080/api/v1/dishes/categories',
		method: 'GET',
		header: {
			'tokenName': token,
			'Accept': 'application/json',
			'userType': '3',
			'Content-Type': 'application/json'
		},
		success: (res) => {
			console.log('预加载菜品分类响应状态码:', res.statusCode);
			
			if (res.statusCode === 200) {
				console.log('成功预加载菜品分类数据');
				// 将分类数据保存到本地存储，添加时间戳以便后续判断数据是否需要更新
				const categoryData = {
					data: res.data.data || res.data,
					timestamp: Date.now()
				};
				uni.setStorageSync('categoryData', JSON.stringify(categoryData));
			}
		},
		fail: (err) => {
			console.error('预加载菜品分类失败:', err);
		}
	});
}

// 预加载菜品列表数据
function preloadDishData(token) {
	// 使用获取到的token请求菜品列表数据
	uni.request({
		url: 'http://localhost:8080/api/v1/dishes/page',
		method: 'GET',
		header: {
			'tokenName': token,
			'Accept': 'application/json',
			'userType': '3',
			'Content-Type': 'application/json'
		},
		success: (res) => {
			console.log('预加载菜品列表响应状态码:', res.statusCode);
			
			if (res.statusCode === 200) {
				console.log('成功预加载菜品列表数据');
				// 将菜品数据保存到本地存储，添加时间戳以便后续判断数据是否需要更新
				const dishData = {
					data: res.data.data || res.data,
					timestamp: Date.now()
				};
				uni.setStorageSync('dishData', JSON.stringify(dishData));
				
				// 同时保存到allDishes，便于购物车使用
				uni.setStorageSync('allDishes', JSON.stringify(res.data.data || res.data));
			}
		},
		fail: (err) => {
			console.error('预加载菜品列表失败:', err);
		}
	});
}

// 预加载推荐菜品数据
function preloadRecommendations(token) {
	// 使用获取到的token请求推荐菜品数据
	uni.request({
		url: 'http://localhost:8080/api/v1/dishes/recommendations',
		method: 'GET',
		header: {
			'tokenName': token,
			'Accept': 'application/json',
			'userType': '3',
			'Content-Type': 'application/json'
		},
		success: (res) => {
			console.log('预加载推荐菜品响应状态码:', res.statusCode);
			
			if (res.statusCode === 200) {
				console.log('成功预加载推荐菜品数据');
				// 将推荐菜品数据保存到本地存储
				const recommendationsData = {
					data: res.data.data || res.data,
					timestamp: Date.now()
				};
				uni.setStorageSync('recommendationsData', JSON.stringify(recommendationsData));
			}
		},
		fail: (err) => {
			console.error('预加载推荐菜品失败:', err);
		}
	});
}

// 预加载购物车数据
function preloadCartData(token) {
	// 使用获取到的token请求购物车数据
	uni.request({
		url: 'http://localhost:8080/api/v1/cart',
		method: 'GET',
		header: {
			'tokenName': token,
			'Accept': 'application/json',
			'userType': '3',
			'Content-Type': 'application/json'
		},
		success: (res) => {
			console.log('预加载购物车响应状态码:', res.statusCode);
			
			if (res.statusCode === 200) {
				console.log('成功预加载购物车数据');
				// 保存到本地存储
				if (res.data && res.data.data) {
					uni.setStorageSync('cartItems', JSON.stringify(res.data.data));
				}
			}
		},
		fail: (err) => {
			console.error('预加载购物车失败:', err);
		}
	});
}

// 预加载用户信息数据
function preloadUserInfo(token) {
	// 使用获取到的token请求用户信息
	uni.request({
		url: 'http://localhost:8080/api/v1/user/info',
		method: 'GET',
		header: {
			'tokenName': token,
			'Accept': 'application/json',
			'userType': '3',
			'Content-Type': 'application/json'
		},
		success: (res) => {
			console.log('预加载用户信息响应状态码:', res.statusCode);
			
			if (res.statusCode === 200) {
				console.log('成功预加载用户信息');
				// 保存用户信息到本地存储
				const userInfoData = {
					data: res.data.data || res.data,
					timestamp: Date.now()
				};
				uni.setStorageSync('userInfoData', JSON.stringify(userInfoData));
			}
		},
		fail: (err) => {
			console.error('预加载用户信息失败:', err);
		}
	});
}

// 预加载订单历史数据
function preloadOrderHistory(token) {
	// 使用获取到的token请求订单历史
	uni.request({
		url: 'http://localhost:8080/api/v1/orders/page',
		method: 'GET',
		header: {
			'tokenName': token,
			'Accept': 'application/json',
			'userType': '3',
			'Content-Type': 'application/json'
		},
		data: { page: 1, size: 10 }, // 获取最近的10条订单
		success: (res) => {
			console.log('预加载订单历史响应状态码:', res.statusCode);
			
			if (res.statusCode === 200) {
				console.log('成功预加载订单历史');
				// 保存订单历史到本地存储
				const orderHistoryData = {
					data: res.data.data || res.data,
					timestamp: Date.now()
				};
				uni.setStorageSync('orderHistoryData', JSON.stringify(orderHistoryData));
			}
		},
		fail: (err) => {
			console.error('预加载订单历史失败:', err);
		}
	});
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

// 获取用户信息
export const getUserInfoApi = () => {
	// 从本地存储获取token
	const token = uni.getStorageSync('token');
	console.log('获取用户信息时的token:', token);
	
	// 用于请求的headers
	const headers = {
		'tokenName': token,
		'Accept': 'application/json',
		'userType': '3',
		'Content-Type': 'application/json'
	};
	
	// 使用uni.request直接发送请求
	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://localhost:8080/api/v1/user/info',
			method: 'GET',
			header: headers,
			success: (res) => {
				console.log('用户信息响应状态码:', res.statusCode);
				
				if (res.statusCode === 200) {
					resolve(res.data);
				} else if (res.statusCode === 401) {
					console.error('认证失败，检查token是否有效');
					reject(res);
				} else {
					console.error('获取用户信息失败:', res.statusCode, res.data);
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

// 获取用户历史订单
export const getOrdersApi = (params) => {
	// 从本地存储获取token
	const token = uni.getStorageSync('token');
	console.log('获取用户订单时的token:', token);
	
	// 用于请求的headers
	const headers = {
		'tokenName': token,
		'Accept': 'application/json',
		'userType': '3',
		'Content-Type': 'application/json'
	};
	
	// 使用uni.request直接发送请求
	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://localhost:8080/api/v1/orders/page',
			method: 'GET',
			header: headers,
			data: params || {},
			success: (res) => {
				console.log('用户订单响应状态码:', res.statusCode);
				
				if (res.statusCode === 200) {
					resolve(res.data);
				} else if (res.statusCode === 401) {
					console.error('认证失败，检查token是否有效');
					reject(res);
				} else {
					console.error('获取用户订单失败:', res.statusCode, res.data);
					reject(res);
				}
			},
			fail: (err) => {
				console.error('请求用户订单接口失败:', err);
				reject(err);
			}
		});
	});
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
