<script setup lang="ts">
defineProps<{ title: string }>()
</script>

<template>
  <section>
    <h1 class="text-xl font-semibold">{{ title }}</h1>
  </section>
</template>
