<template>
  <AppLayout>
    <h1 class="pt-2 text-2xl font-bold text-slate-900">Gerenciar Usuários</h1>

    <section class="p-5 mt-6 bg-white rounded-lg border border-gray-200 shadow-sm">
      <div class="flex justify-end">
        <select v-model="filtroStatus" class="px-3 py-2 text-sm border border-gray-300 rounded">
          <option value="">Todos os status</option>
          <option value="PENDENTE">Pendente</option>
          <option value="ATIVO">Ativo</option>
          <option value="BLOQUEADO">Bloqueado</option>
        </select>
      </div>

      <p v-if="erro" class="mt-4 text-sm text-red-600">{{ erro }}</p>

      <table v-else class="w-full mt-4 text-sm">
        <thead>
          <tr class="text-left text-gray-500 border-b border-gray-200">
            <th class="py-2 pr-4">Nome</th>
            <th class="py-2 pr-4">E-mail</th>
            <th class="py-2 pr-4">Perfil</th>
            <th class="py-2 pr-4">Status</th>
            <th class="py-2 pr-4">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="usuario in usuarios" :key="usuario.id" class="border-b border-gray-100 last:border-0">
            <td class="py-2 pr-4 text-slate-900">{{ usuario.nome }}</td>
            <td class="py-2 pr-4 text-slate-900">{{ usuario.email }}</td>
            <td class="py-2 pr-4 text-slate-900">{{ usuario.perfil }}</td>
            <td class="py-2 pr-4 text-slate-900">{{ usuario.status }}</td>
            <td class="py-2 pr-4">
              <button
                v-if="usuario.status !== 'ATIVO'"
                type="button"
                :disabled="atualizandoId === usuario.id"
                @click="alterarStatus(usuario, 'ATIVO')"
                class="mr-2 font-semibold text-green-600 hover:text-green-700 disabled:opacity-50 cursor-pointer"
              >
                Aprovar
              </button>
              <button
                v-if="usuario.status !== 'BLOQUEADO'"
                type="button"
                :disabled="atualizandoId === usuario.id"
                @click="alterarStatus(usuario, 'BLOQUEADO')"
                class="font-semibold text-red-600 hover:text-red-700 disabled:opacity-50 cursor-pointer"
              >
                Bloquear
              </button>
            </td>
          </tr>
          <tr v-if="usuarios.length === 0">
            <td colspan="5" class="py-4 text-center text-gray-500">Nenhum usuário encontrado.</td>
          </tr>
        </tbody>
      </table>
    </section>
  </AppLayout>
</template>

<script setup>
import { ref, watch } from 'vue'
import AppLayout from './layout/AppLayout.vue'
import { listarUsuarios, atualizarStatusUsuario } from '../services/usuarios.js'
import { extrairMensagemErro } from '../services/api.js'

const usuarios = ref([])
const erro = ref('')
const filtroStatus = ref('')
const atualizandoId = ref(null)

async function carregarUsuarios() {
  erro.value = ''
  try {
    const { data } = await listarUsuarios(filtroStatus.value)
    usuarios.value = data
  } catch (e) {
    erro.value = extrairMensagemErro(e, 'Não foi possível carregar os usuários.')
  }
}

async function alterarStatus(usuario, status) {
  atualizandoId.value = usuario.id
  try {
    await atualizarStatusUsuario(usuario.id, status)
    usuario.status = status
  } catch (e) {
    erro.value = extrairMensagemErro(e, 'Não foi possível atualizar o status do usuário.')
  } finally {
    atualizandoId.value = null
  }
}

watch(filtroStatus, carregarUsuarios, { immediate: true })
</script>
