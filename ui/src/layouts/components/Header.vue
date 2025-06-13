<template>
    <div class="header">
			<div class="menuBox">
				<sub-menu />
			</div>
      <!-- 顶部导航栏右侧部分 -->
      <div class="right">
				<el-dropdown style="outline: none;" @command="(cmd) => handleUser(cmd)">
					<span class="el-dropdown-link">
						{{ userInfo?.realName || 'admin' }}
						<Icon :name="'icon-tuichu'" class="iconSize14 right-box" :color="'#000'" />
					</span>
					<template #dropdown>
						<el-dropdown-menu class="custom-dropdown-item">
							<el-dropdown-item command="logout">退出平台</el-dropdown-item>
						</el-dropdown-menu>
					</template>
				</el-dropdown>
      </div>																																																											
    </div>
  </template>
  
<script setup name="Header">
import { getCurrentInstance, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { session } from '@/utils';
import SubMenu from './SubMenu.vue';

const router = useRouter();

const handleUser = async (cmd) => {
	if (cmd === 'logout') {
		// 退出 ===== 逻辑待优化
		session.clearStorageAll();
		router.push('/login');
	}
};

const userInfo = session.getStorage('userInfo');

const mainName = ref('');
const { proxy } = getCurrentInstance();
mainName.value = proxy.global.mainName;


onMounted(() => {});
</script>
  
<style lang="less" scoped>
.header {
	padding: 0 15px;
	height: @headerHeight;
	background: #FFF;
	box-shadow: @headerShadow;
	color: @subMenuTextHover;
	display: flex;

	.menuBox {
		height: @headerHeight;
	  flex: 1;
	}

	.right{
		width: 90px;
		display: flex;
		justify-content: center;
		align-items: center;

		.el-dropdown-link {
			outline: none;
			display: flex;
			align-items: center;

			.right-box {
				margin-left: 8px;
			}
		}
		
		:deep(.el-dropdown) {
			height: @headerHeight;
			line-height: @headerHeight;
			color: #000;
		}

		.custom-dropdown-item {
				margin-top: 10px;
			}

		.head-icon-box {
			width: 18px;
			height: 18px;

			svg {
				width: 18px;
				height: 18px;
			}
		}

		& > div{
			height: @headerHeight;
			line-height: @headerHeight;

			& + div{
				border-left: 1px solid rgba(249, 250, 252, 0.2);
				// border-left: 1px solid #000000;
			}

			.isDot {
				height: @headerHeight;
				line-height: @headerHeight;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
			}
		}
	}
}
</style>
