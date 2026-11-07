import { vi, type Mock } from 'vitest'

export type MockReply = { status: number; body?: unknown }

/** Replaces global fetch; the handler decides the reply for each request. */
export function mockFetch(handler: (url: string, init: RequestInit) => MockReply): Mock<typeof fetch> {
  const fn = vi.fn<typeof fetch>(async (input, init = {}) => {
    const { status, body } = handler(String(input), init)
    if (body === undefined) {
      return new Response(null, { status })
    }
    const isProblem = status >= 400
    return new Response(JSON.stringify(body), {
      status,
      headers: { 'Content-Type': isProblem ? 'application/problem+json' : 'application/json' },
    })
  })
  vi.stubGlobal('fetch', fn)
  return fn
}
