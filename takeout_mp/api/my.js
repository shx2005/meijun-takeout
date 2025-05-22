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
									token: tokenMatch[1],
									id: idMatch && idMatch[1] ? idMatch[1] : null
								};
								console.log('从XML中提取的数据:', result);
								resolve(result);
							} else {
								uni.$showMsg('解析登录响应失败');
								reject(new Error('解析登录响应失败'));
							}
						} catch (e) {
							console.error('解析XML失败:', e);
							uni.$showMsg('解析登录响应失败');
							reject(e);
						}
					} else {
						// 如果不是XML，直接返回
						resolve(res.data);
					}
				} else {
					// 请求成功但服务器返回错误
					uni.$showMsg(`登录失败: ${res.data.msg || '未知错误'}`);
					reject(new Error(res.data.msg || '登录失败'));
				}
			},
			fail: (err) => {
				console.error('登录请求失败:', err);
				uni.$showMsg('网络请求失败');
				reject(err);
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