<template>
  <AppLayout>
    <header class="flex flex-wrap gap-4 justify-between items-start pt-2">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">Evolução</h1>
        <p v-if="lote" class="mt-1 text-gray-600">
          Lote {{ lote.id }} · {{ lote.arquivoNome }} · {{ periodoLegivel }}
        </p>
      </div>

      <label v-if="anos.length" class="text-sm">
        <span class="block mb-1 text-gray-500">Ano</span>
        <select v-model="anoEscolhido" class="px-3 py-2 text-sm bg-white border border-gray-300 rounded">
          <option value="">Todos os anos</option>
          <option v-for="ano in anos" :key="ano" :value="ano">{{ ano }}</option>
        </select>
      </label>
    </header>

    <p v-if="carregando" class="mt-6 text-sm text-gray-500">Carregando…</p>

    <section v-else-if="semPlanilha" class="p-8 mt-6 text-center bg-white rounded-lg border border-gray-200 shadow-sm">
      <p class="text-gray-600">Nenhuma planilha foi processada ainda.</p>
      <router-link
        to="/upload"
        class="inline-block px-5 py-2 mt-4 text-sm font-semibold bg-slate-900 text-white rounded transition hover:bg-slate-700"
      >
        Enviar uma planilha
      </router-link>
    </section>

    <p v-else-if="erro" class="mt-6 text-sm text-red-600">{{ erro }}</p>

    <p v-else-if="!estatisticas" class="mt-6 text-sm text-gray-600">
      Este lote ainda não tem estatísticas calculadas.
    </p>

    <template v-else>
      <section class="grid gap-4 mt-6 sm:grid-cols-3">
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="text-sm text-gray-500">Clientes contratados</h2>
          <p class="mt-1 text-2xl font-bold text-slate-900">{{ totalNoPeriodo }}</p>
        </article>
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="text-sm text-gray-500">Média por mês</h2>
          <p class="mt-1 text-2xl font-bold text-slate-900">{{ mediaPorMes }}</p>
        </article>
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="text-sm text-gray-500">Melhor mês</h2>
          <p class="mt-1 text-2xl font-bold text-slate-900">{{ melhorMes }}</p>
        </article>
      </section>

      <section v-if="meses.length === 0" class="p-8 mt-4 text-center bg-white rounded-lg border border-gray-200 shadow-sm">
        <p class="text-gray-600">Nenhuma contratação em {{ anoEscolhido }}.</p>
      </section>

      <template v-else>
        <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="font-bold text-slate-900">Novos clientes por mês</h2>
          <Line :data="dadosNovos" :options="opcoesLinha" class="mt-4" />
        </section>

        <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="font-bold text-slate-900">Base acumulada</h2>
          <p class="mt-1 text-sm text-gray-500">
            Total de clientes na carteira ao fim de cada mês — sempre contado desde o início da
            base, mesmo com o filtro de ano aplicado.
          </p>
          <Line :data="dadosAcumulado" :options="opcoesLinha" class="mt-4" />
        </section>

        <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="font-bold text-slate-900">Faturamento contratado por mês</h2>
          <Bar :data="dadosFaturamento" :options="opcoesBarraMoeda" class="mt-4" />
        </section>
      </template>
    </template>
  </AppLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { Bar, Line } from 'vue-chartjs'
import {
  BarElement,
  CategoryScale,
  Chart as ChartJS,
  Legend,
  LineElement,
  LinearScale,
  PointElement,
  Tooltip,
} from 'chart.js'
import AppLayout from './layout/AppLayout.vue'
import { extrairMensagemErro } from '../services/api.js'
import { ehSemPlanilha, obterDashboard } from '../services/dashboard.js'

ChartJS.register(BarElement, CategoryScale, LinearScale, LineElement, PointElement, Legend, Tooltip)

const VERMELHO = '#dc2626'
const SLATE = '#0f172a'

const moeda = (n) => (n == null ? '—' : Number(n).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }))
const compacto = (n) => Number(n).toLocaleString('pt-BR', { notation: 'compact', maximumFractionDigits: 1 })
// O Python manda "2025-06"; a tela mostra "06/2025".
const mesLegivel = (mes) => `${mes.slice(5)}/${mes.slice(0, 4)}`

const route = useRoute()

const lote = ref(null)
const estatisticas = ref(null)
const anoEscolhido = ref('')
const erro = ref('')
const semPlanilha = ref(false)
const carregando = ref(true)

const porMes = computed(() => estatisticas.value?.porMes ?? [])

// Anos presentes nos dados, do mais recente para o mais antigo.
const anos = computed(() => [...new Set(porMes.value.map((m) => m.mes.slice(0, 4)))].sort().reverse())

const meses = computed(() =>
  anoEscolhido.value ? porMes.value.filter((m) => m.mes.startsWith(anoEscolhido.value)) : porMes.value
)

const periodoLegivel = computed(() => {
  if (porMes.value.length === 0) return 'sem contratações'
  const primeiro = mesLegivel(porMes.value[0].mes)
  const ultimo = mesLegivel(porMes.value[porMes.value.length - 1].mes)
  return primeiro === ultimo ? primeiro : `${primeiro} a ${ultimo}`
})

const totalNoPeriodo = computed(() => meses.value.reduce((soma, m) => soma + m.clientes, 0))

const mediaPorMes = computed(() => {
  if (meses.value.length === 0) return '—'
  return (totalNoPeriodo.value / meses.value.length).toLocaleString('pt-BR', { maximumFractionDigits: 1 })
})

const melhorMes = computed(() => {
  if (meses.value.length === 0) return '—'
  const melhor = meses.value.reduce((a, b) => (b.clientes > a.clientes ? b : a))
  return `${mesLegivel(melhor.mes)} (${melhor.clientes})`
})

const rotulos = computed(() => meses.value.map((m) => mesLegivel(m.mes)))

// tension: 0 pelo mesmo motivo do Dashboard — o spline descia abaixo de zero
// entre meses vazios e sugeria contratação negativa.
const dadosNovos = computed(() => ({
  labels: rotulos.value,
  datasets: [
    { label: 'Novos clientes', data: meses.value.map((m) => m.clientes), borderColor: VERMELHO, backgroundColor: VERMELHO, tension: 0 },
  ],
}))

const dadosAcumulado = computed(() => ({
  labels: rotulos.value,
  datasets: [
    { label: 'Clientes na base', data: meses.value.map((m) => m.acumulado), borderColor: SLATE, backgroundColor: SLATE, tension: 0 },
  ],
}))

const dadosFaturamento = computed(() => ({
  labels: rotulos.value,
  datasets: [
    { label: 'Faturamento', data: meses.value.map((m) => m.faturamento), backgroundColor: VERMELHO, borderRadius: 4 },
  ],
}))

const opcoesLinha = {
  responsive: true,
  aspectRatio: 3,
  plugins: { legend: { display: false } },
  scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
}

const opcoesBarraMoeda = {
  responsive: true,
  aspectRatio: 3,
  plugins: {
    legend: { display: false },
    tooltip: { callbacks: { label: (ctx) => moeda(ctx.parsed.y) } },
  },
  scales: { y: { beginAtZero: true, ticks: { callback: compacto } } },
}

onMounted(async () => {
  try {
    // Mesmo ?loteId= do Dashboard, para o Histórico de Uploads poder linkar para cá.
    const { data } = await obterDashboard(route.query.loteId || undefined)
    lote.value = data.lote
    estatisticas.value = data.estatisticas
  } catch (e) {
    semPlanilha.value = ehSemPlanilha(e)
    if (!semPlanilha.value) {
      erro.value = extrairMensagemErro(e, 'Não foi possível carregar a evolução.')
    }
  } finally {
    carregando.value = false
  }
})
</script>
