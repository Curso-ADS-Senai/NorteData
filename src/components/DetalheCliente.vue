<template>
  <AppLayout>
    <nav class="pt-2">
      <router-link to="/relatorios" class="text-sm font-semibold text-red-600 transition hover:text-red-700">
        ← Voltar para Relatórios
      </router-link>
    </nav>

    <p v-if="carregando" class="mt-6 text-sm text-gray-500">Carregando…</p>

    <p v-else-if="erro" class="mt-6 text-sm text-red-600">{{ erro }}</p>

    <template v-else-if="cliente">
      <header class="mt-4">
        <h1 class="text-2xl font-bold text-slate-900">{{ cliente.nomeEmpresa }}</h1>
        <p class="mt-1 text-gray-600">
          {{ cliente.codigoCliente ?? 'sem código' }} · {{ cliente.segmento }} · Nível {{ cliente.nivel }}
        </p>
      </header>

      <section class="grid gap-4 mt-6 sm:grid-cols-3">
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="text-sm text-gray-500">Faturamento anual</h2>
          <p class="mt-1 text-2xl font-bold text-slate-900">{{ moeda(cliente.faturamentoAnual) }}</p>
        </article>
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="text-sm text-gray-500">Serviços contratados</h2>
          <p class="mt-1 text-2xl font-bold text-slate-900">{{ contratos.length }}</p>
        </article>
        <article class="p-5 bg-white rounded-lg border border-gray-200 shadow-sm">
          <h2 class="text-sm text-gray-500">Consultor responsável</h2>
          <p class="mt-1 text-lg font-bold text-slate-900">{{ cliente.consultor }}</p>
        </article>
      </section>

      <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
        <h2 class="font-bold text-slate-900">Cadastro</h2>
        <dl class="grid gap-x-8 gap-y-4 mt-4 sm:grid-cols-3">
          <div>
            <dt class="text-sm text-gray-500">Cidade / UF</dt>
            <dd class="mt-1 font-medium text-slate-900">{{ localidade }}</dd>
          </div>
          <div>
            <dt class="text-sm text-gray-500">Status</dt>
            <dd class="mt-1 font-medium text-slate-900">{{ cliente.status ?? '—' }}</dd>
          </div>
          <div>
            <dt class="text-sm text-gray-500">Planilha de origem</dt>
            <dd class="mt-1 font-medium text-slate-900">
              <router-link
                v-if="cliente.lote"
                :to="{ path: '/dashboard', query: { loteId: cliente.lote.id } }"
                class="text-red-600 transition hover:text-red-700"
              >
                {{ cliente.lote.arquivoNome }}
              </router-link>
              <span v-else>cadastro manual</span>
            </dd>
          </div>
        </dl>
      </section>

      <section class="p-5 mt-4 bg-white rounded-lg border border-gray-200 shadow-sm">
        <h2 class="font-bold text-slate-900">Serviços contratados</h2>

        <p v-if="contratos.length === 0" class="mt-4 text-sm text-gray-600">
          Este cliente não tem serviço contratado. A coluna <code>servicos_contratados</code> é
          opcional no template da planilha.
        </p>

        <table v-else class="w-full mt-4 text-sm">
          <thead>
            <tr class="text-left text-gray-500 border-b border-gray-200">
              <th class="py-2 pr-4 font-medium">Serviço</th>
              <th class="py-2 pr-4 font-medium">Contratado em</th>
              <th v-if="temValor" class="py-2 pr-4 font-medium text-right">Valor</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(contrato, indice) in contratos" :key="indice" class="border-b border-gray-100 last:border-0">
              <td class="py-2 pr-4 font-medium text-slate-900">{{ contrato.servico }}</td>
              <td class="py-2 pr-4 text-gray-600">{{ data(contrato.dataContratacao) }}</td>
              <td v-if="temValor" class="py-2 pr-4 text-right text-slate-900">{{ moeda(contrato.valor) }}</td>
            </tr>
          </tbody>
        </table>
      </section>
    </template>
  </AppLayout>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from './layout/AppLayout.vue'
import { extrairMensagemErro } from '../services/api.js'
import { obterCliente } from '../services/clientes.js'

const route = useRoute()

const cliente = ref(null)
const erro = ref('')
const carregando = ref(true)

const moeda = (n) => (n == null ? '—' : Number(n).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' }))
// O backend manda LocalDate ("2025-03-05"); new Date() nessa string assume UTC e
// no fuso do Brasil voltaria um dia. Por isso formata na mão.
const data = (d) => (d ? String(d).split('-').reverse().join('/') : '—')

const contratos = computed(() => cliente.value?.contratos ?? [])

// O template da planilha não tem coluna de preço, então o ETL grava servico.valor = 0
// para todo mundo. Mostrar uma coluna inteira de "R$ 0,00" parece defeito — ela só
// aparece se algum contrato tiver valor de verdade.
const temValor = computed(() => contratos.value.some((c) => Number(c.valor ?? 0) > 0))

const localidade = computed(() => {
  if (!cliente.value?.cidade && !cliente.value?.uf) return '—'
  return [cliente.value.cidade, cliente.value.uf].filter(Boolean).join('/')
})

async function carregar(id) {
  carregando.value = true
  erro.value = ''
  try {
    const { data: dados } = await obterCliente(id)
    cliente.value = dados
  } catch (e) {
    cliente.value = null
    erro.value = extrairMensagemErro(e, 'Não foi possível carregar o cliente.')
  } finally {
    carregando.value = false
  }
}

// watch com immediate em vez de onMounted: navegar de um cliente para outro reusa
// o mesmo componente, e sem isto a tela ficaria com os dados do anterior.
watch(() => route.params.id, carregar, { immediate: true })
</script>
