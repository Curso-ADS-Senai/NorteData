import { defineStore } from 'pinia'

// Estado de UI compartilhado entre Sidebar e AppLayout: o AppLayout precisa
// saber se a sidebar está colapsada para ajustar a margem do conteúdo.
export const useUiStore = defineStore('ui', {
  state: () => ({
    sidebarColapsada: false,
  }),

  actions: {
    alternarSidebar() {
      this.sidebarColapsada = !this.sidebarColapsada
    },
  },
})
