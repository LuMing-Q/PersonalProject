<!-- 元数据处理 -->

<template>
	<div class="load">
		<el-row v-for="(item,index) in metadataList" :key="index" class="inputBox">
			<el-col :span="8">
				<div>
					<el-input v-model="metadataList[index].key" :class="changeVal(metadataList[index].key) > 1 ? 'isRepeatKey' : ''" :placeholder="props.isDisabled ? '' : '请输入键名'" :disabled='props.isDisabled' :maxlength="63" @blur="repeatMetadataData" @input="metadataList[index].key = metadataList[index].key.replace(/[\u4E00-\u9FA5]/g,'');">
					</el-input>
				</div>
				<span />
				<div>
					<el-input v-model="metadataList[index].value" :placeholder="props.isDisabled ? '' : '请输入键值'" :maxlength="63" :disabled='props.isDisabled'>
				</el-input>
				</div>
			</el-col>
			
			<el-col :span="14">
				<el-button v-if="!props.isDisabled" type="primary" link @click="deleteMetadataData(index, metadataList[index].key)">
					<bz-icon name="icon-shanjian" :size="16" class="iconSize16" />
				</el-button>

				<p v-show="props.inlayFields.indexOf(metadataList[index].key) !== -1" class="info">
					{{ metadataList[index].key }}为限制字段，禁止使用
				</p>
				<p v-if="changeVal(metadataList[index].key) > 1" class="info">键名重复，请重新输入</p>
			</el-col>
		</el-row>
		<div v-if="!props.isDisabled" style="margin-top: 24px;">
			<el-button type="primary" :disabled="isRepeatKey || props.inlayFields.indexOf(metadataList[index]?.key) !== -1" @click="addMetadataData">
				<bz-icon name="icon-tianjia" :size="16" color="#fff" class="iconSize16 icon-box" />
				{{ props.butName }}
			</el-button>
		</div>
	</div>
</template>

<script setup>
import { ref, defineExpose, defineProps, watch, defineEmits } from 'vue';
import { isEmpty } from 'lodash';

const props = defineProps({
	metadataData: { type: Object, default: () => {} }, // 键值数据
	butName: { type: String, default: '添加元数据' }, // 添加按钮展示字
	isDisabled: { type: Boolean, default: false }, // 是否为禁用
	inlayFields: { type: Array, default: () => (['admin_pass', 'system_type']) }, // 限制字段
});

const emits = defineEmits(['handleData', 'handleRemove']);

const metadataList = ref([]);
const metadataObj = ref({});
const isRepeatKey = ref(false);

const changeVal = (e) => {
	if (props.isDisabled || !e) return 0;
	return metadataList.value.filter(r => r.key === e).length;
};

const repeatMetadataData = () => {
	if (props.isDisabled) return;
	isRepeatKey.value = !!document.querySelectorAll('.isRepeatKey').length;
};

// 添加
const addMetadataData = () => {
	metadataList.value.push({
		key: '', value: ''
	});
};

const clearMetadataData = () => { 
	emits('handleRemove', metadataList.value);
	metadataList.value = [{ key: '', value: '' }];
};

// 删除
const deleteMetadataData = async (value, key) => {
	if (metadataList.value.length >= 2){
		metadataList.value = metadataList.value.filter((r, index) => index !== value);
		await changeVal(key);
		repeatMetadataData();
	} else {
		clearMetadataData();
	}
};


// 获取键值对数据
const getMetadataData = () => {
	metadataObj.value = {};
	metadataList.value.forEach(el => {
		// if (el.key === 'aliasName') return;
		if (props.inlayFields.indexOf(el.key) !== -1) return;
		if (el.key) {
			metadataObj.value[el.key] = el.value;
		}
	});
	emits('handleData', metadataObj.value);
	return metadataObj.value;
};

defineExpose({
	getMetadataData,
	clearMetadataData
});

watch(() => props.metadataData, (val) => {
	if (!isEmpty(val)) {
		metadataList.value = [];
		Object.keys(val).forEach(el => {
			if (props.inlayFields.indexOf(el) === -1) metadataList.value.push({ key: el, value: val[el] });
		});
	}
	if (!metadataList.value.length || JSON.stringify(val) === '{}') {
		metadataList.value = [{ key: '', value: '' }];
	}
}, { immediate: true, deep: true });
</script>

<style lang="less" scoped>
.load{
	border-radius: 4px;
	width: calc(~"100% - 36px");
	.inputBox{
		display: flex;
		border-radius: 4px;
		& + .inputBox {
			margin-top: 20px;
		}
		.el-col-8{
			display: flex;
			justify-content: space-between;
			
			& > div {
				flex-shrink: 0;
				width: calc(~"(100% - 22px - 2 * 15px) / 2");
			}

			& > span{
				flex-shrink: 0;
				margin: auto 0;
				width: 22px;
				height: 2px;
				background: #5A5A5A;
			}
		}
		.el-col-14{
			display: flex;
			justify-content: flex-start;
			padding-left: 16px;
		}
	}
}
.info{
	margin-left: 16px;
	line-height: 32px;
	font-size: 12px;
	color: #ED4848;
}
.isRepeatKey :deep(.el-input__wrapper){
	--el-input-border-color: #ED4848;
}
.icon-box {
	padding-right:8px
}
</style>