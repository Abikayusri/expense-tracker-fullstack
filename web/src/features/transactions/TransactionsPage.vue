<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'
import TransactionForm from './TransactionForm.vue'
import type { Transaction, TransactionFilter, TransactionInput } from './api'
import { useCreateTransaction, useDeleteTransaction, useTransactions, useUpdateTransaction } from './queries'
import { useCategories } from '@/features/categories/queries'
import { ApiError } from '@/shared/http'
import { currentMonth, formatDate, formatRupiah } from '@/shared/format'
import ErrorAlert from '@/shared/ui/ErrorAlert.vue'
import MonthPicker from '@/shared/ui/MonthPicker.vue'
import { inputClass, primaryButtonClass, secondaryButtonClass } from '@/shared/ui/inputClass'

const PAGE_SIZE = 20

const month = ref(currentMonth())
const categoryId = ref<number | undefined>(undefined)
const page = ref(0)

watch([month, categoryId], () => {
  page.value = 0
})

const filter = computed<TransactionFilter>(() => ({
  month: month.value,
  categoryId: categoryId.value,
  page: page.value,
  size: PAGE_SIZE,
}))

const transactions = useTransactions(filter)
const categories = useCategories()
const createMutation = useCreateTransaction()
const updateMutation = useUpdateTransaction()
const deleteMutation = useDeleteTransaction()

const categoryById = computed(() => new Map((categories.data.value ?? []).map((c) => [c.id, c])))
const rows = computed(() => transactions.data.value?.content ?? [])
const totalElements = computed(() => transactions.data.value?.totalElements ?? 0)
const hasPrevious = computed(() => page.value > 0)
const hasNext = computed(() => (page.value + 1) * PAGE_SIZE < totalElements.value)

// undefined: form closed, null: creating, Transaction: editing it.
const editing = ref<Transaction | null | undefined>(undefined)
const formError = ref<ApiError | null>(null)
const listError = ref<string>()

function toApiError(e: unknown): ApiError {
  return e instanceof ApiError ? e : new ApiError(0, 'Terjadi kesalahan')
}

function openForm(transaction: Transaction | null) {
  formError.value = null
  listError.value = undefined
  editing.value = transaction
}

async function save(input: TransactionInput) {
  formError.value = null
  try {
    if (editing.value) {
      await updateMutation.mutateAsync({ id: editing.value.id, input })
    } else {
      await createMutation.mutateAsync(input)
    }
    editing.value = undefined
  } catch (e) {
    formError.value = toApiError(e)
  }
}

async function remove(transaction: Transaction) {
  listError.value = undefined
  if (!window.confirm(`Hapus transaksi ${formatRupiah(transaction.amount)}?`)) {
    return
  }
  try {
    await deleteMutation.mutateAsync(transaction.id)
  } catch (e) {
    listError.value = toApiError(e).detail
  }
}

function signedAmount(transaction: Transaction): { text: string; income: boolean } {
  const income = categoryById.value.get(transaction.categoryId)?.type === 'INCOME'
  return { text: formatRupiah(income ? transaction.amount : -transaction.amount), income }
}
</script>

<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-center gap-3">
      <h1 class="mr-auto text-xl font-semibold">Transaksi</h1>
      <button v-if="editing === undefined" type="button" :class="primaryButtonClass" @click="openForm(null)">
        Tambah transaksi
      </button>
    </div>

    <p v-if="categories.data.value?.length === 0" class="text-sm text-slate-600">
      Buat kategori dulu sebelum mencatat transaksi.
      <RouterLink to="/categories" class="text-sky-700 underline">Ke halaman kategori</RouterLink>
    </p>

    <TransactionForm
      v-if="editing !== undefined"
      :key="editing?.id ?? 'new'"
      :categories="categories.data.value ?? []"
      :initial="editing ?? undefined"
      :error="formError"
      :submitting="createMutation.isPending.value || updateMutation.isPending.value"
      @submit="save"
      @cancel="editing = undefined"
    />

    <div class="flex flex-wrap items-center gap-4">
      <MonthPicker v-model="month" />
      <label class="inline-flex items-center gap-2 text-sm">
        <span class="text-slate-600">Kategori</span>
        <select v-model="categoryId" name="filterCategory" :class="[inputClass, 'w-auto']">
          <option :value="undefined">Semua</option>
          <option v-for="category in categories.data.value ?? []" :key="category.id" :value="category.id">
            {{ category.name }}
          </option>
        </select>
      </label>
    </div>

    <ErrorAlert :message="listError ?? transactions.error.value?.message" />

    <div class="overflow-x-auto rounded-lg bg-white shadow">
      <table class="w-full text-left text-sm">
        <thead class="border-b border-slate-200 text-slate-500">
          <tr>
            <th class="px-4 py-2 font-medium">Tanggal</th>
            <th class="px-4 py-2 font-medium">Kategori</th>
            <th class="px-4 py-2 font-medium">Catatan</th>
            <th class="px-4 py-2 text-right font-medium">Jumlah</th>
            <th class="px-4 py-2"><span class="sr-only">Aksi</span></th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
          <tr v-for="transaction in rows" :key="transaction.id" data-row>
            <td class="whitespace-nowrap px-4 py-2">{{ formatDate(transaction.date) }}</td>
            <td class="px-4 py-2">{{ categoryById.get(transaction.categoryId)?.name ?? '-' }}</td>
            <td class="px-4 py-2 text-slate-600">{{ transaction.note ?? '' }}</td>
            <td
              class="whitespace-nowrap px-4 py-2 text-right font-medium"
              :class="signedAmount(transaction).income ? 'text-emerald-700' : 'text-red-700'"
            >
              {{ signedAmount(transaction).text }}
            </td>
            <td class="whitespace-nowrap px-4 py-2 text-right">
              <button
                type="button"
                class="mr-3 text-sky-700 underline"
                :aria-label="`Ubah transaksi ${transaction.id}`"
                @click="openForm(transaction)"
              >
                Ubah
              </button>
              <button
                type="button"
                class="text-red-700 underline"
                :aria-label="`Hapus transaksi ${transaction.id}`"
                @click="remove(transaction)"
              >
                Hapus
              </button>
            </td>
          </tr>
          <tr v-if="!transactions.isPending.value && rows.length === 0">
            <td colspan="5" class="px-4 py-6 text-center text-slate-500">Belum ada transaksi di bulan ini.</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="flex items-center justify-between text-sm text-slate-600">
      <span>{{ totalElements }} transaksi</span>
      <div class="flex gap-2">
        <button type="button" :disabled="!hasPrevious" :class="secondaryButtonClass" @click="page--">Sebelumnya</button>
        <button type="button" :disabled="!hasNext" :class="secondaryButtonClass" @click="page++">Berikutnya</button>
      </div>
    </div>
  </section>
</template>
