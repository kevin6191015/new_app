import http from '@shared/api/http'
import type { LoginPayload, LoginOk} from './types'

// 後端成功回：{ code:200, message:'OK', data:{ token,user } }
// 攔截器已把 payload 攤平成 res.data
export async function loginApi(payload: LoginPayload): Promise<LoginOk> {
  const { data } = await http.post<LoginOk>('/auth/login', payload)
  return data
}
