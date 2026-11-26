import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '../components/LandingPage.vue'
import Cadastro from '../components/Cadastro.vue'
import Login from '../components/Login.vue'
import Dashboard from '../components/Dashboard.vue'
import UploadPlanilha from '../components/UploadPlanilha.vue'
import RelatorioValidacao from '../components/RelatorioValidacao.vue'
import {useAuthStore} from '../stores/auth'
import Consultores from '../components/Consultores.vue'
import Usuarios from '../components/Usuarios.vue'
import Evolucao from '../components/Evolucao.vue'
import HistoricoUploads from '../components/HistoricoUploads.vue'
import Perfil from '../components/Perfil.vue'
import Erro404 from '../components/Erro404.vue'
import Relatorios from '../components/Relatorios.vue'
import DetalheCliente from '../components/DetalheCliente.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'landing', component: LandingPage },
    { path: '/cadastro', name: 'cadastro', component: Cadastro },
    { path: '/login', name: 'login', component: Login },
    { path: '/dashboard', name: 'dashboard', component: Dashboard, meta: { privado: true } },
    { path: '/upload', name: 'upload', component: UploadPlanilha,  meta: { privado: true }},
    { path: '/relatorio-validacao', name: 'relatorio-validacao', component: RelatorioValidacao, meta: { privado: true }},
    { path: '/consultores', name: 'consultores', component: Consultores, meta: { privado: true }},
    { path: '/usuarios', name: 'usuarios', component: Usuarios, meta: { privado: true, admin: true }},
    { path: '/evolucao', name: 'evolucao', component: Evolucao, meta: { privado: true }},
    { path: '/historico-uploads', name: 'historico-uploads', component: HistoricoUploads, meta: { privado: true }},
    { path: '/perfil', name: 'perfil', component: Perfil, meta: { privado: true }},
    { path: '/relatorios', name: 'relatorios', component: Relatorios, meta: { privado: true }},
    { path: '/clientes/:id', name: 'detalhe-cliente', component: DetalheCliente, meta: { privado: true }},
    // Catch-all: precisa ser a ÚLTIMA rota, senão engole as de cima.
    { path: '/:caminho(.*)*', name: 'erro-404', component: Erro404 },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.privado && !auth.token) {
    return '/login'
  }

  if (to.meta.admin && auth.usuario?.perfil !== 'ADMIN') {
    return '/'
  }
})

export default router
