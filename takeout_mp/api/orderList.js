import regeneratorRuntime, { async } from '../lib/runtime/runtime';

// 添加订单
export const addOrderApi = (data) => {
	return uni.$ajax.post({
		url: 'api/v1/orders/submit',
		data: data
	})
}

// 获取订单列表
export const orderListApi = () => {
	return uni.$ajax.get({
		url: 'api/v1/orders/overview',
	})
}

// 分页查询订单
export const orderPagingApi = (data) => {
	return uni.$ajax.get({
		url: 'api/v1/orders/page',
		data: {...data},
	})
}

// 再来一单
export const orderAgainApi = (orderId) => {
	return uni.$ajax.post({
		url: 'api/v1/orders/again',
		data: { orderId }
	})
}

// 删除订单
export const deleteOrderApi = (orderId) => {
	return uni.$ajax.delete({
		url: 'api/v1/orders/' + orderId,
	})
}

// 获取订单详情
export const getOrderDetailApi = (orderId) => {
	return uni.$ajax.get({
		url: 'api/v1/orders/' + orderId,
	})
}

// 提交订单评价
export const submitOrderCommentApi = (data) => {
	return uni.$ajax.post({
		url: 'api/v1/orders/comment',
		data: data
	})
}

// 申请售后
export const applyAfterSaleApi = (data) => {
	return uni.$ajax.post({
		url: 'api/v1/orders/after-sale',
		data: data
	})
}

