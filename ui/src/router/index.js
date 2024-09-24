import { createRouter, createWebHistory } from 'vue-router';
import { uniqBy } from 'lodash';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
import { session } from '@/utils';
import redirectList from './redirect'; // 重定向路由
import staticRoutes from './static.js'; // 静态路由

const modules = import.meta.glob('../views/**/**.vue');
const forms = import.meta.glob('../views/**/form.vue');
let routes = [{ path: '/', redirect: '/home' }];

Object.keys({ ...modules, ...forms }).map((key) => {
	// if (key.indexOf('index.vue') !== -1 || key.indexOf('form.vue') !== -1) {
	let path = key.replace('./', '/').replace('/index.vue', '').substr(7);
	if (key.indexOf('index.vue') === -1) path = path.replace('.vue', ''); // 处理表单路由路径
	// if (key.indexOf('form.vue') !== -1) path = path.replace('.vue', ''); // 处理表单路由路径
	let metaName = path.split('').reduce((name, char) => (name.endsWith('/') ? name.replace('/', '') + char.toUpperCase() : name + char), '');
	if (path === '') metaName = 'Index';
	let layout = '';
	if (path === '/login') {
		layout = 'login-layout';
	} else {
		layout = 'home-layout';
	}
	const tempRoute = {
		path,
		name: metaName,
		component: modules[key],
		meta: { layout, title: metaName },
	};
	// 重定向
	const resultOfRedirect = redirectList.find(r => r.from === path);
	if (resultOfRedirect) tempRoute.redirect = resultOfRedirect.to;

	routes.push(tempRoute);
}).filter(r => r);

// 404路由
routes.push({ path: '/:all(.*)*', component: () => import('@/views/Error.vue'), meta: { layout: 'login-layout', name: '404' } });

// 无权限访问
routes.push({ path: '/noPermission', component: () => import('@/views/NoPermission.vue'), meta: { layout: 'admin-layout', name: 'noPermission' } });

// 静态路由
routes = uniqBy([...staticRoutes, ...routes], 'path');

const router = createRouter({
	history: createWebHistory(),
	routes,
});


router.beforeEach((to, from, next) => {
	NProgress.configure({ showSpinner: false });
	NProgress.start();
	document.title = window.g.siteTitle;
	const user = session.getUser();

	if (!user && to.path !== '/login') {
		return next({ path: '/login' });
	}
	next();
});

// 路由加载后
router.afterEach(() => {
	NProgress.done();
});


export default router;
