<template>
  <AppLayout>
    <header class="pt-2">
      <h1 class="text-2xl font-bold text-slate-900">Histórico de Uploads</h1>
      <p class="mt-1 text-gray-600">Todas as planilhas enviadas, da mais recente para a mais antiga.</p>
    </header>

    <section class="p-5 mt-6 bg-white rounded-lg border border-gray-200 shadow-sm">
      <p v-if="carregando" class="py-4 text-sm text-gray-500">Carregando…</p>

      <p v-else-if="erro" class="py-4 text-sm text-red-600">{{ erro }}</p>

      <div v-else-if="lotes.length === 0" class="py-8 text-center">
        <p class="text-gray-600">Nenhuma planilha foi enviada ainda.</p>
        <router-link
          to="/upload"
          class="inline-block px-5 py-2 mt-4 text-sm font-semibold bg-slate-900 text-white rounded transition hover:bg-slate-700"
        >
          Enviar a primeira planilha
        </router-link>
      </div>

      <div v-else class="overflow-x-auto">
        <table class="w-full text-sm">
          <thead>
            <tr class="text-left text-gray-500 border-b border-gray-200">
              <th class="py-2 pr-4 font-medium">Arquivo</th>
              <th class="py-2 pr-4 font-medium">Enviado em</th>
              <th class="py-2 pr-4 font-medium">Status</th>
              <th class="py-2 pr-4 font-medium text-right">Recebidas</th>
              <th class="py-2 pr-4 font-medium text-right">Válidas</th>
              <th class="py-2 pr-4 font-medium text-right">Inválidas</th>
              <th class="py-2 pr-4 font-medium">Observação</th>
              <th class="py-2 pr-4 font-medium"></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="lote in lotes" :key="lote.id" class="border-b border-gray-100 last:border-0">
              <td class="py-2 pr-4 font-medium text-slate-900">{{ lote.arquivoNome }}</td>
              <td class="py-2 pr-4 text-gray-600">{{ dataHora(lote.enviadoEm) }}</td>
              <td class="py-2 pr-4">
                <span class="px-2 py-0.5 text-xs font-semibold rounded-full" :class="corDoStatus(lote.status)">
                  {{ lote.status }}
                </span>
              </td>
              <td class="py-2 pr-4 text-right text-slate-900">{{ lote.totalLinhas ?? '—' }}</td>
              <td class="py-2 pr-4 text-right text-green-700">{{ lote.linhasValidas ?? '—' }}</td>
              <td class="py-2 pr-4 text-right text-red-600">{{ lote.linhasInvalidas ?? '—' }}</td>
              <td class="py-2 pr-4 text-gray-600">{{ lote.mensagem ?? '—' }}</td>
              <td class="py-2 pr-4 text-right">
                <router-link
                  v-if="lote.status === 'PROCESSADO'"
                  :to="{ path: '/dashboard', query: { loteId: lote.id } }"
                  class="font-semibold text-red-600 transition hover:text-red-700"
                >
                  Ver no painel
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AppLayout>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import AppLayout from './layout/AppLayout.vue'
import { extrairMensagemErro } from '../services/api.js'
import { listarLotes } from '../services/dashboard.js'

const lotes = ref([])
const erro = ref('')
const carregando = ref(true)

const dataHora = (t) => (t ? new Date(t).toLocaleString('pt-BR') : '—')

// Mesmas cores do cabeçalho do Dashboard, para o status significar o mesmo nas duas telas.
function corDoStatus(status) {
  if (status === 'PROCESSADO') return 'bg-green-100 text-green-700'
  if (status === 'ERRO') return 'bg-red-100 text-red-700'
  return 'bg-amber-100 text-amber-700'
}

onMounted(async () => {
  try {
    const { data } = await listarLotes()
    lotes.value = data
  } catch (e) {
    erro.value = extrairMensagemErro(e, 'Não foi possível carregar o histórico de uploads.')
  } finally {
    carregando.value = false
  }
})
</script>
