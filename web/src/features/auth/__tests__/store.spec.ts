import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../store'
import { mockFetch } from '@/test/mockFetch'
import { ApiError } from '@/shared/http'

describe('auth store', () => {
  beforeEach(() => {
    localStorage.clear()
    setActivePinia(createPinia())
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    vi.restoreAllMocks()
  })

  it('stores the token after login', async () => {
    const fetch = mockFetch(() => ({ status: 200, body: { token: 'jwt-1' } }))
    const auth = useAuthStore()

    await auth.login('budi@mail.com', 'rahasia123')

    expect(auth.token).toBe('jwt-1')
    expect(auth.isLoggedIn).toBe(true)
    expect(localStorage.getItem('expense.token')).toBe('jwt-1')
    expect(fetch.mock.calls[0]![1]?.body).toBe('{"email":"budi@mail.com","password":"rahasia123"}')
  })

  it('stores the token after register', async () => {
    mockFetch(() => ({ status: 201, body: { token: 'jwt-2' } }))
    const auth = useAuthStore()

    await auth.register({ email: 'budi@mail.com', password: 'rahasia123', name: 'Budi' })

    expect(auth.token).toBe('jwt-2')
  })

  it('keeps no token when login fails', async () => {
    mockFetch(() => ({ status: 401, body: { detail: 'Email atau password salah' } }))
    const auth = useAuthStore()

    await expect(auth.login('budi@mail.com', 'salah')).rejects.toBeInstanceOf(ApiError)

    expect(auth.token).toBeNull()
    expect(localStorage.getItem('expense.token')).toBeNull()
  })

  it('restores the token from storage', () => {
    localStorage.setItem('expense.token', 'saved')

    expect(useAuthStore().isLoggedIn).toBe(true)
  })

  it('logout clears the token', () => {
    localStorage.setItem('expense.token', 'saved')
    const auth = useAuthStore()

    auth.logout()

    expect(auth.token).toBeNull()
    expect(localStorage.getItem('expense.token')).toBeNull()
  })

  it('survives unavailable storage', async () => {
    vi.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
      throw new DOMException('blocked', 'SecurityError')
    })
    vi.spyOn(Storage.prototype, 'setItem').mockImplementation(() => {
      throw new DOMException('blocked', 'SecurityError')
    })
    mockFetch(() => ({ status: 200, body: { token: 'jwt-3' } }))

    const auth = useAuthStore()
    expect(auth.token).toBeNull()

    await auth.login('budi@mail.com', 'rahasia123')
    expect(auth.token).toBe('jwt-3')
  })
})
