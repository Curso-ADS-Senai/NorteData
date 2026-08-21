<template>
  <header class="flex justify-between items-center px-6 py-4 sm:px-10 bg-white shadow h-20">
    <img :src="logo" alt="Norte Data" class="h-20 sm:h-24" />
    <nav class="hidden md:flex gap-8 font-medium text-slate-900">
      <a href="#inicio" class="hover:text-red-600">Início</a>
      <a href="#solucao" class="hover:text-red-600">Solução</a>
      <a href="#recursos" class="hover:text-red-600">Recursos</a>
      <a href="#sobre" class="hover:text-red-600">Sobre</a>
    </nav>

    <button
      type="button"
      @click="acessarSistema"
      class="px-5 py-2 font-semibold bg-slate-900 text-white rounded transition hover:bg-slate-800 cursor-pointer"
    >
      Acessar Plataforma
    </button>
  </header>

  <main>
    <section id="inicio" class="grid lg:grid-cols-2 lg:items-center gap-10 px-6 py-16 sm:px-10 lg:py-24">
      <div>
        <h1 class="text-4xl font-extrabold leading-tight sm:text-5xl text-slate-900">
          Transforme planilhas operacionais em direcionamento estratégico para o seu negócio.
        </h1>
        <p class="mt-6 max-w-xl text-lg text-gray-600">
          Chega de dados isolados no Excel. O Norte Data organiza, padroniza e analisa a
          sua carteira de clientes para gerar insights comerciais assertivos.
        </p>
        <button
          type="button"
          @click="irParaCadastro"
          class="mt-8 px-8 py-3 font-semibold bg-red-600 text-white rounded transition hover:bg-red-700 cursor-pointer"
        >
          Cadastre-se e acesse a plataforma.
        </button>
      </div>

      <div
        class="flex items-center justify-center aspect-video bg-gray-50 text-gray-400 rounded-lg border-2 border-dashed border-gray-300"
      >
        [ placeholder: preview do dashboard ]
      </div>
    </section>

    <section id="solucao" class="grid md:grid-cols-3 gap-6 px-6 pb-20 sm:px-10">
      <article
        v-for="card in cards"
        :key="card.title"
        class="p-6 bg-slate-900 text-white rounded-md border-l-4 border-red-500"
      >
        <h3 class="text-lg font-bold">{{ card.title }}</h3>
        <p class="mt-2 text-sm text-slate-300">{{ card.description }}</p>
      </article>
    </section>

    <section id="recursos" class="px-6 py-16 sm:px-10 bg-gray-50">
      <h2 class="text-center text-2xl font-bold text-slate-900">O que o sistema faz:</h2>
      <div class="grid sm:grid-cols-2 lg:grid-cols-4 gap-6 mx-auto mt-10 max-w-6xl">
        <article
          v-for="recurso in recursos"
          :key="recurso.title"
          class="group relative flex flex-col justify-end aspect-video overflow-hidden bg-gray-100 rounded-lg border border-gray-200 shadow-sm transition duration-200 hover:-translate-y-1 hover:shadow-lg"
        >
          <div
            class="absolute inset-0 flex items-center justify-center text-xs text-gray-400 border-2 border-dashed border-gray-300 transition duration-200 group-hover:scale-105"
          >
            [ placeholder: {{ recurso.imagem }} ]
          </div>
          <div class="relative p-4 bg-gradient-to-t from-black/70 to-transparent">
            <h3 class="font-bold text-white">{{ recurso.title }}</h3>
            <p class="mt-1 text-sm text-gray-200">{{ recurso.description }}</p>
          </div>
        </article>
      </div>
    </section>
  </main>

    <Footer />
</template>

<script setup>
import { useRouter } from 'vue-router'
import logo from '../assets/logo/nortedata_logoenome-semfundo.png'
import Footer from './layout/Footer.vue'
//import logo2 from'../assets/logo/nortedata_logosemfundo.png'

const router = useRouter()

const cards = [
  {
    title: 'Tratamento de Dados',
    description:
      'Compilamos dados de planilhas inconsistentes e padronizamos formatos, evitando erros de digitação e duplicidade.',
  },
  {
    title: 'Visão 360',
    description:
      'O Norte Data organiza, padroniza e analisa a sua carteira de clientes para gerar dashboards comerciais completos.',
  },
  {
    title: 'Identificação de Oportunidades',
    description:
      'Cruzamento de dados por segmento, faturamento e status para revelar as melhores oportunidades de negócio.',
  },
]

const recursos = [
  {
    title: 'Upload automático',
    description: 'Envie a planilha direto na plataforma, sem tratamento manual antes.',
    imagem: 'upload.png',
  },
  {
    title: 'Padronização de dados',
    description: 'Corrige inconsistências de digitação e unifica os valores da base.',
    imagem: 'padronizacao.png',
  },
  {
    title: 'Dashboards interativos',
    description: 'Gráficos de segmento, faturamento e nível de clientes em tempo real.',
    imagem: 'dashboards.png',
  },
  {
    title: 'Insights estratégicos',
    description: 'Respostas rápidas para as perguntas de negócio mais importantes.',
    imagem: 'insights.png',
  },
]

function acessarSistema() {
  // a tela de login ainda não existe, então isso continua um placeholder
  console.log('ir para login')
}

function irParaCadastro() {
  router.push('/cadastro')
}
</script>
