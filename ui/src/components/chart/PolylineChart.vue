<!---
- 曲线图组件
- <PolylineChart :entityl="entityl" />// 引用
- // 参数
- entityl: {
        id: 'ingress',
        height: 200,
        area: true， //是否面积图
				axis: true， //是否显示坐标轴
				theme: true, // 开启暗黑主题
        earlyWarningLine: false, 是否有预警线
        earlyWarningLineNum: [], 预警线值
        maxFalesNum: false 是否有最大值
        maxNum:0 , 最大值
        unit: '', // 单位
        valueAlias: '', // 数值别名
        position: 'left'， //图例位置, 不传则默认隐藏图例
				customTooltip:false, // 自定义tooltip
        data: [
          { temperature: 10, month: '10:00', city: 'tes' },
          { temperature: 12, month: '10:00', city: 'sfe' },
          { temperature: 16, month: '11:00', city: 'tes' },
          { temperature: 10, month: '11:00', city: 'sfe' },
          { temperature: 15, month: '12:00', city: 'tes' },
          { temperature: 24, month: '12:00', city: 'sfe' },
          { temperature: 16, month: '13:00', city: 'tes' },
          { temperature: 10, month: '13:00', city: 'sfe' },
        ],
      },
--->
<template>
  <div class="chart_tu">
    <div :id="entityl.id" />
    <div v-if="entityl.data.length === 0" class="none">
      暂无数据
    </div>
  </div>
</template>

<script name="PolylineChart" setup>
import { ref, onMounted, watch } from 'vue';
import { Chart } from '@antv/g2';

let data = defineProps({
	entityl: {
		type: Object,
		default: () => ({
			id: 'ingress',
			height: 200,
			area: true,
			theme: false,
			axis: false,
			unit: '', // 单位
			valueAlias: '', // 数值别名
			isFormatHHmm: false, // 是否格式化为HH:mm形式
			isEnterable: false, // 是允许鼠标穿透tooltip
			disabledLegendList: [], // 禁用不展示的图例列表
			data: [],
			paddingStyle: false,
			count: false, // 折线图坐标显示个数
			customTooltip: false, // 自定义tooltip
		})
	}
});
const chartCurrent = ref('');
const numChart = ref(0);
const newArr = ref([]);

const lineChart = () => {
	// chartCurrent存在就销毁重新绘制chart
	if (chartCurrent.value) {
		chartCurrent.value.destroy();
	}
	newArr.value = [];
	numChart.value = -100000;
	if (data.entityl.earlyWarningLineNum) {
		data.entityl.earlyWarningLineNum.forEach(el => {
			const result = newArr.value.findIndex(ol => el.value === ol.value);
			if (result !== -1) {
				// this.newArr[result].value = this.newArr[result].value + el.value;
			} else {
				newArr.value.push(el);
				numChart.value = numChart.value > Number(el.value) ? numChart : Number(el.value);
			}
		});
	}
	const chart = new Chart({
		container: data.entityl.id,
		autoFit: true,
		height: data.entityl.height,
		padding: data.entityl.paddingStyle ? '' : [50, 40, 45, 70],
	});
	chart.data(data.entityl.data);
	const configAlias = { alias: data.entityl.valueAlias ? data.entityl.valueAlias : '' };
	if (data.entityl.maxFalesNum) {
		chart.scale({
			month: {
				range: [0, 1],
			},
			temperature: {
				nice: true,
				min: 0,
				max: data.entityl.maxFalesNum ? data.entityl.maxNum + 1 : false,
				...configAlias,
				
			},
		});
	} else {
		chart.scale({
			month: { range: [0, 1], tickCount: data.entityl.count ? 6 : '' },
			temperature: {
				nice: true,
				...configAlias,
			},
		});
	}
	chart.axis('month', {
		label: {
			formatter: (val) => (data.entityl.isFormatHHmm ? val : val),
			style: {
				fill: '#8c8c8c',
				// textAlign: 'start',
				fontWeight: 400,
			},
			rotate: 0,
		},
		line: {
			style: {
				lineWidth: 1,
				strokeOpacity: 1,
			}
		},
		tickLine: {
			style: {
				strokeOpacity: 0,
			}
		},
		grid: {
			line: {
				style: {
					stroke: '#ccc',
					strokeOpacity: 0.3,
				}
			}
		}
	});
	if (data.entityl.customTooltip){
		chart.tooltip({
			triggerOn: 'mousemove', // tooltip 的触发方式，默认为 mousemove 
			showTitle: false, // 是否展示 title，默认为 true
			containerTpl: '<div class="g2-tooltip">' +
	  '<div class="g2-tooltip-title" style="margin:-10px 0 10px;width:140px"></div>' +
	  '<ul class="g2-tooltip-list"></ul></div>', // tooltip 容器模板
			itemTpl: '<li data-index={index}><span style="background-color:{color};width:8px;height:8px;border-radius:50%;display:inline-block;margin-right:8px;"></span>{name}: {value}</li>', // tooltip 每项记录的默认模板
		// inPlot: true, // 将 tooltip 展示在指定区域内
		// follow: true, // tooltip 是否跟随鼠标移动
		// shared: false, // 默认为 true, false 表示只展示单条 tooltip
		// position: 'bottom' // 固定位置展示 tooltip
		});
	} else {
		chart.tooltip({
			showCrosshairs: true,
			shared: true,
			enterable: data.entityl.isEnterable,
		});
	}
	
	
	chart.axis('temperature', {
		title: data.entityl.valueAlias ? {} : null,
		label: {
			formatter: (val) => data.entityl.unit ? `${parseFloat(Number(val).toFixed(5))}${data.entityl.unit}` : parseFloat(Number(val).toFixed(5)),
		},
	});
	if (data.entityl.axis){
		chart.axis(false);
	}
	if (data.entityl.earlyWarningLine) {
		newArr.value.forEach(item => {
			chart.annotation().line({
				start: ['min', Number(item.value)],
				end: ['max', Number(item.value)],
				style: {
					stroke: '#ff4d4f',
					lineWidth: 1,
					lineDash: [3, 3],
				},
				text: {
					position: 'start',
					style: {
						fill: '#8c8c8c',
						fontSize: 15,
						fontWeight: 'normal',
					},
					content: `告警线 ${newArr.value.length > 1 ? item.name : Number(item.value)}`,
					offsetY: -1,
				},
			});
		});
	}
	// 面积图
	if (data.entityl.area) {
		chart.area().position('month*temperature')
			.color('city')
			.style({
				lineWidth: 1
			})
			.shape('smooth');
	}

	chart
		.line()
		.position('month*temperature')
		.color('city')
		.style({
			lineWidth: 1
		})
		.shape('smooth');
	// 图例位置 "top" | "top-left" | "top-right" | "right" | "right-top" | "right-bottom" | "left" | "left-top" | "left-bottom" | "bottom" | "bottom-left" | "bottom-right"
	if (data.entityl.position) {
		const options = { position: data.entityl.position };
		if (data.entityl.disabledLegendList) {
			const tempObj = {};
			data.entityl.disabledLegendList.forEach((field) => tempObj[field] = false);
			options.selected = tempObj;
			chart.interaction('element-highlight');
			// 复写 图例筛选 交互。1、点击图例名称 进行 unchecked 状态的切换 2、点击图例 marker，进行 checked 状态的切换（进行聚焦）3、双击 重置状态
			chart.interaction('legend-filter', {
				start: [
					{ trigger: 'legend-item-name:click', action: ['list-checked:checked', 'data-filter:filter'] },
					{ trigger: 'legend-item-marker:click', action: ['list-checked:checked', 'data-filter:filter'] },
				],
				end: [
					// { trigger: 'legend-item-name:dblclick', action: ['list-checked:toggle', 'data-filter:filter'] },
					// { trigger: 'legend-item-marker:dblclick', action: ['list-checked:toggle', 'data-filter:filter'] },
				],
			});
		}
		chart.legend('city', options);
	} else {
		chart.legend('city', false); // 隐藏图例
	}
	if (data.entityl.theme) chart.theme('dark'); // 暗黑主题
	// chart.theme('dark'); // 暗黑主题
	// 自定义主题色
	// chart.theme({ 
	// 	styleSheet: {
	// 		backgroundColor: [
	// 			'#17263A'
	// 		],
	// 		fontFamily: [
	// 			'#fff'
	// 		]
	// 	},
		
	// });
	chart.render();
	chartCurrent.value = chart;
};
watch(
	() => data.entityl.data, 
	()=>{
		lineChart();
	},
	{ deep: true }
);
onMounted(() => { 
	lineChart();
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
  // line-height: 142px;
  color: #666;
}
:deep(.g2-tooltip-list) {
  max-height: 100px;
  overflow-y: auto;
}
</style>