<script setup lang="ts">
import { ref } from 'vue'
import type { Category, CategoryInput, CategoryType } from './api'
import type { ApiError } from '@/shared/http'
import FormField from '@/shared/ui/FormField.vue'
import ErrorAlert from '@/shared/ui/ErrorAlert.vue'
import { inputClass, primaryButtonClass, secondaryButtonClass } from '@/shared/ui/inputClass'

const props = defineProps<{ initial?: Category; error?: ApiError | null; submitting?: boolean }>()
const emit = defineEmits<{ submit: [input: CategoryInput]; cancel: [] }>()

const name = ref(props.initial?.name ?? '')
const type = ref<CategoryType>(props.initial?.type ?? 'EXPENSE')
const budget = ref(props.initial?.monthlyBudget?.toString() ?? '')
const budgetError = ref<string>()

function submit() {
  budgetError.value = undefined
  let monthlyBudget: number | null = null
  if (type.value === 'EXPENSE' && budget.value.trim() !== '') {
    const amount = Number(budget.value)
    if (!Number.isInteger(amount) || amount <= 0) {
      budgetError.value = 'Budget harus bilangan bulat lebih dari 0'
      return
    }
    monthlyBudget = amount
  }
  emit('submit', { name: name.value, type: type.value, monthlyBudget })
}
</script>

<template>
  <form class="space-y-4 rounded-lg bg-white p-4 shadow" @submit.prevent="submit">
    <h2 class="font-semibold">{{ initial ? 'Ubah kategori' : 'Kategori baru' }}</h2>
    <ErrorAlert :message="error?.detail" />
    <FormField label="Nama" :error="error?.errors.name">
      <input v-model="name" name="name" required maxlength="50" :class="inputClass" />
    </FormField>
    <FormField label="Tipe" :error="error?.errors.type">
      <select v-model="type" name="type" :disabled="!!initial" :class="inputClass">
        <option value="EXPENSE">Pengeluaran</option>
        <option value="INCOME">Pemasukan</option>
      </select>
    </FormField>
    <FormField
      v-if="type === 'EXPENSE'"
      label="Budget bulanan (kosongkan jika tidak ada)"
      :error="budgetError ?? error?.errors.monthlyBudget"
    >
      <input v-model="budget" name="monthlyBudget" inputmode="numeric" placeholder="1000000" :class="inputClass" />
    </FormField>
    <div class="flex gap-2">
      <button type="submit" :disabled="submitting" :class="primaryButtonClass">Simpan</button>
      <button type="button" :class="secondaryButtonClass" @click="emit('cancel')">Batal</button>
    </div>
  </form>
</template>
