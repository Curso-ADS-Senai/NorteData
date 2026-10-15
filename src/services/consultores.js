import api from './api.js'

export function criarConsultor({ nome, email, senha }) {
  return api.post('/consultores', { nome, email, senha })
}

export function listarConsultores() {
  return api.get('/consultores')
}
