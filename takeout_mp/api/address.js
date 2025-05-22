import regeneratorRuntime, {
	async
} from '../lib/runtime/runtime';

// 获取默认地址
export const getDefaultAddressApi = () => {
	return uni.$ajax.get({
		url: 'v1/address/default',
	})
}

// 更新地址
export const updateAddressApi = (data) => {
	return uni.$ajax.put({
		url: 'v1/address',
		data: data
	})
}

// 添加地址
export const addAddressApi = (data) => {
	return uni.$ajax.post({
		url: 'v1/address',
		data: data
	})
}

// 删除地址
export const deleteAddressApi = (id) => {
	return uni.$ajax.delete({
		url: 'v1/address/' + id,
	})
}

// 查询单个地址
export const addressFindOneApi = (id) => {
	return uni.$ajax.get({
		url: 'v1/address/' + id,
	})
}

// 获取地址列表
export const addressListApi = () => {
	return uni.$ajax.get({
		url: 'v1/address/list',
	})
}

// 设置默认地址
export const setDefaultAddressApi = (data) => {
	return uni.$ajax.put({
		url: 'v1/address/default',
		data: data
	})
}
