import { createApp } from 'vue'
import App from './App.vue'
import Vuex from 'vuex';
import router from './router'; // router
import 'amfe-flexible'; //px单位转rem，设置页面自适应

// layout
import AdminLayout from '@/layouts/admin-layout.vue';
import HomeLayout from '@/layouts/home-layout.vue';
import LoginLayout from '@/layouts/login-layout.vue';

const app = createApp(App);

app.component('admin-layout', AdminLayout);
app.component('home-layout', HomeLayout);
app.component('login-layout', LoginLayout);

// 是否有权限
import { session } from '@/utils';
app.config.globalProperties.$isAccess = (accessPath) => {
let menus = session.getStorage('menus');
	if (!(menus && menus.length)) {
		menus = session.getStorage('menus');
	}
	if (accessPath) {
		return menus.findIndex(r => r.path === accessPath) !== -1;
	} else {
		return true;
	}
};

// element-plus
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component);
}

import 'jsoneditor';

// 列表组件引入
import '@/assets/icons/iconfont.js';

// 全局常量
app.config.globalProperties.global = window.g;

app.use(router);

// vuex
app.use(Vuex);
import store from './stores'
app.use(store);

app.mount('#app')
