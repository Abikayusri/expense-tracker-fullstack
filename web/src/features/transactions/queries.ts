import { toValue, type MaybeRef } from 'vue'
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import {
  createTransaction,
  deleteTransaction,
  listTransactions,
  updateTransaction,
  type TransactionFilter,
  type TransactionInput,
} from './api'

export function useTransactions(filter: MaybeRef<TransactionFilter>) {
  return useQuery({
    queryKey: ['transactions', filter],
    queryFn: () => listTransactions(toValue(filter)),
    // Keeps the current page on screen while the next one loads, so the table doesn't flicker.
    placeholderData: keepPreviousData,
  })
}

function useRefreshTransactionData() {
  const queryClient = useQueryClient()
  return () =>
    Promise.all([
      queryClient.invalidateQueries({ queryKey: ['transactions'] }),
      queryClient.invalidateQueries({ queryKey: ['summary'] }),
    ])
}

export function useCreateTransaction() {
  const refresh = useRefreshTransactionData()
  return useMutation({ mutationFn: createTransaction, onSuccess: refresh })
}

export function useUpdateTransaction() {
  const refresh = useRefreshTransactionData()
  return useMutation({
    mutationFn: ({ id, input }: { id: number; input: TransactionInput }) => updateTransaction(id, input),
    onSuccess: refresh,
  })
}

export function useDeleteTransaction() {
  const refresh = useRefreshTransactionData()
  return useMutation({ mutationFn: deleteTransaction, onSuccess: refresh })
}
