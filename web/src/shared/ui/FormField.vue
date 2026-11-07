<script setup lang="ts">
defineProps<{ label: string; error?: string }>()
</script>

<template>
  <label class="block space-y-1">
    <span class="text-sm font-medium text-slate-700">{{ label }}</span>
    <slot />
    <p v-if="error" class="text-sm text-red-600" data-field-error>{{ error }}</p>
  </label>
</template>
