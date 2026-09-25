<template>
  <AppLayout>
    <div v-if="store.status !== 'concluido'" class="flex flex-col items-center gap-3 p-10 mt-6 bg-white rounded-lg border border-gray-200 shadow-sm">
      <p class="text-slate-900">Nenhuma planilha foi processada ainda.</p>
      <router-link to="/upload" class="font-semibold text-red-600 hover:text-red-700">Ir para o upload</router-link>
    </div>

    <template v-else>
      <div class="flex flex-col gap-1 pt-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 class="text-2xl font-bold text-slate-900">Relatório de Validação</h1>
          <p class="mt-1 text-gray-600">Arquivo analisado: {{ store.nomeArquivo }}</p>
        </div>
        <button
          type="button"
          @click="novaAnalise"
          class="px-5 py-2 text-sm font-semibold bg-red-600 text-white rounded transition hover:bg-red-700 cursor-pointer"
        >
          Nova Análise
        </button>
      </div>

      <section class="grid gap-4 mt-6 sm:grid-cols-3">
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="text-sm text-gray-500">Total de Registros</h2>
          <p class="mt-1 text-2xl font-bold text-slate-900">{{ store.totalRegistros }}</p>
        </article>
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="text-sm text-gray-500">Registros Válidos</h2>
          <p class="mt-1 text-2xl font-bold text-green-600">{{ store.totalRegistrosValidos }}</p>
        </article>
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="text-sm text-gray-500">Registros com Erro</h2>
          <p class="mt-1 text-2xl font-bold text-red-600">{{ store.totalRegistrosComErro }}</p>
        </article>
      </section>

      <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
        <h2 class="font-bold text-slate-900">Dados da Planilha</h2>
        <p class="mt-1 text-sm text-gray-500">Campos com contorno vermelho indicam um erro encontrado naquele registro.</p>
        <div class="overflow-x-auto mt-4">
          <table class="w-full text-sm">
            <thead>
              <tr class="text-left text-gray-500 border-b border-gray-200">
                <th class="py-2 pr-4 font-medium">Linha</th>
                <th v-for="campo in colunasPlanilha" :key="campo" class="py-2 pr-4 font-medium">
                  {{ CAMPOS_LABELS[campo] ?? campo }}
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="linha in store.linhas" :key="linha._linha" class="border-b border-gray-100 last:border-0">
                <td class="py-2 pr-4 text-gray-600">{{ linha._linha }}</td>
                <td
                  v-for="campo in colunasPlanilha"
                  :key="campo"
                  class="py-2 pr-4 text-slate-900"
                  :class="{ 'border border-red-500 rounded bg-red-50 text-red-900': erroDoCampo(linha._linha, campo) }"
                  :title="erroDoCampo(linha._linha, campo)?.descricao ?? ''"
                >
                  {{ formatarValor(linha[campo]) }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
        <button
          type="button"
          @click="mostrarErrosPorTipo = !mostrarErrosPorTipo"
          class="flex w-full items-center justify-between text-left cursor-pointer"
        >
          <h2 class="font-bold text-slate-900">Erros por Tipo</h2>
          <svg
            xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
            class="h-5 w-5 text-gray-500 transition-transform"
            :class="{ 'rotate-180': mostrarErrosPorTipo }"
          >
            <path stroke-linecap="round" stroke-linejoin="round" d="m6 9 6 6 6-6" />
          </svg>
        </button>

        <table v-if="mostrarErrosPorTipo" class="w-full mt-4 text-sm">
          <thead>
            <tr class="text-left text-gray-500 border-b border-gray-200">
              <th class="py-2 pr-4 font-medium">Tipo</th>
              <th class="py-2 pr-4 font-medium">Quantidade</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="tipo in tiposComErro" :key="tipo" class="border-b border-gray-100 last:border-0">
              <td class="py-2 pr-4 text-slate-900">{{ TIPOS_ERRO_LABELS[tipo] ?? tipo }}</td>
              <td class="py-2 pr-4 text-gray-600">{{ store.errosPorTipo[tipo] }}</td>
            </tr>
            <tr class="border-t border-gray-200">
              <td class="py-2 pr-4 font-semibold text-slate-900">Registros válidos</td>
              <td class="py-2 pr-4 font-semibold text-green-600">{{ store.totalRegistrosValidos }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
        <button
          type="button"
          @click="mostrarDetalhamento = !mostrarDetalhamento"
          class="flex w-full items-center justify-between text-left cursor-pointer"
        >
          <h2 class="font-bold text-slate-900">Detalhamento dos Erros</h2>
          <svg
            xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"
            class="h-5 w-5 text-gray-500 transition-transform"
            :class="{ 'rotate-180': mostrarDetalhamento }"
          >
            <path stroke-linecap="round" stroke-linejoin="round" d="m6 9 6 6 6-6" />
          </svg>
        </button>

        <template v-if="mostrarDetalhamento">
          <div class="flex justify-end mt-4">
            <select v-model="filtroTipo" class="px-3 py-2 text-sm border border-gray-300 rounded">
              <option value="">Todos os tipos</option>
              <option v-for="tipo in tiposComErro" :key="tipo" :value="tipo">{{ TIPOS_ERRO_LABELS[tipo] ?? tipo }}</option>
            </select>
          </div>

          <table class="w-full mt-4 text-sm">
            <thead>
              <tr class="text-left text-gray-500 border-b border-gray-200">
                <th class="py-2 pr-4 font-medium">Linha</th>
                <th class="py-2 pr-4 font-medium">Campo</th>
                <th class="py-2 pr-4 font-medium">Tipo</th>
                <th class="py-2 pr-4 font-medium">Descrição</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(erro, indice) in errosFiltrados" :key="indice" class="border-b border-gray-100 last:border-0">
                <td class="py-2 pr-4 text-slate-900">{{ erro.linha }}</td>
                <td class="py-2 pr-4 text-slate-900">{{ CAMPOS_LABELS[erro.campo] ?? erro.campo }}</td>
                <td class="py-2 pr-4 text-gray-600">{{ TIPOS_ERRO_LABELS[erro.tipo] ?? erro.tipo }}</td>
                <td class="py-2 pr-4 text-gray-600">{{ erro.descricao }}</td>
              </tr>
              <tr v-if="errosFiltrados.length === 0">
                <td colspan="4" class="py-4 text-center text-gray-500">Nenhum erro encontrado.</td>
              </tr>
            </tbody>
          </table>
        </template>
      </section>
    </template>
  </AppLayout>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from './layout/AppLayout.vue'
import { useValidacaoPlanilhaStore, TIPOS_ERRO_LABELS, CAMPOS_LABELS } from '../stores/validacaoPlanilha.js'

const router = useRouter()
const store = useValidacaoPlanilhaStore()

const filtroTipo = ref('')
const mostrarErrosPorTipo = ref(false)
const mostrarDetalhamento = ref(false)

const tiposComErro = computed(() => Object.keys(store.errosPorTipo))

const colunasPlanilha = computed(() => Object.keys(CAMPOS_LABELS).filter((campo) => campo in (store.linhas[0] ?? {})))

const errosPorLinhaECampo = computed(() => {
  const mapa = new Map()
  for (const erro of store.erros) {
    mapa.set(`${erro.linha}|${erro.campo}`, erro)
  }
  return mapa
})

function erroDoCampo(linha, campo) {
  return errosPorLinhaECampo.value.get(`${linha}|${campo}`)
}

function formatarValor(valor) {
  if (valor instanceof Date) return valor.toLocaleDateString('pt-BR')
  return valor
}

const errosFiltrados = computed(() =>
  filtroTipo.value ? store.erros.filter((erro) => erro.tipo === filtroTipo.value) : store.erros
)

function novaAnalise() {
  store.resetar()
  router.push('/upload')
}
</script>
