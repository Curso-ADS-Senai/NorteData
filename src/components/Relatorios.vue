<template>
  <AppLayout>
    <header class="pt-2">
      <h1 class="text-2xl font-bold text-slate-900">Relatórios</h1>
      <p class="mt-1 text-gray-600">
        Carteira de clientes da planilha mais recente. Busque por nome ou código e filtre por
        segmento e nível.
      </p>
    </header>

    <section class="p-5 mt-6 bg-white rounded-lg border border-gray-200 shadow-sm">
      <form class="flex flex-wrap gap-3 items-end" @submit.prevent>
        <label class="flex-1 min-w-60 text-sm">
          <span class="block mb-1 text-gray-500">Buscar</span>
          <input
            v-model="busca"
            type="search"
            placeholder="Nome da empresa ou código (ex: CTI015)"
            class="px-3 py-2 w-full text-sm border border-gray-300 rounded"
          />
        </label>

        <label class="text-sm">
          <span class="block mb-1 text-gray-500">Segmento</span>
          <select v-model="segmento" class="px-3 py-2 text-sm bg-white border border-gray-300 rounded">
            <option value="">Todos</option>
            <option v-for="s in segmentos" :key="s" :value="s">{{ s }}</option>
          </select>
        </label>

        <label class="text-sm">
          <span class="block mb-1 text-gray-500">Nível</span>
          <select v-model="nivel" class="px-3 py-2 text-sm bg-white border border-gray-300 rounded">
            <option value="">Todos</option>
            <option v-for="n in niveis" :key="n" :value="n">{{ n }}</option>
          </select>
        </label>

        <button
          v-if="temFiltro"
          type="button"
          @click="limparFiltros"
          class="px-4 py-2 text-sm font-semibold text-slate-900 bg-white rounded border border-gray-300 transition hover:bg-gray-100 cursor-pointer"
        >
          Limpar
        </button>
      </form>

      <p v-if="erro" class="mt-4 text-sm text-red-600">{{ erro }}</p>

      <p v-else-if="carregando" class="mt-4 text-sm text-gray-500">Carregando…</p>

      <div v-else-if="clientes.length === 0" class="py-8 text-center">
        <p v-if="temFiltro" class="text-gray-600">Nenhum cliente encontrado com esses filtros.</p>
        <template v-else>
          <p class="text-gray-600">Nenhuma planilha foi processada ainda.</p>
          <router-link
            to="/upload"
            class="inline-block px-5 py-2 mt-4 text-sm font-semibold bg-slate-900 text-white rounded transition hover:bg-slate-700"
          >
            Enviar uma planilha
          </router-link>
        </template>
      </div>

      <template v-else>
        <p class="mt-4 text-sm text-gray-500">
          {{ clientes.length }} cliente(s){{ loteId ? ` no lote ${loteId}` : '' }}.
        </p>

        <div class="overflow-x-auto">
          <table class="w-full mt-2 text-sm">
            <thead>
              <tr class="text-left text-gray-500 border-b border-gray-200">
                <th class="py-2 pr-4 font-medium">Código</th>
                <th class="py-2 pr-4 font-medium">Cliente</th>
                <th class="py-2 pr-4 font-medium">Segmento</th>
                <th class="py-2 pr-4 font-medium">Nível</th>
                <th class="py-2 pr-4 font-medium">Consultor</th>
                <th class="py-2 pr-4 font-medium">Cidade/UF</th>
                <th class="py-2 pr-4 font-medium text-right">Serviços</th>
                <th class="py-2 pr-4 font-medium text-right">Faturamento anual</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="cliente in clientes"
                :key="cliente.id"
                @click="abrir(cliente.id)"
                class="border-b border-gray-100 last:border-0 transition hover:bg-gray-50 cursor-pointer"
              >
                <td class="py-2 pr-4 text-gray-600">{{ cliente.codigoCliente ?? '—' }}</td>
                <td class="py-2 pr-4 font-medium text-slate-900">{{ cliente.nomeEmpresa }}</td>
                <td class="py-2 pr-4 text-slate-900">{{ cliente.segmento }}</td>
                <td class="py-2 pr-4 text-slate-900">{{ cliente.nivel }}</td>
                <td class="py-2 pr-4 text-slate-900">{{ cliente.consultor }}</td>
                <td class="py-2 pr-4 text-gray-600">{{ localidade(cliente) }}</td>
                <td class="py-2 pr-4 text-right text-slate-900">{{ cliente.qtdServicos }}</td>
                <td class="py-2 pr-4 text-right font-medium text-slate-900">{{ moeda(cliente.faturamentoAnual) }}</td>
              </tr>
            </tbody>
            <tfoot>
              <tr class="border-t border-gray-200">
                <td colspan="7" class="py-2 pr-4 font-semibold text-slate-900">Total</td>
                <td class="py-2 pr-4 text-right font-semibold text-slate-900">{{ moeda(faturamentoTotal) }}</td>
              </tr>
            </tfoot>
          </table>
        </div>
      </template>
    </section>
  </AppLayout>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from './layout/AppLayout.vue'
import { extrairMensagemErro } from '../services/api.js'
import { listarClientes, listarFiltrosClientes } from '../services/clientes.js'

const router = useRouter()

const clientes = ref([])
const segmentos = ref([])
const niveis = ref([])
const loteId = ref(null)
const busca = ref('')
const segmento = ref('')
const nivel = ref('')
const erro = ref('')
const carregando = ref(true)

let debounce = null

const moeda = (n) => (n == null ? '—' : Number(n).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }))

const temFiltro = computed(() => busca.value !== '' || segmento.value !== '' || nivel.value !== '')

const faturamentoTotal = computed(() =>
  clientes.value.reduce((soma, c) => soma + Number(c.faturamentoAnual ?? 0), 0)
)

function localidade(cliente) {
  if (!cliente.cidade && !cliente.uf) return '—'
  return [cliente.cidade, cliente.uf].filter(Boolean).join('/')
}

function limparFiltros() {
  busca.value = ''
  segmento.value = ''
  nivel.value = ''
}

function abrir(id) {
  router.push(`/clientes/${id}`)
}

async function carregar() {
  erro.value = ''
  try {
    const { data } = await listarClientes({ busca: busca.value, segmento: segmento.value, nivel: nivel.value })
    clientes.value = data.clientes
    loteId.value = data.loteId
  } catch (e) {
    clientes.value = []
    erro.value = extrairMensagemErro(e, 'Não foi possível carregar os clientes.')
  } finally {
    carregando.value = false
  }
}

// Digitar dispara uma requisição por tecla; 300 ms espera a pessoa parar de digitar.
// Os selects não precisam de espera, mas passam pelo mesmo caminho por simplicidade.
watch([busca, segmento, nivel], () => {
  clearTimeout(debounce)
  debounce = setTimeout(carregar, 300)
})

// Os filtros saem do próprio lote: só oferecem segmentos e níveis que existem nos dados.
listarFiltrosClientes()
  .then(({ data }) => {
    segmentos.value = data.segmentos
    niveis.value = data.niveis
  })
  .catch(() => {
    segmentos.value = []
    niveis.value = []
  })

carregar()
</script>
