import { defineStore } from 'pinia'
export const useAppStore = defineStore('app', {
  state: () => ({ user: null as null | { id: string; name: string }, theme: 'light' as 'light'|'dark' }),
  actions: { setUser(u: any){ this.user = u }, toggleTheme(){ this.theme = this.theme==='light'?'dark':'light' } }
})
