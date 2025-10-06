// src/shared/api/http.ts
import axios, { AxiosError, AxiosResponse } from 'axios'
import router from '@app/router'

// 小工具：動態抓 Pinia store，避免檔案頂部靜態 import 造成循環依賴
function useAuthSafely() {
  // 為了避免循環 import，這裡在函式內 import
  const { useAuthStore } = require('@app/stores/useAuthStore') as typeof import('@app/stores/useAuthStore')
  return useAuthStore()
}

/** 從後端 ApiEnvelope 取出 payload；非 Envelope 則原樣返回 */
function unwrapEnvelope<T>(res: AxiosResponse<T>): AxiosResponse<any> {
  const data: any = res.data
  // 後端統一格式：{ code, message, data }
  if (data && typeof data === 'object' && 'code' in data && 'message' in data) {
    if (data.code === 200) {
      // 直接把 payload 放到 res.data，呼叫端就不用再 .data.data
      res.data = data.data
      return res
    }
    // 非 200：丟出帶 message 的錯誤
    const err = new Error(data.message || '發生錯誤')
    ;(err as any).__apiCode = data.code
    throw err
  }
  // 非 Envelope（例如登入還沒包裝時），照舊返回
  return res
}

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/',
  timeout: 15000,
  withCredentials: false, // 你用的是 Bearer Token，不需要帶 cookie
  headers: {
    Accept: 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  }
})

// ---- Request ----
http.interceptors.request.use((cfg) => {
  // 優先從 store 取；沒有時退回 localStorage
  try {
    const auth = useAuthSafely()
    const token = auth?.token ?? localStorage.getItem('token')
    if (token) cfg.headers.Authorization = `Bearer ${token}`
  } catch {
    const token = localStorage.getItem('token')
    if (token) cfg.headers.Authorization = `Bearer ${token}`
  }
  return cfg
})

// ---- Response ----
http.interceptors.response.use(
  (res) => unwrapEnvelope(res),
  async (error: AxiosError<any>) => {
    // 從 ApiEnvelope 錯誤或 Axios 錯誤抽出訊息
    const status = error.response?.status
    const payload = error.response?.data as any
    const apiMessage =
      (payload && typeof payload === 'object' && (payload.message || payload.error || payload.msg)) ||
      (error.message || '網路錯誤')

    // 401：自動登出並導向登入頁，帶上 redirect
    if (status === 401) {
      try {
        const auth = useAuthSafely()
        auth?.logout?.()
      } catch {
        // 忽略
      }
      const current = router.currentRoute.value
      const redirect = current?.fullPath && current.fullPath !== '/login' ? { redirect: current.fullPath } : undefined
      router.replace({ name: 'login', query: redirect })
    }

    // 可在這裡丟到全域 toast（自行整合 UI）
    // showToast(apiMessage)

    // 讓呼叫端可以捕捉
    return Promise.reject(new Error(apiMessage))
  }
)

export default http
