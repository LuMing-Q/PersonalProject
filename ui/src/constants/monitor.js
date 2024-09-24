import { ref } from 'vue';

// system 图标
export const SYSTEM_ICON = {
	linux: {
		url: new URL('@/assets/images/system-type/linux.png', import.meta.url).href, // 小图标
		// imageUrl: new URL('@/assets/images/advanceFunction/linux.png', import.meta.url).href, // 大图标
	},
	windows: {
		url: new URL('@/assets/images/system-type/Windows.png', import.meta.url).href,
		// imageUrl: new URL('@/assets/images/advanceFunction/Windows.png', import.meta.url).href,
	},
	other: {
		url: new URL('@/assets/images/system-type/other.png', import.meta.url).href,
		// imageUrl: new URL('@/assets/images/advanceFunction/other.png', import.meta.url).href,
	},
	centos: {
		url: new URL('@/assets/images/system-type/centos.png', import.meta.url).href,
		// imageUrl: new URL('@/assets/images/advanceFunction/centos.png', import.meta.url).href,
	},
	ubuntu: {
		url: new URL('@/assets/images/system-type/ubuntu.png', import.meta.url).href,
		// imageUrl: new URL('@/assets/images/advanceFunction/ubuntu.png', import.meta.url).href,
	},
	rhel: {
		url: new URL('@/assets/images/system-type/rhel.png', import.meta.url).href,
		// imageUrl: new URL('@/assets/images/advanceFunction/rhel.png', import.meta.url).href,
	}
};
	

// windows 监控指标
export const MONITOR_WINDOES = [
	{	
		url: 'exhibition/monitoring/windows/disk_use_rate', 
		span: 8, 
		title: '磁盘占用率', 
		type: 'diskRate',
		data: ref([])
	}, 
	{	
		url: 'exhibition/monitoring/windows/server_status', 
		span: 8, 
		title: '服务状态', 
		data: {
			id: 'server_status',
			height: 160,
			theme: false,
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/cpu_rate', 
		span: 8, 
		title: 'CPU使用率', 
		data: {
			id: 'cpu_rate',
			height: 160,
			theme: false,
			unit: '%',
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/memory_info', 
		span: 8, 
		title: '内存信息', 
		type: 'GB',
		data: {
			id: 'memory_info',
			height: 160,
			theme: false,
			unit: 'GB',
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/memory_use_rate', 
		span: 8, 
		title: '内存使用率', 
		data: {
			id: 'memory_use_rate',
			height: 160,
			theme: false,
			unit: '%',
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/disk_available', 
		span: 8, 
		type: 'GB',
		title: '磁盘剩余空间', 
		data: {
			id: 'disk_available',
			height: 160,
			theme: false,
			unit: 'GB',
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/disk_rw_rate', 
		span: 8, 
		title: '磁盘读写速率', 
		type: 'MB',
		data: {
			id: 'disk_rw_rate',
			height: 160,
			theme: false,
			unit: 'MB/s',
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/disk_io_rate', 
		span: 8, 
		title: '磁盘IO速率', 
		data: {
			id: 'disk_io_rate',
			height: 160,
			theme: false,
			unit: 'io/s',
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/network_rate', 
		span: 8, 
		title: '网络速率', 
		type: 'MB',
		data: {
			id: 'network_rate',
			height: 160,
			theme: false,
			unit: 'MB/s',
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/network_use_rate', 
		span: 8, 
		title: '网络使用率', 
		data: {
			id: 'network_use_rate',
			height: 160,
			theme: false,
			unit: '%',
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/network_discarded_and_error_packet', 
		span: 8, 
		title: '网络丢弃/异常包', 
		data: {
			id: 'network_discarded_and_error_packet',
			height: 160,
			theme: false,
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/system_exception_dispatches', 
		span: 8, 
		title: '系统异常分配', 
		data: {
			id: 'system_exception_dispatches',
			height: 160,
			theme: false,
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/system_thread', 
		span: 8, 
		title: '系统线程数', 
		data: {
			id: 'system_thread',
			height: 160,
			theme: false,
			data: [],
		} 
	}, 
	{	
		url: 'exhibition/monitoring/windows/system_process', 
		span: 8, 
		title: '系统进程数量', 
		data: {
			id: 'system_process',
			height: 160,
			theme: false,
			data: [],
		} 
	}, 
];


// linux 监控指标
export const MONITOR_LINUX = [
	{	url: 'exhibition/monitoring/linux/partition_space', span: 10, type: 'table', title: '各分区可用空间', data: [] }, 
	
	{	url: 'exhibition/monitoring/linux/cpu_rate', span: 8, title: 'CPU使用率', data: {
		id: 'cpu_rate',
		height: 130,
		unit: '%',
		theme: false,
		data: [],
	} }, 
	{	url: 'exhibition/monitoring/linux/memory_info', span: 8, title: '内存信息', type: 'GB', data: {
		id: 'memory_info',
		height: 130,
		unit: 'GB',
		theme: false,
		data: [],
	} }, 
	{	url: 'exhibition/monitoring/linux/memory_rate', span: 8, title: '内存使用率', data: {
		id: 'memory_rate',
		height: 130,
		unit: '%',
		theme: false,
		data: [],
	} }, 
	{	
		url: 'exhibition/monitoring/linux/hourly_network_flow', 
		span: 8, 
		type: 'GB',
		title: '每小时流量', 
		data: {
			id: 'hourly_network_flow',
			height: 130,
			unit: 'GB',
			theme: false,
			data: [],
		} 
	}, 
	{	url: 'exhibition/monitoring/linux/bandwidth_per_sec', span: 8, type: 'MB', title: '每秒网络宽带使用', data: {
		id: 'bandwidth_per_sec',
		height: 130,
		unit: 'MB',
		theme: false,
		data: [],
	} }, 
	{	url: 'exhibition/monitoring/linux/system_avg_load', span: 8, title: '系统平均负载', data: {
		id: 'system_avg_load',
		height: 130,
		theme: false,
		data: [],
	} },
	{	url: 'exhibition/monitoring/linux/disk_rw_per_sec', span: 8, title: '每秒磁盘读写容量', type: 'MB', data: {
		id: 'disk_rw_per_sec',
		height: 130,
		theme: false,
		unit: 'MB',
		data: [],
	} },
	{	url: 'exhibition/monitoring/linux/disk_rate', span: 8, title: '磁盘使用率', data: {
		id: 'disk_rate',
		height: 130,
		unit: '%',
		theme: false,
		data: [],
	} }, 
	{	url: 'exhibition/monitoring/linux/disk_rw_rate', positionLi: true, span: 8, title: '磁盘读写速率（IOPS）', data: {
		id: 'disk_rw_rate',
		height: 130,
		position: 'bottom',
		theme: false,
		unit: '%',
		data: [],
	} }, 
	{	url: 'exhibition/monitoring/linux/io_time_ratio', span: 8, title: '每1秒内i/O操作耗时占比', data: {
		id: 'io_time_ratio',
		height: 130,
		unit: '%',
		theme: false,
		data: [],
	} }, 
	{	url: 'exhibition/monitoring/linux/io_time', positionLi: true, span: 8, title: '每次IO读写耗时（参考：小于100ms）', data: {
		id: 'io_time',
		height: 130,
		unit: 'ms/次',
		position: 'bottom',
		theme: false,
		data: [],
	} }, 
	{	url: 'exhibition/monitoring/linux/socket_info', span: 8, title: '网络Socket连接信息', data: {
		id: 'socket_info',
		height: 130,
		theme: false,
		data: [],
	} }, 
	{	url: 'exhibition/monitoring/linux/fd_ctx_switch_per_sec', span: 8, title: '打开的文件描述符/每秒上下文切换次数', data: {
		id: 'fd_ctx_switch_per_sec',
		height: 130,
		theme: false,
		data: [],
	} }, 
	{	url: 'exhibition/monitoring/linux/partition_nodes', type: 'table', span: 10, title: '各分区可用节点数', data: [] }, 
];