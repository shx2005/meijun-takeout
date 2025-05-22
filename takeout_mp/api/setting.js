import regeneratorRuntime, { async } from '../lib/runtime/runtime';

// 获取用户信息
export const getUserInfoApi = () => {
	return uni.$ajax.get({
		url: 'api/v1/user/info'
	})
}

// 更新用户设置
export const updateSettingsApi = (data) => {
	return uni.$ajax.put({
		url: 'api/v1/user/settings',
		data: data
	})
}

// 获取用户设置
export const getSettingsApi = () => {
	return uni.$ajax.get({
		url: 'api/v1/user/settings'
	})
}