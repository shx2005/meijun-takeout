import axios from 'axios';

const service = axios.create({
  baseURL: '/api/v1',
  timeout: 5000,
});

service.interceptors.request.use(config => {
  // 根据当前页面或localStorage设置不同token
  const path = window.location.pathname;
  let token = '';
  if (path.startsWith('/adm')) {
    token = localStorage.getItem('token') || '';
    if (token) config.headers['adminToken'] = `Bearer ${token}`;
  } else if (path.startsWith('/mer')) {
    token = localStorage.getItem('token') || '';
    if (token) config.headers['merchantToken'] = `Bearer ${token}`;
  } else if (path.startsWith('/emp')) {
    token = localStorage.getItem('token') || '';
    if (token) config.headers['employeeToken'] = `Bearer ${token}`;
  }
  return config;
});

export default service;
