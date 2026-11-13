<template>
  <AppLayout>
    <header class="flex flex-wrap gap-4 items-end justify-between pt-2">
      <div>
        <h1 class="text-2xl font-bold text-slate-900">Dashboard</h1>
        <p v-if="lote" class="flex flex-wrap gap-2 items-center mt-1 text-sm text-gray-500">
          <span>Lote {{ lote.id }} · {{ lote.arquivoNome }} · enviado em {{ dataHora(lote.enviadoEm) }}</span>
          <span class="px-2 py-0.5 text-xs font-medium rounded-full" :class="corDoStatus(lote.status)">
            {{ lote.status }}
          </span>
        </p>
      </div>

      <label v-if="lotes.length" class="flex flex-col gap-1 text-sm text-gray-500">
        Planilha (lote)
        <select
          v-model="loteEscolhido"
          @change="trocarLote"
          class="px-3 py-2 text-sm text-slate-900 bg-white border border-gray-300 rounded"
        >
          <option value="">Mais recente processado</option>
          <option v-for="l in lotes" :key="l.id" :value="l.id">
            #{{ l.id }} · {{ l.arquivoNome }} · {{ l.status }}
          </option>
        </select>
      </label>
    </header>

    <!-- Avisa na cara que os números são de exemplo, para ninguém confundir
         fixture com dado real. Sai junto com dashboard.fixture.js. -->
    <p v-if="fixtureAtivo()" class="p-3 mt-4 text-sm text-amber-800 bg-amber-50 rounded border border-amber-200">
      Dados de exemplo (<code>VITE_DASHBOARD_FIXTURE=1</code>) — não vêm do backend.
    </p>

    <p v-if="erro" class="p-3 mt-4 text-sm text-orange-700 bg-orange-50 rounded border border-orange-200">
      {{ erro }}
      <RouterLink v-if="semPlanilha" to="/upload" class="font-semibold underline">Enviar planilha</RouterLink>
    </p>
    <p v-else-if="carregando && !estatisticas" class="mt-4 text-sm text-gray-500">Carregando...</p>
    <p
      v-else-if="lote?.status === 'ERRO'"
      class="p-3 mt-4 text-sm text-red-700 bg-red-50 rounded border border-red-200"
    >
      O Python não conseguiu processar este lote: {{ lote.mensagem }}
    </p>
    <p
      v-else-if="lote && !estatisticas"
      class="p-3 mt-4 text-sm text-slate-700 bg-slate-100 rounded border border-slate-200"
    >
      O Python está processando a planilha. Esta tela atualiza sozinha.
    </p>

    <template v-if="estatisticas">
      <p class="mt-4 text-xs text-gray-500">
        Estatísticas calculadas pelo Python em {{ dataHora(geradoEm) }}
      </p>

      <section class="grid gap-4 mt-4 sm:grid-cols-2 lg:grid-cols-5">
        <article
          v-for="kpi in kpis"
          :key="kpi.label"
          class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm"
        >
          <h2 class="text-sm text-gray-500">{{ kpi.label }}</h2>
          <p class="mt-1 text-xl font-bold text-slate-900">{{ kpi.valor }}</p>
        </article>
      </section>

      <section class="grid gap-4 mt-4 lg:grid-cols-2">
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="font-bold text-slate-900">Clientes por segmento</h2>
          <div class="mt-4">
            <Bar :data="dadosSegmento" :options="opcoesBarra" />
          </div>
        </article>

        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="font-bold text-slate-900">Clientes por nível (A/B/C)</h2>
          <div class="max-w-xs mx-auto mt-4">
            <Doughnut :data="dadosNivel" :options="opcoesRosca" />
          </div>
        </article>

        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="font-bold text-slate-900">Faturamento por consultor</h2>
          <div class="mt-4">
            <Bar :data="dadosConsultor" :options="opcoesBarraMoeda" />
          </div>
        </article>

        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="font-bold text-slate-900">Serviços mais contratados</h2>
          <div class="mt-4">
            <Bar :data="dadosServico" :options="opcoesBarraDeitada" />
          </div>
        </article>

        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm lg:col-span-2">
          <h2 class="font-bold text-slate-900">Contratações por mês</h2>
          <div class="mt-4">
            <Line :data="dadosMes" :options="opcoesLinha" />
          </div>
        </article>
      </section>

      <section
        v-if="topClientes.length"
        class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm overflow-x-auto"
      >
        <h2 class="font-bold text-slate-900">Maiores clientes</h2>
        <table class="w-full mt-4 text-sm">
          <thead>
            <tr class="text-left text-gray-500 border-b border-gray-200">
              <th class="py-2 pr-4 font-medium">Código</th>
              <th class="py-2 pr-4 font-medium">Cliente</th>
              <th class="py-2 pr-4 font-medium">Segmento</th>
              <th class="py-2 pr-4 font-medium">Nível</th>
              <th class="py-2 pr-4 font-medium text-right">Faturamento anual</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cliente in topClientes" :key="cliente.codigo" class="border-b border-gray-100 last:border-0">
              <td class="py-2 pr-4 text-gray-600">{{ cliente.codigo }}</td>
              <td class="py-2 pr-4 text-slate-900">{{ cliente.nome }}</td>
              <td class="py-2 pr-4 text-gray-600">{{ cliente.segmento }}</td>
              <td class="py-2 pr-4 text-gray-600">{{ cliente.nivel }}</td>
              <td class="py-2 pr-4 text-right text-slate-900">{{ moeda(cliente.faturamento) }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section
        v-if="estatisticas.insights?.length"
        class="p-5 mt-4 bg-slate-50 rounded-lg border border-slate-200"
      >
        <h2 class="font-bold text-slate-900">O que os dados mostram</h2>
        <ul class="pl-5 mt-2 text-sm text-slate-700 list-disc space-y-1">
          <li v-for="frase in estatisticas.insights" :key="frase">{{ frase }}</li>
        </ul>
      </section>

      <footer v-if="qualidade" class="mt-4 text-xs text-gray-500">
        Linhas recebidas: {{ numero(qualidade.linhasRecebidas) }} ·
        válidas: {{ numero(qualidade.linhasValidas) }} ·
        inválidas: {{ numero(qualidade.linhasInvalidas) }}
      </footer>
    </template>
  </AppLayout>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { Bar, Doughnut, Line } from 'vue-chartjs'
import {
  ArcElement,
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
import {
  INTERVALO_POLLING_MS,
  ehSemPlanilha,
  estaProcessando,
  fixtureAtivo,
  listarLotes,
  obterDashboard,
} from '../services/dashboard.js'

ChartJS.register(ArcElement, BarElement, CategoryScale, LinearScale, LineElement, PointElement, Legend, Tooltip)

const VERMELHO = '#dc2626'
const SLATE = '#0f172a'
// Uma cor por nível/categoria da rosca; a ordem acompanha porNivel (A, B, C).
const CORES_CATEGORIA = ['#dc2626', '#0f172a', '#94a3b8', '#f59e0b', '#0ea5e9', '#16a34a']

const moeda = (n) => (n == null ? '—' : Number(n).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }))
const numero = (n) => (n == null ? '—' : Number(n).toLocaleString('pt-BR', { maximumFractionDigits: 2 }))
const dataHora = (t) => (t ? new Date(t).toLocaleString('pt-BR') : '—')
const compacto = (n) => Number(n).toLocaleString('pt-BR', { notation: 'compact', maximumFractionDigits: 1 })
// O Python manda "2025-06"; a tela mostra "06/2025".
const mesLegivel = (mes) => `${mes.slice(5)}/${mes.slice(0, 4)}`

const lote = ref(null)
const geradoEm = ref(null)
const estatisticas = ref(null)
const lotes = ref([])
const loteEscolhido = ref('')
const erro = ref('')
const semPlanilha = ref(false)
const carregando = ref(false)
let timer = null

const porSegmento = computed(() => estatisticas.value?.porSegmento ?? [])
const porNivel = computed(() => estatisticas.value?.porNivel ?? [])
const porConsultor = computed(() => estatisticas.value?.porConsultor ?? [])
const porServico = computed(() => estatisticas.value?.porServico ?? [])
const porMes = computed(() => estatisticas.value?.porMes ?? [])
const topClientes = computed(() => estatisticas.value?.topClientes ?? [])
const qualidade = computed(() => estatisticas.value?.qualidade ?? null)

// Os cinco cartões vêm todos de `resumo` — é o que o Python calcula.
// "Serviços Ativos" não existe no contrato, por isso saiu daqui.
const kpis = computed(() => {
  const resumo = estatisticas.value?.resumo ?? {}
  return [
    { label: 'Clientes', valor: numero(resumo.totalClientes) },
    { label: 'Faturamento total', valor: moeda(resumo.faturamentoTotal) },
    { label: 'Média', valor: moeda(resumo.media) },
    { label: 'Mediana', valor: moeda(resumo.mediana) },
    { label: 'Desvio padrão', valor: moeda(resumo.desvioPadrao) },
  ]
})

const dadosSegmento = computed(() => ({
  labels: porSegmento.value.map((s) => s.segmento),
  datasets: [{ label: 'Clientes', data: porSegmento.value.map((s) => s.clientes), backgroundColor: VERMELHO, borderRadius: 4 }],
}))

const dadosNivel = computed(() => ({
  labels: porNivel.value.map((n) => `Nível ${n.nivel}`),
  datasets: [
    {
      data: porNivel.value.map((n) => n.clientes),
      backgroundColor: CORES_CATEGORIA.slice(0, porNivel.value.length),
      borderWidth: 0,
    },
  ],
}))

const dadosConsultor = computed(() => ({
  labels: porConsultor.value.map((c) => c.consultor),
  datasets: [{ label: 'Faturamento', data: porConsultor.value.map((c) => c.faturamentoTotal), backgroundColor: SLATE, borderRadius: 4 }],
}))

const dadosServico = computed(() => ({
  labels: porServico.value.map((s) => s.servico),
  datasets: [{ label: 'Contratos', data: porServico.value.map((s) => s.contratos), backgroundColor: VERMELHO, borderRadius: 4 }],
}))

const dadosMes = computed(() => ({
  labels: porMes.value.map((m) => mesLegivel(m.mes)),
  datasets: [
    {
      label: 'Clientes',
      data: porMes.value.map((m) => m.clientes),
      borderColor: VERMELHO,
      backgroundColor: VERMELHO,
      // Sem curva: são contagens inteiras por mês, e o spline ultrapassava o
      // zero entre meses vazios, sugerindo contratação negativa.
      tension: 0,
    },
  ],
}))

const opcoesBarra = {
  responsive: true,
  plugins: { legend: { display: false } },
  scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
}

const opcoesBarraDeitada = {
  responsive: true,
  indexAxis: 'y',
  plugins: { legend: { display: false } },
  scales: { x: { beginAtZero: true, ticks: { precision: 0 } } },
}

// Faturamento em reais: eixo abreviado ("4,8 mi") e tooltip com o valor cheio.
const opcoesBarraMoeda = {
  responsive: true,
  indexAxis: 'y',
  plugins: {
    legend: { display: false },
    tooltip: { callbacks: { label: (ctx) => moeda(ctx.parsed.x) } },
  },
  scales: { x: { beginAtZero: true, ticks: { callback: compacto } } },
}

const opcoesRosca = {
  responsive: true,
  plugins: { legend: { position: 'bottom' } },
}

const opcoesLinha = {
  responsive: true,
  // Ocupa as duas colunas: sem isto a proporção padrão (2:1) deixaria o
  // gráfico com a altura de meia tela.
  aspectRatio: 4,
  plugins: { legend: { display: false } },
  scales: { y: { beginAtZero: true, ticks: { precision: 0 } } },
}

function pararPolling() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

// Só insiste enquanto o lote estiver PROCESSANDO; PROCESSADO e ERRO param.
function sincronizarPolling(dados) {
  if (estaProcessando(dados)) {
    if (!timer) timer = setInterval(carregar, INTERVALO_POLLING_MS)
  } else {
    pararPolling()
  }
}

async function carregar() {
  carregando.value = true
  erro.value = ''
  semPlanilha.value = false
  try {
    const { data } = await obterDashboard(loteEscolhido.value || undefined)
    lote.value = data.lote
    geradoEm.value = data.geradoEm
    estatisticas.value = data.estatisticas
    sincronizarPolling(data)
  } catch (e) {
    pararPolling()
    lote.value = null
    geradoEm.value = null
    estatisticas.value = null
    // 404 do backend vem com { erro: "Nenhuma planilha foi processada ainda..." };
    // o 404 genérico do Spring (rota inexistente) não traz esse campo e cai no padrão.
    semPlanilha.value = ehSemPlanilha(e)
    erro.value = semPlanilha.value
      ? extrairMensagemErro(e, 'Nenhuma planilha foi processada ainda.')
      : extrairMensagemErro(e, 'Não foi possível carregar o dashboard.')
  } finally {
    carregando.value = false
  }
}

async function trocarLote() {
  pararPolling()
  await carregar()
}

function corDoStatus(status) {
  if (status === 'PROCESSADO') return 'bg-green-100 text-green-700'
  if (status === 'ERRO') return 'bg-red-100 text-red-700'
  return 'bg-amber-100 text-amber-700'
}

onMounted(async () => {
  try {
    const { data } = await listarLotes()
    lotes.value = data
  } catch {
    lotes.value = []
  }
  await carregar()
})

onBeforeUnmount(pararPolling)
</script>
