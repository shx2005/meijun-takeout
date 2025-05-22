import regeneratorRuntime, { async } from '../lib/runtime/runtime';

// 更新用户信息
export const updateUserInfo = (data) => {
	return uni.$ajax.put({
		url: 'v1/user/update',
		data: data
	})
}

// 发送验证码
export const sendCode = (data) => {
	return uni.$ajax.get({
		url: 'v1/auth/send-code',
		data: data
	})
}