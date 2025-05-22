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

// 手机验证码登录
export const phoneLoginApi = (data) => {
	return uni.$ajax.post({
		url: 'api/v1/auth/phone-login',
		data: data
	})
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

