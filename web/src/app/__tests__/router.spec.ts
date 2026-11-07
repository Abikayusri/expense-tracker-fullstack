import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { renderApp } from '@/test/render'
import { mockFetch } from '@/test/mockFetch'

const ME = { id: 1, email: 'budi@mail.com', name: 'Budi' }
const EMPTY_SUMMARY = { totalIncome: 0, totalExpense: 0, balance: 0, perCategory: [] }

function loggedInBackend() {
  return mockFetch((url) => ({ status: 200, body: url === '/api/me' ? ME : EMPTY_SUMMARY }))
}

describe('routing and session', () => {
  beforeEach(() => localStorage.clear())
  afterEach(() => {
    vi.unstubAllGlobals()
    document.body.innerHTML = ''
  })

  it('sends logged-out users to login with a redirect', async () => {
    mockFetch(() => ({ status: 200, body: {} }))
    const { router } = await renderApp('/transactions')

    expect(router.currentRoute.value.fullPath).toBe('/login?redirect=/transactions')
  })

  it('sends logged-in users away from login', async () => {
    localStorage.setItem('expense.token', 'jwt-1')
    loggedInBackend()
    const { router } = await renderApp('/login')

    expect(router.currentRoute.value.path).toBe('/')
  })

  it('logs out and redirects when the token is rejected mid-session', async () => {
    localStorage.setItem('expense.token', 'expired')
    mockFetch(() => ({ status: 401, body: { detail: 'Token tidak ada, tidak valid, atau kedaluwarsa' } }))
    const { router } = await renderApp('/categories')

    await flushPromises()
    await vi.waitFor(() => expect(router.currentRoute.value.fullPath).toBe('/login?redirect=/categories'))
    expect(localStorage.getItem('expense.token')).toBeNull()
  })

  it('logout button clears the session', async () => {
    localStorage.setItem('expense.token', 'jwt-1')
    loggedInBackend()
    const { wrapper, router } = await renderApp('/')

    await wrapper.findAll('button').find((b) => b.text() === 'Keluar')!.trigger('click')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/login')
    expect(localStorage.getItem('expense.token')).toBeNull()
  })
})
