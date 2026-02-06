import {createApp} from 'vue'
import App from './App.vue'
// router
import router from './router';
//px单位转rem，设置页面自适应
import 'amfe-flexible';
// layout
import AdminLayout from '@/layouts/admin-layout.vue';
import HomeLayout from '@/layouts/home-layout.vue';
import LoginLayout from '@/layouts/login-layout.vue';
// element-plus
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import 'jsoneditor';
// 列表组件引入
import '@/assets/icons/iconfont.js';
import store from './stores'

const app = createApp(App);
// 注册全局组件 
app.component('admin-layout', AdminLayout);
app.component('home-layout', HomeLayout);
app.component('login-layout', LoginLayout);
// 遍历引入的图标组件，并注册为全局组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component);
}
// 全局常量
app.config.globalProperties.global = window.g;
app.use(router);
app.use(store);
app.mount('#app')
