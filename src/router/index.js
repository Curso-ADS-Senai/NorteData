import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '../components/LandingPage.vue'
import Cadastro from '../components/Cadastro.vue'
import Login from '../components/Login.vue'
import Dashboard from '../components/Dashboard.vue'
import UploadPlanilha from '../components/UploadPlanilha.vue'
import RelatorioValidacao from '../components/RelatorioValidacao.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'landing', component: LandingPage },
    { path: '/cadastro', name: 'cadastro', component: Cadastro },
    { path: '/login', name: 'login', component: Login },
    { path: '/dashboard', name: 'dashboard', component: Dashboard },
    { path: '/upload', name: 'upload', component: UploadPlanilha },
    { path: '/relatorio-validacao', name: 'relatorio-validacao', component: RelatorioValidacao },
  ],
})

export default router
