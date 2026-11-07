import { createRouter, type RouterHistory } from 'vue-router'
import { useAuthStore } from '@/features/auth/store'
import AppLayout from './AppLayout.vue'
import PlaceholderPage from './PlaceholderPage.vue'
import LoginPage from '@/features/auth/LoginPage.vue'
import RegisterPage from '@/features/auth/RegisterPage.vue'
import CategoriesPage from '@/features/categories/CategoriesPage.vue'
import TransactionsPage from '@/features/transactions/TransactionsPage.vue'

declare module 'vue-router' {
  interface RouteMeta {
    public?: boolean
  }
}

export function createAppRouter(history: RouterHistory) {
  const router = createRouter({
    history,
    routes: [
      { path: '/login', component: LoginPage, meta: { public: true } },
      { path: '/register', component: RegisterPage, meta: { public: true } },
      {
        path: '/',
        component: AppLayout,
        children: [
          { path: '', component: PlaceholderPage, props: { title: 'Dashboard' } },
          { path: 'transactions', component: TransactionsPage },
          { path: 'categories', component: CategoriesPage },
        ],
      },
      { path: '/:pathMatch(.*)*', redirect: '/' },
    ],
  })

  router.beforeEach((to) => {
    const auth = useAuthStore()
    if (!to.meta.public && !auth.isLoggedIn) {
      return { path: '/login', query: { redirect: to.fullPath } }
    }
    if (to.meta.public && auth.isLoggedIn) {
      return '/'
    }
  })

  return router
}
