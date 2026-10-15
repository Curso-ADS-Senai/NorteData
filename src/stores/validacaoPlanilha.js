import { defineStore } from 'pinia'
import * as XLSX from 'xlsx'

export const TIPOS_ERRO_LABELS = {
  campo_vazio: 'Campos vazios',
  espaco_branco: 'Espaços em branco',
  duplicado: 'Registros duplicados',
  email_invalido: 'E-mails inválidos',
  fora_padrao: 'Dados fora do padrão',
  padronizacao: 'Textos a padronizar',
}

export const CAMPOS_LABELS = {
  codigo_cliente: 'Código do Cliente',
  nome_cliente: 'Nome do Cliente',
  consultor: 'Consultor',
  email: 'E-mail',
  segmento: 'Segmento',
  nivel_cliente: 'Nível',
  faturamento_anual: 'Faturamento Anual',
  servicos_contratados: 'Serviços Contratados',
  data_contratacao: 'Data de Contratação',
  cidade: 'Cidade',
  uf: 'UF',
}

const COLUNAS_OBRIGATORIAS = [
  'codigo_cliente',
  'nome_cliente',
  'consultor',
  'segmento',
  'nivel_cliente',
  'faturamento_anual',
  'data_contratacao',
]
const COLUNAS_TEXTO = ['codigo_cliente', 'nome_cliente', 'consultor', 'email', 'segmento', 'servicos_contratados', 'cidade', 'uf']
const NIVEIS_VALIDOS = ['A', 'B', 'C']
const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const UFS_VALIDAS = [
  'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB',
  'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO',
]

// espelha o MAPA_SEGMENTOS de ciencia-de-dados/tratamento.py, para o relatório
// já apontar o mesmo desvio de padrão que o tratamento em Python vai corrigir depois.
const SEGMENTOS_PADRAO = {
  ind: 'Indústria',
  industria: 'Indústria',
  comercio: 'Comércio',
  servicos: 'Serviços',
  tecnologia: 'Tecnologia',
  saude: 'Saúde',
  educacao: 'Educação',
}

function normalizarChave(valor) {
  return String(valor ?? '')
    .trim()
    .toLowerCase()
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .replace(/[^a-z0-9]/g, '')
}

function ehNumerico(valor) {
  if (valor === '' || valor === null || valor === undefined) return false
  const normalizado = String(valor).trim().replace(/\./g, '').replace(',', '.')
  return !Number.isNaN(Number(normalizado))
}

function ehDataValida(valor) {
  if (valor instanceof Date) return !Number.isNaN(valor.getTime())
  if (typeof valor === 'number') return true
  if (typeof valor === 'string' && valor.trim() !== '') return !Number.isNaN(Date.parse(valor))
  return false
}

export const useValidacaoPlanilhaStore = defineStore('validacaoPlanilha', {
  state: () => ({
    nomeArquivo: '',
    linhas: [],
    // Versão corrigida/padronizada de "linhas", gerada por tratarDados().
    // Fica separada da planilha bruta para a tela poder exibir "antes x depois".
    dadosTratados: [],
    erros: [],
    status: 'idle', // idle | processando | concluido | erro
    erroTemplate: '',
  }),

  getters: {
    totalRegistros: (state) => state.linhas.length,

    linhasComErro: (state) => new Set(state.erros.map((erro) => erro.linha)),

    totalRegistrosComErro() {
      return this.linhasComErro.size
    },

    totalRegistrosValidos() {
      return this.totalRegistros - this.totalRegistrosComErro
    },

    errosPorTipo: (state) => {
      return state.erros.reduce((contagem, erro) => {
        contagem[erro.tipo] = (contagem[erro.tipo] || 0) + 1
        return contagem
      }, {})
    },

    // Total de clientes já tratados (equivalente ao "totalClientes" da Aula 09).
    totalClientes: (state) => state.dadosTratados.length,

    // Quantos clientes (já tratados) caem em cada nível A/B/C.
    // Equivalente ao "clientesNivelA" da Aula 09, mas cobre os 3 níveis de uma vez
    // para não precisar de um getter repetido por nível.
    clientesPorNivel: (state) => {
      return state.dadosTratados.reduce(
        (contagem, linha) => {
          const nivel = linha.nivel_cliente
          if (NIVEIS_VALIDOS.includes(nivel)) {
            contagem[nivel] += 1
          }
          return contagem
        },
        { A: 0, B: 0, C: 0 }
      )
    },
  },

  actions: {
    registrarErro(linha, campo, tipo, descricao) {
      this.erros.push({ linha, campo, tipo, descricao })
    },

    async processarArquivo(file) {
      this.resetar()
      this.status = 'processando'
      this.nomeArquivo = file.name

      let planilha
      try {
        const buffer = await file.arrayBuffer()
        planilha = XLSX.read(buffer, { type: 'array', cellDates: true })
      } catch (erro) {
        this.status = 'erro'
        this.erroTemplate = `Não foi possível ler o arquivo. Verifique se ele não está corrompido ou em um formato inválido. (${erro.message})`
        return
      }

      const primeiraAba = planilha.Sheets[planilha.SheetNames[0]]

      const cabecalho = (XLSX.utils.sheet_to_json(primeiraAba, { header: 1 })[0] || []).map((c) =>
        String(c).trim()
      )
      const colunasFaltando = COLUNAS_OBRIGATORIAS.filter((coluna) => !cabecalho.includes(coluna))

      if (colunasFaltando.length > 0) {
        this.status = 'erro'
        this.erroTemplate = `A planilha não segue o template esperado. Coluna(s) obrigatória(s) ausente(s): ${colunasFaltando.join(', ')}.`
        return
      }

      const linhasBrutas = XLSX.utils.sheet_to_json(primeiraAba, { defval: '' })
      this.linhas = linhasBrutas.map((linha, indice) => ({ ...linha, _linha: indice + 2 }))

      this.validarLinhas()
      this.tratarDados()
      this.status = 'concluido'
    },

    // Recebe uma linha bruta da planilha e devolve uma CÓPIA com os valores
    // já corrigidos — a planilha original (this.linhas) nunca é alterada.
    // Hoje só padroniza "segmento" e "nivel_cliente", que são os campos com
    // mapa de correção conhecido (SEGMENTOS_PADRAO / NIVEIS_VALIDOS).
    tratarLinha(linha) {
      const linhaTratada = { ...linha }

      const segmento = String(linha.segmento ?? '').trim()
      if (segmento !== '') {
        const chave = normalizarChave(segmento)
        const segmentoPadrao = SEGMENTOS_PADRAO[chave]
        // Se o segmento já bate com um valor do mapa (ex: "ind.", "industria"),
        // troca pela grafia oficial. Senão, mantém o valor como veio.
        linhaTratada.segmento = segmentoPadrao ?? segmento
      }

      const nivel = String(linha.nivel_cliente ?? '').trim().toUpperCase()
      if (nivel !== '') {
        linhaTratada.nivel_cliente = nivel
      }

      return linhaTratada
    },

    // Gera dadosTratados aplicando tratarLinha em cada registro de "linhas".
    // Chamado depois de validarLinhas() para que o relatório de erros (que
    // aponta o desvio) e a prévia tratada (que mostra a correção) fiquem
    // sempre consistentes entre si.
    tratarDados() {
      this.dadosTratados = this.linhas.map((linha) => this.tratarLinha(linha))
    },

    validarLinhas() {
      this.erros = []
      const codigosVistos = new Map()

      for (const linha of this.linhas) {
        const numeroLinha = linha._linha

        for (const campo of COLUNAS_OBRIGATORIAS) {
          const valor = linha[campo]
          if (valor === '' || valor === null || valor === undefined) {
            this.registrarErro(numeroLinha, campo, 'campo_vazio', `Campo "${CAMPOS_LABELS[campo]}" obrigatório está vazio.`)
          }
        }

        for (const campo of COLUNAS_TEXTO) {
          const valor = linha[campo]
          if (typeof valor === 'string' && valor !== '' && (valor !== valor.trim() || /\s{2,}/.test(valor))) {
            this.registrarErro(
              numeroLinha,
              campo,
              'espaco_branco',
              `Campo "${CAMPOS_LABELS[campo]}" contém espaços em branco desnecessários.`
            )
          }
        }

        const codigo = String(linha.codigo_cliente ?? '').trim()
        if (codigo !== '') {
          const chave = normalizarChave(codigo)
          if (codigosVistos.has(chave)) {
            this.registrarErro(
              numeroLinha,
              'codigo_cliente',
              'duplicado',
              `Código de cliente "${codigo}" duplicado (já aparece na linha ${codigosVistos.get(chave)}).`
            )
          } else {
            codigosVistos.set(chave, numeroLinha)
          }
        }

        const nivel = String(linha.nivel_cliente ?? '').trim().toUpperCase()
        if (nivel !== '' && !NIVEIS_VALIDOS.includes(nivel)) {
          this.registrarErro(
            numeroLinha,
            'nivel_cliente',
            'fora_padrao',
            `Nível "${linha.nivel_cliente}" fora do padrão (esperado A, B ou C).`
          )
        }

        const faturamento = linha.faturamento_anual
        if (faturamento !== '' && faturamento !== null && faturamento !== undefined && !ehNumerico(faturamento)) {
          this.registrarErro(
            numeroLinha,
            'faturamento_anual',
            'fora_padrao',
            `Faturamento "${faturamento}" não é um valor numérico válido.`
          )
        }

        const dataContratacao = linha.data_contratacao
        if (dataContratacao !== '' && dataContratacao !== null && dataContratacao !== undefined && !ehDataValida(dataContratacao)) {
          this.registrarErro(
            numeroLinha,
            'data_contratacao',
            'fora_padrao',
            `Data de contratação "${dataContratacao}" não é uma data válida.`
          )
        }

        const email = String(linha.email ?? '').trim()
        if (email !== '' && !REGEX_EMAIL.test(email)) {
          this.registrarErro(numeroLinha, 'email', 'email_invalido', `E-mail "${email}" não é um endereço válido.`)
        }

        const uf = String(linha.uf ?? '').trim().toUpperCase()
        if (uf !== '' && !UFS_VALIDAS.includes(uf)) {
          this.registrarErro(numeroLinha, 'uf', 'fora_padrao', `UF "${linha.uf}" não é uma sigla de estado válida.`)
        }

        const segmento = String(linha.segmento ?? '').trim()
        if (segmento !== '') {
          const chave = normalizarChave(segmento)
          const padrao = SEGMENTOS_PADRAO[chave]
          if (padrao && padrao !== segmento) {
            this.registrarErro(
              numeroLinha,
              'segmento',
              'padronizacao',
              `Segmento "${segmento}" deveria ser padronizado para "${padrao}".`
            )
          }
        }
      }
    },

    resetar() {
      this.nomeArquivo = ''
      this.linhas = []
      this.dadosTratados = []
      this.erros = []
      this.status = 'idle'
      this.erroTemplate = ''
    },
  },
})
