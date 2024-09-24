<!---
- 拓扑图组件
- <Topological :entityl="entityl" />// 引用
- // 参数
- entityl: {
        id: 'ingress', // 唯一标识
        height: 200, // 高度
        data: {
					'id': 'Modeling Methods', // 名称
					'type': 'image', // 节点类型
					'img': 'https://g.alicdn.com/cm-design/arms-trace/1.0.155/styles/armsTrace/images/TAIR.png', // 图片
					'children': [
						{
							'id': 'Logistic regression',
						},
					]	
				}
      },
--->
<template>
  <div class="chart_tu">
    <div :id="entitylData.entityl.id" />
  </div>
</template>

<script name="Topological" setup>
import G6 from '@antv/g6';
import { onMounted, watch, ref } from 'vue';
let entitylData = defineProps({
	entityl: {
		type: Object,
		default: () => ({
			id: 'Topological',
			height: 600,
			data: {}
		})
	}
});
const tooltip = new G6.Tooltip({
	offsetX: 10,
	offsetY: 10,
	// 	// the types of items that allow the tooltip show up
	// 	// 允许出现 tooltip 的 item 类型
	itemTypes: ['node'],
	// 	// custom the tooltip's content
	// 	// 自定义 tooltip 内容
	getContent: (e) => {
		const outDiv = document.createElement('div');
		if (e.item['_cfg'].model.icon === 'cloud'){
			outDiv.innerHTML = `
					<ul>
						<li>ID: ${e.item['_cfg'].model.id}</li>
						<li>名称: ${e.item['_cfg'].model.name}</li>
					</ul>`;
			return outDiv;
		} else if (e.item['_cfg'].model.icon === 'pc'){
			outDiv.innerHTML = `
					<ul>
						<li>ID: ${e.item['_cfg'].model.info.id}</li>
						<li>名称: ${e.item['_cfg'].model.info.hypervisor_hostname}</li>
						<li>状态: ${e.item['_cfg'].model.info.state === 'up' ? '运行' : '停止'}</li>
						<li>可用状态: ${e.item['_cfg'].model.info.status === 'enabled' ? '可用' : '不可用'}</li>
						<li>CPU总核数: ${e.item['_cfg'].model.info.vcpus} 核</li>
						<li>CPU剩余: ${e.item['_cfg'].model.info.vcpus - e.item['_cfg'].model.info.vcpus_used} 核</li>
						<li>内存总大小: ${e.item['_cfg'].model.info.memory_gb} GB</li>
						<li>内存剩余: ${e.item['_cfg'].model.info.memory_gb - e.item['_cfg'].model.info.memory_gb_used} GB</li>
						<li>磁盘大小: ${e.item['_cfg'].model.info.local_gb} GB</li>
						<li>磁盘剩余: ${e.item['_cfg'].model.info.local_gb_used} GB</li>
					</ul>`;
			return outDiv;
		} else if (e.item['_cfg'].model.icon === 'vpc'){
			const getAddr = ((val)=>{
				const hh = ref('');
			 val.map(r=>{
					e.item['_cfg']?.model?.info?.addresses[r].map((w)=> hh.value = hh.value + w.addr + ' ');
				});
				return hh.value;
			});
			const addr = Object.keys(e.item['_cfg']?.model?.info?.addresses);
			outDiv.innerHTML = `
				<ul>
					<li>ID: ${e.item['_cfg'].model?.info?.id}</li>
					<li>名称: ${e.item['_cfg'].model.info.name}</li>
					<li>项目: ${e.item['_cfg'].model.info.project_name}</li>
					<li>状态: ${e.item['_cfg'].model.info.status === 'ACTIVE' ? '运行' : e.item['_cfg'].model.info.status === 'BUILD' ? '构建' : e.item['_cfg'].model.info.status === 'SHUTOFF' ? '关闭' : '异常'}</li>
					<li>CPU核数: ${e.item['_cfg'].model.info.vcpus} 核</li>
					<li>GPU型号和数量: ${e.item['_cfg'].model.info.gpu}</li>
					<li>内存大小: ${e.item['_cfg'].model.info.ram} GB</li>
					<li>磁盘大小: ${e.item['_cfg'].model.info.disk} GB</li>
					<li>IP地址: ${getAddr(addr)}</li>
				</ul>`;
			return outDiv;
		}
	},
});
const getDate = ()=>{
	let data = entitylData.entityl.data;
	if (JSON.stringify(data) !== '{}'){
		const container = document.getElementById(entitylData.entityl.id);
		const width = container.scrollWidth;
		const graph = new G6.TreeGraph({
			container: entitylData.entityl.id,
			width,
			height: entitylData.entityl.height,
			linkCenter: true,
			plugins: [tooltip],
			modes: {
				default: [
					{
						// type: 'Tooltip',
						onChange: function onChange(item, collapsed) {
							const datast = item.get('model');
							datast.collapsed = collapsed;
							return true;
						},
					},
					'drag-canvas',
					'zoom-canvas',
				],
			},
			defaultNode: {
				size: 26,
				anchorPoints: [
					[0, 0.5],
					[1, 0.5],
				],
			},
			defaultEdge: {
				type: 'cubic-vertical',
			},
			layout: {
				type: 'dendrogram',
				direction: 'TB', // H / V / LR / RL / TB / BT
				nodeSep: 40,
				rankSep: 60,
			},
		});
		graph.node(function (node) {
			let position = 'right';
			let rotate = 0;
			if (!node.children) {
				position = 'bottom';
				rotate = Math.PI / 2;
			}
			return {
				label: node.name,
				labelCfg: {
					position,
					offset: 5,
					style: {
						rotate,
						textAlign: 'start',
					},
				},
			};
		});
		// graph.on('node:mouseenter', (e) => {
		// 	const nodeItem = e.item; // 获取被点击的节点元素对象
		// 	console.log(nodeItem._cfg, 'llll');
		// });
		graph.data(entitylData.entityl.data);
		graph.render();
		graph.fitView();
		if (typeof window !== 'undefined')
			window.onresize = () => {
				if (!graph || graph.get('destroyed')) return;
				if (!container || !container.scrollWidth || !container.scrollHeight) return;
				graph.changeSize(container.scrollWidth, container.scrollHeight);
			};
	}
	
};
watch(
	() => entitylData.entityl.data, 
	()=>{
		getDate();

	},
);
onMounted(() => {
	getDate();
});

</script>

<style lang="less" scoped>
.chart_tu {
  position: relative;
  //border: 1px solid red;
}
.none {
  position: absolute;
  top: 30%;
  left: 30%;
  text-align: center;
  line-height: 142px;
  color: #666;
}
:deep(.g2-tooltip-list) {
  max-height: 300px;
  overflow-y: auto;
}
</style>