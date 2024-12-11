<template>
  <div class=wrap>
    <div class="container">
			<div class="drop">
				<div class="content">
					<h2>登 录</h2>
					<el-form
						ref="formRef"
						:model="form"
						:hide-required-asterisk="true"
						:label-width="autoSize(55)"
						:rules="rules"
						status-icon
						class="form-style">
						<el-form-item label="用户名" prop="username" class="inputBox">
							<el-input v-model="form.username" placeholder="请输用户名"/>
						</el-form-item>
						<el-form-item label="密码" prop="password" class="inputBox">
							<el-input v-model="form.password" type="password" show-password autocomplete="off" placeholder="请输入密码" />
						</el-form-item>
						<div class="btn-box">
							<el-button type="primary" :disabled="isDisabledSubmit" class="btn" @click="submit(formRef)">
								登录
							</el-button>
						</div>
					</el-form>
				</div>
			</div>
			<!-- <a href="#" class="btns">忘记密码</a>
			<a href="#" class="btns signup">注册</a> -->
		</div>
		<!-- <div style="width: 50%;">
			<CircularProgressBar />
		</div> -->
  </div>
</template>

<script setup name="InteriorLogin">
import { onMounted, onUnmounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { getUserMenuList, userLogin } from '@/api/platform/userInfo';
import { autoSize, session } from '@/utils';

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
			isDisabledSubmit.value = true;
			let res = await userLogin(form.value);
			if (res.code && res.code === 200) {
				isDisabledSubmit.value = false;
				session.setStorage('token', res.data.token);
				session.setStorage('userInfo', res.data.user);
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

onMounted(() => {
	window.addEventListener('keydown', keydown);
});


onUnmounted(()=> {
	window.removeEventListener('keydown', keydown, false);
});
</script>

<style lang="less" scoped>
@import '@/assets/styles/login.less';
.wrap{
	height: 100vh;
	min-height: 520px;
  background-image: url(@/assets/images/bg.jpg);
	background-repeat: no-repeat;
  background-position: center;
	background-size: 100% 100%;
	// background-color: #eff0f4;
	overflow: hidden;
	display: flex;
	justify-content: center;
	align-items: center;
	:deep(.el-input) {
		.el-input__wrapper {
			padding: 0;
			background-color: transparent !important;
			box-shadow: none;
		}
	}
}
</style>
