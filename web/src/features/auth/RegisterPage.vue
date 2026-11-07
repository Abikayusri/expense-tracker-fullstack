<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from './store'
import { ApiError } from '@/shared/http'
import FormField from '@/shared/ui/FormField.vue'
import ErrorAlert from '@/shared/ui/ErrorAlert.vue'
import { inputClass, primaryButtonClass } from '@/shared/ui/inputClass'

const auth = useAuthStore()
const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const error = ref<ApiError | null>(null)
const submitting = ref(false)

async function submit() {
  submitting.value = true
  error.value = null
  try {
    await auth.register({ name: name.value, email: email.value, password: password.value })
    await router.replace('/')
  } catch (e) {
    error.value = e instanceof ApiError ? e : new ApiError(0, 'Terjadi kesalahan')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <main class="mx-auto mt-16 max-w-sm space-y-6 rounded-lg bg-white p-6 shadow">
    <h1 class="text-xl font-semibold">Daftar</h1>
    <form class="space-y-4" @submit.prevent="submit">
      <ErrorAlert :message="error?.detail" />
      <FormField label="Nama" :error="error?.errors.name">
        <input v-model="name" name="name" autocomplete="name" required :class="inputClass" />
      </FormField>
      <FormField label="Email" :error="error?.errors.email">
        <input v-model="email" name="email" type="email" autocomplete="email" required :class="inputClass" />
      </FormField>
      <FormField label="Password (minimal 8 karakter)" :error="error?.errors.password">
        <input v-model="password" name="password" type="password" autocomplete="new-password" required :class="inputClass" />
      </FormField>
      <button type="submit" :disabled="submitting" :class="[primaryButtonClass, 'w-full']">Daftar</button>
    </form>
    <p class="text-sm text-slate-600">
      Sudah punya akun?
      <RouterLink to="/login" class="text-sky-700 underline">Masuk</RouterLink>
    </p>
  </main>
</template>
