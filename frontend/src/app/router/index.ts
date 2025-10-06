// src/app/router/index.ts
import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import HomePage from '@features/home/pages/HomePage.vue'
import { useAuthStore } from '@app/stores/useAuthStore'

/**
 * 路由設定
 * - meta.public: 公開頁（不需登入）
 * - meta.requiresAuth: 需要登入
 * - meta.roles: 需要特定角色（'student'|'ta'|'teacher'|'root'，大小寫不敏感）
 */
const routes: RouteRecordRaw[] = [
  // 根路徑 → /home
  { path: '/', name: 'root', redirect: { name: 'home' } },

  // 登入頁（公開）
  {
    path: '/login',
    name: 'login',
    component: () => import('@features/auth/pages/LoginPage.vue'),
    meta: { public: true }
  },

  // 首頁（需登入）
  {
    path: '/home',
    name: 'home',
    component: HomePage,
    meta: { requiresAuth: true }
  },

  // 課程選擇（需登入，可限制角色）
  {
    path: '/choose-courses',
    name: 'choose-courses',
    component: () => import('@features/courses/pages/ChooseCoursesPage.vue'),
    meta: { requiresAuth: true, roles: ['teacher', 'student', 'ta', 'root'] }
  },

  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('@features/misc/pages/NotFoundPage.vue'),
    meta: { public: true }
  }
]

const router = createRouter({
  // 若部署在子路徑，請在 vite.config.ts 設 base，並使用下行的 BASE_URL
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior() {
    return { left: 0, top: 0 }
  }
})

/**
 * 全域前置守門：
 * 1) 未登入但要進 requiresAuth → 轉到 /login?redirect=原路徑
 * 2) 已登入去 /login → 轉回 redirect 或 /home
 * 3) 有 roles 限制時，檢查使用者角色
 */
router.beforeEach((to) => {
  const auth = useAuthStore()

  // 第一次導航時，確保從 localStorage 恢復
  if (!auth.initialized && typeof auth.hydrate === 'function') {
    auth.hydrate()
  }

  const isPublic = to.meta.public === true
  const requiresAuth = to.meta.requiresAuth === true
  const isAuthed = !!auth.token

  // 需要登入但尚未登入
  if (requiresAuth && !isAuthed) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  // 已登入又要去 /login → 轉回原本想去的頁或首頁
  if (to.name === 'login' && isAuthed) {
    const redirect = (to.query.redirect as string) || '/home'
    return redirect
  }

  // 角色保護（若路由設定了 roles）
  const allowed = (to.meta.roles as string[] | undefined)?.map(r => r.toLowerCase())
  if (allowed && isAuthed) {
    const userRole = auth.user?.role?.toString().toLowerCase()
    if (!userRole || !allowed.includes(userRole)) {
      // 無權存取 → 導回首頁或可改為 403 頁
      return { name: 'home' }
    }
  }

  // 公開頁或條件皆通過 → 放行
  return true
})

export default router

// （選配）若你使用 TypeScrip
