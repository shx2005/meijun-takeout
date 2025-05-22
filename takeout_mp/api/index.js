import regeneratorRuntime, { async } from '../lib/runtime/runtime';

// 购物车相关接口
export const addCartApi = (data) => {
	return uni.$ajax.post({
		url: 'api/v1/cart/add',
		data: data
	})
}

export const updateCartApi = (data) => {
	return uni.$ajax.post({
		url: 'api/v1/cart/update',
		data: data
	})
}

export const clearCartApi = () => {
	return uni.$ajax.delete({
		url: 'api/v1/cart/delete',
	})
}

export const cartListApi = () => {
	return uni.$ajax.get({
		url: "api/v1/cart",
	})
}

// 菜品相关接口
export const dishListApi = (data) => {
	return uni.$ajax.get({
		url: 'api/v1/dishes/page',
		data: { ...data }
	})
}

export const categoryListApi = () => {
	return uni.$ajax.get({
		url: "api/v1/dishes/categories",
	})
}

// 用户相关接口
export const loginApi = (data) => {
	return uni.$ajax.post({
		url: "api/v1/auth/login",
		data: data
	})
}

export const registerApi = (data) => {
	return uni.$ajax.post({
		url: "api/v1/auth/register",
		data: data
	})
}

export const logoutApi = () => {
	return uni.$ajax.post({
		url: "api/v1/auth/logout"
	})
}

export const getUserInfoApi = () => {
	return uni.$ajax.get({
		url: "api/v1/user/info"
	})
}

// 订单相关接口
export const getOrdersApi = (data) => {
	return uni.$ajax.get({
		url: "api/v1/orders/page",
		data: { ...data }
	})
}

export const getOrderDetailApi = (orderId) => {
	return uni.$ajax.get({
		url: `api/v1/orders/${orderId}`,
	})
}

export const submitOrderCommentApi = (data) => {
	return uni.$ajax.post({
		url: "api/v1/orders/comment",
		data: data
	})
}

export const setMealDishDetailsApi = (id) => {
	return uni.$ajax.get({
		url: `mp/setmeal/${id}`,
	})
}

export const setmealListApi = (data) => {
	return uni.$ajax.get({
		url: 'mp/setmeal/list',
		data: { ...data }
	})
}
