<template>
  <main class="nf-wrapper" role="main" aria-labelledby="nf-title">
    <section class="nf-card" tabindex="0">
      <div class="nf-emoji" aria-hidden="true">🔍</div>
      <h1 id="nf-title" class="nf-title">
        404
        <span class="nf-subtitle">找不到頁面</span>
      </h1>

      <p class="nf-desc">
        您造訪的連結可能已被移除、名稱變更，或暫時不可用。請檢查網址是否正確，或返回首頁。
      </p>

      <div class="nf-actions">
        <button class="btn btn-primary" @click="goHome" autofocus>
          回首頁
        </button>
        <button class="btn btn-ghost" @click="goBack">
          返回上一頁
        </button>
      </div>

      <div class="nf-hint">
        <span class="dot" aria-hidden="true"></span>
        如問題持續發生，請聯繫系統管理員。
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.replace({ name: 'home' })
}

function goBack() {
  // 若沒有上一頁，退而導向首頁
  if (window.history.length > 1) router.back()
  else router.replace({ name: 'home' })
}
</script>

<style scoped>
/* ----------- 版面與背景 ----------- */
.nf-wrapper {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background:
    radial-gradient(1200px 600px at 0% 0%, hsl(220 90% 62% / .12), transparent 60%),
    radial-gradient(1200px 600px at 100% 100%, hsl(280 80% 62% / .12), transparent 60%),
    linear-gradient(180deg, hsl(220 20% 98%), hsl(220 20% 96%));
}
@media (prefers-color-scheme: dark) {
  .nf-wrapper {
    background:
      radial-gradient(1200px 600px at 0% 0%, hsl(220 90% 62% / .12), transparent 60%),
      radial-gradient(1200px 600px at 100% 100%, hsl(280 80% 62% / .12), transparent 60%),
      linear-gradient(180deg, hsl(220 15% 10%), hsl(220 15% 8%));
  }
}

/* ----------- 卡片 ----------- */
.nf-card {
  width: min(720px, 92vw);
  padding: 28px 28px 24px;
  border-radius: 18px;
  background: hsl(0 0% 100% / .7);
  backdrop-filter: blur(8px) saturate(120%);
  border: 1px solid hsl(220 20% 88% / .6);
  box-shadow:
    0 10px 30px hsl(220 30% 20% / .08),
    0 2px 10px hsl(220 30% 20% / .05);
  outline: none;
  animation: pop .35s ease-out both;
}
@media (prefers-color-scheme: dark) {
  .nf-card {
    background: hsl(230 20% 12% / .6);
    border-color: hsl(220 20% 30% / .6);
    box-shadow:
      0 10px 30px hsl(220 30% 2% / .6),
      0 2px 10px hsl(220 30% 10% / .3);
  }
}
@keyframes pop {
  from { transform: translateY(8px) scale(.98); opacity: 0 }
  to   { transform: translateY(0) scale(1);     opacity: 1 }
}

/* ----------- 標題與圖示 ----------- */
.nf-emoji {
  font-size: 42px;
  line-height: 1;
  filter: drop-shadow(0 4px 10px hsl(220 10% 10% / .12));
  margin-bottom: 6px;
}
.nf-title {
  margin: 0;
  font-size: clamp(40px, 6vw, 72px);
  font-weight: 800;
  letter-spacing: 1px;
  color: hsl(220 25% 20%);
  display: grid;
  gap: 6px;
  place-items: center;
}
.nf-subtitle {
  display: block;
  font-size: clamp(14px, 2.3vw, 18px);
  font-weight: 600;
  color: hsl(220 10% 35%);
  letter-spacing: .5px;
}
@media (prefers-color-scheme: dark) {
  .nf-title { color: hsl(220 15% 92%) }
  .nf-subtitle { color: hsl(220 10% 70%) }
}

/* ----------- 說明 ----------- */
.nf-desc {
  margin: 12px auto 18px;
  max-width: 56ch;
  color: hsl(220 10% 30%);
  line-height: 1.7;
  text-align: center;
}
@media (prefers-color-scheme: dark) {
  .nf-desc { color: hsl(220 10% 75%) }
}

/* ----------- 按鈕 ----------- */
.nf-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  margin: 8px 0 6px;
}
.btn {
  appearance: none;
  border: 1px solid transparent;
  border-radius: 999px;
  padding: 10px 16px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: transform .06s ease, box-shadow .2s ease, background-color .2s ease, border-color .2s ease, color .2s ease;
}
.btn:focus-visible {
  outline: 3px solid hsl(220 90% 66% / .6);
  outline-offset: 2px;
}
.btn:active { transform: translateY(1px) }

.btn-primary {
  background: linear-gradient(135deg, hsl(222 90% 58%), hsl(268 75% 60%));
  color: #fff;
  box-shadow: 0 8px 16px hsl(240 70% 40% / .25);
}
.btn-primary:hover {
  filter: brightness(1.05);
}
.btn-ghost {
  background: transparent;
  color: hsl(220 30% 40%);
  border-color: hsl(220 20% 80%);
}
.btn-ghost:hover {
  background: hsl(220 20% 96%);
}
@media (prefers-color-scheme: dark) {
  .btn-ghost {
    color: hsl(220 20% 85%);
    border-color: hsl(220 20% 30%);
  }
  .btn-ghost:hover {
    background: hsl(230 20% 16%);
  }
}

/* ----------- 小提示 ----------- */
.nf-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: hsl(220 10% 45%);
  font-size: 12.5px;
  margin-top: 12px;
}
@media (prefers-color-scheme: dark) {
  .nf-hint { color: hsl(220 10% 70%) }
}
.dot {
  width: 6px; height: 6px; border-radius: 50%;
  background: linear-gradient(135deg, hsl(222 90% 58%), hsl(268 75% 60%));
  display: inline-block;
}
</style>
