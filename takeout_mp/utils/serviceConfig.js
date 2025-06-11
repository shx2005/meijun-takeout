// 统一的客服信息配置
export const SERVICE_CONFIG = {
	phone: '400-123-4567',
	workTime: '9:00-18:00',
	email: 'service@meijun.com',
	wechat: 'meijun-service',
	description: '我们的客服团队随时为您服务，解答您的疑问和问题。如有任何问题，请随时联系我们。'
};

// 获取格式化的客服信息文本
export const getServiceInfoText = () => {
	return `客服电话：${SERVICE_CONFIG.phone}\n工作时间：${SERVICE_CONFIG.workTime}\n电子邮箱：${SERVICE_CONFIG.email}`;
};

// 获取客服信息对象
export const getServiceInfo = () => {
	return {
		...SERVICE_CONFIG
	};
}; 