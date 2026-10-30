<template>
  <div class="flex flex-col min-h-screen">
    <main class="flex-1 px-6 pt-10 pb-16 sm:px-10">
      <div class="flex flex-col items-center text-center">
        <div class="flex items-center gap-4">
          <button type="button" @click="voltarLandingPage" class="cursor-pointer">
            <img :src="logo3" alt="Norte Data" class="h-16 sm:h-20" />
          </button>
          <h1 class="text-3xl font-extrabold text-slate-900 sm:text-4xl">Faça Login na sua Conta Norte Data</h1>
        </div>
        <p class="mt-2 text-gray-600">Insira suas credenciais para acessar o painel de insights.</p>
      </div>

      <form class="grid gap-y-5 mx-auto mt-10 max-w-md" @submit.prevent="entrar">
        <div>
          <label for="email" class="block text-sm font-medium text-slate-900">E-mail Corporativo</label>
          <div class="relative mt-1">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="absolute inset-y-0 left-3 my-auto h-5 w-5 text-gray-400">
              <path stroke-linecap="round" stroke-linejoin="round" d="M16 7a4 4 0 1 1-8 0 4 4 0 0 1 8 0Zm-8 10a5 5 0 0 1 10 0" />
              <circle cx="12" cy="7" r="4" stroke-linecap="round" stroke-linejoin="round" />
            </svg>
            <input
              id="email"
              type="email"
              placeholder="contato@empresa.com.br"
              :value="email"
              @input="email = $event.target.value"
              class="w-full pl-10 pr-3 py-2 text-sm border border-gray-300 rounded"
            />
          </div>
        </div>

        <div>
          <label for="senha" class="block text-sm font-medium text-slate-900">Senha</label>
          <div class="relative mt-1">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="absolute inset-y-0 left-3 my-auto h-5 w-5 text-gray-400">
              <rect x="5" y="10" width="14" height="10" rx="2" stroke-linecap="round" stroke-linejoin="round" />
              <path stroke-linecap="round" stroke-linejoin="round" d="M8 10V7a4 4 0 0 1 8 0v3" />
            </svg>
            <input
              id="senha"
              :type="mostrarSenha ? 'text' : 'password'"
              :value="senha"
              @input="senha = $event.target.value"
              class="w-full pl-10 pr-10 py-2 text-sm border border-gray-300 rounded"
            />
            <button
              type="button"
              @click="mostrarSenha = !mostrarSenha"
              class="absolute inset-y-0 right-3 flex items-center cursor-pointer"
            >
              <img :src="mostrarSenha ? iconeEsconderSenha : iconeMostrarSenha" alt="" class="h-5 w-5" />
            </button>
          </div>
        </div>

        <div class="flex items-center justify-between text-sm">
          <label for="lembrar-me" class="flex items-center gap-2 text-slate-900">
            <input id="lembrar-me" type="checkbox" v-model="lembrarMe" class="rounded border-gray-300" />
            Lembrar-me
          </label>
          <a href="#" class="font-semibold text-red-600 hover:text-red-700">Esqueci minha senha</a>
        </div>

        <div v-if="erro" class="p-4 bg-red-50 rounded-lg border border-red-200">
          <p class="text-sm text-red-600">{{ erro }}</p>
        </div>

        <button
          type="submit"
          class="px-8 py-3 font-semibold bg-red-600 text-white rounded transition hover:bg-red-700 cursor-pointer"
        >
          Entrar
        </button>

        <p class="text-center text-sm text-gray-600">
          Não tem uma conta?
          <a href="#" @click.prevent="irParaCadastro" class="font-semibold text-red-600 hover:text-red-700">Crie uma agora</a>
        </p>
      </form>
    </main>

    <Footer />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import logo3 from '../assets/logo/nortedata_logosemfundo.png'
import iconeMostrarSenha from '../assets/logo/mostrar-senha.png'
import iconeEsconderSenha from '../assets/logo/esconder-senha.png'
import Footer from './layout/Footer.vue'
import { useAuthStore } from '../stores/auth.js'
import { extrairMensagemErro } from '../services/api.js'

const router = useRouter()
const auth = useAuthStore()
const mostrarSenha = ref(false)
const email = ref('')
const senha = ref('')
const lembrarMe = ref(false)
const erro = ref('')

function voltarLandingPage() {
  router.push('/')
}

function irParaCadastro() {
  router.push('/cadastro')
}

async function entrar() {
  erro.value = ''

  try {
    await auth.login(email.value, senha.value)
    router.push('/dashboard')
  } catch (e) {
    erro.value = extrairMensagemErro(e, 'E-mail ou senha inválidos.')
  }
}
</script>
