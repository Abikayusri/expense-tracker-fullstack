import { http } from '@/shared/http'

export type Transaction = { id: number; categoryId: number; amount: number; date: string; note: string | null }
export type TransactionInput = { categoryId: number; amount: number; date: string; note: string | null }
export type Page<T> = { content: T[]; page: number; size: number; totalElements: number }
export type TransactionFilter = { month: string; categoryId?: number; page: number; size: number }

export function listTransactions(filter: TransactionFilter): Promise<Page<Transaction>> {
  return http<Page<Transaction>>('/api/transactions', { query: { ...filter } })
}

export function createTransaction(input: TransactionInput): Promise<Transaction> {
  return http<Transaction>('/api/transactions', { method: 'POST', body: input })
}

export function updateTransaction(id: number, input: TransactionInput): Promise<Transaction> {
  return http<Transaction>(`/api/transactions/${id}`, { method: 'PUT', body: input })
}

export function deleteTransaction(id: number): Promise<void> {
  return http<void>(`/api/transactions/${id}`, { method: 'DELETE' })
}
