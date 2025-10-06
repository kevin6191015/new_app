<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@app/stores/useAuthStore'
import type { LoginPayload } from '@features/auth/types'

import bg from '@assets/bg.png'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

// 帶型別有助於開發時提示
const form = reactive<LoginPayload>({ username: '', password: '' })

const loading = ref(false)
const error = ref('')
const canSubmit = computed(() => form.username.length > 0 && form.password.length > 0)

// ✅ 設定背景樣式（cover/置中/不重複）
const bgStyle = computed(() => ({
  backgroundImage: `url(${bg})`,
  backgroundSize: 'cover',
  backgroundPosition: 'center',
  backgroundRepeat: 'no-repeat'
}))

async function onSubmit() {
  if (!canSubmit.value) return
  error.value = ''
  loading.value = true
  try {
    await auth.login(form)      // form 型別已對齊 LoginPayload
    const redirect = (route.query.redirect as string) || '/choose-courses'
    router.replace(redirect)
  } catch (e: any) {
    error.value = e?.message ?? '登入失敗'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="login-page" :style="bgStyle">
    <form class="card" @submit.prevent="onSubmit" novalidate>
      <h1 class="title">系統登入</h1>

      <div class="field">
        <label for="username">帳號</label>
        <input id="username" v-model.trim="form.username" autocomplete="username" required />
      </div>

      <div class="field">
        <label for="password">密碼</label>
        <input id="password" v-model="form.password" type="password" autocomplete="current-password" required />
      </div>

      <button class="btn" type="submit" :disabled="loading || !canSubmit">
        {{ loading ? '登入中…' : '登入' }}
      </button>

      <p v-if="error" class="error" role="alert">{{ error }}</p>
    </form>
  </section>
</template>


<style scoped>
.login-page {
  display: grid;
  place-items: center;
  min-height: 100vh;
  /* 若圖片載入前的底色 */
  background-color: #f6f7fb;
  padding: 16px;
}
.login-page { display:grid; place-items:center; min-height:100vh; background:#f6f7fb; padding:16px; }
.card { width:100%; max-width:360px; background:#fff; border:1px solid #eee; border-radius:12px; padding:20px; }
.title { text-align:center; margin:0 0 16px; }
.field { display:flex; flex-direction:column; gap:6px; margin-bottom:12px; }
input { padding:8px; border:1px solid #ccc; border-radius:8px; }
.btn { width:100%; padding:10px; border:1px solid #ddd; border-radius:10px; cursor:pointer; }
.btn:disabled { opacity:.6; cursor:not-allowed; }
.error { margin-top:10px; color:#c00; }
</style>
