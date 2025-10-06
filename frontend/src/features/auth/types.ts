import type { Course } from '@features/courses/types'

export interface LoginPayload { username: string; password: string }
export interface User { id: string; name: string; role: 'ROOT'|'teacher'|'TA'|'student', courses: Course[]}
export interface LoginOk { token: string; user: User }
