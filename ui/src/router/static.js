// 静态路由列表
let staticRoutes = [

];

staticRoutes = staticRoutes.map((route) => {
	const tempObj = { ...route };
	if (route.children) {
		route.children = route.children.map((child) => ({ ...child, ...{ meta: { layout: 'admin-layout', title: child.name } } }));
	}
	return tempObj;
});

export default staticRoutes;
