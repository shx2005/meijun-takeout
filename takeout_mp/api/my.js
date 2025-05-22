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
export const logout = () => {
	return authLogoutApi();
}

// 获取用户信息
export const getUserInfo = () => {
	return getUserInfoApi();
}

// 更新用户信息
export const updateUserInfoApi = (data) => {
	return uni.$ajax.put({
		url: 'api/v1/user/update',
		data: data
	})
}

// 手机密码登录
export const phoneLoginApi = (data) => {
	return new Promise((resolve, reject) => {
		uni.request({
			url: 'http://localhost:8080/api/v1/auth/login',
			method: 'POST',
			data: {
				username: data.phone,
				password: data.password,
				identity: 'CUSTOMER'
			},
			success: (res) => {
				console.log('登录响应:', res);
				
				// 检查状态码
				if (res.statusCode === 200) {
					// 成功，但需要从XML中提取数据
					if (typeof res.data === 'string' && res.data.includes('<Result>')) {
						try {
							// 简单解析XML获取token (这里用简单的字符串处理方式)
							const tokenMatch = res.data.match(/<token>(.*?)<\/token>/);
							const idMatch = res.data.match(/<id>(.*?)<\/id>/);
							
							if (tokenMatch && tokenMatch[1]) {
								const result = {
									statusCode: res.statusCode, // Preserve status code
									data: { // Structure as expected by my.vue
										token: tokenMatch[1],
										id: idMatch && idMatch[1] ? idMatch[1] : null
									}
								};
								console.log('从XML中提取的数据封装后:', result);
								resolve(result); // Resolve with the structured object
							} else {
								// uni.$showMsg('解析登录响应失败'); // Let my.vue handle UI messages
								resolve(res); // Resolve with original response if parsing fails
							}
						} catch (e) {
							console.error('解析XML失败:', e);
							// uni.$showMsg('解析登录响应失败'); // Let my.vue handle UI messages
							resolve(res); // Resolve with original response on error
						}
					} else {
						// 如果不是XML，直接返回，确保res.data is what my.vue expects or wrap it
						// Assuming my.vue expects { statusCode: ..., data: { token: ..., id: ... } } for success
						// If res.data is already in the correct { token: ..., id: ... } format, this is fine
						// Otherwise, it might need adjustment like the XML case
						resolve({ statusCode: res.statusCode, data: res.data }); 
					}
				} else {
					// 请求成功但服务器返回错误 (e.g., 4xx, 5xx)
					// uni.$showMsg(`登录失败: ${res.data.msg || '未知错误'}`); // Let my.vue handle UI messages
					resolve(res); // Resolve with the full response object (including error status and data)
				}
			},
			fail: (err) => {
				console.error('登录请求失败:', err);
				// uni.$showMsg('网络请求失败'); // Let my.vue handle UI messages
				// For network errors, we should still resolve with an object that my.vue can inspect
				// err for uni.request typically doesn't have statusCode or data, so construct a compatible error object
				resolve({ 
					statusCode: 0, // Or some other indicator of network error
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
		url: 'api/v1/auth/send-code',
		data: data
	})
}

// 刷新Token
export const refreshTokenApi = () => {
	return uni.$ajax.post({
		url: 'api/v1/auth/refresh-token'
	})
} 