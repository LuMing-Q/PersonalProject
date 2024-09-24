
// 健康监控协议
// export const TYPE_LIST = [
// 	{ id: 'HTTP', name: 'HTTP' },
// 	{ id: 'HTTPS', name: 'HTTPS' },
// 	{ id: 'PING', name: 'PING' },
// 	{ id: 'SCTP ', name: 'SCTP ' },
// 	{ id: 'TCP ', name: 'TCP ' },
// 	{ id: 'TLS-HELLO ', name: 'TLS-HELLO ' },
// 	{ id: 'UDP-CONNECT ', name: 'UDP-CONNECT ' },
// ];
export const TYPE_LIST = {
	HTTP: [
		{ id: 'HTTP', name: 'HTTP' },
		{ id: 'HTTPS', name: 'HTTPS' },
		{ id: 'PING', name: 'PING' },
		{ id: 'TCP', name: 'TCP' },
		{ id: 'TLS-HELLO', name: 'TLS-HELLO' },
		{ id: 'UDP-CONNECT', name: 'UDP-CONNECT' }
	],
	HTTPS: [
		{ id: 'HTTP', name: 'HTTP' },
		{ id: 'HTTPS', name: 'HTTPS' },
		{ id: 'PING', name: 'PING' },
		{ id: 'TCP', name: 'TCP' },
		{ id: 'TLS-HELLO', name: 'TLS-HELLO' },
		{ id: 'UDP-CONNECT', name: 'UDP-CONNECT' }
	],
	TCP: [
		{ id: 'HTTP', name: 'HTTP' },
		{ id: 'HTTPS', name: 'HTTPS' },
		{ id: 'PING', name: 'PING' },
		{ id: 'TCP', name: 'TCP' },
		{ id: 'TLS-HELLO', name: 'TLS-HELLO' },
	],
	UDP: [
		{ id: 'HTTP', name: 'HTTP' },
		// { id: 'SCTP', name: 'SCTP' },
		{ id: 'TCP', name: 'TCP' },
		{ id: 'UDP-CONNECT', name: 'UDP-CONNECT' }
	],
};
// 请求方式
export const HTTP_LIST = [
	{ id: 'CONNECT', name: 'CONNECT' },
	{ id: 'DELETE', name: 'DELETE' },
	{ id: 'GET', name: 'GET' },
	{ id: 'HEAD', name: 'HEAD' },
	{ id: 'OPTIONS', name: 'OPTIONS' },
	{ id: 'PATCH', name: 'PATCH' },
	{ id: 'POST', name: 'POST' },
	{ id: 'PUT', name: 'PUT' },
	{ id: 'TRACE', name: 'TRACE' },
];
// 操作状态
export const OPERATING = {
	'ONLINE': '正常',
	'OFFLINE': '管理禁用',
	'DRAINING': '停止服务',
	'DEGRADED': '部分异常',
	'ERROR': '异常',
	'NO_MONITOR': '无监控',
};
// 运行状态
export const PROVISIONING = {
	'ACTIVE': '运行',
	'DELETED': '删除',
	'ERROR': '异常',
	'PENDING_CREATE': '创建中',
	'PENDING_UPDATE': '修改中',
	'PENDING_DELETE': '删除中',
};

// 负载均衡监听协议
export const PROTOCOL_LIST = [
	{ label: 'HTTP', value: 'http' },
	{ label: 'HTTPS', value: 'https' },
	{ label: 'STCP', value: 'stcp' },
	{ label: 'TCP', value: 'tcp' },
	{ label: 'UDP', value: 'udp' }
];
// 负载均衡调度算法
export const ALGORITHM_LIST = [
	{ label: '轮询', value: 'roundrobin' },
	{ label: '最少连接数', value: 'leastconn' },
	{ label: '首次连接', value: 'first' },
	{ label: '客户端地址HASH', value: 'source' },
	{ label: '请求地址HASH', value: 'uri' },
];
