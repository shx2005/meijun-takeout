import regeneratorRuntime, { async } from '../lib/runtime/runtime';
import request from '../utils/request'

// 购物车相关接口
export const addCartApi = (data) => {
	return request({
		url: '/api/v1/cart',
		method: 'post',
		data
	})
}

export const updateCartApi = (data) => {
	return request({
		url: '/api/v1/cart',
		method: 'put',
		data
	})
}

export const clearCartApi = () => {
	return request({
		url: '/api/v1/cart',
		method: 'delete'
	})
}

export const cartListApi = (params) => {
	return request({
		url: '/api/v1/cart',
		method: 'get',
		params
	})
}

// 菜品相关接口
export const dishListApi = (params) => {
	return request({
		url: '/api/v1/dishes',
		method: 'get',
		params
	})
}

export const categoryListApi = (params) => {
	return request({
		url: '/api/v1/dishes/categories',
		method: 'get',
		params
	})
}

// 用户相关接口
export const loginApi = (data) => {
	return uni.$ajax.post({
		url: "v1/auth/login",
		data: data
	})
}

export const registerApi = (data) => {
	return uni.$ajax.post({
		url: "register",
		data: data
	})
}

export const logoutApi = () => {
	return uni.$ajax.post({
		url: "v1/auth/logout"
	})
}

export const getUserInfoApi = () => {
	return uni.$ajax.get({
		url: "v1/user/info"
	})
}

// 订单相关接口
export const getOrdersApi = (data) => {
	return uni.$ajax.get({
		url: "v1/orders/page",
		data: { ...data }
	})
}

export const getOrderDetailApi = (orderId) => {
	return uni.$ajax.get({
		url: `v1/orders/${orderId}`,
	})
}

export const submitOrderCommentApi = (data) => {
	return uni.$ajax.post({
		url: "v1/orders/comment",
		data: data
	})
}

export const setMealDishDetailsApi = (id) => {
	return request({
		url: `/api/v1/setmeals/${id}`,
		method: 'get'
	})
}

export const setmealListApi = (params) => {
	return request({
		url: '/api/v1/setmeals',
		method: 'get',
		params
	})
}

export const recommendationsApi = () => {
	return uni.$ajax.get({
		url: 'v1/dishes/recommendations'
	})
}
