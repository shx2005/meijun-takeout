import axios from 'axios';

const service = axios.create({
  baseURL: '/api/v1',
  timeout: 5000,
});

// 请求拦截器
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

export default service;
