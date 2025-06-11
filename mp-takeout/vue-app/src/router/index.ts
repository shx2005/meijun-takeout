import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import DashboardAdm from '../views/Dashboard-adm.vue'; // 可以自行创建

const routes = [
  { path: '/login', component: Login },
  { path: '/adm-dashboard', component: DashboardAdm, meta: { requiresAuth: true } }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.meta.requiresAuth && !token) {
    next('/login');
  } else {
    next();
  }
});

export default router;
