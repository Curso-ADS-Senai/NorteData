<template>
  <div class="flex flex-col min-h-screen bg-gray-50">
    <main class="flex flex-col flex-1 justify-center items-center px-6 text-center">
      <p class="text-6xl font-bold text-red-600">404</p>
      <h1 class="mt-4 text-2xl font-bold text-slate-900">Página não encontrada</h1>
      <p class="mt-2 max-w-md text-gray-600">
        O endereço <span class="font-semibold text-slate-900">{{ caminho }}</span> não existe no Norte Data.
        Ele pode ter sido removido ou você digitou errado.
      </p>

      <nav class="flex gap-3 mt-8">
        <router-link
          :to="destinoPrincipal"
          class="px-5 py-2 text-sm font-semibold bg-slate-900 text-white rounded transition hover:bg-slate-700"
        >
          {{ auth.token ? 'Ir para o painel' : 'Ir para o início' }}
        </router-link>
        <button
          type="button"
          @click="voltar"
          class="px-5 py-2 text-sm font-semibold text-slate-900 bg-white rounded border border-gray-300 transition hover:bg-gray-100 cursor-pointer"
        >
          Voltar
        </button>
      </nav>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Footer from './layout/Footer.vue'
import { useAuthStore } from '../stores/auth.js'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const caminho = computed(() => route.fullPath)

// Quem está logado volta para o dashboard; visitante volta para a landing.
const destinoPrincipal = computed(() => (auth.token ? '/dashboard' : '/'))

// window.history.length <= 1 quando a 404 foi o primeiro acesso da aba:
// nesse caso não há para onde voltar e o botão levaria a tela em branco.
function voltar() {
  if (window.history.length > 1) {
    router.back()
  } else {
    router.push(destinoPrincipal.value)
  }
}
</script>
