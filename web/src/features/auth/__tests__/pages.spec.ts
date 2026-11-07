import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { flushPromises } from '@vue/test-utils'
import { renderApp } from '@/test/render'
import { mockFetch } from '@/test/mockFetch'

const ME = { id: 1, email: 'budi@mail.com', name: 'Budi' }

describe('auth pages', () => {
  beforeEach(() => localStorage.clear())
  afterEach(() => {
    vi.unstubAllGlobals()
    document.body.innerHTML = ''
  })

  it('logs in and returns to the requested page', async () => {
    const fetch = mockFetch((url) =>
      url === '/api/auth/login' ? { status: 200, body: { token: 'jwt-1' } } : { status: 200, body: ME },
    )
    const { wrapper, router } = await renderApp('/login?redirect=/transactions')

    await wrapper.find('input[name=email]').setValue('budi@mail.com')
    await wrapper.find('input[name=password]').setValue('rahasia123')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(router.currentRoute.value.fullPath).toBe('/transactions')
    expect(fetch.mock.calls[0]![0]).toBe('/api/auth/login')
    await vi.waitFor(() => expect(wrapper.text()).toContain('Budi'))
  })

  it('shows the backend message when login fails', async () => {
    mockFetch(() => ({ status: 401, body: { detail: 'Email atau password salah' } }))
    const { wrapper, router } = await renderApp('/login')

    await wrapper.find('input[name=email]').setValue('budi@mail.com')
    await wrapper.find('input[name=password]').setValue('salah12345')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(wrapper.get('[role=alert]').text()).toBe('Email atau password salah')
    expect(router.currentRoute.value.path).toBe('/login')
  })

  it('shows field errors next to the register inputs', async () => {
    mockFetch(() => ({
      status: 400,
      body: { detail: 'Input tidak valid', errors: { password: 'size must be between 8 and 72' } },
    }))
    const { wrapper } = await renderApp('/register')

    await wrapper.find('input[name=name]').setValue('Budi')
    await wrapper.find('input[name=email]').setValue('budi@mail.com')
    await wrapper.find('input[name=password]').setValue('1234567')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    const passwordField = wrapper.find('input[name=password]').element.closest('label')!
    expect(passwordField.textContent).toContain('size must be between 8 and 72')
    expect(wrapper.get('[role=alert]').text()).toBe('Input tidak valid')
  })

  it('registers and goes to the dashboard', async () => {
    mockFetch((url) =>
      url === '/api/auth/register' ? { status: 201, body: { token: 'jwt-2' } } : { status: 200, body: ME },
    )
    const { wrapper, router } = await renderApp('/register')

    await wrapper.find('input[name=name]').setValue('Budi')
    await wrapper.find('input[name=email]').setValue('budi@mail.com')
    await wrapper.find('input[name=password]').setValue('rahasia123')
    await wrapper.find('form').trigger('submit')
    await flushPromises()

    expect(router.currentRoute.value.path).toBe('/')
    expect(localStorage.getItem('expense.token')).toBe('jwt-2')
  })
})
