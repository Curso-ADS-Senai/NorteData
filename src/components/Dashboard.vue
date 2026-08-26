<template>
  <AppLayout>
    <h1 class="pt-2 text-2xl font-bold text-slate-900">Dashboard</h1>

    <section class="grid gap-4 mt-6 sm:grid-cols-3">
      <article v-for="kpi in kpis" :key="kpi.label" class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
        <h2 class="text-sm text-gray-500">{{ kpi.label }}</h2>
        <p class="mt-1 text-2xl font-bold text-slate-900">{{ kpi.valor }}</p>
      </article>
    </section>

    <section class="grid gap-4 mt-4 lg:grid-cols-2">
      <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
        <h2 class="font-bold text-slate-900">Distribuição por Nível (A/B/C)</h2>
        <div class="max-w-xs mx-auto mt-4">
          <Doughnut :data="dadosNivel" :options="opcoesDoughnut" />
        </div>
      </article>

      <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
        <h2 class="font-bold text-slate-900">Distribuição por Segmento</h2>
        <div class="mt-4">
          <Bar :data="dadosSegmento" :options="opcoesBar" />
        </div>
      </article>
    </section>

    <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
      <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <h2 class="font-bold text-slate-900">Carteira de Clientes</h2>
        <div class="flex gap-3">
          <input
            type="search"
            v-model="busca"
            placeholder="Buscar empresa..."
            class="px-3 py-2 text-sm border border-gray-300 rounded"
          />
          <select v-model="filtroSegmento" class="px-3 py-2 text-sm border border-gray-300 rounded">
            <option value="">Todos os segmentos</option>
            <option v-for="segmento in segmentos" :key="segmento" :value="segmento">{{ segmento }}</option>
          </select>
        </div>
      </div>

      <table class="w-full mt-4 text-sm">
        <thead>
          <tr class="text-left text-gray-500 border-b border-gray-200">
            <th class="py-2 pr-4 font-medium">ID</th>
            <th class="py-2 pr-4 font-medium">Empresa</th>
            <th class="py-2 pr-4 font-medium">Segmento</th>
            <th class="py-2 pr-4 font-medium">Faturamento</th>
            <th class="py-2 pr-4 font-medium">Nível</th>
            <th class="py-2 pr-4 font-medium">Status</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cliente in clientesFiltrados" :key="cliente.id" class="border-b border-gray-100 last:border-0">
            <td class="py-2 pr-4 text-slate-900">{{ cliente.id }}</td>
            <td class="py-2 pr-4 text-slate-900">{{ cliente.empresa }}</td>
            <td class="py-2 pr-4 text-gray-600">{{ cliente.segmento }}</td>
            <td class="py-2 pr-4 text-gray-600">{{ cliente.faturamento }}</td>
            <td class="py-2 pr-4 text-gray-600">{{ cliente.nivel }}</td>
            <td class="py-2 pr-4">
              <span
                class="px-2 py-0.5 text-xs font-medium rounded-full"
                :class="cliente.status === 'Ativo' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'"
              >
                {{ cliente.status }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  </AppLayout>
</template>

<script setup>
import { computed, ref } from 'vue'
import { Bar, Doughnut } from 'vue-chartjs'
import {
  BarElement,
  CategoryScale,
  Chart as ChartJS,
  Legend,
  LinearScale,
  ArcElement,
  Tooltip,
} from 'chart.js'
import AppLayout from './layout/AppLayout.vue'

ChartJS.register(BarElement, CategoryScale, LinearScale, ArcElement, Legend, Tooltip)

const kpis = [
  { label: 'Total de Clientes', valor: '142' },
  { label: 'Faturamento Médio', valor: 'R$ 5.600,00' },
  { label: 'Serviços Ativos', valor: '128' },
]

const dadosNivel = {
  labels: ['Nível A', 'Nível B', 'Nível C'],
  datasets: [
    {
      data: [28, 40, 32],
      backgroundColor: ['#dc2626', '#0f172a', '#94a3b8'],
      borderWidth: 0,
    },
  ],
}

const opcoesDoughnut = {
  responsive: true,
  plugins: { legend: { position: 'bottom' } },
}

const segmentos = ['Indústria', 'Comércio', 'Serviços', 'Tecnologia']

const dadosSegmento = {
  labels: segmentos,
  datasets: [
    {
      label: 'Clientes',
      data: [48, 35, 41, 18],
      backgroundColor: '#dc2626',
      borderRadius: 4,
    },
  ],
}

const opcoesBar = {
  responsive: true,
  plugins: { legend: { display: false } },
  scales: { y: { beginAtZero: true } },
}

const clientes = [
  { id: 'C001', empresa: 'Metalúrgica Bragança', segmento: 'Indústria', faturamento: 'R$ 5.660,00', nivel: 'A', status: 'Ativo' },
  { id: 'C002', empresa: 'Comercial Rosa Ltda', segmento: 'Comércio', faturamento: 'R$ 3.200,00', nivel: 'B', status: 'Ativo' },
  { id: 'C003', empresa: 'Padola Serviços', segmento: 'Serviços', faturamento: 'R$ 4.580,00', nivel: 'B', status: 'Inativo' },
  { id: 'C004', empresa: 'TechFrouca Sistemas', segmento: 'Tecnologia', faturamento: 'R$ 7.900,00', nivel: 'A', status: 'Ativo' },
  { id: 'C005', empresa: 'Flusaz Distribuidora', segmento: 'Comércio', faturamento: 'R$ 2.150,00', nivel: 'C', status: 'Ativo' },
]

const busca = ref('')
const filtroSegmento = ref('')

const clientesFiltrados = computed(() =>
  clientes.filter((cliente) => {
    const bateBusca = cliente.empresa.toLowerCase().includes(busca.value.toLowerCase())
    const bateSegmento = !filtroSegmento.value || cliente.segmento === filtroSegmento.value
    return bateBusca && bateSegmento
  })
)
</script>
