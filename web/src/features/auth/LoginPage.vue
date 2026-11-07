<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './store'
import { ApiError } from '@/shared/http'
import { safeRedirect } from '@/shared/redirect'
import FormField from '@/shared/ui/FormField.vue'
import ErrorAlert from '@/shared/ui/ErrorAlert.vue'
import { inputClass, primaryButtonClass } from '@/shared/ui/inputClass'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const email = ref('')
const password = ref('')
const error = ref<ApiError | null>(null)
const submitting = ref(false)

async function submit() {
  submitting.value = true
  error.value = null
  try {
    await auth.login(email.value, password.value)
    await router.replace(safeRedirect(route.query.redirect))
  } catch (e) {
    error.value = e instanceof ApiError ? e : new ApiError(0, 'Terjadi kesalahan')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="mx-auto mt-16 max-w-sm space-y-6 rounded-lg bg-white p-6 shadow">
    <h1 class="text-xl font-semibold">Masuk</h1>
    <form class="space-y-4" @submit.prevent="submit">
      <ErrorAlert :message="error?.detail" />
      <FormField label="Email" :error="error?.errors.email">
        <input v-model="email" name="email" type="email" autocomplete="email" required :class="inputClass" />
      </FormField>
      <FormField label="Password" :error="error?.errors.password">
        <input v-model="password" name="password" type="password" autocomplete="current-password" required :class="inputClass" />
      </FormField>
      <button type="submit" :disabled="submitting" :class="[primaryButtonClass, 'w-full']">Masuk</button>
    </form>
    <p class="text-sm text-slate-600">
      Belum punya akun?
      <RouterLink to="/register" class="text-sky-700 underline">Daftar</RouterLink>
    </p>
  </main>
</template>
