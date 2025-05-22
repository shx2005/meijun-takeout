import regeneratorRuntime, { async } from '../lib/runtime/runtime';

export const addOrderApi = (data) => {
	return uni.$ajax.post({
		url: 'v1/orders/submit',
		data: data
	})
}

export const getOrdersOverviewApi = () => {
	return uni.$ajax.get({
		url: 'v1/orders/overview',
	})
}