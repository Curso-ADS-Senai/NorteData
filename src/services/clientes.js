import api from './api.js'

// Carteira de clientes (telas de Relatórios e Detalhe do Cliente).
//
//   GET /api/clientes?busca=&segmento=&nivel=&loteId=  -> { loteId, clientes, total }
//   GET /api/clientes/filtros?loteId=                  -> { segmentos, niveis }
//   GET /api/clientes/{id}                             -> cliente + contratos + lote
//
// Sem loteId o backend usa o último lote PROCESSADO. Esse recorte é obrigatório:
// cada upload regrava a carteira inteira num lote novo, então sem ele a tabela
// mostraria o mesmo cliente repetido uma vez por planilha enviada.
//
// O arquivo anterior chamava `GET /clientes` e `POST /clientes/consultor/{id}`,
// que nunca existiram no backend — era código morto e foi removido.

// Omite os filtros vazios para não mandar `?busca=&segmento=` à toa.
function apenasPreenchidos(filtros) {
  return Object.fromEntries(Object.entries(filtros).filter(([, valor]) => valor !== '' && valor != null))
}

export function listarClientes({ busca = '', segmento = '', nivel = '', loteId = null } = {}) {
  return api.get('/api/clientes', { params: apenasPreenchidos({ busca, segmento, nivel, loteId }) })
}

export function listarFiltrosClientes(loteId = null) {
  return api.get('/api/clientes/filtros', { params: apenasPreenchidos({ loteId }) })
}

export function obterCliente(id) {
  return api.get(`/api/clientes/${id}`)
}
