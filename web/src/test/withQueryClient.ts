import { defineComponent } from 'vue'
import { mount } from '@vue/test-utils'
import { QueryClient, VueQueryPlugin } from '@tanstack/vue-query'

/** Runs composables that need a component and a query client; returns what `setup` returned. */
export function withQueryClient<T>(setup: () => T) {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  let result!: T
  mount(
    defineComponent({
      setup() {
        result = setup()
        return () => null
      },
    }),
    { global: { plugins: [[VueQueryPlugin, { queryClient }]] } },
  )
  return { result, queryClient }
}
