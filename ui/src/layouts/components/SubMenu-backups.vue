<!-- 侧边导航栏 -->
<template>
  <el-menu
    :default-active="activeIndex"
    active-text-color="#FFA028"
    background-color="#17263A"
    text-color="#fff"
    router
    unique-opened
    class="sub_menu"
  >
    <template v-for="menu in subMenu" :key="menu.path">
      <el-sub-menu v-if="menu.children && menu.children.length" :index="menu.path">
        <template #title>{{ menu.name }}</template>
				<template v-for="menus in menu.children" :key="menus.path">

					<el-sub-menu v-if="menus.children && menus.children.length" :index="menus.path">
						<template #title>{{ menus.name }}</template>
						<template v-for="item in menus.children" :key="item.path">
							<el-menu-item :index="item.path">{{ item.name }}</el-menu-item>
						</template>
					</el-sub-menu>
					<el-menu-item v-else :index="menus.path">{{ menus.name }}</el-menu-item>

				</template>
      </el-sub-menu>
			<el-menu-item v-else :index="menu.path">{{ menu.name }}</el-menu-item>
    </template>
  </el-menu>
</template>

<script setup name='SubMenu'>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useStore } from 'vuex';

import { session, buildTree } from '@/utils';

const { commit, state } = useStore();
const route = useRoute();
const router = useRouter();

const activeIndex = ref(window.location.pathname);
const subMenu = ref([]);
const userMenuList = ref([]);

// 页面切换时菜单的选择状态跟随变化
router.beforeEach((to) => {
	activeIndex.value = to.path;
});

const openPath = (path) => {
	const data = session.getStorage('userMenuList').find(r => r.path === path) || {};
	session.setStorage('permissionList', state.global.permissionObj[data.id] || []);
};

watch(() => route.path, (val) => {
	if (val && val !== '/') {
		openPath(val);
	}
}, { immediate: true, deep: true });

onMounted(() => {
	userMenuList.value = session.getStorage('userMenuList');
	if (userMenuList.value && userMenuList.value.length) {
		const userMenu = []; // 用户菜单
		const permissionObj = {}; // 用户操作权限数据
		userMenuList.value.forEach(el => {
			if (el.type) {
				userMenu.push(el);
			} else {
				if (permissionObj[el.parentId]) {
					permissionObj[el.parentId].push({ command: el.opDirective, ...el });
				} else {
					permissionObj[el.parentId] = [{ command: el.opDirective, ...el }];
				}
			}
		});
		subMenu.value = buildTree(userMenu, 'id', 'parentId');
		commit('global/setPermissionObj', { data: permissionObj });
	}
});
</script>

<style lang="less" scoped>
.sub_menu {
  height: 100%;
	overflow-y: auto;
  box-shadow: 5px 0 10px 0 rgba(0,0,0,0.05);

  :deep(.is-opened > .el-sub-menu__title) {
    font-weight: Bold !important;
    font-size: 14px !important;
    color: @menuBgColor !important;
  }
	// :deep(.el-sub-menu .el-menu-item)
  :deep(.el-menu-item) {
    font-weight: 400;
    font-size: 14px;
    color: #FFFFFF;
    min-width: auto;
  }
  // :deep(.el-sub-menu .el-menu-item.is-active) {
  :deep(.el-menu-item.is-active) {
    background: @menuBgColor;
    position: relative;
    &::after {
      content: '';
      width: 4px;
      height: 50px;
      transform: scaleY(-1);
      background: #FFFFFF;
      border-radius: 0 2px 2px 0;
      position: absolute;
      left: 0;
    }
  }
}
</style>
