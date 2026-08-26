<template>
  <aside class="flex flex-col shrink-0 w-64 min-h-screen bg-slate-900 text-white">
    <router-link to="/dashboard" class="flex items-center gap-3 px-6 py-6 bg-white">
      <img :src="logo" alt="Norte Data" class="h-10" />
    </router-link>

    <nav class="flex flex-col flex-1 gap-1 px-4 py-6">
      <router-link
        v-for="item in itens"
        :key="item.to"
        :to="item.to"
        class="flex items-center gap-3 px-3 py-2 rounded text-sm font-medium text-slate-300 transition hover:bg-slate-800 hover:text-white"
        active-class="bg-slate-800 text-white"
      >
        <component :is="item.icone" class="h-5 w-5" />
        {{ item.label }}
      </router-link>
    </nav>

    <router-link
      to="/perfil"
      class="flex items-center gap-3 px-4 py-4 border-t border-slate-800 text-sm font-medium text-slate-300 transition hover:bg-slate-800 hover:text-white"
      active-class="bg-slate-800 text-white"
    >
      <span class="flex items-center justify-center h-8 w-8 rounded-full bg-slate-700 text-slate-300">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="h-5 w-5">
          <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0Zm-8 10a5 5 0 0 1 10 0" />
        </svg>
      </span>
      Meu Perfil
    </router-link>
  </aside>
</template>

<script setup>
import { h } from 'vue'
import logo from '../../assets/logo/nortedata_logoenome-semfundo.png'

function icone(path) {
  return () =>
    h(
      'svg',
      { xmlns: 'http://www.w3.org/2000/svg', viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 1.5 },
      [h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', d: path })]
    )
}

const itens = [
  { label: 'Painel Principal', to: '/dashboard', icone: icone('M4 6h7V4H4v2Zm0 7h7v-5H4v5Zm9 7h7V10h-7v10Zm0-17v5h7V3h-7Z') },
  { label: 'Minha Carteira', to: '/carteira', icone: icone('M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V7Z') },
  { label: 'Consultores', to: '/consultores', icone: icone('M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0ZM4 21a6 6 0 0 1 12 0M18 21a5.5 5.5 0 0 0-3-4.9M15 3.1a4 4 0 0 1 0 7.8') },
  { label: 'Configurações', to: '/configuracoes', icone: icone('M10.3 3h3.4l.6 2.6a7.9 7.9 0 0 1 2 1.2l2.6-.8 1.7 3-2 1.8a8 8 0 0 1 0 2.4l2 1.8-1.7 3-2.6-.8a7.9 7.9 0 0 1-2 1.2L13.7 21h-3.4l-.6-2.6a7.9 7.9 0 0 1-2-1.2l-2.6.8-1.7-3 2-1.8a8 8 0 0 1 0-2.4l-2-1.8 1.7-3 2.6.8a7.9 7.9 0 0 1 2-1.2L10.3 3ZM9 12a3 3 0 1 0 6 0 3 3 0 0 0-6 0Z') },
  { label: 'Importar Dados', to: '/upload', icone: icone('M12 4v10m0-10 4 4m-4-4-4 4M4 16v2a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-2') },
]
</script>
