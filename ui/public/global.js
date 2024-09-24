const { location: { hostname, protocol } } = window;
// 地矿
const port = '2693';
const devServer = '127.0.0.1'; 

window.g = {
	BASE_URL: `${protocol}//${devServer}:${port}`,
	WEBSOCKET_URL: `ws://${devServer}:${port}`,
	mainName: 'qkj基础信息平台',
	subName: 'qkj',
	projectName: 'qkj',
	siteTitle: 'qkj',
	siteInformation: '©备案号',
	time: 500000, //首页轮播图时间 毫秒
}