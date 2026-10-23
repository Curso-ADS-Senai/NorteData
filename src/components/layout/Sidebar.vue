<template>
  <aside
    class="flex flex-col shrink-0 fixed inset-y-0 left-0 z-20 h-screen bg-slate-900 text-white transition-all duration-200"
    :class="ui.sidebarColapsada ? 'w-20' : 'w-64'"
  >
    <router-link
      to="/dashboard"
      class="flex items-center bg-white transition-all duration-200"
      :class="ui.sidebarColapsada ? 'justify-center px-3 py-6' : 'justify-start px-6 py-6'"
    >
      <!-- shrink-0 evita que o flexbox comprima a largura da imagem (o que
           distorcia a logo); a altura muda por estado e a largura segue o
           aspect ratio natural do arquivo, sem esticar nem espremer. -->
      <img
        :src="ui.sidebarColapsada ? logoIcone : logoCompleto"
        alt="Norte Data"
        class="w-auto shrink-0 transition-all duration-200"
        :class="ui.sidebarColapsada ? 'h-8' : 'h-20'"
      />
    </router-link>

    <nav class="flex flex-col flex-1 gap-1 px-4 py-6">
      <router-link
        v-for="item in itensVisiveis"
        :key="item.to"
        :to="item.to"
        :title="item.label"
        class="flex items-center gap-3 px-3 py-2 rounded text-sm font-medium text-slate-300 transition hover:bg-slate-800 hover:text-white"
        :class="ui.sidebarColapsada ? 'justify-center' : ''"
        active-class="bg-slate-800 text-white"
      >
        <img v-if="item.iconeSrc" :src="item.iconeSrc" :alt="item.label" class="h-5 w-5 shrink-0 object-contain" />
        <component v-else :is="item.icone" class="h-5 w-5 shrink-0" />
        <span v-if="!ui.sidebarColapsada">{{ item.label }}</span>
      </router-link>
    </nav>

    <button
      type="button"
      @click="ui.alternarSidebar"
      class="flex items-center gap-3 px-4 py-3 border-t border-slate-800 text-sm font-medium text-slate-300 transition hover:bg-slate-800 hover:text-white cursor-pointer"
      :class="ui.sidebarColapsada ? 'justify-center' : ''"
    >
      <svg
        xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
        class="h-5 w-5 shrink-0 transition-transform"
        :class="ui.sidebarColapsada ? 'rotate-180' : ''"
      >
        <path stroke-linecap="round" stroke-linejoin="round" d="M11 19l-7-7 7-7M19 19l-7-7 7-7" />
      </svg>
      <span v-if="!ui.sidebarColapsada">Recolher menu</span>
    </button>

    <router-link
      to="/perfil"
      title="Meu Perfil"
      class="flex items-center gap-3 px-4 py-4 border-t border-slate-800 text-sm font-medium text-slate-300 transition hover:bg-slate-800 hover:text-white"
      :class="ui.sidebarColapsada ? 'justify-center' : ''"
      active-class="bg-slate-800 text-white"
    >
      <span class="flex items-center justify-center h-8 w-8 shrink-0 rounded-full bg-slate-700 text-slate-300">
        <img :src="iconePerfil" alt="" class="h-5 w-5 object-contain" />
      </span>
      <span v-if="!ui.sidebarColapsada">Meu Perfil</span>
    </router-link>

    <button
      type="button"
      @click="sair"
      title="Sair"
      class="flex items-center gap-3 px-4 py-4 border-t border-slate-800 text-sm font-medium text-slate-300 transition hover:bg-slate-800 hover:text-white cursor-pointer"
      :class="ui.sidebarColapsada ? 'justify-center' : ''"
    >
      <img :src="iconeLogout" alt="" class="h-5 w-5 shrink-0 object-contain" />
      <span v-if="!ui.sidebarColapsada">Sair</span>
    </button>
  </aside>
</template>

<script setup>
import { h, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUiStore } from '../../stores/ui.js'
import { useAuthStore } from '../../stores/auth.js'
import logoCompleto from '../../assets/logo/nortedata_logoenome-semfundo.png'
import logoIcone from '../../assets/logo/nortedata_logosemfundo.png'
import iconeDashboard from '../../assets/logo/iconessidebar/iconedashboard.png'
import iconeCarteira from '../../assets/logo/iconessidebar/iconecarteira.png'
import iconeConsultoresImg from '../../assets/logo/iconessidebar/iconeconsultores.png'
import iconeConfig from '../../assets/logo/iconessidebar/iconeconfig.png'
import iconeUpload from '../../assets/logo/iconessidebar/iconeupload.png'
import iconeRelatorio from '../../assets/logo/iconessidebar/iconerelatorio.png'
import iconePerfil from '../../assets/logo/iconessidebar/iconeperfil.png'
import iconeLogout from '../../assets/logo/iconessidebar/iconelogout.png'

const ui = useUiStore()
const auth = useAuthStore()
const router = useRouter()

function sair() {
  auth.logout()
  router.push('/login')
}

function icone(path) {
  return () =>
    h(
      'svg',
      { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 1.5 },
      [h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', d: path })]
    )
}

const itens = [
  { label: 'Painel Principal', to: '/dashboard', iconeSrc: iconeDashboard },
  { label: 'Minha Carteira', to: '/carteira', iconeSrc: iconeCarteira },
  { label: 'Consultores', to: '/consultores', iconeSrc: iconeConsultoresImg },
  { label: 'Configurações', to: '/configuracoes', iconeSrc: iconeConfig },
  { label: 'Importar Dados', to: '/upload', iconeSrc: iconeUpload },
  { label: 'Relatório da Planilha', to: '/relatorio-validacao', iconeSrc: iconeRelatorio },
  { label: 'Usuários', to: '/usuarios', admin: true, icone: icone('M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0ZM4 21a6 6 0 0 1 12 0M20 8v4m-2-2h4') },
]

const itensVisiveis = computed(() =>
  itens.filter((item) => !item.admin || auth.usuario?.perfil === 'ADMIN')
)
</script>
