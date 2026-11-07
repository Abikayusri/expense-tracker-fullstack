import { http } from '@/shared/http'
import type { CategoryType } from '@/features/categories/api'

export type CategorySummary = {
  categoryId: number
  name: string
  type: CategoryType
  spent: number
  budget: number | null
  overBudget: boolean
}

export type Summary = {
  totalIncome: number
  totalExpense: number
  balance: number
  perCategory: CategorySummary[]
}

export function getSummary(month: string): Promise<Summary> {
  return http<Summary>('/api/summary', { query: { month } })
}
