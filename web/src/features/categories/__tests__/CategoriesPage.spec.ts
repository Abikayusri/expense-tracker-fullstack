import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises, type VueWrapper } from '@vue/test-utils'
import { renderApp } from '@/test/render'
import { mockFetch } from '@/test/mockFetch'
import type { Category } from '../api'

type Call = { method: string; url: string; body: unknown }

/** Minimal in-memory backend for /api/categories so the page can be driven end to end. */
function fakeBackend(initial: Category[], options: { inUse?: number[] } = {}) {
  let categories = [...initial]
  let nextId = 100
  const calls: Call[] = []
  mockFetch((url, init) => {
    const method = init.method ?? 'GET'
    const body = init.body ? JSON.parse(String(init.body)) : undefined
    calls.push({ method, url, body })
    if (url === '/api/me') return { status: 200, body: { id: 1, email: 'budi@mail.com', name: 'Budi' } }
    if (method === 'GET') return { status: 200, body: categories }
    if (method === 'POST') {
      const created = { id: nextId++, ...body }
      categories.push(created)
      return { status: 201, body: created }
    }
    const id = Number(url.split('/').pop())
    if (method === 'PUT') {
      categories = categories.map((c) => (c.id === id ? { ...c, ...body } : c))
      return { status: 200, body: categories.find((c) => c.id === id) }
    }
    if (options.inUse?.includes(id)) return { status: 409, body: { detail: 'Kategori masih dipakai transaksi' } }
    categories = categories.filter((c) => c.id !== id)
    return { status: 204 }
  })
  return calls
}

const makan: Category = { id: 1, name: 'Makan', type: 'EXPENSE', monthlyBudget: 1_000_000 }
const transport: Category = { id: 2, name: 'Transport', type: 'EXPENSE', monthlyBudget: null }
const gaji: Category = { id: 3, name: 'Gaji', type: 'INCOME', monthlyBudget: null }

async function openPage() {
  const app = await renderApp('/categories')
  await flushPromises()
  return app
}

function addButton(wrapper: VueWrapper) {
  return wrapper.findAll('button').find((b) => b.text() === 'Tambah kategori')!
}

function section(wrapper: VueWrapper, title: string) {
  return wrapper.findAll('h2').find((h) => h.text() === title)!.element.parentElement!.textContent!
}

describe('CategoriesPage', () => {
  beforeEach(() => localStorage.setItem('expense.token', 'jwt-1'))
  afterEach(() => {
    vi.unstubAllGlobals()
    vi.restoreAllMocks()
    localStorage.clear()
    document.body.innerHTML = ''
  })

  it('groups categories by type and shows budgets', async () => {
    fakeBackend([makan, transport, gaji])
    const { wrapper } = await openPage()

    expect(section(wrapper, 'Pengeluaran')).toContain('Makan')
    expect(section(wrapper, 'Pengeluaran')).toContain('Budget Rp1.000.000')
    expect(section(wrapper, 'Pengeluaran')).toContain('Tanpa budget')
    expect(section(wrapper, 'Pemasukan')).toContain('Gaji')
    expect(section(wrapper, 'Pemasukan')).not.toContain('Makan')
  })

  it('creates a category and refreshes the list', async () => {
    const calls = fakeBackend([])
    const { wrapper } = await openPage()

    await addButton(wrapper).trigger('click')
    await wrapper.get('input[name=name]').setValue('Hobi')
    await wrapper.get('input[name=monthlyBudget]').setValue('500000')
    await wrapper.get('form').trigger('submit')
    await flushPromises()

    expect(calls.find((c) => c.method === 'POST')!.body).toEqual({ name: 'Hobi', type: 'EXPENSE', monthlyBudget: 500000 })
    expect(wrapper.find('form').exists()).toBe(false)
    expect(section(wrapper, 'Pengeluaran')).toContain('Budget Rp500.000')
  })

  it('edits without sending the type and keeps the type locked', async () => {
    const calls = fakeBackend([makan])
    const { wrapper } = await openPage()

    await wrapper.get('[aria-label="Ubah Makan"]').trigger('click')
    expect(wrapper.get('select[name=type]').attributes('disabled')).toBeDefined()
    await wrapper.get('input[name=monthlyBudget]').setValue('1500000')
    await wrapper.get('form').trigger('submit')
    await flushPromises()

    const put = calls.find((c) => c.method === 'PUT')!
    expect(put.url).toBe('/api/categories/1')
    expect(put.body).toEqual({ name: 'Makan', monthlyBudget: 1500000 })
    expect(section(wrapper, 'Pengeluaran')).toContain('Budget Rp1.500.000')
  })

  it('shows the 409 message when the category is still used', async () => {
    fakeBackend([makan], { inUse: [1] })
    vi.spyOn(window, 'confirm').mockReturnValue(true)
    const { wrapper } = await openPage()

    await wrapper.get('[aria-label="Hapus Makan"]').trigger('click')
    await flushPromises()

    expect(wrapper.get('[role=alert]').text()).toBe('Kategori masih dipakai transaksi')
    expect(section(wrapper, 'Pengeluaran')).toContain('Makan')
  })

  it('does nothing when delete is not confirmed', async () => {
    const calls = fakeBackend([makan])
    vi.spyOn(window, 'confirm').mockReturnValue(false)
    const { wrapper } = await openPage()

    await wrapper.get('[aria-label="Hapus Makan"]').trigger('click')
    await flushPromises()

    expect(calls.some((c) => c.method === 'DELETE')).toBe(false)
  })

  it('hides the budget field for income and rejects a bad budget', async () => {
    const calls = fakeBackend([])
    const { wrapper } = await openPage()

    await addButton(wrapper).trigger('click')
    await wrapper.get('input[name=name]').setValue('Makan')
    await wrapper.get('input[name=monthlyBudget]').setValue('12.5')
    await wrapper.get('form').trigger('submit')
    expect(wrapper.text()).toContain('Budget harus bilangan bulat lebih dari 0')
    expect(calls.some((c) => c.method === 'POST')).toBe(false)

    await wrapper.get('select[name=type]').setValue('INCOME')
    expect(wrapper.find('input[name=monthlyBudget]').exists()).toBe(false)
  })

  it('shows field errors from the backend', async () => {
    mockFetch((url, init) => {
      if (url === '/api/me') return { status: 200, body: { id: 1, email: 'b@mail.com', name: 'Budi' } }
      if (init.method === 'POST') {
        return { status: 409, body: { detail: 'Kategori dengan nama ini sudah ada' } }
      }
      return { status: 200, body: [] }
    })
    const { wrapper } = await openPage()

    await addButton(wrapper).trigger('click')
    await wrapper.get('input[name=name]').setValue('Makan')
    await wrapper.get('form').trigger('submit')
    await flushPromises()

    expect(wrapper.get('form [role=alert]').text()).toBe('Kategori dengan nama ini sudah ada')
    expect(wrapper.find('form').exists()).toBe(true)
  })
})
