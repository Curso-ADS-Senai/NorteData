import { defineStore } from 'pinia'

// Estado de UI compartilhado entre Sidebar e AppLayout: o AppLayout precisa
// saber se a sidebar está colapsada para ajustar a margem do conteúdo.
// A escolha é persistida porque é uma preferência do usuário (editável também
// pela tela de Perfil / Configurações), não um estado de navegação.
const CHAVE = 'ui.sidebarColapsada'

export const useUiStore = defineStore('ui', {
  state: () => ({
    sidebarColapsada: localStorage.getItem(CHAVE) === '1',
  }),

  actions: {
    alternarSidebar() {
      this.definirSidebarColapsada(!this.sidebarColapsada)
    },

    definirSidebarColapsada(colapsada) {
      this.sidebarColapsada = colapsada
      localStorage.setItem(CHAVE, colapsada ? '1' : '0')
    },
  },
})
