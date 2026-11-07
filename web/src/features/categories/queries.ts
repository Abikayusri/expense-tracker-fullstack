import { toValue, type MaybeRef } from 'vue'
import { useMutation, useQuery, useQueryClient } from '@tanstack/vue-query'
import {
  createCategory,
  deleteCategory,
  listCategories,
  updateCategory,
  type CategoryType,
  type CategoryUpdate,
} from './api'

export function useCategories(type?: MaybeRef<CategoryType | undefined>) {
  return useQuery({
    queryKey: ['categories', type],
    queryFn: () => listCategories(toValue(type)),
  })
}

// Category changes also change the dashboard (names, budgets), so both caches are refreshed.
function useRefreshCategoryData() {
  const queryClient = useQueryClient()
  return () =>
    Promise.all([
      queryClient.invalidateQueries({ queryKey: ['categories'] }),
      queryClient.invalidateQueries({ queryKey: ['summary'] }),
    ])
}

export function useCreateCategory() {
  const refresh = useRefreshCategoryData()
  return useMutation({ mutationFn: createCategory, onSuccess: refresh })
}

export function useUpdateCategory() {
  const refresh = useRefreshCategoryData()
  return useMutation({
    mutationFn: ({ id, input }: { id: number; input: CategoryUpdate }) => updateCategory(id, input),
    onSuccess: refresh,
  })
}

export function useDeleteCategory() {
  const refresh = useRefreshCategoryData()
  return useMutation({ mutationFn: deleteCategory, onSuccess: refresh })
}
