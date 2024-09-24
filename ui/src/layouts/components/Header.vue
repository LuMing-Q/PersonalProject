<template>
    <div class="header">
      <!-- 顶部导航栏左侧部分 -->
      <div class="left">
        <!-- logo -->
		    <div class="logo">
					<img src="@/assets/images/logo.png">
				</div>
        <!-- 标题 -->
				<div class="nameBox">
					<span class="mainTitle">{{ mainName }}</span>
					<span class="subTitle">{{ subName }}</span>
				</div>
        <span class="projectTitle">{{ projectName }}</span>
      </div>
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
const subName = ref('');
const projectName = ref('');
const { proxy } = getCurrentInstance();
mainName.value = proxy.global.mainName;
subName.value = proxy.global.subName;
projectName.value = proxy.global.projectName;


onMounted(() => {});
</script>
  
<style lang="less" scoped>
.header {
	padding: 0 27px;
	height: @headerHeight;
	background: @headerBgColor;
	box-shadow: @headerShadow;
	color: @subMenuTextHover;
	display: flex;
	// justify-content: space-between;
	.left{
		height: @headerHeight;
		display: flex;
		align-items: center;
		.logo {
			height: 36px;
			width: 36px;
			display: flex;
			flex-direction: column;
			justify-content: center;
			margin-right: 12px;
			
			img {
				width: 100%;
				height: 100%;
			}
		}

		.siteTitle {
			height: @headerHeight;
			line-height: @headerHeight;
			font-size: 24px;
			color: @subMenuTextHover;
		}
	}

	.nameBox {
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.mainTitle {
		font-size: 16px;
		font-weight: 600;
		letter-spacing: 0.02em;
	}
	.subTitle {
		font-size: 12px;
		font-weight: normal;
		letter-spacing: 0.265em;
		opacity: 0.8;
    text-align-last: justify;
	}
	.projectTitle {
		margin-left: 12px;
		padding-left: 12px;
		border-left: 1px solid rgba(249, 250, 252, 0.2);
		font-size: 14px;
	}

	.menuBox {
		margin-left: 40px;
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
