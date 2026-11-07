<script setup lang="ts">
import { computed } from 'vue'
import type { CategorySummary } from './api'
import { formatRupiah } from '@/shared/format'

const props = defineProps<{ item: CategorySummary }>()

const percent = computed(() => {
  const budget = props.item.budget ?? 0
  return budget > 0 ? Math.round(Math.min(props.item.spent / budget, 1) * 100) : 0
})
</script>

<template>
  <div class="space-y-1">
    <div class="flex justify-between text-sm">
      <span class="font-medium">{{ item.name }}</span>
      <span class="text-slate-600">{{ formatRupiah(item.spent) }} / {{ formatRupiah(item.budget ?? 0) }}</span>
    </div>
    <div
      role="progressbar"
      :aria-label="`Pemakaian budget ${item.name}`"
      aria-valuemin="0"
      aria-valuemax="100"
      :aria-valuenow="percent"
      class="h-2 overflow-hidden rounded-full bg-slate-200"
    >
      <div class="h-full rounded-full" :class="item.overBudget ? 'bg-red-600' : 'bg-sky-600'" :style="{ width: `${percent}%` }" />
    </div>
  </div>
</template>
