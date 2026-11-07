<script setup lang="ts">
import { computed } from 'vue'
import type { CategorySummary } from './api'
import { formatRupiah } from '@/shared/format'

const props = defineProps<{ items: CategorySummary[] }>()
const over = computed(() => props.items.filter((item) => item.overBudget))
</script>

<template>
  <div v-if="over.length > 0" role="alert" class="space-y-1 rounded-lg border border-red-200 bg-red-50 p-4 text-sm text-red-800">
    <p class="font-semibold">Budget terlampaui</p>
    <p v-for="item in over" :key="item.categoryId" data-over-budget>
      Pengeluaran {{ item.name }} sudah {{ formatRupiah(item.spent) }}, melebihi budget {{ formatRupiah(item.budget ?? 0) }}
    </p>
  </div>
</template>
