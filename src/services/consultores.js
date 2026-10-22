import api from './api.js'

export function listarConsultores() {
  return api.get('/consultores')
}
