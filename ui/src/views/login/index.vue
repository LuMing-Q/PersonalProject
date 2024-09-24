<template>
  <div class=wrap>
		<h3> {{ siteName }} </h3>
    <div class="form">
			<el-form
					ref="formRef"
					:model="form"
					:hide-required-asterisk="true"
					:rules="rules"
					status-icon>
				<h4>登录</h4>
				<el-form-item label="用户名" prop="username">
					<el-input v-model="form.username" placeholder="请输用户名"/>
				</el-form-item>
				<el-form-item label="密码" prop="password">
					<el-input v-model="form.password" type="password" autocomplete="off" placeholder="请输入密码" />
				</el-form-item>
				<el-form-item>
					<el-button type="primary" :disabled="isDisabledSubmit" @click="submit(formRef)">登录</el-button>
				</el-form-item>
			</el-form>
		</div>
		<div class="siteInformation">
			<p>{{ siteInformation }}</p>
		</div>
  </div>
</template>

<script setup name="InteriorLogin">
import { reactive, ref, getCurrentInstance, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { userLogin, getUserMenuList } from '@/api/platform/userInfo';
import { session, buildTree } from '@/utils';

const isDisabledSubmit = ref(false); // 登录按钮是否可用
const formRef = ref();

const form = ref({
	username: 'admin',
	password: 'Admin@123',
});

const router = useRouter();

const rules = reactive({
	username: [{ required: true, trigger: 'change', message: '请输入用户名' }],
	password: [{ required: true, trigger: 'change', message: '请输入密码' }],
});

// 提交
const submit = (formEle) => {
	if (!formEle) return;
	formEle.validate(async (valid) => {
		if (valid) {
			let res = await userLogin(form.value);
			if (res.code && res.code === 200) {
				session.setStorage('token', res.data.token);
				session.setStorage('userInfo', res.data.user);
				router.push('/');
				let userMenus = await getUserMenuList(res.data.user.roleId);
				if (userMenus && userMenus.length > 0) {
					session.setStorage('userMenus', userMenus);
					router.push('/');
				}
			}
		}
	});
};

const keydown = (e) => {
	if (e.keyCode === 13) {
		submit(formRef.value);
	}
};

const siteName = ref('');
const siteInformation = ref('');

onMounted(() => {
	const { proxy } = getCurrentInstance();
	siteName.value = proxy.global.siteName;
	siteInformation.value = proxy.global.siteInformation;
	window.addEventListener('keydown', keydown);
});


onUnmounted(()=> {
	window.removeEventListener('keydown', keydown, false);
});
</script>

<style lang="less" scoped>
.wrap{
	height: 100vh;
	min-height: 520px;
	// background-image: url(../../assets/AdminIcon/loginBack1.png);
	background-repeat: no-repeat;
	// background-position: center;
	background-size: auto 100%;
	background-color: #000;
	overflow: hidden;
	h3 {
			height: 32px;
			margin-top: 46px;
			margin-left: 46px;
			font-weight: Bold;
			font-size: 25px;
			color: #fff;
			img {
				vertical-align: middle;
				margin-right: 30px;
			}
		}
	.form {
		background-color: #fff;
		padding: 50px;
		:deep(.el-input) {
			width: 285px;
		}
		:deep(.el-form-item) {
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			align-items:start;
			height: 60px;

			.el-form-item__label, .el-form-item__content {
				height: 30px;
			}
		}
		position: absolute;
		right: 140px;
		top: 50%;
		margin-top: -264px;

		h4 {
			font-weight: 600;
			font-size: 20px;
			color: #3E3E3E;
			margin-bottom: 20px;
		}
		:deep(.el-button) {
			width: 285px;
			height: 44px;
			border-radius: 4px;
			font-weight: 400;
			font-size: 14px;
			color: #FFFFFF;
			border: none;
			margin-top: 30px;
		}
	}
}
.siteInformation {
	position: absolute;
	left: 57px;
	bottom: 39px;
	font-weight: 400;
	font-size: 14px;
	color: #FFFFFF;
}
</style>
