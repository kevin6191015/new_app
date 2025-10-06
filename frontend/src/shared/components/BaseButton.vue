<template>
  <button class="base-btn" v-bind="$attrs"><slot /></button>
</template>

<style scoped>
.base-btn { padding: 8px 12px; border: 1px solid #ddd; border-radius: 8px; cursor: pointer; }
.base-btn:hover { opacity: .9; }
</style>
