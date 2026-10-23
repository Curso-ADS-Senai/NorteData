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
