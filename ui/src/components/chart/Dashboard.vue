<!---
- 仪表盘公用组件
- <Dashboard :entityl="entitylDashboard" /> // 引用
- // 参数
- entitylDashboard: {
-       id: 'Dashboard', // mode id
-       height: 130, // 自定义高度
-       width: 200,
-       data: [ // 数据
-         { type: 'CPU', value: 0.11, color: '#34B015' }
-       ]
-     },
--->
<template>
  <div :style="{ width: `${entityl.width}px` }" class="Chart">
    <div class="title">
      <div v-for="item in entityl.data" :key="item.type">{{ item.type }}</div>
    </div>
    <div :id="entityl.id" />
    <div v-if="entityl.data.length === 0" class="none">
      暂无数据
    </div>
  </div>
</template>

<script  name="Dashboard" setup>
import { Chart, registerShape } from '@antv/g2';
import { onMounted, watch, ref, defineProps } from 'vue';

const chartCurrent = ref('');
const props = defineProps({
	entityl: {
		type: Object,
		default: () => ({ id: 'Dashboard', height: 130, width: 200, data: [{ type: 'CPU', value: 0.11, color: '#34B015' }] })
	}
});

const getData = () => {
	// chartCurrent存在就销毁重新绘制chart
	if (chartCurrent.value) {
		chartCurrent.value.destroy();
	}
	registerShape('point', 'pointer', {
		draw(cfg, group) {
			const point = cfg.points[0];
			const center = this.parsePoint({ x: 0, y: 0 });
			const target = this.parsePoint({ x: point.x, y: 0.9 });
			const dirvec = { x: center.x - target.x, y: center.y - target.y };
			// normalize
			const length = Math.sqrt(dirvec.x * dirvec.x + dirvec.y * dirvec.y);
			dirvec.x *= (1 / length);
			dirvec.y *= (1 / length);
			// rotate dirvector by -90 and scale
			const angle1 = -Math.PI / 2;
			const x1 = Math.cos(angle1) * dirvec.x - Math.sin(angle1) * dirvec.y;
			const y1 = Math.sin(angle1) * dirvec.x + Math.cos(angle1) * dirvec.y;
			// rotate dirvector by 90 and scale
			const angle2 = Math.PI / 2;
			const x2 = Math.cos(angle2) * dirvec.x - Math.sin(angle2) * dirvec.y;
			const y2 = Math.sin(angle2) * dirvec.x + Math.cos(angle2) * dirvec.y;
			// polygon vertex
			const path = [
				['M', target.x + x1 * 1, target.y + y1 * 1],
				['L', center.x + x1 * 3, center.y + y1 * 3],
				['L', center.x + x2 * 3, center.y + y2 * 3],
				['L', target.x + x2 * 1, target.y + y2 * 1],
				['Z']
			];
			const tick = group.addShape('path', {
				attrs: {
					path,
					fill: cfg.color
				}
			});
			return tick;
		}
	});

	const chart = new Chart({
		container: props.entityl.id,
		autoFit: true,
		height: props.entityl.height,
	});
	// console.log(this.data);
	chart.data(props.entityl.data);
	chart.coordinate('polar', {
		startAngle: -8.5 / 8 * Math.PI,
		endAngle: 0.5 / 8 * Math.PI,
		radius: 0.75
	});
	chart.scale('value', {
		min: 0,
		max: 1,
		tickInterval: 1,
	});
	chart.axis(false);
	chart.axis('value', {
		line: null,
		label: {
			offset: -10,
			style: {
				fontSize: 1,
				textAlign: 'center',
				stroke: props.entityl.data.length > 0 ? props.entityl.data[0].color : '',
			},
		},
		subTickLine: {
			count: 9000,
			length: 8,
			style: {
				stroke: '#67C39E99',
			},
		},
		tickLine: {
			length: 0,
		},
		grid: null,
	});
	chart.facet('rect', {
		fields: ['type'],
		showTitle: false,
		eachView: function eachView(view, facet) {
			const data = facet.data[0];
			// 指针
			// console.log(data, 66666666);
			view
				.point()
				.position('value*1')
				.shape('pointer')
				.color('#fff')
				.animate({
					appear: {
						animation: 'fade-in',
					},
				});
			// 仪表盘背景
			view.annotation().arc({
				start: [0, 1],
				end: [1, 1],
				style: {
					stroke: '#BFBFBF',
					lineWidth: 12,
					offsetY: 0
				}
			});
         
			// 仪表盘前景
			view.annotation().arc({
				start: [0, 1],
				end: [data.value, 1],
				style: {
					stroke: data.color,
					lineWidth: 10
				}
			});
			// 仪表盘信息
			// console.log(data, facet, that.entityl);
			const percent = (data.value * 100) % 1 === 0 ? data.value : (data.value * 100).toFixed(2);
			view.annotation().text({
				position: ['50%', '75%'],
				content: data.type,
				style: {
					fontSize: 0,
					fill: '#000',
					fontWeight: 400,
					textAlign: 'center'
				},
				offsetX: 0,
				offsetY: 0
			});
			view.annotation().text({
				position: ['50%', '70%'],
				content: `${percent}%`,
				style: {
					fontSize: 10,
					fill: data.color,
					fontWeight: 400,
					textAlign: 'center'
				},
				offsetX: 0,
				offsetY: 15
			});
		}
	});
	chart.render();
	chartCurrent.value = chart;
};

watch(
	() => props.entityl,
	() => {
		getData();
	},
	{ deep: true }
);
onMounted(() => { 
	getData();
});
</script>

<style lang="less" scoped>
.Chart {
  width: 100% !important;
  text-align: center;
  height: 90px !important;
  background: #FFFFFF;
  box-shadow: 0 2px 4px 0 rgba(0,0,0,0.05);
  border-radius: 4px;
  margin-top: 12px;
  .none {
    text-align: center;
    line-height: 142px;
    color: #666;
  }
  .title {
    display: flex;
    justify-content: center;
    font-weight: 400;
    font-size: 10px;
    color: #262626;
    margin: 8px 0;
    
  }
}

</style>
