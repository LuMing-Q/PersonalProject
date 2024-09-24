<template>
  <el-menu :default-active="activeIndex" active-text-color="#FFA028" mode="horizontal" collapse-transition router :unique-opened="true" class="sub_menu">
    <template v-for="menu in menus" :key="menu.id">
      <el-sub-menu v-if="menu.children && menu.children.length > 0" :key="`${menu.id}sub`" :index="menu.path || menu.id">
        <template #title>
          <div class="menuName">
            <bz-icon v-if="menu.icon" :name="menu.icon" color="#fff"  class="iconSize16 icon-box" />
            {{ menu.name }}
          </div>
        </template>
        <template v-for="child in menu.children" :key="child.id">
          <el-menu-item :index="child.path">{{ child.name }}</el-menu-item>
        </template>
      </el-sub-menu>
      <el-menu-item v-else :key="`${menu.id}menu`" :index="menu.path">
        <template #title>
          <div class="menuName">
            <bz-icon v-if="menu.icon" :name="menu.icon" color="#fff" class="iconSize16 icon-box" />
            {{ menu.name }}
          </div>
        </template>
      </el-menu-item>
    </template>
  </el-menu>
</template>

<script setup name="SubMenu">
import { onMounted, ref, watch } from 'vue';
import { session, buildTree } from '@/utils';
import { useRouter } from 'vue-router';

const router = useRouter();
const menus = ref([]);
const activeIndex = ref(window.location.pathname);

onMounted(() => {
	let userMenus = session.getStorage('userMenus');
	if (userMenus && userMenus.length > 0) {
		menus.value = buildTree(userMenus, 'id', 'parent_id');
		// menus.value.sort((a, b) => a.orderIndex - b.orderIndex);
	} 
});

watch(() => router.currentRoute.value.path, (val) => {
	if (val) {
		activeIndex.value = val;
	}
}, { deep: true, immediate: true });
</script>
<style lang="less" scoped>
.sub_menu {
  height: 100%;
  border: none;
  background: none;

  :deep(.is-opened .el-sub-menu__title) {
    font-size: 16px !important;
    color: #eee !important;
  }
  :deep(.el-menu-item),
  :deep(.el-sub-menu) {
    margin-right: 8px;
  }
  :deep(.el-sub-menu .el-menu-item) {
    font-size: 16px;
    color: #eee;
  }
  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    height: 60px !important;
    color: #eee !important;
    font-size: 16px;
    line-height: 22px;
  }
  img.icon {
    width: 16px;
    height: 16px;
    margin-right: 12px;
  }
}
</style>
<style lang="less">
.menuName {
  height: 16px;
  line-height: 16px;
  padding: 14px 15px;
  border-radius: 4px 4px 4px 4px;
  margin: 8px auto;
  text-align: center;

	.icon-box {
		margin-right: 2px;
	}
}

.menuBox > .el-menu--horizontal .el-menu-item:not(.is-disabled):hover,
.menuBox > .el-menu--horizontal > .el-sub-menu .el-sub-menu__title:hover {
  padding: 0;
  text-align: center;
  font-size: 16px;
  // width: 120px;
  border: none !important;
  background-color: transparent;

  .menuName {
    color: #fff;
    background-color: #3e7ee8;
  }
}

.el-sub-menu.is-active .el-sub-menu__title {
  padding: 0;
  text-align: center;
  // width: 120px;
  border: none !important;
  background-size: 100% 100%;
}

.menuBox > .el-menu--horizontal .el-menu-item:not(.is-disabled):focus {
  background-color: transparent;
}

.el-menu--horizontal .el-menu-item:not(.is-disabled),
.el-menu--horizontal .el-menu-item:not(.is-disabled),
.el-menu--horizontal > .el-sub-menu .el-sub-menu__title {
  padding: 0;
  text-align: center;
  // width: 125px;
  background-color: transparent;
  border: none;
}

.el-menu--horizontal > .el-menu-item.is-active,
.el-sub-menu.is-active .el-sub-menu__title {
  border: none;
  .menuName {
    color: #fff;
    background-color: #3e7ee8;
  }
}

.el-menu--popup {
  border: 1px solid #225d89;
  background: #092132;
  box-shadow: inset 0 0 6px 0 #1e98d1b3;
  // width: 125px !important;
  min-width: 0 !important;
}

.el-popper.is-light .el-menu--horizontal .el-menu-item:not(.is-disabled) {
  width: 115px !important;
  cursor: pointer;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

.el-popper.is-light {
  border: none;
  // width: 150px !important;

  .el-menu--horizontal .el-menu-item:not(.is-disabled) {
    display: block;
    width: 100px;
    padding: 0px 20px;
    text-align: center;
  }

  .el-menu--horizontal .el-menu-item:not(.is-disabled):hover {
    color: @brand-color-primary-hover;
    text-align: center;
  }

  .el-menu--horizontal .el-menu .el-menu-item.is-active {
    color: @brand-color-primary-active;
  }
}

.el-sub-menu .el-sub-menu__icon-arrow {
  display: none;
}

.el-menu:not(.el-menu--collapse) .el-sub-menu__title {
  padding: 0 !important;
}

.el-menu--horizontal.el-menu,
.el-menu--horizontal > .el-sub-menu .el-sub-menu__title,
.el-menu--horizontal > .el-menu-item.is-active {
  border: none !important;
}
</style>
