import { defineStore } from 'pinia'
import type { Course } from '@features/courses/types'

const STORAGE_KEY = 'course' // localStorage key

export const useCourseStore = defineStore('course', {
  state: () => ({
    selectedClassId: null as string | null,
    selectedClassLabel: '' as string
  }),
  getters: {
    hasSelection: (s) => !!s.selectedClassId
  },
  actions: {
    selectCourse(c: Course) {
      this.selectedClassId = c.classId
      this.selectedClassLabel = `${c.semester}  ${c.name}`
      // 持久化
      localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({
          selectedClassId: this.selectedClassId,
          selectedClassLabel: this.selectedClassLabel
        })
      )
    },
    clearSelection() {
      this.selectedClassId = null
      this.selectedClassLabel = ''
      localStorage.removeItem(STORAGE_KEY)
    },
    // 啟動時回灌（F5 後仍能記住選課）
    hydrate() {
      try {
        const raw = localStorage.getItem(STORAGE_KEY)
        if (!raw) return
        const parsed = JSON.parse(raw) as {
          selectedClassId?: string | null
          selectedClassLabel?: string
        }
        this.selectedClassId = parsed.selectedClassId ?? null
        this.selectedClassLabel = parsed.selectedClassLabel ?? ''
      } catch {
        // 壞資料時重置並清掉儲存
        this.selectedClassId = null
        this.selectedClassLabel = ''
        localStorage.removeItem(STORAGE_KEY)
      }
    }
  }
})
