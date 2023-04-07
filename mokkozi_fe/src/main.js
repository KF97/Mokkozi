import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import './assets/public.css'
import Foot from '@/components/Foot.vue'
import Top from '@/components/Top.vue'

Vue.component('top', Top)
Vue.component('foot', Foot)

Vue.config.productionTip = false
new Vue({
  router,
  store,
  vuetify,
  render: h => h(App)
}).$mount('#app')
