<template>
  <div id="appRoot">
		<el-config-provider :locale="zhCn">
			<component :is="layout">
				<router-view />
			</component>
		</el-config-provider>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { ElConfigProvider } from 'element-plus';

import zhCn from 'element-plus/dist/locale/zh-cn.mjs';

let route = useRoute();
const layout = computed(() => route?.meta?.layout || 'admin-layout');
</script>

<style lang="less">

	body{
    font-weight: 400;
    font-size: 14px;
		background: @bg-color-page-container;
	}

// 滚动条重置
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
	-webkit-appearance: none;
}
input[type="number"]{
	-moz-appearance: textfield;
}
	// 自定义滚动条
::-webkit-scrollbar {
	width: 4px;
}

::-webkit-scrollbar-track {
	background: #D7D7D7;
	box-shadow: inset 0 0 14px 0 #176c9d4d;
}

::-webkit-scrollbar-thumb {
	background: @mainColor;
	border-radius: 2px;
	height: 20px;
}

::-webkit-scrollbar-thumb:hover {
	background: @mainColor;
	box-shadow: inset 0 0 14px 0 #176c9d4d;
}

::-webkit-scrollbar-thumb:active {
	background: @mainColor;
	box-shadow: inset 0 0 14px 0 #176c9d4d;
}
</style>
