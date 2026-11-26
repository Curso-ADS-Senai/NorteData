<template>
  <AppLayout>
    <header class="pt-2">
      <h1 class="text-2xl font-bold text-slate-900">Perfil e Configurações</h1>
      <p class="mt-1 text-gray-600">Dados da sua conta e preferências de exibição do sistema.</p>
    </header>

    <p v-if="erro" class="mt-6 text-sm text-red-600">{{ erro }}</p>

    <section class="p-5 mt-6 bg-white rounded-lg border border-gray-200 shadow-sm">
      <h2 class="font-bold text-slate-900">Dados da conta</h2>

      <dl class="grid gap-x-8 gap-y-4 mt-4 sm:grid-cols-2">
        <div>
          <dt class="text-sm text-gray-500">Nome</dt>
          <dd class="mt-1 font-medium text-slate-900">{{ usuario?.nome ?? '—' }}</dd>
        </div>
        <div>
          <dt class="text-sm text-gray-500">E-mail</dt>
          <dd class="mt-1 font-medium text-slate-900">{{ usuario?.email ?? '—' }}</dd>
        </div>
        <div>
          <dt class="text-sm text-gray-500">Perfil de acesso</dt>
          <dd class="mt-1">
            <span class="px-2 py-0.5 text-xs font-semibold bg-slate-100 text-slate-700 rounded-full">
              {{ usuario?.perfil ?? '—' }}
            </span>
          </dd>
        </div>
        <div>
          <dt class="text-sm text-gray-500">Status</dt>
          <dd class="mt-1">
            <span class="px-2 py-0.5 text-xs font-semibold rounded-full" :class="corDoStatus(usuario?.status)">
              {{ usuario?.status ?? '—' }}
            </span>
          </dd>
        </div>
      </dl>

      <p class="mt-5 text-sm text-gray-500">
        A alteração de nome e senha ainda não está disponível — depende de um endpoint que o
        back-end não expõe. Para trocar o perfil de acesso de alguém, use a tela de
        <router-link to="/usuarios" class="font-semibold text-red-600 transition hover:text-red-700">
          Usuários
        </router-link>
        (restrita a ADMIN).
      </p>
    </section>

    <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
      <h2 class="font-bold text-slate-900">Preferências</h2>

      <label class="flex gap-3 items-start mt-4 cursor-pointer">
        <input
          type="checkbox"
          :checked="ui.sidebarColapsada"
          @change="ui.definirSidebarColapsada($event.target.checked)"
          class="mt-1"
        />
        <span>
          <span class="block font-medium text-slate-900">Menu lateral recolhido</span>
          <span class="block text-sm text-gray-500">
            Mostra só os ícones e libera espaço para os gráficos. Fica salvo neste navegador.
          </span>
        </span>
      </label>
    </section>

    <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
      <h2 class="font-bold text-slate-900">Sessão</h2>
      <p class="mt-1 text-sm text-gray-500">Encerra a sessão neste navegador e volta para o login.</p>
      <button
        type="button"
        @click="sair"
        class="px-5 py-2 mt-4 text-sm font-semibold bg-red-600 text-white rounded transition hover:bg-red-700 cursor-pointer"
      >
        Sair da conta
      </button>
    </section>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from './layout/AppLayout.vue'
import { extrairMensagemErro } from '../services/api.js'
import { useAuthStore } from '../stores/auth.js'
import { useUiStore } from '../stores/ui.js'

const router = useRouter()
const auth = useAuthStore()
const ui = useUiStore()
const erro = ref('')

const usuario = computed(() => auth.usuario)

function corDoStatus(status) {
  if (status === 'ATIVO') return 'bg-green-100 text-green-700'
  if (status === 'BLOQUEADO') return 'bg-red-100 text-red-700'
  return 'bg-amber-100 text-amber-700'
}

function sair() {
  auth.logout()
  router.push('/login')
}

// O usuário do localStorage é o do login e pode estar velho; relê no backend.
// Falhar aqui não esvazia a tela — o cache continua válido para exibição.
onMounted(async () => {
  try {
    await auth.carregarUsuario()
  } catch (e) {
    erro.value = extrairMensagemErro(e, 'Não foi possível atualizar os dados da conta.')
  }
})
</script>
