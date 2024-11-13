<template>
    <div class="header">
			<div class="menuBox">
				<sub-menu />
			</div>
      <!-- 顶部导航栏右侧部分 -->
      <div class="right">
      <!-- dropdown下拉 -->
        <!-- <div>
					<el-badge :is-dot="isDot" class="item isDot">
						<el-icon class="head-icon-box" @click="$router.push('/operationsCenter/alert')"><Bell  /></el-icon>
					</el-badge>
        </div> -->
				<div>
          <el-dropdown @command="(cmd) => handleUser(cmd)">
						<span class="el-dropdown-link">
							{{ userInfo?.name || 'admin' }}
							<el-icon class="el-icon--right">
								<arrow-down />
							</el-icon>
						</span>
						<template #dropdown>
							<el-dropdown-menu>
								<el-dropdown-item command="logout">退出平台</el-dropdown-item>
							</el-dropdown-menu>
						</template>
					</el-dropdown>
        </div>
      </div>
    </div>
  </template>
  
<script setup name="Header">
import { ref, getCurrentInstance, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { session } from '@/utils';
import SubMenu from './SubMenu.vue';

const router = useRouter();

const handleUser = async (cmd) => {
	if (cmd === 'logout') {
		// 退出 ===== 逻辑待优化
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
	background: #42a5f5;
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
		:deep(.el-dropdown) {
			height: @headerHeight;
			line-height: @headerHeight;
			color: #FFF;
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
