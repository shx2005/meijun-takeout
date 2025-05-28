import regeneratorRuntime, { async } from '../lib/runtime/runtime';
import { getOrdersApi } from './index.js';

/**
 * 分页查询订单
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码，从1开始
 * @param {number} params.size - 每页记录数
 * @returns {Promise}
 */
export const orderPagingApi = (params) => {
	return uni.$ajax.get({
		url: '/api/v1/orders/page',
		data: params
	});
};

/**
 * 获取订单详情
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const getOrderDetailApi = (orderId) => {
	return uni.$ajax.get({
		url: `/api/v1/orders/${orderId}`
	});
};

/**
 * 提交订单
 * @param {Object} data - 订单数据
 * @returns {Promise}
 */
export const addOrderApi = (data) => {
	return uni.$ajax.post({
		url: '/api/v1/orders',
		data: data
	});
};

/**
 * 再来一单
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const orderAgainApi = (orderId) => {
	return uni.$ajax.post({
		url: `/api/v1/orders/again`,
		data: { orderId }
	});
};

/**
 * 删除订单
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const deleteOrderApi = (orderId) => {
	return uni.$ajax.delete({
		url: `/api/v1/orders/${orderId}`
	});
};

/**
 * 提交订单评论
 * @param {Object} data - 评论数据
 * @returns {Promise}
 */
export const submitCommentApi = (data) => {
	return uni.$ajax.post({
		url: '/api/v1/orders/comment',
		data: data
	});
};

/**
 * 申请售后
 * @param {Object} data - 售后申请数据
 * @returns {Promise}
 */
export const applyAfterSaleApi = (data) => {
	return uni.$ajax.post({
		url: '/api/v1/orders/after-sale',
		data: data
	});
};

/**
 * 查询订单状态
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const getOrderStatusApi = (orderId) => {
	return uni.$ajax.get({
		url: `/api/v1/orders/status`,
		data: { orderId }
	});
};

/**
 * 支付订单
 * @param {Object} data - 支付数据
 * @returns {Promise}
 */
export const payOrderApi = (data) => {
	return uni.$ajax.post({
		url: '/api/v1/orders/pay',
		data: data
	});
};

/**
 * 取消订单
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const cancelOrderApi = (orderId) => {
	return uni.$ajax.post({
		url: `/api/v1/orders/cancel/${orderId}`
	});
};

/**
 * 获取客服信息
 * @returns {Promise}
 */
export const getServiceInfoApi = () => {
	return uni.$ajax.get({
		url: `/api/v1/service/info`
	});
};

/**
 * 申请退款
 * @param {Object} data - 退款申请数据
 * @returns {Promise}
 */
export const applyRefundApi = (data) => {
	return uni.$ajax.post({
		url: '/api/v1/orders/refund',
		data: data
	});
};

export {
	getOrdersApi
}

