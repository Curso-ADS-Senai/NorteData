<template>
  <AppLayout>
    <h1 class="pt-2 text-2xl font-bold text-slate-900">Upload de Planilha</h1>
    <p class="mt-1 text-gray-600">Envie a planilha de clientes (.xlsx, .xls ou .csv) para validação automática.</p>

    <section
      class="flex flex-col items-center gap-3 p-10 mt-6 bg-white rounded-lg border-2 border-dashed transition"
      :class="arrastando ? 'border-red-600 bg-red-50' : 'border-gray-300'"
      @dragover.prevent="arrastando = true"
      @dragleave.prevent="arrastando = false"
      @drop.prevent="soltarArquivo"
    >
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="h-12 w-12 text-gray-400">
        <path stroke-linecap="round" stroke-linejoin="round" d="M12 4v10m0-10 4 4m-4-4-4 4M4 16v2a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-2" />
      </svg>

      <p class="text-slate-900">
        Arraste a planilha aqui ou
        <button type="button" @click="abrirSeletor" class="font-semibold text-red-600 hover:text-red-700 cursor-pointer">
          escolha um arquivo
        </button>
      </p>
      <p class="text-sm text-gray-500">Formatos aceitos: .xlsx, .xls, .csv</p>

      <input ref="inputArquivo" type="file" accept=".xlsx,.xls,.csv" class="hidden" @change="selecionarArquivo" />

      <p v-if="arquivo" class="mt-2 text-sm font-medium text-slate-900">
        Arquivo selecionado: {{ arquivo.name }}
      </p>
    </section>

    <div v-if="store.erroTemplate" class="p-4 mt-4 bg-red-50 rounded-lg border border-red-200">
      <p class="font-semibold text-red-700">Erro no arquivo enviado</p>
      <p class="mt-1 text-sm text-red-600">{{ store.erroTemplate }}</p>
    </div>

    <div class="flex justify-end mt-6">
      <button
        type="button"
        :disabled="!arquivo || store.status === 'processando'"
        @click="processar"
        class="px-8 py-3 font-semibold bg-red-600 text-white rounded transition hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer"
      >
        {{ store.status === 'processando' ? 'Processando...' : 'Processar Planilha' }}
      </button>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from './layout/AppLayout.vue'
import { useValidacaoPlanilhaStore } from '../stores/validacaoPlanilha.js'

const router = useRouter()
const store = useValidacaoPlanilhaStore()

const inputArquivo = ref(null)
const arquivo = ref(null)
const arrastando = ref(false)

function abrirSeletor() {
  inputArquivo.value.click()
}

function selecionarArquivo(evento) {
  arquivo.value = evento.target.files[0] ?? null
}

function soltarArquivo(evento) {
  arrastando.value = false
  arquivo.value = evento.dataTransfer.files[0] ?? null
}

async function processar() {
  if (!arquivo.value) return
  await store.processarArquivo(arquivo.value)
  if (store.status === 'concluido') {
    router.push('/relatorio-validacao')
  }
}
</script>
