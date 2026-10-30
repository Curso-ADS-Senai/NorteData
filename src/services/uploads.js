import api from './api.js'

// O DTO do backend (LinhaEnvio) espera camelCase; a store usa snake_case
// (mesmo nome das colunas da planilha) — este mapa faz a tradução no envio.
const CAMPOS_PARA_ENVIO = {
  codigo_cliente: 'codigoCliente',
  nome_cliente: 'nomeCliente',
  consultor: 'consultor',
  segmento: 'segmento',
  nivel_cliente: 'nivelCliente',
  faturamento_anual: 'faturamentoAnual',
  servicos_contratados: 'servicosContratados',
  data_contratacao: 'dataContratacao',
  cidade: 'cidade',
  uf: 'uf',
}

function mapearLinhaParaEnvio(linha) {
  const linhaMapeada = { numeroLinha: linha._linha }
  for (const [origem, destino] of Object.entries(CAMPOS_PARA_ENVIO)) {
    linhaMapeada[destino] = linha[origem]
  }
  return linhaMapeada
}

export function enviarPlanilhaValidada({ arquivoNome, linhasValidas, descartadas }) {
  return api.post('/api/uploads/validadas', {
    arquivoNome,
    descartadas,
    linhas: linhasValidas.map(mapearLinhaParaEnvio),
  })
}
