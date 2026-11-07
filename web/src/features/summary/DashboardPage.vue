<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'
import OverBudgetBanner from './OverBudgetBanner.vue'
import BudgetProgress from './BudgetProgress.vue'
import { useSummary } from './queries'
import { currentMonth, formatRupiah } from '@/shared/format'
import ErrorAlert from '@/shared/ui/ErrorAlert.vue'
import MonthPicker from '@/shared/ui/MonthPicker.vue'

const month = ref(currentMonth())
const summary = useSummary(month)

const data = computed(() => summary.data.value)
const budgets = computed(() =>
  (data.value?.perCategory ?? []).filter((item) => item.type === 'EXPENSE' && item.budget !== null),
)
const cards = computed(() => [
  { label: 'Pemasukan', value: data.value?.totalIncome ?? 0, tone: 'text-emerald-700', testId: 'income' },
  { label: 'Pengeluaran', value: data.value?.totalExpense ?? 0, tone: 'text-red-700', testId: 'expense' },
  {
    label: 'Saldo',
    value: data.value?.balance ?? 0,
    tone: (data.value?.balance ?? 0) < 0 ? 'text-red-700' : 'text-slate-900',
    testId: 'balance',
  },
])
</script>

<template>
  <section class="space-y-6">
    <div class="flex flex-wrap items-center gap-3">
      <h1 class="mr-auto text-xl font-semibold">Dashboard</h1>
      <MonthPicker v-model="month" />
    </div>

    <ErrorAlert :message="summary.error.value?.message" />

    <div class="grid gap-4 sm:grid-cols-3">
      <div v-for="card in cards" :key="card.label" class="rounded-lg bg-white p-4 shadow">
        <p class="text-sm text-slate-500">{{ card.label }}</p>
        <p class="text-2xl font-semibold" :class="card.tone" :data-testid="card.testId">{{ formatRupiah(card.value) }}</p>
      </div>
    </div>

    <OverBudgetBanner :items="data?.perCategory ?? []" />

    <p v-if="data && data.perCategory.length === 0" class="text-sm text-slate-600">
      Belum ada kategori.
      <RouterLink to="/categories" class="text-sky-700 underline">Buat kategori dulu</RouterLink>
    </p>

    <div v-if="budgets.length > 0" class="space-y-4 rounded-lg bg-white p-4 shadow">
      <h2 class="font-semibold">Budget bulan ini</h2>
      <BudgetProgress v-for="item in budgets" :key="item.categoryId" :item="item" />
    </div>

    <RouterLink to="/transactions" class="inline-block text-sm text-sky-700 underline">Catat transaksi</RouterLink>
  </section>
</template>
