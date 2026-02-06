<template>
  <div class="line_box">
    <div v-for="(item, index) in list" :key="index" class="box">
      <div class="box_title">{{ item.name }}</div>
      <div class="box_content">
        <div ref="boxWidth" class="container-box">
          <svg :viewBox="`0 0 ${radius * 2 + ringWidth} ${radius * 2 + ringWidth}`">
            <defs>
              <linearGradient id="gradient" cx="50%" cy="50%" r="60%" fx="50%" fy="50%">
                <stop offset="0%" stop-color="#9ee178" />
                <stop offset="100%" stop-color="#67c23a" />
              </linearGradient>
              <linearGradient id="gradient1" cx="50%" cy="50%" r="60%" fx="50%" fy="50%">
                <stop offset="0%" stop-color="#f77b77" />
                <stop offset="100%" stop-color="#ed4848" />
              </linearGradient>
              <linearGradient id="gradient2" cx="50%" cy="50%" r="60%" fx="50%" fy="50%">
                <stop offset="0%" stop-color="#ffba42" />
                <stop offset="100%" stop-color="#f19a17" />
              </linearGradient>
              <filter id="innerShadow" x="-10%" y="-10%" width="200%" height="200%">
                <feComponentTransfer in="SourceAlpha">
                  <feFuncA type="table" tableValues="1 0" />
                </feComponentTransfer>
                <feGaussianBlur stdDeviation="5" result="blur" />
								<feFlood flood-color="#6FC743" result="color" /> 
                <feOffset dx="0" dy="0" result="offsetBlur" />
                <feComposite in2="offsetBlur" operator="in" />
                <feComposite in2="blur" operator="in" />
                <feComposite in2="SourceAlpha" operator="in" />
              </filter>
              <filter id="innerShadow1" x="-10%" y="-10%" width="200%" height="200%">
                <feComponentTransfer in="SourceAlpha">
                  <feFuncA type="table" tableValues="1 0" />
                </feComponentTransfer>
                <feGaussianBlur stdDeviation="5" result="blur" />
								<feFlood flood-color="#F39E1D" result="color" /> 
                <feOffset dx="0" dy="0" result="offsetBlur" />
                <feComposite in2="offsetBlur" operator="in" />
                <feComposite in2="blur" operator="in" />
                <feComposite in2="SourceAlpha" operator="in" />
              </filter>
              <filter id="innerShadow2" x="-10%" y="-10%" width="200%" height="200%">
                <feComponentTransfer in="SourceAlpha">
                  <feFuncA type="table" tableValues="1 0" />
                </feComponentTransfer>
                <feGaussianBlur stdDeviation="5" result="blur" />
								<feFlood flood-color="#EE4F4E" result="color" /> 
                <feOffset dx="0" dy="0" result="offsetBlur" />
                <feComposite in2="offsetBlur" operator="in" />
                <feComposite in2="blur" operator="in" />
                <feComposite in2="SourceAlpha" operator="in" />
              </filter>
            </defs>
            <!-- 背景圈 -->
            <circle
              :cx="radius + ringWidth / 2"
              :cy="radius + ringWidth / 2"
              :r="radius"
              fill="none"
              :stroke="getStrokeColor(item.used / item.total)"
              :stroke-width="ringWidth"
              :filter="getShadowColor(item.used / item.total)"
            />
            <!-- 动态进度圈 -->
            <circle
              :cx="radius + ringWidth / 2"
              :cy="radius + ringWidth / 2"
              :r="radius"
              :stroke="getStrokeColor(item.used / item.total)"
              :stroke-width="ringWidth"
              stroke-linecap="round"
              fill="none"
              :style="getCircleStyle(item.used, item.total)"
            />
          </svg>
          <!-- 中间显示百分比 -->
          <div class="progress-text">
            {{ getPercentage(item.used, item.total) }}
          </div>
        </div>
        <div class="bottom">
          {{ item.used.toFixed(0) }}/{{ item.total.toFixed(0) }}{{ item.unit }} (已用/总量)
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watchEffect } from 'vue';

// Props
const props = defineProps({
	ringWidth: {
		type: Number,
		default: 50
	}, // 环宽
	list: {
		type: Array,
		default: () => [
			{ name: 'CPU', used: 16, total: 64, unit: '核' },
			{ name: '内存', used: 48, total: 64, unit: 'GB' },
			{ name: '存储', used: 950, total: 1024, unit: 'GB' },
			{ name: 'GPU', used: 0, total: 6, unit: '个' }
		],
	},
});
// 内圆半径
const radius = ref(35);

// 获取百分比
const getPercentage = (used, total) => {
	if (!total) return '0%';
	return ((used / total) * 100).toFixed(0) + '%';
};

// 获取进度圈样式
const getCircleStyle = (used, total) => {
	const perimeter = 2 * Math.PI * radius.value; // 圆周长
	if (total === 0) return {};
	const offset = perimeter - (perimeter * used) / total;
	return {
		strokeDasharray: `${perimeter}px`,
		strokeDashoffset: `${offset}px`,
		transition: 'stroke-dashoffset 0.3s',
	};
};

// 获取颜色
const getStrokeColor = (percentage) => {
	if (percentage < 0.7) return 'url(#gradient)';
	if (percentage < 0.9) return 'url(#gradient2)';
	return 'url(#gradient1)';
};

// 获取内阴影颜色
const getShadowColor = (percentage) => {
	if (percentage < 0.7) return 'url(#innerShadow)';
	if (percentage < 0.9) return 'url(#innerShadow1)';
	return 'url(#innerShadow2)';
};


const boxWidth = ref(null);
watchEffect(() => {
	if (boxWidth.value) {
		console.log(boxWidth.value[0].offsetHeight);
		radius.value = boxWidth.value[0].offsetHeight - props.ringWidth;
	}
});
</script>

<style scoped>
/* 父容器 */
.line_box {
  display: flex;
  justify-content: space-between;
	width: 100%;
}

/* 每个盒子 */
.box {
  width: calc((100% - 80px) / 4);
  padding: 16px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
}
.box + .box {
	margin-left: 20px;
}

/* 标题 */
.box_title {
  font-size: 14px;
  font-weight: bold;
	text-align: left;
  margin-bottom: 16px;
}

/* 进度条容器 */
.container-box {
  position: relative;
  width: 100%;
  padding-bottom: 100%; /* 保持正方形 */
  margin: 0 auto;
}

svg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transform: rotate(-90deg); /* 从顶部开始 */
}

/* 中间文字 */
.progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 20px;
  font-weight: bold;
}

/* 底部文字 */
.bottom {
  margin-top: 12px;
  font-size: 12px;
  color: #606266;
}
</style>
