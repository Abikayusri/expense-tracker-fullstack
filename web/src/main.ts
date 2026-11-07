import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createWebHistory } from 'vue-router'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'

import App from './App.vue'
import { createAppRouter } from './app/router'
import { connectHttpToSession } from './app/session'

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: false, staleTime: 30_000 } },
})

const app = createApp(App)
app.use(createPinia())
const router = createAppRouter(createWebHistory(import.meta.env.BASE_URL))
app.use(router)
app.use(VueQueryPlugin, { queryClient })
connectHttpToSession(router, queryClient)

app.mount('#app')
