// src/mocks/modules/auth.ts
import type AxiosMockAdapter from 'axios-mock-adapter'
import type { LoginPayload, LoginOk, User } from '@features/auth/types'
import { ok, err, json } from '../utils'
import { ALL_COURSES } from './courses'

// 帳號基本資料（不含 courses）
const users: Record<string, Omit<User,'courses'>> = {
  student: { id:'1', name:'學生小明', role:'student' },
  ta:      { id:'2',  name:'助教小美', role:'TA' },
  teacher: { id:'3', name:'王老師',   role:'teacher' },
  root:    { id:'4', name:'系統管理員', role:'ROOT' }
}
// 以 id 快速反查
const idIndex: Record<string, Omit<User,'courses'>> =
  Object.fromEntries(Object.values(users).map(u => [u.id, u]))

// 誰可看哪些課
const enrollments: Record<string, string[]> = {
  '1': ['CLS101','CLS103'],
  '2' : ['CLS101'],
  '3': ['CLS101','CLS102'],
  '4': ['CLS101','CLS102','CLS103']
}

// 由基本資料計算出含 courses 的完整 User
function materializeUser(base: Omit<User,'courses'>): User {
  const ids = enrollments[base.id] ?? []
  const courses = (base.role === 'teacher' || base.role === 'ROOT')
    ? ALL_COURSES
    : ALL_COURSES.filter(c => ids.includes(c.classId))
  return { ...base, courses }
}

// ✅ 無狀態：從 Bearer token 解析出 userId，據以還原 User
export function getUserByAuthHeader(headers: any): User | null {
  const auth = headers?.Authorization || headers?.authorization
  if (!auth?.startsWith('Bearer ')) return null
  const token = auth.slice('Bearer '.length)
  const m = token.match(/^mock-(.+?)-\d+$/)   // 對應發 token 的格式
  if (!m) return null
  const uid = m[1]
  const base = idIndex[uid]
  return base ? materializeUser(base) : null
}

export function mountAuth(mock: AxiosMockAdapter) {
  mock.onPost(/^\/auth\/login$/).reply(config => {
    try {
      const { username } = json<LoginPayload>(config)
      const base = users[(username || '').trim().toLowerCase()]
      if (!base) return [200, err('帳號或密碼錯誤', 401)]
      const token = `mock-${base.id}-${Date.now()}`
      const user  = materializeUser(base)
      const body: LoginOk = { token, user }
      return [200, ok(body)]
    } catch {
      return [200, err('無法解析請求內容', 400)]
    }
  })
}
