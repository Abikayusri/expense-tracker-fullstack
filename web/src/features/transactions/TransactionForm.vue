<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { Transaction, TransactionInput } from './api'
import type { Category } from '@/features/categories/api'
import type { ApiError } from '@/shared/http'
import { today } from '@/shared/format'
import FormField from '@/shared/ui/FormField.vue'
import ErrorAlert from '@/shared/ui/ErrorAlert.vue'
import { inputClass, primaryButtonClass, secondaryButtonClass } from '@/shared/ui/inputClass'

const MAX_NOTE = 255

const props = defineProps<{
  categories: Category[]
  initial?: Transaction
  error?: ApiError | null
  submitting?: boolean
}>()
const emit = defineEmits<{ submit: [input: TransactionInput]; cancel: [] }>()

const categoryId = ref<number | ''>(props.initial?.categoryId ?? '')
const amount = ref(props.initial?.amount.toString() ?? '')
const date = ref(props.initial?.date ?? today())
const note = ref(props.initial?.note ?? '')
const localErrors = reactive<Record<string, string | undefined>>({})

function fieldError(field: string): string | undefined {
  return localErrors[field] ?? props.error?.errors[field]
}

function submit() {
  const parsedAmount = Number(amount.value.trim())
  localErrors.categoryId = categoryId.value === '' ? 'Pilih kategori' : undefined
  localErrors.amount =
    amount.value.trim() === '' || !Number.isInteger(parsedAmount) || parsedAmount <= 0
      ? 'Jumlah harus bilangan bulat lebih dari 0'
      : undefined
  localErrors.date = date.value === '' ? 'Tanggal wajib diisi' : undefined
  localErrors.note = note.value.length > MAX_NOTE ? `Catatan maksimal ${MAX_NOTE} karakter` : undefined
  if (Object.values(localErrors).some(Boolean) || categoryId.value === '') {
    return
  }
  emit('submit', {
    categoryId: categoryId.value,
    amount: parsedAmount,
    date: date.value,
    note: note.value.trim() === '' ? null : note.value.trim(),
  })
}
</script>

<template>
  <form class="space-y-4 rounded-lg bg-white p-4 shadow" novalidate @submit.prevent="submit">
    <h2 class="font-semibold">{{ initial ? 'Ubah transaksi' : 'Transaksi baru' }}</h2>
    <ErrorAlert :message="error?.detail" />
    <FormField label="Kategori" :error="fieldError('categoryId')">
      <select v-model="categoryId" name="categoryId" :class="inputClass">
        <option value="" disabled>Pilih kategori</option>
        <option v-for="category in categories" :key="category.id" :value="category.id">
          {{ category.name }} ({{ category.type === 'INCOME' ? 'Pemasukan' : 'Pengeluaran' }})
        </option>
      </select>
    </FormField>
    <FormField label="Jumlah (Rp)" :error="fieldError('amount')">
      <input v-model="amount" name="amount" inputmode="numeric" placeholder="25000" :class="inputClass" />
    </FormField>
    <FormField label="Tanggal" :error="fieldError('date')">
      <input v-model="date" name="date" type="date" :class="inputClass" />
    </FormField>
    <FormField label="Catatan (opsional)" :error="fieldError('note')">
      <input v-model="note" name="note" :class="inputClass" />
    </FormField>
    <div class="flex gap-2">
      <button type="submit" :disabled="submitting" :class="primaryButtonClass">Simpan</button>
      <button type="button" :class="secondaryButtonClass" @click="emit('cancel')">Batal</button>
    </div>
  </form>
</template>
