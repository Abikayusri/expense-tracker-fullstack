import { toValue, type MaybeRef } from 'vue'
import { useQuery } from '@tanstack/vue-query'
import { getSummary } from './api'

export function useSummary(month: MaybeRef<string>) {
  return useQuery({
    queryKey: ['summary', month],
    queryFn: () => getSummary(toValue(month)),
  })
}
