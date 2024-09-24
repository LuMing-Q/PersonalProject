<!-- 面包屑导肮 -->
<template>
	<div class="bread">
		<el-breadcrumb separator="/">
			<el-breadcrumb-item :to="'/home'">首页</el-breadcrumb-item>
			<el-breadcrumb-item
				v-for="bread in breadList"
				:key="bread.id"
				:to="{ path: bread.path }"
				>{{ bread.name }}</el-breadcrumb-item
			>
		</el-breadcrumb>
	</div>
</template>

<script setup name="Bread">
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { session } from '@/utils';

const router = useRouter();
const breadList = ref(['/']);

watch(
	() => router.currentRoute.value.path,
	(value) => {
		const query = router.currentRoute.value.query;
		const editId = query.id || query.edit || '';
		const titleName = query.name;
		const tempArr = [];
		let tempPath = value.split('/').filter((r) => r);
		const menus = session.getStorage('menus');
		if (menus) {
			tempPath = tempPath.map((_, index) => {
				_.value;
				let tempStr = '';
				for (let i = 0; i <= index; i += 1) {
					tempStr += `/${tempPath[i]}`;
				}
				return tempStr;
			});
			tempPath.forEach((path) => {
				const result = menus.find((r) => r.path === path);
				if (result && result.path.indexOf('form') === -1) tempArr.push(result);
				if (path.indexOf('form') !== -1)
					tempArr.push({ path, name: `${editId ? '编辑' : '创建'}` });
			});
			if (titleName) tempArr.push({ name: titleName });
			breadList.value = tempArr;
		}
	},
	{ deep: true, immediate: true }
);
</script>

<style lang="less" scoped>
.bread{
	background: #fff;
	.el-breadcrumb{
		margin-left: 20px;
		height: @breadHeight;
		line-height: @breadHeight;
	}
}
</style>
