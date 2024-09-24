// 状态码错误信息
export const codeMessage = {
	200: '服务器成功返回请求的数据。',
	201: '新建或修改数据成功。',
	202: '一个请求已经进入后台排队（异步任务）。',
	204: '删除数据成功。',
	400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
	401: '用户没有权限（令牌、用户名、密码错误）。',
	403: '用户得到授权，但是访问是被禁止的。',
	404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
	406: '请求的格式不可得。',
	410: '请求的资源被永久删除，且不会再得到的。',
	422: '当创建一个对象时，发生一个验证错误。',
	500: '服务器发生错误，请检查服务器。',
	502: '网关错误。',
	503: '服务不可用，服务器暂时过载或维护。',
	504: '网关超时。',
};

// 硬盘状态
export const DISK_STATUS = {
	'available': '未挂载',
	'in-use': '已挂载',
	'error': '异常',
	'error_backing-up': '备份异常',
	'error_restoring': '恢复异常',
	'error_extending': '扩展异常',
	'error_deleting': '删除异常',
	'deleting': '删除中',
	'creating': '创建中',
	'detaching': '分离中',
	'attaching': '挂载中',
	'reserved': '保留',
	'maintenance': '维护',
	'restoring-backup': '备份恢复中',
	'backing-up': '备份中'
};
// 云主机状态
export const MACHINE_STATUS = {
	'ACTIVE': '运行',
	'BUILD': '构建',
	'deleting': '删除中',
	'SHUTOFF': '关闭',
	'ERROR': '异常',
	'REBOOT': '重启中',
	'VERIFY_RESIZE': '待确认',
	'RESIZE': '重置中',
	'RESCUE': '救援中',
	'REBUILD': '重建中',
	'SHELVED_OFFLOADED': '搁置中',
	'shelving_image_uploading': '搁置上传镜像中',
	'spawning': '孵化中',
	'HARD_REBOOT': '重启中',
	'PAUSED': '暂停',
	'shelving_image_pending_upload': '搁置镜像上传中',
	// 'spawning': '取消搁置中'
};
// 物理主机状态
export const PHYSICAL_HOST_STATUS = {
	'enabled': '开启',
	'disabled': '关闭',
	'up': '运行',
	'down': '停止',
};
// 镜像状态
export const IMAGE_STATUS = {
	'queued': '等待',
	'active': '激活',
	'killed': '导入异常',
	'deleted': '不可用',
	'deactivated': '不可用',
	'wait': '等待导入',
	'saving': '导入中'
};

// 备份状态
export const BACKUPS_STATUS = {
	'creating': '创建中',
	'available': '可用',
	'deleting': '删除中',
	'restoring': '恢复中',
	'error': '异常',
	'error_deleting': '删除异常',
};

// 快照状态
export const SNAPSHOT_STATUS = {
	'creating': '创建中',
	'available': '可用',
	'deleted': '不可用',
	'error': '异常',
	'deleting': '删除中',
	'error_deleting': '删除异常',
	'backing-up': '备份中',
	'restoring': '恢复中',
	'unmanaging': '托管中',
};

// 网络状态
export const NETWORK_STATUS = {
	'ACTIVE': '激活',
	'DOWN': '未激活',
	'BUILD': '构建中',
	'ERROR': '异常',
};

// 端口状态
export const PORT_STATUS = {
	'ACTIVE': '可用',
	'DOWN': '不可用',
	'BUILD': '构建中',
	'ERROR': '异常',
};

// 路由状态
export const ROUTE_STATUS = {
	'ACTIVE': '可用',
	'DOWN': '不可用',
	'BUILD': '构建中',
	'ERROR': '异常',
};

// 弹性IP状态
export const ELASTIC_IP_STATUS = {
	'ACTIVE': '可用',
	'DOWN': '不可用',
	'BUILD': '构建中',
	'ERROR': '异常',
};
// 云主机详情接口动作状态
export const MACHINE_INTERFACE_STATUS = {
	'start': '开机',
	'stop': '关机',
	'createImage': '备份',
	'create': '创建',
	'unlock': '解锁',
	'lock': '锁定',
	'attach_volume': '挂载硬盘',
	'detach_volume': '卸载硬盘',
	'changePassword': '修改密码',
	'reboot': '重启',
	'live-migration': '热迁移',
	'unrescue': '取消救援',
	'rescue': '救援',
	'confirmResize': '确认迁移/扩容',
	'migrate': '冷迁移',
	'rebuild': '重装系统',
	'resize': '调整规格',
	revertResize: '恢复调整'
};
// 负载均衡
export const LOAD_BALANCE = {
	'DOWN': '停止',
	'UP': '运行',
};

