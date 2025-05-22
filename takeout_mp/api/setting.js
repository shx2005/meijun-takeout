import regeneratorRuntime, { async } from '../lib/runtime/runtime';
import { getUserInfoApi } from './index';

// 更新用户设置
export const updateSettingsApi = (data) => {
	return uni.$ajax.put({
		url: 'v1/user/settings',
		data: data
	})
}

// 获取用户设置
export const getSettingsApi = () => {
	return uni.$ajax.get({
		url: 'v1/user/settings'
	})
}

// 导出getUserInfoApi以供使用
export { getUserInfoApi };