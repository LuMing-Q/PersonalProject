<template>
	<div class="adminLayout">
		<el-container>
			<!-- 头部导航栏 -->
			<el-header class="adminHeader">
				<Header />
			</el-header>
			<el-container>
				<!-- 侧边栏 -->
				<el-aside class="adminAside">
					<SubMenu />
				</el-aside>
				<el-main>
					<!-- 面包屑 -->
					<Bread />
					<!-- 内容区域 -->
					<div :class="isDetailPath ? 'contentWrap detailWrap' : 'contentWrap'">
						<router-view v-slot="{ Component, route }">
							<component :is="Component" :key="route.fullPath" />
						</router-view>
					</div>
				</el-main>
			</el-container>
		</el-container>
	</div>
</template>

<script setup name="AdminLayout">
import { watch, ref } from 'vue';
import { useRoute } from 'vue-router';

import Header from './components/Header.vue';
import SubMenu from './components/SubMenu.vue';
import Bread from './components/Bread.vue';

const route = useRoute();
const isDetailPath = ref(false);

watch(() => route.path, (val) => {
	isDetailPath.value = val.indexOf('detail') !== -1;
}, { deep: true });
</script>

<style lang="less" scope>
.adminLayout {
	.el-main {
		padding: 0;
		background: #EEEFEF;

		.contentWrap{
			padding: 18px;
			height: calc(100vh - @headerHeight - @breadHeight - 36px);
			overflow: hidden;
		}
		.contentWrap.detailWrap {
			padding: 0 0 18px;
			height: calc(100vh - @headerHeight - @breadHeight - 18px);
		}
	}

	.adminHeader {
		height: @headerHeight;
		padding: 0;
	}

	.adminAside {
		width: @SubMenuWidth;
		height: calc(100vh - @headerHeight);
		overflow: hidden;
	}

}
</style>
