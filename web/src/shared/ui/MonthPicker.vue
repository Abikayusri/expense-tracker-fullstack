<script setup lang="ts">
import { inputClass } from './inputClass'

const month = defineModel<string>({ required: true })
defineProps<{ label?: string }>()
</script>

<template>
  <label class="inline-flex items-center gap-2 text-sm">
    <span class="text-slate-600">{{ label ?? 'Bulan' }}</span>
    <input v-model="month" name="month" type="month" required :class="[inputClass, 'w-auto']" />
  </label>
</template>
