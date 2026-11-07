import { afterEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { ref } from 'vue'
import { useCreateTransaction, useTransactions } from '../queries'
import type { TransactionFilter } from '../api'
import { mockFetch } from '@/test/mockFetch'
import { withQueryClient } from '@/test/withQueryClient'

describe('transaction queries', () => {
  afterEach(() => vi.unstubAllGlobals())

  it('builds the URL from the filter and refetches on change', async () => {
    const fetch = mockFetch(() => ({ status: 200, body: { content: [], page: 0, size: 20, totalElements: 0 } }))
    const filter = ref<TransactionFilter>({ month: '2026-11', page: 0, size: 20 })
    withQueryClient(() => useTransactions(filter))
    await flushPromises()

    filter.value = { month: '2026-11', categoryId: 3, page: 1, size: 20 }
    await flushPromises()

    expect(fetch.mock.calls.map((c) => c[0])).toEqual([
      '/api/transactions?month=2026-11&page=0&size=20',
      '/api/transactions?month=2026-11&categoryId=3&page=1&size=20',
    ])
  })

  it('create refreshes transactions and summary', async () => {
    mockFetch(() => ({ status: 201, body: { id: 1, categoryId: 1, amount: 1000, date: '2026-11-01', note: null } }))
    const { result, queryClient } = withQueryClient(() => useCreateTransaction())
    const invalidate = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ categoryId: 1, amount: 1000, date: '2026-11-01', note: null })

    expect(invalidate).toHaveBeenCalledWith({ queryKey: ['transactions'] })
    expect(invalidate).toHaveBeenCalledWith({ queryKey: ['summary'] })
  })
})
