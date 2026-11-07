import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { ApiError, configureHttp, http } from '../http'
import { mockFetch } from '@/test/mockFetch'

describe('http', () => {
  const onUnauthorized = vi.fn<() => void>()
  let token: string | null

  beforeEach(() => {
    token = 'jwt-123'
    onUnauthorized.mockReset()
    configureHttp({ getToken: () => token, onUnauthorized })
  })

  afterEach(() => {
    vi.unstubAllGlobals()
  })

  it('sends the bearer token and JSON body', async () => {
    const fetch = mockFetch(() => ({ status: 201, body: { id: 1 } }))

    const result = await http<{ id: number }>('/api/categories', { method: 'POST', body: { name: 'Makan' } })

    expect(result).toEqual({ id: 1 })
    const [url, init = {}] = fetch.mock.calls[0]!
    expect(url).toBe('/api/categories')
    expect(init.method).toBe('POST')
    expect(init.headers).toEqual({ Authorization: 'Bearer jwt-123', 'Content-Type': 'application/json' })
    expect(init.body).toBe('{"name":"Makan"}')
  })

  it('omits the Authorization header without a token', async () => {
    token = null
    const fetch = mockFetch(() => ({ status: 200, body: [] }))

    await http('/api/categories')

    expect(fetch.mock.calls[0]![1]?.headers).toEqual({})
  })

  it('drops undefined query values', async () => {
    const fetch = mockFetch(() => ({ status: 200, body: {} }))

    await http('/api/transactions', { query: { month: '2026-11', categoryId: undefined, page: 0 } })

    expect(fetch.mock.calls[0]![0]).toBe('/api/transactions?month=2026-11&page=0')
  })

  it('returns undefined for 204', async () => {
    mockFetch(() => ({ status: 204 }))

    await expect(http('/api/categories/1', { method: 'DELETE' })).resolves.toBeUndefined()
  })

  it('turns ProblemDetail into ApiError', async () => {
    mockFetch(() => ({ status: 409, body: { status: 409, detail: 'Kategori masih dipakai transaksi' } }))

    const error = await http('/api/categories/1', { method: 'DELETE' }).catch((e) => e)

    expect(error).toBeInstanceOf(ApiError)
    expect(error).toMatchObject({ status: 409, detail: 'Kategori masih dipakai transaksi', errors: {} })
  })

  it('exposes field errors', async () => {
    mockFetch(() => ({ status: 400, body: { detail: 'Input tidak valid', errors: { password: 'size must be between 8 and 72' } } }))

    const error = (await http('/api/auth/register', { method: 'POST', body: {} }).catch((e) => e)) as ApiError

    expect(error.errors.password).toBe('size must be between 8 and 72')
  })

  it('handles a non-JSON error body', async () => {
    vi.stubGlobal('fetch', vi.fn(async () => new Response('Bad Gateway', { status: 502 })))

    const error = (await http('/api/me').catch((e) => e)) as ApiError

    expect(error.detail).toBe('Terjadi kesalahan (502)')
  })

  it('calls onUnauthorized for 401 outside auth endpoints', async () => {
    mockFetch(() => ({ status: 401, body: { detail: 'Token tidak ada, tidak valid, atau kedaluwarsa' } }))

    await expect(http('/api/me')).rejects.toBeInstanceOf(ApiError)

    expect(onUnauthorized).toHaveBeenCalledTimes(1)
  })

  it('does not call onUnauthorized for a failed login', async () => {
    mockFetch(() => ({ status: 401, body: { detail: 'Email atau password salah' } }))

    await expect(http('/api/auth/login', { method: 'POST', body: {} })).rejects.toMatchObject({
      status: 401,
      detail: 'Email atau password salah',
    })
    expect(onUnauthorized).not.toHaveBeenCalled()
  })

  it('reports network failures as status 0', async () => {
    vi.stubGlobal('fetch', vi.fn(async () => {
      throw new TypeError('Failed to fetch')
    }))

    await expect(http('/api/me')).rejects.toMatchObject({ status: 0, detail: 'Tidak bisa terhubung ke server' })
  })
})
