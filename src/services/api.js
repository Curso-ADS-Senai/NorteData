// Cliente HTTP único para o back-end (Java/Spring Boot). Os services por
// recurso (consultores.js, clientes.js, dashboard.js) importam este arquivo em
// vez de chamar axios/fetch direto.

import axios from 'axios'

// VITE_API_URL é a raiz do backend, SEM o /api no fim: todas as chamadas dos
// services já escrevem o prefixo (api.get('/api/dashboard')), e o consultores.js
// chama '/consultores' sem ele. Com '/api' na baseURL viraria /api/api/... → 404.
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' },
})


api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Respostas de erro do backend vêm como { erro: "mensagem", campos: {...} }.
// Centraliza a leitura desse envelope para não duplicar o fallback em cada tela.
export function extrairMensagemErro(erro, mensagemPadrao) {
  return erro.response?.data?.erro ?? mensagemPadrao
}

export default api
