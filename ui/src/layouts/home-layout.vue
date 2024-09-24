<template>
    <div :class="!padding ? 'homeLayout' : 'homeLayout1'">
        <el-container>
            <!-- 头部导航栏 -->
            <el-header class="adminHeader">
                <Header />
            </el-header>
            <!-- 内容区域 -->
            <el-main>
                <router-view v-slot="{ Component, route }">
                    <component :is="Component" :key="route.fullPath" />
                </router-view>
            </el-main>
        </el-container>
    </div>
</template>

<script setup name="Home-Layout">
import Header from './components/Header.vue';
import { watch, ref } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const padding = ref(false);

watch(
	()=> router.currentRoute.value.path,
	(value) => {
		let urlPath = router.currentRoute.value.path;
		if (urlPath === '/home') {
			padding.value = true;
		} else {
			padding.value = false;
		}
	},
	{ deep: true, immediate: true }
);
</script>

<style lang="less" scope>
.homeLayout{
	.el-main {
		padding: 20px;
		background: #EEEFEF;
		height: calc(100vh - @headerHeight);

	}

	.adminHeader {
		height: @headerHeight;
		padding: 0;
	}
}
.homeLayout1{
	.el-main {
		padding: 0px;
		background: #EEEFEF;
		height: calc(100vh - @headerHeight);

	}

	.adminHeader {
		height: @headerHeight;
		padding: 0;
	}
}
</style>