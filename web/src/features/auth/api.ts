import { http } from '@/shared/http'

export type Me = { id: number; email: string; name: string }
export type RegisterInput = { email: string; password: string; name: string }
type TokenResponse = { token: string }

export function login(email: string, password: string): Promise<TokenResponse> {
  return http<TokenResponse>('/api/auth/login', { method: 'POST', body: { email, password } })
}

export function register(input: RegisterInput): Promise<TokenResponse> {
  return http<TokenResponse>('/api/auth/register', { method: 'POST', body: input })
}

export function fetchMe(): Promise<Me> {
  return http<Me>('/api/me')
}
