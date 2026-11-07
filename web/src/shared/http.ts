export class ApiError extends Error {
  readonly status: number
  readonly detail: string
  readonly errors: Record<string, string>

  constructor(status: number, detail: string, errors: Record<string, string> = {}) {
    super(detail)
    this.name = 'ApiError'
    this.status = status
    this.detail = detail
    this.errors = errors
  }
}

type HttpOptions = { getToken: () => string | null; onUnauthorized: () => void }

type RequestOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  body?: unknown
  query?: Record<string, string | number | undefined>
}

let options: HttpOptions = { getToken: () => null, onUnauthorized: () => {} }

// Injected from main.ts so shared code never imports the auth feature.
export function configureHttp(next: HttpOptions): void {
  options = next
}

export async function http<T>(path: string, init: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = {}
  const token = options.getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }
  if (init.body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  let response: Response
  try {
    response = await fetch(withQuery(path, init.query), {
      method: init.method ?? 'GET',
      headers,
      body: init.body === undefined ? undefined : JSON.stringify(init.body),
    })
  } catch {
    throw new ApiError(0, 'Tidak bisa terhubung ke server')
  }

  if (response.status === 401 && !path.startsWith('/api/auth/')) {
    options.onUnauthorized()
  }
  if (!response.ok) {
    throw await toApiError(response)
  }
  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}

function withQuery(path: string, query: RequestOptions['query']): string {
  if (!query) {
    return path
  }
  const params = new URLSearchParams()
  for (const [key, value] of Object.entries(query)) {
    if (value !== undefined) {
      params.set(key, String(value))
    }
  }
  const qs = params.toString()
  return qs ? `${path}?${qs}` : path
}

async function toApiError(response: Response): Promise<ApiError> {
  try {
    const problem = (await response.json()) as { detail?: string; errors?: Record<string, string> }
    return new ApiError(response.status, problem.detail ?? 'Terjadi kesalahan', problem.errors ?? {})
  } catch {
    return new ApiError(response.status, `Terjadi kesalahan (${response.status})`)
  }
}
