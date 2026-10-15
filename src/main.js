import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'
import { persistirSessao } from './stores/persistenciaSessao.js'

const pinia = createPinia()
pinia.use(persistirSessao)

createApp(App).use(pinia).use(router).mount('#app')