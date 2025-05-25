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
	return uni.$ajax.put({
		url: 'v1/user/info',
		data: data
	})
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
					if (res.data && res.data.data && res.data.data.token) {
						const token = res.data.data.token;
						console.log('保存token成功:', token);
						uni.setStorageSync('token', token);
					  } else if (res.data && res.data.token) {
						const token = res.data.token;
						console.log('保存token成功:', token);
						uni.setStorageSync('token', token);
					  }
					// 成功，优先尝试JSON解析
					if (typeof res.data === 'object') {
						// 如果已经是对象，直接返回
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
							const nameMatch = res.data.match(/<name>(.*?)<\/name>/);
							
							if (tokenMatch && tokenMatch[1]) {
								const result = {
									statusCode: res.statusCode,
									data: {
										token: tokenMatch[1],
										id: idMatch && idMatch[1] ? idMatch[1] : null,
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