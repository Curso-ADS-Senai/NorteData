import { createRouter, createWebHistory } from 'vue-router'
import LandingPage from '../components/LandingPage.vue'
import Cadastro from '../components/Cadastro.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', name: 'landing', component: LandingPage },
    { path: '/cadastro', name: 'cadastro', component: Cadastro },
  ],
})

export default router
