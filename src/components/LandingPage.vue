<template>
  <header class="flex justify-between items-center px-6 py-4 sm:px-10 bg-white shadow">
    <img :src="logo" alt="Norte Data" class="h-10 sm:h-12" />

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
          @click="acessarSistema"
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
      <h2 class="text-center text-2xl font-bold text-slate-900">O que o sistema faz</h2>
      <ul class="flex flex-col text-center mx-auto mt-6 max-w-2xl gap-2 text-gray-700">
        <li>Upload automático de planilhas</li>
        <li>Tratamento e padronização de dados</li>
        <li>Gráficos e dashboards interativos</li>
        <li>Insights estratégicos para a empresa</li>
      </ul>
    </section>
  </main>

  <footer id="sobre" class="px-6 py-10 sm:px-10 bg-slate-900 text-white">
    <div class="flex flex-col items-center md:flex-row md:justify-between gap-6">
      <img :src="logo" alt="Norte Data" class="h-10 brightness-0 invert" />

      <p class="text-center text-sm text-slate-400 md:text-center">
        Norte Data© &mdash; Todos os direitos reservados.
      </p>

    </div>
  </footer>
</template>

<script setup>
import logo from '../assets/logo/nortedata_logoenome.png'

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

function acessarSistema() {
  // quando o vue-router estiver configurado,
  // isso vira um router.push('/login')
  console.log('ir para login')
}
</script>
