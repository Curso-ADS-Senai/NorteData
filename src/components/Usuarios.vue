<template>
  <AppLayout>
    <h1 class="pt-2 text-2xl font-bold text-slate-900">Gerenciar Usuários</h1>

    <section class="p-5 mt-6 bg-white rounded-lg border border-gray-200 shadow-sm">
      <table class="w-full text-sm">
        <thead>
          <tr class="text-left text-gray-500 border-b border-gray-200">
            <th class="py-2">Nome</th>
            <th class="py-2">E-mail</th>
            <th class="py-2">Perfil</th>
          </tr>
        </thead>
        <tbody>
          <tr class="border-b border-gray-100">
            <td class="py-2 text-slate-900">{{ auth.usuario?.nome }}</td>
            <td class="py-2 text-slate-900">{{ auth.usuario?.email }}</td>
            <td class="py-2 text-slate-900">{{ auth.usuario?.perfil }}</td>
          </tr>
        </tbody>
      </table>

      <p class="mt-4 text-sm text-gray-600">
        O backend ainda não tem um endpoint para listar todos os usuários
        (<code>AuthController</code> hoje só expõe cadastro, login e
        <code>/me</code>) — por enquanto esta tela mostra só o usuário logado.
      </p>
    </section>
  </AppLayout>
</template>

<script setup>
import AppLayout from './layout/AppLayout.vue'
import { useAuthStore } from '../stores/auth.js'

const auth = useAuthStore()
</script>
