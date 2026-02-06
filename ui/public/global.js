const { location: { hostname, protocol } } = window;

const port = '2693';
// const port = '8080';
const devServer = '127.0.0.1'; 

window.g = {
	BASE_URL: `${protocol}//${devServer}:${port}`,
	WEBSOCKET_URL: `ws://${devServer}:${port}`,
	siteTitle: 'qkj',
	time: 500000, //首页轮播图时间 毫秒
}