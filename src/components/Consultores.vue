<template>
  <AppLayout>
    <h1 class="pt-2 text-2xl font-bold text-slate-900">Consultores</h1>

    <section class="p-5 mt-6 bg-white rounded-lg border border-gray-200 shadow-sm">
      <p v-if="erro" class="text-sm text-red-600">{{ erro }}</p>

      <table v-else class="w-full text-sm">
        <thead>
          <tr class="text-left text-gray-500 border-b border-gray-200">
            <th class="py-2">Nome</th>
            <th class="py-2">E-mail</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="consultor in consultores" :key="consultor.id" class="border-b border-gray-100">
            <td class="py-2 text-slate-900">{{ consultor.nome }}</td>
            <td class="py-2 text-slate-900">{{ consultor.email }}</td>
          </tr>
        </tbody>
      </table>
    </section>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AppLayout from './layout/AppLayout.vue'
import { listarConsultores } from '../services/consultores.js'

const consultores = ref([])
const erro = ref('')

onMounted(async () => {
  try {
    const { data } = await listarConsultores()
    consultores.value = data
  } catch (e) {
    erro.value = 'Não foi possível carregar os consultores.'
  }
})
</script>
