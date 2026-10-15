const PREFIXO = 'norte-data:'

// O xlsx lê "data_contratacao" como objeto Date. JSON.stringify vira string
// ISO; sem reviver, ao restaurar a planilha ficaria como texto cru em vez de
// Date, e a formatação de data na tela (formatarValor) quebraria.
const ISO_DATA = /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}Z$/
function revivirDatas(_chave, valor) {
  return typeof valor === 'string' && ISO_DATA.test(valor) ? new Date(valor) : valor
}

// Plugin do Pinia: guarda o estado de uma store no sessionStorage, para
// sobreviver a uma troca de tela ou a um F5 sem precisar reenviar a
// planilha. Usa sessionStorage (não localStorage) de propósito: os dados são
// de um upload temporário e devem sumir ao fechar a aba, não virar
// persistência permanente no navegador.
export function persistirSessao({ store }) {
  if (store.$id !== 'validacaoPlanilha') return

  const chave = PREFIXO + store.$id
  const estadoSalvo = sessionStorage.getItem(chave)
  if (estadoSalvo) {
    store.$patch(JSON.parse(estadoSalvo, revivirDatas))
  }

  store.$subscribe(
    (_mutacao, estado) => {
      sessionStorage.setItem(chave, JSON.stringify(estado))
    },
    { detached: true }
  )
}
