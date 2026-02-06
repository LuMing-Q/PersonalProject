// 导入 Vue Router 的 createRouter 和 createWebHistory 方法
import { createRouter, createWebHistory } from 'vue-router';
// 导入 lodash 的 uniqBy 方法
import { uniqBy } from 'lodash';
// 导入 NProgress 库和样式
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';
// 导入 session 工具函数
import { session } from '@/utils';
// 导入重定向路由配置
import redirectList from './redirect';
// 导入静态路由配置
import staticRoutes from './static.js';

// 动态导入 views 目录下的所有 Vue 组件
const modules = import.meta.glob('../views/**/**.vue');
// 动态导入 views 目录下的所有表单组件
const forms = import.meta.glob('../views/**/form.vue');

// 初始化路由数组，包含一个根路径重定向到 /home 的路由
let routes = [{ path: '/', redirect: '/home' }];

// 遍历所有动态导入的组件路径
Object.keys({ ...modules, ...forms }).map((key) => {
	// 提取路径部分，去除前缀和后缀
	let path = key.replace('./', '/').replace('/index.vue', '').substr(7);
	// 如果不是 index.vue 文件，则去除.vue 后缀
	if (key.indexOf('index.vue') === -1) path = path.replace('.vue', '');
	// 构建路由名称，将路径中的斜杠替换为大写字母
	let metaName = path.split('').reduce((name, char) =>
		(name.endsWith('/') ? name.replace('/', '') + char.toUpperCase() : name + char), '');
	// 如果路径为空，则设置名称为 Index
	if (path === '') metaName = 'Index';
	// 定义布局名称，登录页面使用 login-layout，其他页面使用 home-layout
	let layout = '';
	if (path === '/login') {
		layout = 'login-layout';
	} else {
		layout = 'home-layout';
	}
	// 构建临时路由对象，包含路径、名称、组件、元数据等信息
	const tempRoute = {
		path,
		name: metaName,
		component: modules[key],
		meta: { layout, title: metaName },
	};
	// 检查是否需要重定向
	const resultOfRedirect = redirectList.find(r => r.from === path);
	if (resultOfRedirect) tempRoute.redirect = resultOfRedirect.to;

	// 将临时路由对象添加到路由数组中
	routes.push(tempRoute);
}).filter(r => r);

// 添加 404 路由，当访问的路径不存在时，重定向到 Error 页面
routes.push({
	// 使用正则表达式匹配所有路径
	path: '/:all(.*)*',
	// 当访问的路径不存在时，加载 Error 页面组件
	component: () => import('@/views/Error.vue'),
	meta: {
		layout: 'login-layout',
		name: '404'
	}
});

// 添加无权限访问路由，当用户没有权限访问某个页面时，重定向到 NoPermission 页面
routes.push({
	path: '/noPermission',
	component: () => import('@/views/NoPermission.vue'),
	meta: {
		layout: 'admin-layout',
		name: 'noPermission'
	}
});

// 合并静态路由和动态路由，去除重复的路由
routes = uniqBy([...staticRoutes, ...routes], 'path');

// 创建 Vue Router 实例
const router = createRouter({
	history: createWebHistory(),
	routes,
});

// 全局前置守卫，在路由跳转前执行
router.beforeEach((to, from, next) => {
	// 配置 NProgress 进度条
	NProgress.configure({ showSpinner: false });
	// 开始加载进度条
	NProgress.start();
	// 设置文档标题
	document.title = window.g.siteTitle;
	// 获取用户信息
	const user = session.getUser();

	// 如果用户未登录且访问的不是登录页面，则重定向到登录页面
	if (!user && to.path !== '/login') {
		return next({ path: '/login' });
	}
	// 放行
	next();
});

// 全局后置钩子，在路由跳转后执行
router.afterEach(() => {
	// 完成进度条
	NProgress.done();
});

// 导出路由实例
export default router;
