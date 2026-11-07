import { http } from '@/shared/http'

export type CategoryType = 'INCOME' | 'EXPENSE'
export type Category = { id: number; name: string; type: CategoryType; monthlyBudget: number | null }
export type CategoryInput = { name: string; type: CategoryType; monthlyBudget: number | null }
export type CategoryUpdate = { name: string; monthlyBudget: number | null }

export function listCategories(type?: CategoryType): Promise<Category[]> {
  return http<Category[]>('/api/categories', { query: { type } })
}

export function createCategory(input: CategoryInput): Promise<Category> {
  return http<Category>('/api/categories', { method: 'POST', body: input })
}

export function updateCategory(id: number, input: CategoryUpdate): Promise<Category> {
  return http<Category>(`/api/categories/${id}`, { method: 'PUT', body: input })
}

export function deleteCategory(id: number): Promise<void> {
  return http<void>(`/api/categories/${id}`, { method: 'DELETE' })
}
