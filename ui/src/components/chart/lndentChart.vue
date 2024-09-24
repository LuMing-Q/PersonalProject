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
const chartCurrent = ref('');
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
const getDate = ()=>{
	if (chartCurrent.value) {
		chartCurrent.value.destroy();
	}
	G6.registerNode('file-node', {
		draw: function draw(cfg, group) {
			const keyShape = group.addShape('rect', {
				attrs: {
					x: 10,
					y: -12,
					fill: '#fff',
					stroke: null,
				},
			});
			let isLeaf = false;
			if (cfg.collapsed) {
				group.addShape('marker', {
					attrs: {
						symbol: 'triangle',
						x: 4,
						y: -2,
						r: 4,
						fill: '#666',
					},
					name: 'marker-shape',
				});
			} else if (cfg.children && cfg.children.length > 0) {
				group.addShape('marker', {
					attrs: {
						symbol: 'triangle-down',
						x: 4,
						y: -2,
						r: 4,
						fill: '#666',
					},
					// must be assigned in G6 3.3 and later versions. it can be any string you want, but should be unique in a custom item type
					name: 'marker-shape',
				});
			} else {
				isLeaf = true;
			}
			const shape = group.addShape('text', {
				attrs: {
					x: 15,
					y: 4,
					text: cfg.name,
					fill: '#666',
					fontSize: 16,
					textAlign: 'left',
					fontFamily:
          typeof window !== 'undefined' ?
          	window.getComputedStyle(document.body, null).getPropertyValue('font-family') ||
              'Arial, sans-serif' :
          	'Arial, sans-serif',
				},
				// must be assigned in G6 3.3 and later versions. it can be any string you want, but should be unique in a custom item type
				name: 'text-shape',
			});
			const bbox = shape.getBBox();
			let backRectW = bbox.width;
			let backRectX = keyShape.attr('x');
			if (!isLeaf) {
				backRectW += 8;
				backRectX -= 15;
			}
			keyShape.attr({
				width: backRectW,
				height: bbox.height + 4,
				x: backRectX,
			});
			return keyShape;
		},
	});
	G6.registerEdge(
		'step-line',
		{
			getControlPoints: function getControlPoints(cfg) {
				const startPoint = cfg.startPoint;
				const endPoint = cfg.endPoint;
				return [
					startPoint,
					{
						x: startPoint.x,
						y: endPoint.y,
					},
					endPoint,
				];
			},
		},
		'polyline',
	);

	const container = document.getElementById(entitylData.entityl.id);
	const width = window.innerWidth - 500;
	const height = entitylData.entityl.height;
	const graph = new G6.TreeGraph({
		container: entitylData.entityl.id,
		width,
		height,
		linkCenter: true,
		maxZoom: 1,
		modes: {
			default: [
				{
					type: 'collapse-expand',
					animate: false,
					onChange: function onChange(item, collapsed) {
						const data = item.get('model');
						data.collapsed = collapsed;
						return true;
					},
				},
				'drag-canvas',
				// 'zoom-canvas', //缩放
			],
		},
		defaultEdge: {
			style: {
				stroke: '#A3B1BF',
			},
		},
		layout: {
			type: 'indented',
			isHorizontal: true,
			direction: 'LR',
			indent: 30,
			getHeight: function getHeight() {
				return 5;
			},
			getWidth: function getWidth() {
				return 5;
			},
		},
	});
	const data = {
		id: '1',
		name: 'src',
		children: [
			{
				id: '1-1',
				name: 'behavior',
				children: [],
			},
			{
				id: '1-3',
				name: 'graph',
				children: [
					{
						id: '1-3-1',
						name: 'controller',
						children: [],
					},
				],
			},
			{
				id: '1-5',
				name: 'item',
				children: [],
			},
			{
				id: '1-6',
				name: 'shape',
				children: [
					{
						id: '1-6-2',
						name: 'extend',
						children: [],
					},
				],
			},
			{
				id: '1-7',
				name: 'util',
				children: [],
			},
		],
	};

	graph.node((node) => ({
		type: 'file-node',
		label: node.name,
	}));
	graph.edge(() => ({
		type: 'step-line',
	}));

	graph.data(entitylData.entityl.data);
	graph.render();
	graph.fitView();
	if (typeof window !== 'undefined')
		window.onresize = () => {
			if (!graph || graph.get('destroyed')) return;
			if (!container || !window.innerWidth - 500 || !entitylData.entityl.height) return;
			graph.changeSize(window.innerWidth - 500, entitylData.entityl.height);
		};
	chartCurrent.value = graph;

};
watch(
	() => entitylData.entityl.data, 
	()=>{
		getDate();
	}
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