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
    url: '/api/v1/user/info'
  });
};

/**
 * 修改商家信息
 * @param {Object} data - 商家信息
 * @returns {Promise}
 */
export const updateMerchantInfoApi = (data) => {
  return merchantRequest.put({
    url: '/api/v1/user/update',
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
  // 使用商家专用的订单查询接口
  return merchantRequest.get({
    url: '/api/v1/orders/page',
    data: {
      pageNum: params.page || params.pageNum || 1,
      pageSize: params.size || params.pageSize || 10,
      status: params.status,
      beginTime: params.beginTime,
      endTime: params.endTime
    }
  });
};

/**
 * 获取商家特定订单详情
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const getMerchantOrderDetailApi = (orderId) => {
  return merchantRequest.get({
    url: `/api/v1/merchants/${orderId}`
  });
};

/**
 * 获取订单详情
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const getOrderDetailApi = (orderId) => {
  return merchantRequest.get({
    url: `/api/v1/orders/${orderId}`
  });
};

/**
 * 修改订单状态（通用方法）
 * @param {string} orderId - 订单ID
 * @param {number} status - 新状态
 * @returns {Promise}
 */
export const updateOrderStatusApi = (orderId, status) => {
  return merchantRequest.put({
    url: `/api/v1/merchants/orders/${orderId}/status`,
    data: { status: status }
  });
};

/**
 * 接受订单
 * @param {string} orderId - 订单ID
 * @returns {Promise}
 */
export const acceptOrderApi = (orderId) => {
  return updateOrderStatusApi(orderId, 3); // 状态3表示已确认
};

/**
 * 拒绝订单
 * @param {string} orderId - 订单ID
 * @param {Object} data - 拒绝原因
 * @returns {Promise}
 */
export const rejectOrderApi = (orderId, data) => {
  return merchantRequest.put({
    url: `/api/v1/merchants/orders/${orderId}/status`,
    data: { status: 6, reason: data.reason || '商家拒绝' }
  });
};

/**
 * 开始配送订单
 * @param {string|number} orderId - 订单ID
 * @returns {Promise}
 */
export const deliverOrderApi = (orderId) => {
  console.log('调用配送API，订单ID:', orderId, '状态: 3');
  return merchantRequest.put({
    url: `/api/v1/merchants/orders/${orderId}/status?status=3`
  });
};

/**
 * 完成订单
 * @param {string|number} orderId - 订单ID
 * @returns {Promise}
 */
export const completeOrderApi = (orderId) => {
  console.log('调用完成订单API，订单ID:', orderId, '状态: 4');
  return merchantRequest.put({
    url: `/api/v1/merchants/orders/${orderId}/status?status=4`
  });
};

/**
 * 取消订单
 * @param {string} orderId - 订单ID
 * @param {Object} data - 取消原因
 * @returns {Promise}
 */
export const cancelOrderApi = (orderId, data) => {
  return merchantRequest.put({
    url: `/api/v1/merchants/orders/${orderId}/status`,
    data: { status: 6, reason: data.reason || '商家取消' }
  });
};

/**
 * 获取菜品分类列表
 * @returns {Promise}
 */
export const getCategoriesApi = () => {
  return merchantRequest.get({
    url: '/api/v1/dishes/categories'
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
    url: '/api/v1/dishes/page',
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
    url: '/api/v1/dishes/save',
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
    url: `/api/v1/dishes/${dishId}`,
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
    url: `/api/v1/dishes/${dishId}`
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
    url: `/api/v1/dishes/${dishId}/status`,
    data: { status }
  });
};

/**
 * 获取员工列表
 * @returns {Promise}
 */
export const getEmployeesApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/staff'
  });
};

/**
 * 新增员工
 * @param {Object} data - 员工数据
 * @returns {Promise}
 */
export const addEmployeeApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/merchants/staff',
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
    url: `/api/v1/merchants/staff/${employeeId}`,
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
    url: `/api/v1/merchants/staff/${employeeId}`
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
  if (params && params.type === 'day') {
    return merchantRequest.get({
      url: '/api/v1/merchants/sales/total',
      data: params
    });
  } else {
    return merchantRequest.get({
      url: '/api/v1/merchants/sales',
      data: params
    });
  }
};

/**
 * 获取销售数据
 * @returns {Promise}
 */
export const getSalesDataApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/sales'
  });
};

/**
 * 获取流量数据
 * @returns {Promise}
 */
export const getTrafficDataApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/traffic'
  });
};

/**
 * 获取售后列表
 * @returns {Promise}
 */
export const getAfterSaleListApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/after-sale'
  });
};

/**
 * 审批售后申请
 * @param {Object} data - 审批数据
 * @param {number} data.requestId - 售后请求ID
 * @param {number} data.userId - 用户ID
 * @returns {Promise}
 */
export const approveAfterSaleApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/merchants/after-sale/approve',
    data: data
  });
};

/**
 * 拒绝售后申请
 * @param {Object} data - 拒绝数据
 * @param {number} data.requestId - 售后请求ID
 * @param {number} data.userId - 用户ID
 * @returns {Promise}
 */
export const rejectAfterSaleApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/merchants/after-sale/reject',
    data: data
  });
};

/**
 * 获取所有用户
 * @returns {Promise}
 */
export const getAllUsersApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/users'
  });
};

/**
 * 搜索用户
 * @param {Object} params - 搜索参数
 * @param {string} params.name - 用户名
 * @param {number} params.id - 用户ID
 * @returns {Promise}
 */
export const searchUsersApi = (params) => {
  return merchantRequest.get({
    url: '/api/v1/merchants/users/search',
    data: params
  });
};

/**
 * 获取门店列表
 * @returns {Promise}
 */
export const getStoresApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/stores'
  });
};

/**
 * 修改门店信息
 * @param {string} storeId - 门店ID
 * @param {Object} data - 门店数据
 * @returns {Promise}
 */
export const updateStoreApi = (storeId, data) => {
  return merchantRequest.put({
    url: `/api/v1/merchants/stores/${storeId}`,
    data: data
  });
};

/**
 * 获取优惠券列表
 * @returns {Promise}
 */
export const getCouponsApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/coupons'
  });
};

/**
 * 添加优惠券
 * @param {Object} data - 优惠券数据
 * @returns {Promise}
 */
export const addCouponApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/merchants/coupons',
    data: data
  });
};

/**
 * 修改优惠券
 * @param {string} couponId - 优惠券ID
 * @param {Object} data - 优惠券数据
 * @returns {Promise}
 */
export const updateCouponApi = (couponId, data) => {
  return merchantRequest.put({
    url: `/api/v1/merchants/coupons/${couponId}`,
    data: data
  });
};

/**
 * 删除优惠券
 * @param {string} couponId - 优惠券ID
 * @returns {Promise}
 */
export const deleteCouponApi = (couponId) => {
  return merchantRequest.delete({
    url: `/api/v1/merchants/coupons/${couponId}`
  });
};

/**
 * 发放优惠券
 * @param {Object} data - 发放数据
 * @returns {Promise}
 */
export const distributeCouponApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/merchants/coupons/distribute',
    data: data
  });
};

/**
 * 获取促销活动列表
 * @returns {Promise}
 */
export const getPromotionsApi = () => {
  return merchantRequest.get({
    url: '/api/v1/merchants/promotions'
  });
};

/**
 * 添加促销活动
 * @param {Object} data - 促销活动数据
 * @returns {Promise}
 */
export const addPromotionApi = (data) => {
  return merchantRequest.post({
    url: '/api/v1/merchants/promotions',
    data: data
  });
};

/**
 * 修改促销活动
 * @param {string} promotionId - 促销活动ID
 * @param {Object} data - 促销活动数据
 * @returns {Promise}
 */
export const updatePromotionApi = (promotionId, data) => {
  return merchantRequest.put({
    url: `/api/v1/merchants/promotions/${promotionId}`,
    data: data
  });
};

/**
 * 删除促销活动
 * @param {string} promotionId - 促销活动ID
 * @returns {Promise}
 */
export const deletePromotionApi = (promotionId) => {
  return merchantRequest.delete({
    url: `/api/v1/merchants/promotions/${promotionId}`
  });
};

/**
 * 获取员工分页数据
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export const getEmployeePageApi = (params) => {
  return merchantRequest.get({
    url: '/api/v1/merchants/staff/page',
    data: params
  });
}; 