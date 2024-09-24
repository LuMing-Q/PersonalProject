<!---
- 柱状图组件
- <Histogram :entityl="entityl" />// 引用
- // 参数
- entityl: {
        id: 'ingress', // 唯一标识
        height: 200, // 高度
        data: {
				}
      },
--->
<template>
  <div class="chart_tu">
    <div :id="props.entityl.id" />
  </div>
</template>

<script name="Histogram" setup>
import { Chart, registerTheme } from '@antv/g2';
import { onMounted, watch, ref } from 'vue';

const chartCurrent = ref('');
const props = defineProps({
	entityl: { type: Object, default: () => {} }, // 
	dataEntityl: { type: Array, default: () => [] } // 
});

registerTheme('newTheme', {
	maxColumnWidth: 20,
	minColumnWidth: 20
});
const getData = () => {
	if (chartCurrent.value) {
		chartCurrent.value.destroy();
	}
	const chart = new Chart({
		container: props.entityl.id,
		autoFit: true,
		height: 130,
	});
	chart.data(props.dataEntityl);
	chart.axis('time', false);
	chart.axis('value', false);

	chart.tooltip(false);
	chart.interaction('active-region');
	chart.legend({
		position: 'right',
		itemWidth: 150,
		maxWidthRatio: 0.5,
		itemSpacing: 20,
		itemName: {
			formatter(text, item, index) {
				console.log(text, item, index);
				return `${props.dataEntityl[index].type}`;
			},
			style: {
				lineHeight: 30
			}
		},
		itemValue: {
			alignRight: true,
			formatter(text, item, index) {
				console.log(text, item, index);
				return `${props.dataEntityl[index].value}个`;
			},
			style: {
				fill: '#516FE6',
				textAlign: 'end',
				lineHeight: 30,
				fontWeight: '400',
				fontSize: 16
			}
		},
	});
	chart
		.interval()
		.adjust('stack')
		.position('time*value')
		.color('type', ['#516FE6', '#5E7AFF99', '#FFA028', '#FC5260']);
	chart.theme('newTheme');
	chart.render();
	chartCurrent.value = chart;
};
watch(
	() => props.dataEntityl,
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
.chart_tu {
  position: relative;
  margin-right: 50px;
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