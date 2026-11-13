import api from './api.js'

// Dados agregados do dashboard. Quem calcula e grava as estatísticas é o
// serviço Python (analytics-python); o backend Java só lê e devolve.
//
// Contrato (ver GAP_AULA14_VS_PROJETO.md e docs/CONTRATO.md do professor):
//   GET /api/dashboard              -> último lote com status PROCESSADO
//   GET /api/dashboard?loteId=3     -> um lote específico
//   GET /api/dashboard/lotes        -> os 20 lotes mais recentes
//
// Resposta: { lote, geradoEm, estatisticas }, tudo camelCase.
//   - nenhum lote no banco          -> 404 (usar ehSemPlanilha)
//   - lote PROCESSANDO              -> estatisticas = null; refazer o GET a
//     cada INTERVALO_POLLING_MS (usar estaProcessando)
//   - lote ERRO                     -> estatisticas = null e lote.mensagem
//     explica o motivo; NÃO insistir

// O Python recalcula a cada upload, então 5s basta para a tela sair de
// "processando" sozinha sem martelar a API.
export const INTERVALO_POLLING_MS = 5000

// --- Fixture de desenvolvimento ------------------------------------------
// Temporário: existe só enquanto o DashboardController não foi escrito.
// Liga com VITE_DASHBOARD_FIXTURE=1 no .env.local — nunca liga sozinho, e o
// import dinâmico mantém o arquivo fora do bundle de produção.
// Para remover: apagar dashboard.fixture.js e este bloco inteiro.
const USAR_FIXTURE = import.meta.env.DEV && import.meta.env.VITE_DASHBOARD_FIXTURE === '1'

export function fixtureAtivo() {
  return USAR_FIXTURE
}

async function carregarFixture() {
  const { DASHBOARD_FIXTURE, LOTES_FIXTURE } = await import('./dashboard.fixture.js')
  return { DASHBOARD_FIXTURE, LOTES_FIXTURE }
}
// --- fim do fixture ------------------------------------------------------

export async function obterDashboard(loteId) {
  if (USAR_FIXTURE) {
    const { DASHBOARD_FIXTURE } = await carregarFixture()
    return { data: DASHBOARD_FIXTURE }
  }
  // loteId é opcional: sem ele o backend escolhe o último lote processado.
  return api.get('/api/dashboard', { params: loteId ? { loteId } : undefined })
}

export async function listarLotes() {
  if (USAR_FIXTURE) {
    const { LOTES_FIXTURE } = await carregarFixture()
    return { data: LOTES_FIXTURE }
  }
  return api.get('/api/dashboard/lotes')
}

// 404 aqui não é erro de verdade — é "nenhuma planilha foi enviada ainda".
// A tela deve convidar para o upload, não mostrar uma falha.
export function ehSemPlanilha(erro) {
  return erro.response?.status === 404
}

// Gatilho do polling. Chaveia pelo status do lote, não por `estatisticas ==
// null`: um lote com ERRO também vem sem estatísticas e insistir nele seria
// um loop infinito.
export function estaProcessando(dados) {
  return dados?.lote?.status === 'PROCESSANDO'
}
