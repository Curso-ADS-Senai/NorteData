import { createPinia, setActivePinia } from 'pinia'
import { readFileSync } from 'fs'
import { useValidacaoPlanilhaStore } from './src/stores/validacaoPlanilha.js'

setActivePinia(createPinia())
const store = useValidacaoPlanilhaStore()

const buffer = readFileSync('./cti-insights/cti-insights/data/CTI_Insights_modelo_upload_aula.xlsx')
const file = new File([buffer], 'CTI_Insights_modelo_upload_aula.xlsx')

await store.processarArquivo(file)

console.log('status:', store.status)
console.log('erroTemplate:', store.erroTemplate)
console.log('totalRegistros:', store.totalRegistros)
console.log('totalRegistrosValidos:', store.totalRegistrosValidos)
console.log('totalRegistrosComErro:', store.totalRegistrosComErro)
console.log('errosPorTipo:', store.errosPorTipo)
console.table(store.erros)
