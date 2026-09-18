import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/',
    name: 'PlanForm',
    component: () => import('../views/PlanForm.vue')
  },
  {
    path: '/plan',
    name: 'PlanDetail',
    component: () => import('../views/PlanDetail.vue')
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('../views/OrderList.vue')
  },
  {
    path: '/trips',
    name: 'MyTrips',
    component: () => import('../views/MyTrips.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router