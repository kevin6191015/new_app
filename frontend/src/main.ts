// src/main.ts
import { createApp } from 'vue'
import App from './App.vue'
import router from './app/router'
import { createPinia } from 'pinia'
import './app/styles/global.scss'
import http from '@shared/api/http'



const app = createApp(App)
const pinia = createPinia()

app.use(pinia)

// ⬇️ 在這裡引入需要還原的 stores
import { useAuthStore } from '@app/stores/useAuthStore'
// （如果你的課程選擇也有做持久化，順便還原）
import { useCourseStore } from '@app/stores/useCourseStore'

// ✅ 先還原 store（從 localStorage 讀回 token/user/roleCode 等）

useAuthStore(pinia).hydrate?.()
useCourseStore(pinia).hydrate?.()

// 只在開發且設定了旗標時載入 Mock
if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK === '1') {
  const { setupMock } = await import('./mocks')
  setupMock(http)
}

// 再安裝 router（此時守衛能讀到正確的狀態）
app.use(router)

// 最後掛載
app.mount('#app')
