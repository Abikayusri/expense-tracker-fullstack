import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { renderApp } from '@/test/render'
import { mockFetch } from '@/test/mockFetch'
import { currentMonth } from '@/shared/format'
import type { Summary } from '../api'

const november: Summary = {
  totalIncome: 0,
  totalExpense: 150_000,
  balance: -150_000,
  perCategory: [
    { categoryId: 1, name: 'Makan', type: 'EXPENSE', spent: 150_000, budget: 100_000, overBudget: true },
    { categoryId: 2, name: 'Gaji', type: 'INCOME', spent: 0, budget: null, overBudget: false },
  ],
}

function fakeBackend(summary: Summary = november) {
  return mockFetch((url) => {
    if (url === '/api/me') return { status: 200, body: { id: 1, email: 'b@mail.com', name: 'Budi' } }
    if (url.startsWith('/api/summary')) return { status: 200, body: summary }
    return { status: 201, body: { id: 1, categoryId: 1, amount: 1000, date: '2026-11-01', note: null } }
  })
}

function summaryUrls(fetch: ReturnType<typeof fakeBackend>) {
  return fetch.mock.calls.map((c) => String(c[0])).filter((u) => u.startsWith('/api/summary'))
}

describe('DashboardPage', () => {
  beforeEach(() => localStorage.setItem('expense.token', 'jwt-1'))
  afterEach(() => {
    vi.unstubAllGlobals()
    localStorage.clear()
    document.body.innerHTML = ''
  })

  it('shows formatted totals, a red negative balance, and the banner', async () => {
    const fetch = fakeBackend()
    const { wrapper } = await renderApp('/')
    await flushPromises()

    expect(summaryUrls(fetch)[0]).toBe(`/api/summary?month=${currentMonth()}`)
    expect(wrapper.get('[data-testid=expense]').text()).toBe('Rp150.000')
    const balance = wrapper.get('[data-testid=balance]')
    expect(balance.text()).toBe('-Rp150.000')
    expect(balance.classes()).toContain('text-red-700')
    expect(wrapper.get('[data-over-budget]').text()).toBe('Pengeluaran Makan sudah Rp150.000, melebihi budget Rp100.000')
    expect(wrapper.findAll('[role=progressbar]')).toHaveLength(1)
  })

  it('loads another month when the picker changes', async () => {
    const fetch = fakeBackend()
    const { wrapper } = await renderApp('/')
    await flushPromises()

    await wrapper.get('input[name=month]').setValue('2026-10')
    await flushPromises()

    expect(summaryUrls(fetch).pop()).toBe('/api/summary?month=2026-10')
  })

  it('suggests creating a category when there are none', async () => {
    fakeBackend({ totalIncome: 0, totalExpense: 0, balance: 0, perCategory: [] })
    const { wrapper } = await renderApp('/')
    await flushPromises()

    expect(wrapper.text()).toContain('Buat kategori dulu')
  })

  it('refetches when the summary cache is invalidated by a save elsewhere', async () => {
    const fetch = fakeBackend()
    const { queryClient } = await renderApp('/')
    await flushPromises()
    const before = summaryUrls(fetch).length

    // Category and transaction mutations invalidate this prefix (see their queries specs).
    await queryClient.invalidateQueries({ queryKey: ['summary'] })
    await flushPromises()

    expect(summaryUrls(fetch).length).toBe(before + 1)
  })
})
