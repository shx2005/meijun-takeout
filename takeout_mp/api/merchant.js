import merchantRequest from '@/utils/merchantRequest';
import regeneratorRuntime, { async } from '../lib/runtime/runtime';

/**
 * 商家登录
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise}
 */
export const merchantLoginApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/auth/login',
    data: {
      ...data,
      identity: 'MERCHANT'
    }
  });
};

/**
 * 员工登录
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {number} data.merchantId - 商家ID
 * @returns {Promise}
 */
export const employeeLoginApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/auth/login',
    data: {
      ...data,
      identity: 'EMPLOYEE'
    }
  });
};

/**
 * 获取商家信息
 * @returns {Promise}
 */
export const getMerchantInfoApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/info'
  });
};

/**
 * 修改商家信息
 * @param {Object} data - 商家信息
 * @returns {Promise}
 */
export const updateMerchantInfoApi = (data) => {
  return merchantRequest.put({
    url: '/api/v1/merchants/info',
    data: data
  });
};

/**
 * 获取商家订单列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码，从1开始
 * @param {number} params.size - 每页记录数
 * @param {number} [params.status] - 订单状态（可选）
 * @returns {Promise}
 */
export const getOrderListApi = (params) => {
  return merchantRequest.get({
    url: '/api/v1/merchants/orders',
    data: params
  });
};

/**
 * 获取订单详情
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const getOrderDetailApi = (orderId) => {
  return merchantRequest.get({
    url: `/api/v1/merchants/orders/${orderId}`
  });
};

/**
 * 接受订单
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const acceptOrderApi = (orderId) => {
  return merchantRequest.post({
    url: `/api/v1/merchants/orders/${orderId}/accept`
  });
};

/**
 * 拒绝订单
 * @param {string} orderId - 订单ID
 * @param {Object} data - 拒绝原因
 * @returns {Promise}
 */
export const rejectOrderApi = (orderId, data) => {
  return merchantRequest.post({
    url: `/api/v1/merchants/orders/${orderId}/reject`,
    data: data
  });
};

/**
 * 开始配送订单
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const deliverOrderApi = (orderId) => {
  return merchantRequest.post({
    url: `/api/v1/merchants/orders/${orderId}/deliver`
  });
};

/**
 * 完成订单
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const completeOrderApi = (orderId) => {
  return merchantRequest.post({
    url: `/api/v1/merchants/orders/${orderId}/complete`
  });
};

/**
 * 取消订单
 * @param {string} orderId - 订单ID
 * @param {Object} data - 取消原因
 * @returns {Promise}
 */
export const cancelOrderApi = (orderId, data) => {
  return merchantRequest.post({
    url: `/api/v1/merchants/orders/${orderId}/cancel`,
    data: data
  });
};

/**
 * 获取菜品分类列表
 * @returns {Promise}
 */
export const getCategoriesApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/categories'
  });
};

/**
 * 获取菜品列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码，从1开始
 * @param {number} params.size - 每页记录数
 * @param {number} [params.categoryId] - 分类ID（可选）
 * @returns {Promise}
 */
export const getDishesApi = (params) => {
  return merchantRequest.get({
    url: '/api/v1/merchants/dishes',
    data: params
  });
};

/**
 * 新增菜品
 * @param {Object} data - 菜品数据
 * @returns {Promise}
 */
export const addDishApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/merchants/dishes',
    data: data
  });
};

/**
 * 修改菜品
 * @param {string} dishId - 菜品ID
 * @param {Object} data - 菜品数据
 * @returns {Promise}
 */
export const updateDishApi = (dishId, data) => {
  return merchantRequest.put({
    url: `/api/v1/merchants/dishes/${dishId}`,
    data: data
  });
};

/**
 * 删除菜品
 * @param {string} dishId - 菜品ID
 * @returns {Promise}
 */
export const deleteDishApi = (dishId) => {
  return merchantRequest.delete({
    url: `/api/v1/merchants/dishes/${dishId}`
  });
};

/**
 * 上架/下架菜品
 * @param {string} dishId - 菜品ID
 * @param {number} status - 状态（1:上架，0:下架）
 * @returns {Promise}
 */
export const changeDishStatusApi = (dishId, status) => {
  return merchantRequest.put({
    url: `/api/v1/merchants/dishes/${dishId}/status`,
    data: { status }
  });
};

/**
 * 获取员工列表
 * @returns {Promise}
 */
export const getEmployeesApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/employees'
  });
};

/**
 * 新增员工
 * @param {Object} data - 员工数据
 * @returns {Promise}
 */
export const addEmployeeApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/merchants/employees',
    data: data
  });
};

/**
 * 修改员工信息
 * @param {string} employeeId - 员工ID
 * @param {Object} data - 员工数据
 * @returns {Promise}
 */
export const updateEmployeeApi = (employeeId, data) => {
  return merchantRequest.put({
    url: `/api/v1/merchants/employees/${employeeId}`,
    data: data
  });
};

/**
 * 删除员工
 * @param {string} employeeId - 员工ID
 * @returns {Promise}
 */
export const deleteEmployeeApi = (employeeId) => {
  return merchantRequest.delete({
    url: `/api/v1/merchants/employees/${employeeId}`
  });
};

/**
 * 获取商家统计数据
 * @param {Object} params - 查询参数
 * @param {string} params.type - 统计类型（day, week, month, year）
 * @param {string} [params.startDate] - 开始日期（可选）
 * @param {string} [params.endDate] - 结束日期（可选）
 * @returns {Promise}
 */
export const getStatisticsApi = (params) => {
  return merchantRequest.get({
    url: '/api/v1/merchants/statistics',
    data: params
  });
}; 