import regeneratorRuntime, { async } from '../lib/runtime/runtime';
import { loginApi, registerApi, logoutApi as authLogoutApi, getUserInfoApi } from './index';

// 登录
export const login = (data) => {
	return loginApi(data);
}

// 注册
export const register = (data) => {
	return registerApi(data);
}

// 登出
export const logoutApi = () => {
	return authLogoutApi();
}

// 获取用户信息
export const getUserInfo = () => {
	return getUserInfoApi();
}

// 导出获取用户信息API以供直接使用
export { getUserInfoApi };

// 更新用户信息
export const updateUserInfoApi = (data) => {
	// 从本地存储获取token
	const token = uni.getStorageSync('token');
	console.log('更新用户信息时的token:', token);
	console.log('更新用户信息的数据:', JSON.stringify(data));
	
	// 使用Promise包装请求
	return new Promise((resolve, reject) => {
		// 直接使用uni.request发送请求
		uni.request({
			url: 'http://localhost:8080/api/v1/user/update',
			method: 'POST', // 尝试POST方法
			header: {
				'customerToken': token,
				'Accept': 'application/json',
				'userType': '3',
				'Content-Type': 'application/json'
			},
			data: data,
			success: (res) => {
				console.log('更新用户信息响应:', res);
				if (res.statusCode === 200 || res.statusCode === 405) {
					// 如果返回405，说明需要使用GET方法，重新尝试
					if (res.statusCode === 405) {
						console.log('POST方法不允许，尝试使用GET方法');
						
						// 构建查询字符串
						const queryParams = Object.keys(data)
							.map(key => `${encodeURIComponent(key)}=${encodeURIComponent(data[key])}`)
							.join('&');
						
						// 使用GET方法重试
						uni.request({
							url: `http://localhost:8080/api/v1/user/update?${queryParams}`,
							method: 'GET',
							header: {
								'customerToken': token,
								'Accept': 'application/json',
								'userType': '3',
								'Content-Type': 'application/json'
							},
							success: (getRes) => {
								console.log('GET方法更新用户信息响应:', getRes);
								if (getRes.statusCode === 200) {
									resolve(getRes.data);
								} else {
									console.error('GET方法更新用户信息失败:', getRes.statusCode, getRes.data);
									reject(getRes);
								}
							},
							fail: (err) => {
								console.error('GET方法更新用户信息请求失败:', err);
								reject(err);
							}
						});
					} else {
						resolve(res.data);
					}
				} else {
					console.error('更新用户信息失败:', res.statusCode, res.data);
					reject(res);
				}
			},
			fail: (err) => {
				console.error('更新用户信息请求失败:', err);
				reject(err);
			}
		});
	});
}

// 手机密码登录
export const phoneLoginApi = (data) => {
	return new Promise((resolve, reject) => {
		// 创建请求对象
		const requestBody = {
			username: data.phone,
			password: data.password,
			identity: "CUSTOMER" // 使用字符串形式的枚举值
		};
		
		console.log('登录请求参数:', JSON.stringify(requestBody));
		
		uni.request({
			url: 'http://localhost:8080/api/v1/auth/login',
			method: 'POST',
			header: {
				'Content-Type': 'application/json', 
				'Accept': 'application/json',
				'userType': '3'
			},
			data: requestBody,
			success: (res) => {
				console.log('登录响应:', res);
				
				// 检查状态码
				if (res.statusCode === 200) {
					// 处理登录响应
					let token = null;
					let userId = null;
					let username = null;
					
					// 从响应中解析数据
					if (res.data && res.data.data && res.data.data.token) {
						token = res.data.data.token;
						userId = res.data.data.id;
						username = res.data.data.username;
					} else if (res.data && res.data.token) {
						token = res.data.token;
						userId = res.data.id;
						username = res.data.username;
					}
					
					if (token) {
						// 将原始token直接存储，不再进行Base64编码和结构化处理
						console.log('后端返回的原始token:', token);
						uni.setStorageSync('originalToken', token);
						uni.setStorageSync('token', token);
						
						// 保存用户ID，确保为数字类型
						if (userId) {
							// 确保ID是数字类型
							if (typeof userId === 'string') {
								try {
									userId = parseInt(userId);
								} catch (e) {
									console.error('解析用户ID失败:', e);
								}
							}
							uni.setStorageSync('userId', userId);
						}
					}
					
					// 返回响应对象
					if (typeof res.data === 'object') {
						resolve({ 
							statusCode: res.statusCode, 
							data: res.data 
						});
					} else if (typeof res.data === 'string' && res.data.includes('<r>')) {
						try {
							// 简单解析XML获取token
							const tokenMatch = res.data.match(/<token>(.*?)<\/token>/);
							const idMatch = res.data.match(/<id>(.*?)<\/id>/);
							const uuidMatch = res.data.match(/<uuid>(.*?)<\/uuid>/);
							const usernameMatch = res.data.match(/<username>(.*?)<\/username>/);
							const nameMatch = res.data.match(/<n>(.*?)<\/name>/);
							
							if (tokenMatch && tokenMatch[1]) {
								const result = {
									statusCode: res.statusCode,
									data: {
										token: tokenMatch[1],
										id: idMatch && idMatch[1] ? parseInt(idMatch[1]) : null,
										uuid: uuidMatch && uuidMatch[1] ? uuidMatch[1] : null,
										username: usernameMatch && usernameMatch[1] ? usernameMatch[1] : null,
										name: nameMatch && nameMatch[1] ? nameMatch[1] : null
									}
								};
								console.log('从XML中提取的数据封装后:', result);
								resolve(result);
							} else {
								resolve(res);
							}
						} catch (e) {
							console.error('解析XML失败:', e);
							resolve(res);
						}
					} else {
						resolve({ statusCode: res.statusCode, data: res.data }); 
					}
				} else {
					// 输出更详细的错误信息以便调试
					if (res.data && typeof res.data === 'string' && res.data.includes('Internal Server Error')) {
						console.error('服务器内部错误，请检查后端日志');
					}
					
					// 500错误可能是后端问题，建议检查服务器日志
					if (res.statusCode === 500) {
						console.error('服务器内部错误(500)，详情:', res.data);
					}
					
					resolve(res);
				}
			},
			fail: (err) => {
				console.error('登录请求失败:', err);
				resolve({ 
					statusCode: 0,
					data: null, 
					errMsg: (err && err.errMsg) ? err.errMsg : '网络请求失败' 
				});
			}
		});
	});
}

// 发送验证码
export const sendValidateCodeApi = (data) => {
	return uni.$ajax.get({
		url: 'v1/auth/send-code',
		data: data
	})
}

// 刷新Token
export const refreshTokenApi = () => {
	return uni.$ajax.post({
		url: 'api/v1/auth/refresh-token'
	})
} 