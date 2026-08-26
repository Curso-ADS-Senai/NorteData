<template>
  <div class="flex flex-col min-h-screen">
    <main class="flex-1 px-6 pt-10 pb-16 sm:px-10">
    <div class="flex flex-col items-center text-center">
      <div class="flex items-center gap-4">
        <button type="button" @click="voltarLandingPage" class="cursor-pointer">
          <img :src="logo3" alt="Norte Data" class="h-16 sm:h-20" />
        </button>
        <h1 class="text-3xl font-extrabold text-slate-900 sm:text-4xl">Crie sua Conta na Norte Data</h1>
      </div>
      <p class="mt-2 text-gray-600">Transforme seus dados em estratégias.</p>
    </div>

    <form class="grid gap-x-8 gap-y-5 mx-auto mt-10 max-w-3xl sm:grid-cols-2" @submit.prevent="criarConta">
      <div>
        <label for="nome" class="block text-sm font-medium text-slate-900">Nome Completo</label>
        <div class="relative mt-1">
          <input
            id="nome"
            type="text"
            placeholder="João da Silva"
            :value="nome"
            @input="atualizarNome"
            class="w-full px-3 py-2 text-sm border border-gray-300 rounded"
          />
        </div>
      </div>

      <div>
        <label for="empresa" class="block text-sm font-medium text-slate-900">Nome da Empresa</label>
        <div class="relative mt-1">
          <input
            id="empresa"
            type="text"
            placeholder="Empresa S.A."
            class="w-full px-3 py-2 pr-10 text-sm border border-gray-300 rounded"
          />
          <span class="absolute inset-y-0 right-3 flex items-center text-gray-300">[icone]</span>
        </div>
      </div>

      <div>
        <label for="email" class="block text-sm font-medium text-slate-900">E-mail Corporativo</label>
        <div class="relative mt-1">
          <input
            id="email"
            type="email"
            placeholder="contato@empresa.com.br"
            class="w-full px-3 py-2 pr-10 text-sm border border-gray-300 rounded"
          />
          <span class="absolute inset-y-0 right-3 flex items-center text-gray-300">[icone]</span>
        </div>
      </div>

      <div>
        <label for="telefone" class="block text-sm font-medium text-slate-900">Telefone de Contato</label>
        <div class="relative mt-1">
          <input
            id="telefone"
            type="tel"
            inputmode="numeric"
            placeholder="(11) 98765-4321"
            :value="telefone"
            @input="atualizarTelefone"
            class="w-full px-3 py-2 text-sm border border-gray-300 rounded"
          />
        </div>
      </div>

      <div>
        <label for="senha" class="block text-sm font-medium text-slate-900">Nova Senha (requerido)</label>
        <div class="relative mt-1">
          <input
            id="senha"
            :type="mostrarSenha ? 'text' : 'password'"
            class="w-full px-3 py-2 pr-10 text-sm border border-gray-300 rounded"
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

      <div>
        <label for="confirmar-senha" class="block text-sm font-medium text-slate-900">Confirmar Senha</label>
        <div class="relative mt-1">
          <input
            id="confirmar-senha"
            :type="mostrarConfirmarSenha ? 'text' : 'password'"
            class="w-full px-3 py-2 pr-10 text-sm border border-gray-300 rounded"
          />
          <button
            type="button"
            @click="mostrarConfirmarSenha = !mostrarConfirmarSenha"
            class="absolute inset-y-0 right-3 flex items-center cursor-pointer"
          >
            <img :src="mostrarConfirmarSenha ? iconeEsconderSenha : iconeMostrarSenha" alt="" class="h-5 w-5" />
          </button>
        </div>
      </div>

      <button
        type="submit"
        class="mt-2 px-8 py-3 font-semibold bg-red-600 text-white rounded transition hover:bg-red-700 cursor-pointer sm:col-span-2"
      >
        Criar Conta
      </button>

      <p class="text-center text-sm text-gray-600 sm:col-span-2">
        Já tem uma conta?
        <a href="#" class="font-semibold text-red-600 hover:text-red-700">Entrar</a>
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

const router = useRouter()
const mostrarSenha = ref(false)
const mostrarConfirmarSenha = ref(false)
const nome = ref('')
const telefone = ref('')

function voltarLandingPage() {
  router.push('/')
}

function atualizarNome(e) {
  const filtrado = e.target.value.replace(/[0-9]/g, '')
  e.target.value = filtrado
  nome.value = filtrado
}

function atualizarTelefone(e) {
  let numeros = e.target.value.replace(/\D/g, '').slice(0, 11)

  if (numeros.length > 10) {
    numeros = numeros.replace(/^(\d{2})(\d{5})(\d{0,4}).*/, '($1) $2-$3')
  } else if (numeros.length > 6) {
    numeros = numeros.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, '($1) $2-$3')
  } else if (numeros.length > 2) {
    numeros = numeros.replace(/^(\d{2})(\d{0,5})/, '($1) $2')
  } else if (numeros.length > 0) {
    numeros = numeros.replace(/^(\d*)/, '($1')
  }

  e.target.value = numeros
  telefone.value = numeros
git}

function criarConta() {
  // quando o back-end estiver configurado,
  // isso envia os dados do formulário
  console.log('criar conta')
}
</script>
