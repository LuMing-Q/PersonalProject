<template>
	<div v-if="!isForm" class="menu">
		<el-menu
				:default-active="activeMenu"
				mode="horizontal"
				@select="onSelect"
		>
			<el-menu-item v-for="({ name, index }) in props.data" :key="name" :index=index>{{ name }}</el-menu-item>
		</el-menu>
	</div>
	<router-view />
</template>

<script setup name="BzContentMenu">
import { ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const props = defineProps({
	// 内容区域的菜单导航, 必须先在router/static下添加静态路由方可使用
	parentPath: { type: String, default: '' }, // 父路径
	data: { type: Array, default: () => [], required: true }, // 菜单 [ index: '路径', name: '名称 ]
});

const route = useRoute();
const router = useRouter();

const activeMenu = ref('');
const isForm = ref(false);

const { parentPath } = props;

const onSelect = (index) => router.push({ path: index, query: route.query });

watch(
	() => [router.currentRoute.value.path, props.data],
	(value) => {
		isForm.value = value.indexOf('Create') !== -1 || value.indexOf('Edit') !== -1 || value.indexOf('Info') !== -1;
		if (props.data.length > 0) {
			activeMenu.value = value[0] === parentPath ? props.data[0].index : value[0];
			if (value[0] === parentPath) router.push({ path: props.data[0].index, query: route.query });
		} else {
			activeMenu.value = value[0];
		}
	}, { immediate: true, deep: true }
);
</script>
<style lang="less" scoped>
	.menu {
		margin: -24px -18px 24px -18px;
	}
	:deep(.el-menu) {
		padding-left: 18px;
	}
	:deep(.el-menu-item) {
		line-height: 45px;
		padding-left: 10px;
		padding-right: 10px;
	}
	:deep(.el-menu--horizontal){
		border: none;
	}
</style>
