<template>
	<div class="listWrap">
		<div class="select-box">
			检测时间：
			<el-select v-model="time" placeholder="请选择" @change="getCpu(1)">
				<el-option label="10秒" value="10"/>
				<el-option label="30秒" value="30"/>
				<el-option label="1分钟" value="60"/>
			</el-select>

		</div>
		<div style="width: 100%;display: flex;flex-wrap: wrap;">
			<div v-for="(item, index) in cpuNum" :key="index" class="content-box" 
				:style="index < 6 ? 'margin-top: 0' : ''">
				<div class="title-box">CPU {{ index + 1 }}</div>
				<div :id="`cpu-${index}`" class="chart-box"></div>
			</div>
		</div>
	</div>
</template>
<script setup>
import { onMounted, nextTick, ref, onBeforeUnmount } from 'vue';
import { getCpuLoad } from '@/api/monitor/monitor';
import * as echarts from 'echarts';
// 是否一直获取数据
const isContinue = ref(true);
const time = ref(30);
// doms数组
const chartDoms = ref([]);
// 获取cpu核数
const cpuNum = ref([]);
const initChartFlag = ref(false);
// 初始化多有图标
const initChart = (total) => {
	for (let i = 0; i < total; i++) {
		let chartDom = document.getElementById('cpu-' + i);	
		let myChart = echarts.init(chartDom);
		chartDoms.value.push(myChart);
	}
};
const drawChart = (index, cpuData) => {
	let myChart = chartDoms.value[index];
	let option;
	option = {
		grid: {
			left: 0,
			right: 0,
			top: 0,
			bottom: 0
		},
		xAxis: {
			show: false,
			type: 'category'
		},
		yAxis: {
			show: false,
			type: 'value',
			min: 0,
			max: 1
		},
		series: [
			{
				data: cpuData,
				type: 'line',
				symbol: 'none',
				areaStyle: {
					origin: 'start',
					color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
						{
							offset: 0,
							color: '#90caaf'
						},
						{
							offset: 1,
							color: '#90caaf00'
						}
					])
				},
				lineStyle: {
					color: '#90caaf'
				},
				smooth: true
			}
		]
	};

	option && myChart.setOption(option);
};
// 获取所有cpu数据,每一项数据又对应每一核cpu的负载数组
const cpuAllData = ref([]);
const getCpu = async (param = -1) => {
	if (!isContinue.value) return;
	let res = await getCpuLoad();
	if (!initChartFlag.value) {
		initChartFlag.value = true;
		cpuNum.value = res.length;
		await nextTick();
		initChart(res.length);
	}
	if (param != -1) {
		cpuAllData.value = [];
	}
	for (let i = 0; i < res.length; i++) {
		if (!cpuAllData.value[i]) {
			cpuAllData.value[i] = [];
		}
		if (cpuAllData.value[i].length > time.value) {
			cpuAllData.value[i].shift();
		}
		cpuAllData.value[i].push(res[i]);
		drawChart(i, cpuAllData.value[i]);
	}
	await getCpu();
};
onMounted(async () => {
	getCpu();
});
onBeforeUnmount(() => (isContinue.value = false));
</script>
<style lang="less" scoped>
@import '@/assets/styles/list.less';
.listWrap {
	height: calc(100% - 40px);
	overflow-y: auto;

	.select-box {
		display: flex;
		align-items: center;
		margin-bottom: 20px;
		font-size: 14px;

		.el-select {
			width: 200px;
		}
	}
	.content-box {
		width: calc((100% - 100px - 12px)/6 - 10px);
		height: 200px;
		border: 1px solid #88d5b9;
		margin-right: 20px;
		margin-top: 20px;
		padding: 5px;

		.title-box {
			height: 14px;
			line-height: 14px;
			font-size: 14px;
			font-weight: bold;
		}

		.chart-box {
			height: calc(100% - 14px);
			width: 100%;
			display: flex;
			justify-content: center;
			align-items: center;
		}
	}

	.content-box:nth-child(6n) {
		margin-right: 0;
	}
}
</style>