import type { Router } from 'vue-router'
import type { QueryClient } from '@tanstack/vue-query'
import { useAuthStore } from '@/features/auth/store'
import { configureHttp } from '@/shared/http'

/** Gives the HTTP client the current token, and logs out when the backend rejects it. */
export function connectHttpToSession(router: Router, queryClient: QueryClient): void {
  const auth = useAuthStore()
  configureHttp({
    getToken: () => auth.token,
    onUnauthorized: () => {
      const current = router.currentRoute.value
      auth.logout()
      queryClient.clear()
      if (!current.meta.public) {
        void router.push({ path: '/login', query: { redirect: current.fullPath } })
      }
    },
  })
}
