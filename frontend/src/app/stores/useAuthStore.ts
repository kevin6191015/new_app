import { defineStore } from 'pinia'
import type { LoginPayload, User } from '@features/auth/types'
import { loginApi } from '@features/auth/api'

export type RoleCode = '1' | '2' | '3' | '4'   // 1=STUDENT, 2=TA, 3=TEACHER, 4=ROOT

const roleToCode = (role: User['role']): RoleCode => {
  const r = (role ?? '').toString().trim().toUpperCase()
  switch (r) {
    case 'ROOT':    return '4'
    case 'TEACHER': return '3'
    case 'TA':      return '2'
    default:        return '1'
  }
}

const safeParse = <T>(raw: string | null): T | null => {
  if (!raw) return null
  try { return JSON.parse(raw) as T } catch { return null }
}

interface AuthState {
  token: string | null
  user: User | null
  roleCode: RoleCode | null
  initialized: boolean
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => {
    const token = localStorage.getItem('token')
    const user = safeParse<User>(localStorage.getItem('user'))
    const savedRole = localStorage.getItem('roleCode') as RoleCode | null
    return { token, user, roleCode: savedRole ?? (user ? roleToCode(user.role) : null), initialized: true }
  },

  getters: {
    isAuthenticated: (s) => !!s.token,
    role(s): RoleCode | null {
      return s.roleCode ?? (s.user ? roleToCode(s.user.role) : null)
    },
    isStudent(): boolean { return this.role === '1' },
    isTA(): boolean { return this.role === '2' },
    isTeacher(): boolean { return this.role === '3' },
    isRoot(): boolean { return this.role === '4' },
    isStaff(): boolean { return this.role === '2' || this.role === '3' || this.role === '4' }
  },

  actions: {
    async login(payload: LoginPayload) {
      const { token, user } = await loginApi(payload)  // 確保 loginApi 回的是 ApiEnvelope.data
      this.token = token
      // 正規化角色後存
      const normalizedUser = { ...user, role: user.role.toUpperCase() as User['role'] }
      this.user = normalizedUser
      this.roleCode = roleToCode(normalizedUser.role)

      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(normalizedUser))
      localStorage.setItem('roleCode', this.roleCode)
    },

    hydrate() {
      if (this.initialized) return
      this.token = this.token ?? localStorage.getItem('token')
      this.user = this.user ?? safeParse<User>(localStorage.getItem('user'))
      const savedRole = localStorage.getItem('roleCode') as RoleCode | null
      this.roleCode = savedRole ?? (this.user ? roleToCode(this.user.role) : null)
      this.initialized = true
    },

    logout() {
      this.token = null
      this.user = null
      this.roleCode = null
      this.initialized = true
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('roleCode')
    }
  }
})
