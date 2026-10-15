import api from './api.js'

export function criarCliente(consultorId, { nomeEmpresa, segmento, faturamentoAnual, nivel, status }) {
  return api.post(`/clientes/consultor/${consultorId}`, { nomeEmpresa, segmento, faturamentoAnual, nivel, status })
}

export function listarClientes() {
  return api.get('/clientes')
}
