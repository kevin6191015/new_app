import type { AxiosRequestConfig } from 'axios'
import type { ApiEnvelope } from '@features/auth/types'

export const ok = <T,>(data: T, code = 200): ApiEnvelope<T> => ({ code, message: 'OK', data })
export const err = (message: string, code = 400): ApiEnvelope<null> => ({ code, message, data: null })

export function json<T = any>(config: AxiosRequestConfig): T {
  try { return typeof config.data === 'string' ? JSON.parse(config.data) : (config.data as T) }
  catch { throw new Error('Bad JSON') }
}
