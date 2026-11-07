<script setup lang="ts">
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useQuery, useQueryClient } from '@tanstack/vue-query'
import { useAuthStore } from '@/features/auth/store'
import { fetchMe } from '@/features/auth/api'

const auth = useAuthStore()
const router = useRouter()
const queryClient = useQueryClient()
const me = useQuery({ queryKey: ['me'], queryFn: fetchMe })

async function logout() {
  auth.logout()
  queryClient.clear()
  await router.push('/login')
}

const links = [
  { to: '/', label: 'Dashboard' },
  { to: '/transactions', label: 'Transaksi' },
  { to: '/categories', label: 'Kategori' },
]
</script>

<template>
  <div>
    <header class="border-b border-slate-200 bg-white">
      <nav class="mx-auto flex max-w-4xl flex-wrap items-center gap-4 px-4 py-3">
        <span class="font-semibold text-sky-700">Expense Tracker</span>
        <RouterLink
          v-for="link in links"
          :key="link.to"
          :to="link.to"
          class="text-sm text-slate-600 hover:text-slate-900"
          exact-active-class="font-semibold text-slate-900"
        >
          {{ link.label }}
        </RouterLink>
        <span class="ml-auto text-sm text-slate-500">{{ me.data.value?.name }}</span>
        <button type="button" class="text-sm text-slate-600 underline" @click="logout">Keluar</button>
      </nav>
    </header>
    <main class="mx-auto max-w-4xl px-4 py-6">
      <RouterView />
    </main>
  </div>
</template>
