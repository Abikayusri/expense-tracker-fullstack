<script setup lang="ts">
import { computed, ref } from 'vue'
import CategoryForm from './CategoryForm.vue'
import type { Category, CategoryInput } from './api'
import { useCategories, useCreateCategory, useDeleteCategory, useUpdateCategory } from './queries'
import { ApiError } from '@/shared/http'
import { formatRupiah } from '@/shared/format'
import ErrorAlert from '@/shared/ui/ErrorAlert.vue'
import { primaryButtonClass } from '@/shared/ui/inputClass'

const categories = useCategories()
const createMutation = useCreateCategory()
const updateMutation = useUpdateCategory()
const deleteMutation = useDeleteCategory()

// undefined: form closed, null: creating, Category: editing that category.
const editing = ref<Category | null | undefined>(undefined)
const formError = ref<ApiError | null>(null)
const listError = ref<string>()

const groups = computed(() => {
  const all = categories.data.value ?? []
  return [
    { title: 'Pengeluaran', items: all.filter((c) => c.type === 'EXPENSE') },
    { title: 'Pemasukan', items: all.filter((c) => c.type === 'INCOME') },
  ]
})

function toApiError(e: unknown): ApiError {
  return e instanceof ApiError ? e : new ApiError(0, 'Terjadi kesalahan')
}

function openForm(category: Category | null) {
  formError.value = null
  listError.value = undefined
  editing.value = category
}

async function save(input: CategoryInput) {
  formError.value = null
  try {
    if (editing.value) {
      await updateMutation.mutateAsync({
        id: editing.value.id,
        input: { name: input.name, monthlyBudget: input.monthlyBudget },
      })
    } else {
      await createMutation.mutateAsync(input)
    }
    editing.value = undefined
  } catch (e) {
    formError.value = toApiError(e)
  }
}

async function remove(category: Category) {
  listError.value = undefined
  if (!window.confirm(`Hapus kategori ${category.name}?`)) {
    return
  }
  try {
    await deleteMutation.mutateAsync(category.id)
  } catch (e) {
    listError.value = toApiError(e).detail
  }
}
</script>

<template>
  <section class="space-y-6">
    <div class="flex items-center justify-between">
      <h1 class="text-xl font-semibold">Kategori</h1>
      <button v-if="editing === undefined" type="button" :class="primaryButtonClass" @click="openForm(null)">
        Tambah kategori
      </button>
    </div>

    <CategoryForm
      v-if="editing !== undefined"
      :key="editing?.id ?? 'new'"
      :initial="editing ?? undefined"
      :error="formError"
      :submitting="createMutation.isPending.value || updateMutation.isPending.value"
      @submit="save"
      @cancel="editing = undefined"
    />

    <ErrorAlert :message="listError ?? categories.error.value?.message" />
    <p v-if="categories.isPending.value" class="text-sm text-slate-500">Memuat...</p>

    <div v-for="group in groups" :key="group.title" class="space-y-2">
      <h2 class="text-sm font-semibold uppercase tracking-wide text-slate-500">{{ group.title }}</h2>
      <p v-if="!categories.isPending.value && group.items.length === 0" class="text-sm text-slate-500">
        Belum ada kategori.
      </p>
      <ul class="divide-y divide-slate-100 rounded-lg bg-white shadow">
        <li v-for="category in group.items" :key="category.id" class="flex items-center gap-3 px-4 py-3">
          <span class="font-medium">{{ category.name }}</span>
          <span v-if="category.type === 'EXPENSE'" class="text-sm text-slate-500">
            {{ category.monthlyBudget === null ? 'Tanpa budget' : `Budget ${formatRupiah(category.monthlyBudget)}` }}
          </span>
          <button
            type="button"
            class="ml-auto text-sm text-sky-700 underline"
            :aria-label="`Ubah ${category.name}`"
            @click="openForm(category)"
          >
            Ubah
          </button>
          <button
            type="button"
            class="text-sm text-red-700 underline"
            :aria-label="`Hapus ${category.name}`"
            @click="remove(category)"
          >
            Hapus
          </button>
        </li>
      </ul>
    </div>
  </section>
</template>
