import regeneratorRuntime, { async } from '../lib/runtime/runtime';

export const addOrderApi = (data) => {
	return uni.$ajax.post({
		url: 'api/v1/orders/submit',
		data: data
	})
}

export const getOrdersOverviewApi = () => {
	return uni.$ajax.get({
		url: 'api/v1/orders/overview',
	})
}