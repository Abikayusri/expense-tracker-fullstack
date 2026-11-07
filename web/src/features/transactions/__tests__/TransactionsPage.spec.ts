import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises, type VueWrapper } from '@vue/test-utils'
import { renderApp } from '@/test/render'
import { mockFetch } from '@/test/mockFetch'
import { currentMonth } from '@/shared/format'
import type { Transaction } from '../api'

const categories = [
  { id: 1, name: 'Makan', type: 'EXPENSE', monthlyBudget: 1000000 },
  { id: 2, name: 'Gaji', type: 'INCOME', monthlyBudget: null },
]

type Call = { method: string; url: string; body: unknown }

function fakeBackend(transactions: Transaction[]) {
  const calls: Call[] = []
  mockFetch((url, init) => {
    const method = init.method ?? 'GET'
    calls.push({ method, url, body: init.body ? JSON.parse(String(init.body)) : undefined })
    if (url === '/api/me') return { status: 200, body: { id: 1, email: 'b@mail.com', name: 'Budi' } }
    if (url.startsWith('/api/categories')) return { status: 200, body: categories }
    if (method === 'POST') return { status: 201, body: { id: 999, ...JSON.parse(String(init.body)) } }
    const query = new URL(url, 'http://test').searchParams
    const page = Number(query.get('page'))
    const size = Number(query.get('size'))
    return {
      status: 200,
      body: { content: transactions.slice(page * size, (page + 1) * size), page, size, totalElements: transactions.length },
    }
  })
  return calls
}

function many(count: number): Transaction[] {
  return Array.from({ length: count }, (_, i) => ({
    id: i + 1,
    categoryId: 1,
    amount: 1000,
    date: `2026-11-${String(25 - i).padStart(2, '0')}`,
    note: null,
  }))
}

function button(wrapper: VueWrapper, text: string) {
  return wrapper.findAll('button').find((b) => b.text() === text)!
}

function listUrls(calls: Call[]) {
  return calls.filter((c) => c.method === 'GET' && c.url.startsWith('/api/transactions')).map((c) => c.url)
}

function lastListUrl(calls: Call[]) {
  const urls = listUrls(calls)
  return urls[urls.length - 1]
}

describe('TransactionsPage', () => {
  beforeEach(() => localStorage.setItem('expense.token', 'jwt-1'))
  afterEach(() => {
    vi.unstubAllGlobals()
    localStorage.clear()
    document.body.innerHTML = ''
  })

  it('loads the current month and shows category names and signed amounts', async () => {
    const calls = fakeBackend([
      { id: 1, categoryId: 1, amount: 25000, date: '2026-11-10', note: 'Nasi goreng' },
      { id: 2, categoryId: 2, amount: 5000000, date: '2026-11-01', note: null },
    ])
    const { wrapper } = await renderApp('/transactions')
    await flushPromises()

    expect(listUrls(calls)[0]).toBe(`/api/transactions?month=${currentMonth()}&page=0&size=20`)
    const rows = wrapper.findAll('[data-row]')
    expect(rows[0]!.text()).toContain('Makan')
    expect(rows[0]!.text()).toContain('-Rp25.000')
    expect(rows[1]!.text()).toContain('Gaji')
    expect(rows[1]!.text()).toContain('Rp5.000.000')
  })

  it('pages through results and enables buttons at the edges', async () => {
    const calls = fakeBackend(many(25))
    const { wrapper } = await renderApp('/transactions')
    await flushPromises()

    expect(wrapper.findAll('[data-row]')).toHaveLength(20)
    expect(button(wrapper, 'Sebelumnya').attributes('disabled')).toBeDefined()
    expect(button(wrapper, 'Berikutnya').attributes('disabled')).toBeUndefined()

    await button(wrapper, 'Berikutnya').trigger('click')
    await flushPromises()

    expect(lastListUrl(calls)).toContain('page=1')
    expect(wrapper.findAll('[data-row]')).toHaveLength(5)
    expect(button(wrapper, 'Berikutnya').attributes('disabled')).toBeDefined()
  })

  it('goes back to the first page when the month or category changes', async () => {
    const calls = fakeBackend(many(25))
    const { wrapper } = await renderApp('/transactions')
    await flushPromises()
    await button(wrapper, 'Berikutnya').trigger('click')
    await flushPromises()

    await wrapper.get('input[name=month]').setValue('2026-10')
    await flushPromises()
    expect(lastListUrl(calls)).toBe('/api/transactions?month=2026-10&page=0&size=20')

    await button(wrapper, 'Berikutnya').trigger('click')
    await flushPromises()
    await wrapper.get('select[name=filterCategory]').setValue('1')
    await flushPromises()
    expect(lastListUrl(calls)).toBe('/api/transactions?month=2026-10&categoryId=1&page=0&size=20')
  })

  it('adds a transaction and refreshes the list', async () => {
    const calls = fakeBackend([])
    const { wrapper } = await renderApp('/transactions')
    await flushPromises()
    const before = listUrls(calls).length

    await button(wrapper, 'Tambah transaksi').trigger('click')
    await wrapper.get('select[name=categoryId]').setValue('1')
    await wrapper.get('input[name=amount]').setValue('25000')
    await wrapper.get('input[name=date]').setValue('2026-11-10')
    await wrapper.get('form').trigger('submit')
    await flushPromises()

    expect(calls.find((c) => c.method === 'POST')!.body).toEqual({
      categoryId: 1,
      amount: 25000,
      date: '2026-11-10',
      note: null,
    })
    expect(wrapper.find('form').exists()).toBe(false)
    expect(listUrls(calls).length).toBeGreaterThan(before)
  })
})
