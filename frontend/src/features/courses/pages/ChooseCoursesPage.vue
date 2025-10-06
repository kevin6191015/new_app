<template>
  <section class="page" :style="pageBgVar">
    <header class="header">
      <h2 class="title" v-if="isTeacher">課程選擇（老師）</h2>
      <h2 class="title" v-else>課程選擇（學生）</h2>
      <p class="subtitle" v-if="userName">使用者：{{ userName }}</p>
    </header>

    <div class="filters">
      <label>
        學期
        <select v-model="sem" @change="onChangeSem">
          <option value="" disabled>請選擇</option>
          <option v-for="s in allSem" :key="s" :value="s">{{ s }}</option>
        </select>
      </label>

      <label>
        課程名稱
        <select v-model="className" @change="onChangeClass" :disabled="coursesBySem.length === 0">
          <option value="" disabled>請選擇</option>
          <option
            v-for="c in classNameOptions"
            :key="c.classId"
            :value="c.name"
          >
            {{ c.name }}
          </option>
        </select>
      </label>
    </div>

    <main class="list">
      <ul class="table list-table">
        <li
          v-for="c in displayed"
          :key="c.classId"
          class="row"
          @click="go(c)"
          tabindex="0"
          @keydown.enter.prevent="go(c)"
        >
          <span class="cell">{{ c.semester }} {{ c.name }}</span>
        </li>
      </ul>

    </main>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getSemesters, getAllCourses, getCoursesBySemester } from '@features/courses/api'
import type { Course } from '@features/courses/types'
import { useCourseStore } from '@app/stores/useCourseStore'
import { useAuthStore } from '@app/stores/useAuthStore' // 若尚未接 auth，role 會是空，邏輯會自動降級

// ✅ 匯入背景圖（依你的別名調整路徑；以下示範 src/app/assets/code.jpg）
import codeBg from '@assets/code.jpg'

// 用 CSS 變數把 url 傳給樣式
const pageBgVar = computed(() => ({ '--page-bg': `url(${codeBg})` }))

const router = useRouter()
const route = useRoute()
const courseStore = useCourseStore()
const auth = useAuthStore()

// 角色判斷：舊版邏輯「roleCode === '1' 為學生；其餘視為老師」
const roleCode = computed(() => auth.roleCode ?? (route.query.role as string) ?? '1') // 可用 ?role=3 模擬老師
const isTeacher = computed(() => roleCode.value !== '1')
const isStudent = computed(() => !isTeacher.value)

const userName = computed(() => auth.user?.name ?? '')

// 下拉與資料
const allSem = ref<string[]>([])
const sem = ref<string>('')
const className = ref<string>('')

const coursesBySem = ref<Course[]>([])
const displayed = ref<Course[]>([]) // 顯示在「表格」上的資料

// 學生擁有的課程（用 user.classes 逗號分隔，例如 "C001,C003"），若無資料則視為全部可見
const studentOwnedIds = computed<string[] | null>(() => {
  const raw = (auth.user as any)?.classes as string | undefined
  return raw ? raw.split(',').map(s => s.trim()).filter(Boolean) : null
})

// 學生情境：課名下拉只給有權限的課；老師情境：顯示該學期全部
const classNameOptions = computed<Course[]>(() => {
  if (isTeacher.value || !studentOwnedIds.value) return coursesBySem.value
  const ids = new Set(studentOwnedIds.value)
  return coursesBySem.value.filter(c => ids.has(c.classId))
})

onMounted(async () => {
  allSem.value = await getSemesters()
  // 預設選第一個學期
  await refreshBySem()
})

async function refreshBySem() {
  className.value = ''
  coursesBySem.value = sem.value ? await getCoursesBySemester(sem.value) : await getAllCourses()
  // 初始顯示
  if (isTeacher.value) {
    displayed.value = coursesBySem.value.map(c => ({ ...c }))
  } else {
    displayed.value = filterForStudent(coursesBySem.value)
  }
}

function filterForStudent(list: Course[]): Course[] {
  if (!studentOwnedIds.value) return list
  const ids = new Set(studentOwnedIds.value)
  return list.filter(c => ids.has(c.classId))
}

async function onChangeSem() {
  await refreshBySem()
}

function onChangeClass() {
  if (!className.value) {
    // 清除篩選
    displayed.value = isTeacher.value ? coursesBySem.value : filterForStudent(coursesBySem.value)
    return
    }
  const base = isTeacher.value ? coursesBySem.value : filterForStudent(coursesBySem.value)
  const picked = base.find(c => c.name === className.value)
  displayed.value = picked ? [picked] : []
}

function go(c: Course) {
  // 儲存選擇
  courseStore.selectCourse(c)

  // 舊案：老師→/teacherhome，學生→/studenthome
  // 為了先跑畫面，統一導向課程首頁（可依需求改成上面兩條路徑）
  router.replace('/home')
}
</script>

<style scoped>
.page {
  position: relative;
  min-height: 100vh;          /* ✅ 撐滿視窗高度 */
}

/* ✅ 用偽元素鋪滿整個視窗並淡化 */
.page::before {
  content: '';
  position: fixed;            /* 固定在視窗，整頁鋪滿 */
  inset: 0;
  background-image: var(--page-bg);
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.15;              /* 淡化程度：0.1~0.3 自行調 */
  pointer-events: none;       /* 不擋互動 */
  z-index: -1;                /* 放在內容下方 */
}

/* 用 display: table 模擬表格並給正常邊框 */
.list-table {
  display: table;
  width: 100%;
  border-collapse: collapse;
  border: 1px solid #d0d7de;   /* 外框 */
  background: #fff;
  list-style: none;
  padding: 0;
  margin: 0;
}

.list-table > .row {
  display: table-row;
}

.list-table > .row > .cell {
  display: table-cell;
  border: 1px solid #d0d7de;   /* 內格線 */
  padding: 8px 12px;
  text-align: center;          /* 依需求調整 */
}

.list-table > .row:hover { background: #f6f7fb; }

.page { padding: 16px; }
.header { text-align: center; margin-bottom: 12px; }
.title { margin: 0; }
.subtitle { margin: 6px 0 0; color: #555; }

.filters {
  display: flex; gap: 12px; justify-content: center;
  margin: 12px 0 20px;
}
.filters select { padding: 6px 8px; }

.list { display: flex; justify-content: center; }
.table { width: 50%; max-width: none; }
.row {
  display: flex; align-items: center; min-height: 44px; padding: 0 12px;
  border-bottom: 1px solid #eee; cursor: pointer;
}
.row:last-child { border-bottom: none; }
.row:hover, .row:focus { background: #f6f7fb; outline: none; }
.cell { width: 100%; text-align: center; padding: 8px 0; }
.empty { text-align: center; color: #777; margin-top: 8px; }

</style>
