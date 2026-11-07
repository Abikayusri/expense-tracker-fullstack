import { afterEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { ref } from 'vue'
import { useCategories, useCreateCategory, useDeleteCategory } from '../queries'
import { mockFetch } from '@/test/mockFetch'
import { withQueryClient } from '@/test/withQueryClient'

describe('category queries', () => {
  afterEach(() => vi.unstubAllGlobals())

  it('passes the type filter and refetches when it changes', async () => {
    const fetch = mockFetch(() => ({ status: 200, body: [] }))
    const type = ref<'INCOME' | 'EXPENSE' | undefined>('INCOME')
    withQueryClient(() => useCategories(type))
    await flushPromises()

    type.value = undefined
    await flushPromises()

    expect(fetch.mock.calls.map((c) => c[0])).toEqual(['/api/categories?type=INCOME', '/api/categories'])
  })

  it('create refreshes categories and summary', async () => {
    mockFetch(() => ({ status: 201, body: { id: 1, name: 'Makan', type: 'EXPENSE', monthlyBudget: null } }))
    const { result, queryClient } = withQueryClient(() => useCreateCategory())
    const invalidate = vi.spyOn(queryClient, 'invalidateQueries')

    await result.mutateAsync({ name: 'Makan', type: 'EXPENSE', monthlyBudget: null })

    expect(invalidate).toHaveBeenCalledWith({ queryKey: ['categories'] })
    expect(invalidate).toHaveBeenCalledWith({ queryKey: ['summary'] })
  })

  it('a failed delete does not refresh anything', async () => {
    mockFetch(() => ({ status: 409, body: { detail: 'Kategori masih dipakai transaksi' } }))
    const { result, queryClient } = withQueryClient(() => useDeleteCategory())
    const invalidate = vi.spyOn(queryClient, 'invalidateQueries')

    await expect(result.mutateAsync(1)).rejects.toMatchObject({ status: 409 })

    expect(invalidate).not.toHaveBeenCalled()
  })
})
