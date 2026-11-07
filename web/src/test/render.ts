import { mount } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createMemoryHistory } from 'vue-router'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'
import App from '@/App.vue'
import { createAppRouter } from '@/app/router'
import { connectHttpToSession } from '@/app/session'

/** Mounts the whole app at `route` with fresh Pinia, router and query cache. */
export async function renderApp(route: string) {
  const pinia = createPinia()
  setActivePinia(pinia)
  const router = createAppRouter(createMemoryHistory())
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  connectHttpToSession(router, queryClient)

  await router.push(route)
  await router.isReady()
  const wrapper = mount(App, {
    attachTo: document.body,
    global: { plugins: [pinia, router, [VueQueryPlugin, { queryClient }]] },
  })
  return { wrapper, router, queryClient }
}
