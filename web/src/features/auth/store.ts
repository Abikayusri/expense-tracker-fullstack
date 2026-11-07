import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import * as api from './api'

const TOKEN_KEY = 'expense.token'

// Storage can be unavailable (private mode, blocked site data); the app then just keeps the token in memory.
function readToken(): string | null {
  try {
    return localStorage.getItem(TOKEN_KEY)
  } catch {
    return null
  }
}

function writeToken(token: string | null): void {
  try {
    if (token === null) {
      localStorage.removeItem(TOKEN_KEY)
    } else {
      localStorage.setItem(TOKEN_KEY, token)
    }
  } catch {
    // Ignored on purpose, see readToken.
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(readToken())
  const isLoggedIn = computed(() => token.value !== null)

  function setToken(next: string | null) {
    token.value = next
    writeToken(next)
  }

  async function login(email: string, password: string) {
    setToken((await api.login(email, password)).token)
  }

  async function register(input: api.RegisterInput) {
    setToken((await api.register(input)).token)
  }

  function logout() {
    setToken(null)
  }

  return { token, isLoggedIn, login, register, logout }
})
