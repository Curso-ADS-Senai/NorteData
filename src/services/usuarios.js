import api from './api.js'

export function listarUsuarios(status) {
  return api.get('/api/admin/usuarios', { params: status ? { status } : {} })
}

export function atualizarStatusUsuario(id, status) {
  return api.patch(`/api/admin/usuarios/${id}/status`, { status })
}
