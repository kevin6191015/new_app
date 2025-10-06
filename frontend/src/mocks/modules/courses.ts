import type AxiosMockAdapter from 'axios-mock-adapter'
import { ok, err } from '../utils'
import type { Course } from '@features/courses/types'
import { getUserByAuthHeader } from './auth'

// 全部課程（範例）
export const ALL_COURSES: Course[] = [
  { classId:'CLS101', name:'演算法', teacher:'王老師',  semester:'112-1' },
  { classId:'CLS102', name:'資料結構',teacher:'李老師', semester:'112-2' },
  { classId:'CLS103', name:'作業系統',teacher:'陳老師', semester:'113-1' },
  { classId:'CLS104', name:'程式設計',teacher:'陳老師', semester:'113-2' }
]

export function mountCourses(mock: AxiosMockAdapter) {
  // 學期清單
  mock.onGet(/^\/courses\/semesters$/).reply(() => {
    const semesters = Array.from(new Set(ALL_COURSES.map(c => c.semester)))
    return [200, ok(semesters)]
  })

  // 課程列表（?sem= 可選）
    mock.onGet(/^\/courses(?:\?.*)?$/).reply(config => {
    // ✅ 先讀 axios 的 params，再退回解析 URL
    const semParam =
        (config as any).params?.sem ??
        new URLSearchParams(config.url?.split('?')[1] ?? '').get('sem')

    const sem = typeof semParam === 'string' ? semParam : (semParam ?? '')
    const listBySem = sem ? ALL_COURSES.filter(c => c.semester === sem) : ALL_COURSES

    // 授權過濾
    const user = getUserByAuthHeader(config.headers)
    if (!user) return [200, err('未授權', 401)]

    if (user.role === 'teacher' || user.role === 'ROOT') {
        return [200, ok(listBySem)]                 // ApiEnvelope<Course[]>
    }

    const ids = new Set((user.courses ?? []).map(c => c.classId))
    const authorized = listBySem.filter(c => ids.has(c.classId))
    return [200, ok(authorized)]
    })

  // （選）單一課程（排除 /courses/semesters）
  mock.onGet(/^\/courses\/(?!semesters$)([\w-]+)$/).reply(config => {
    const cid = config.url!.match(/^\/courses\/(?!semesters$)([\w-]+)$/)![1]
    const found = ALL_COURSES.find(c => c.classId === cid) ?? null
    return [200, ok(found)]
  })
}
