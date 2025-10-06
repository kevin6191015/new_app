<template>
  <div class="page" :style="pageBgVar">
    <!-- 頂部導覽 -->
    <header class="topbar">
      <nav class="nav">
        <router-link class="brand" to="/home">CourseHub</router-link>

      
        <!-- 主要功能選單（依角色顯示） -->
        <ul class="menu">
          <li><router-link to="/home">首頁</router-link></li>

          <li v-if="isTeacher || isRoot" class="has-sub">
            <span>題目管理</span>
            <ul class="sub">
              <li v-if="isTeacher || isRoot" class="dropdown-item" ><router-link to="/questions/new">新增題目</router-link></li>
              <li v-if="isTeacher || isRoot" class="dropdown-item"><router-link to="/questions/delete">刪除題目</router-link></li>
              <li class="dropdown-item"><router-link to="/assignments/publish">發布作業</router-link></li>
            </ul>
          </li>

          <li v-if="isTeacher || isRoot"  class="has-sub">
            <span>帳號管理</span>
            <ul class="sub">
              <li v-if="isRoot" class="dropdown-item"><router-link to="/accounts/system">系統帳號管理</router-link></li>
              <li v-if="isTeacher" class="dropdown-item"><router-link to="/accounts/course">課程帳號管理</router-link></li>
            </ul>
          </li>

          <li v-if="isRoot"><router-link to="/courses/manage">課程管理</router-link></li>
        </ul>

        <router-link class="chip" to="/choose-courses">
          當前課程：<strong>{{ selectedClassLabel || '尚未選擇' }}</strong>
        </router-link>

        <div class="account">
          <button class="avatar" title="帳戶">{{ userInitial }}</button>
          <div class="dropdown">
            <div class="dropdown-item" aria-disabled="true">{{ userName || '未登入' }}</div>
            <button class="dropdown-item" @click="onLogout">登出</button>
          </div>
        </div>
      </nav>
    </header>

    <!-- 主內容區 -->
    <main class="container">
      <!-- 首頁：依角色顯示不同卡片 -->
      <section class="cards" v-if="route.name === 'home' || route.path === '/home'">
        <article v-if="isTeacher" class="card">
          <h3>教師功能</h3>
          <ul>
            <li><router-link to="/assignments/a1">作業總覽（示例）</router-link></li>
            <li><router-link to="/courses">課程列表（示例）</router-link></li>
            <li><router-link to="/accounts/courses">課程帳號管理（示例）</router-link></li>
          </ul>
        </article>
        
        <article v-else-if="isRoot" class="card">
          <h3>管理員功能</h3>
          <ul>
            <li><router-link to="/assignments/a1">作業總覽（示例）</router-link></li>
            <li><router-link to="/courses">課程列表（示例）</router-link></li>
            <li><router-link to="/accounts/system">系統帳號管理（示例）</router-link></li>
          </ul>
        </article>

        <article v-else class="card">
          <h3>學生功能</h3>
          <ul>
            <li><router-link to="/assignments/a1">作業總覽（示例）</router-link></li>
            <li><router-link to="/courses">課程列表（示例）</router-link></li>
          </ul>
        </article>

        <!-- 可選：首頁顯示作業摘要 -->
        <AssignmentsWidget class="span-2"/>
      </section>
      

      <!-- 子頁顯示位 -->
      <router-view/>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@app/stores/useAuthStore'
import { useCourseStore } from '@app/stores/useCourseStore'

import dashboardbg from '@assets/code.jpg'

const pageBgVar = computed(() => ({ '--page-bg': `url(${dashboardbg})` }))


const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const course = useCourseStore()

const roleCode = computed(() => auth.roleCode)               // '1'=學生, '3'=老師, '4'=ROOT
const isRoot = computed(() => roleCode.value === '4')
const isTeacher = computed(() => roleCode.value === '3' )

const userName = computed(() => auth.user?.name ?? '')
const userInitial = computed(() => userName.value ? userName.value[0] : 'U')
const selectedClassLabel = computed(() => course.selectedClassLabel)

function onLogout() {
  auth.logout()
  router.replace('/login')
}
</script>

<style scoped lang="scss">
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
  opacity: 0.25;              /* 淡化程度：0.1~0.3 自行調 */
  pointer-events: none;       /* 不擋互動 */
  z-index: -1;                /* 放在內容下方 */
}
/* 頂部導覽維持不變 */
.topbar { 
  background:#545c64; 
  color:#fff; 
  border-bottom:1px solid rgba(255,255,255,.12); 
}
.nav {
  max-width: 100%; 
  display:flex; 
  align-items:center; 
  gap:16px; 
  padding:10px 16px; /* 確保 dropdown 不被裁切 */ 
  overflow: visible; 
}
.brand { 
  color:#ffd04b; 
  text-decoration:none; 
  font-weight:800; 
}

.menu { 
  display:flex; 
  align-items:center; 
  gap:12px; 
  margin:0; 
  padding:0; 
  list-style:none; 
}

.menu > li > a, 
.menu > li > span { 
  color:#fff; 
  text-decoration:none; 
  padding:8px 10px; 
  border-radius:8px; 
}

.menu > li > a:hover, 
.menu > li > span:hover { 
  background:rgba(255,255,255,.1); 
}

/* --- 子選單（題目管理 / 帳號管理）改為無縫 hover --- */
.has-sub { 
  position:relative; 
}
.has-sub .sub {
  position:absolute;
  top: calc(100% + 8px); left:0;
  background:#fff; color:#111;
  border:1px solid #ddd; border-radius:8px;
  min-width:180px; padding:2px 0; list-style:none; z-index:1000;

  /* 用透明度/可見度切換，避免 display 造成閃爍 */
  opacity: 0; visibility: hidden; transform: translateY(-4px);
  transition: opacity .15s ease, transform .15s ease, visibility 0s linear .15s;
}
.has-sub:hover .sub,
.has-sub:focus-within .sub,
.has-sub .sub:hover {
  opacity: 1; visibility: visible; transform: translateY(0); transition-delay: 0s;
}
/* hover 橋（覆蓋按鈕與選單之間的 8px 縫） */
.has-sub:hover::after {
  content:''; position:absolute; top:100%; left:0; width:100%; height:10px;
}

/* --- 帳號下拉（右上角）改為無縫 hover --- */
.account { 
  position:relative; 
}
.avatar { 
  width:34px; 
  height:34px; 
  border-radius:50%; 
  border:0; 
  background:aquamarine; 
  cursor:pointer; 
}

/* 移除你原本的 `.account:hover .dropdown { display:block; }` 舊寫法 */
.dropdown {
  position:absolute; right:0; top: calc(100% + 8px);
  background:#fff; color:#111; border:1px solid #ddd; border-radius:10px;
  min-width:180px; overflow:hidden; z-index:1000;

  opacity: 0; visibility: hidden; transform: translateY(-4px);
  transition: opacity .15s ease, transform .15s ease, visibility 0s linear .15s;
}
.account:hover .dropdown,
.account:focus-within .dropdown,
.dropdown:hover {
  opacity: 1; visibility: visible; transform: translateY(0); transition-delay: 0s;
}
/* hover 橋（覆蓋 8px 縫） */
.account:hover::after {
  content:''; 
  position:absolute; 
  right:0; 
  top:100%; 
  width:220px; 
  height:10px;
}

.dropdown-item { 
  display:block; width:100%; 
  text-align:left; 
  padding:10px 12px; 
  background:#fff; 
  border:0; 
  cursor:pointer;
}
.dropdown-item[aria-disabled="true"] { 
  color:#666; 
  cursor:default; 
}
.dropdown-item:hover { 
  background:#f3f4f6; 
}

/* 其他原樣式保持 */
.chip { 
  margin-left:auto; 
  color:#fff; 
  text-decoration:none; 
  border:2px solid rgba(255,255,255,.2); 
  padding:6px 10px; 
  border-radius:999px; 
}
.chip strong { 
  color:#ffd04b; 
}
.container { 
  padding:16px; 
}
.cards { 
  display:grid; 
  grid-template-columns: repeat(auto-fit,minmax(280px,1fr)); 
  gap:16px; 
}
.card { 
  background:#fff; 
  border:1px solid #e5e7eb;
  border-radius:12px; 
  padding:14px 16px; 
}
.card h3 { 
  margin:0 0 8px; 
}
.card ul { 
  margin:0; 
  padding-left:18px; 
}
.span-2 { 
  grid-column: span 2; 
}

/* 針對選單與下拉連結：取消底線、統一顏色（含 visited/active） */
.menu a,
.has-sub .sub li > a,
.account .dropdown a,
.chip,
.brand {
  text-decoration: none;
}

/* 各狀態都維持同色，不要瀏覽器預設的「點過變紫」 */
.menu a:link,
.menu a:visited,
.menu a:active,
.has-sub .sub li > a:link,
.has-sub .sub li > a:visited,
.has-sub .sub li > a:active,
.account .dropdown a:link,
.account .dropdown a:visited,
.account .dropdown a:active,
.chip:link,
.chip:visited,
.chip:active,
router-link{
  color: inherit; /* 跟父層一樣（頂欄白色、下拉#111） */
  text-decoration: none;
}

/* hover 只改背景、不加底線 */
.menu a:hover,
.has-sub .sub li > a:hover,
.account .dropdown a:hover,
.chip:hover,
.brand:hover {
  text-decoration: none;
}

.card a:link,
.card a:visited,
.card a:hover{
  color: #7b4bff;
  text-decoration: none;
}


</style>
