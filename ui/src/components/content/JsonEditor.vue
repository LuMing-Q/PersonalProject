
<template>
	<div class="json-editor-box">
		<json-editor-vue v-model="jsonobj" class="editor" :currentMode="currentMode" :modeList="modeList" :options="options" @blur="remarkValidate" />
	</div>
</template>

<script setup>
import { ref, defineEmits, defineProps, watch } from 'vue';
import JsonEditorVue from 'json-editor-vue3';

const props = defineProps({
	data: { type: String, default: '' },
	isDetail: { type: Boolean, default: false }
});

const currentMode = ref('code');
const jsonstr = ref('{}');
const jsonobj = ref(JSON.parse(jsonstr.value));

const emits = defineEmits(['changeValue']);

const options = ref({
	search: false,
	history: false,
});
const modeList = ref(['code', 'view']); // 可选模式
// const modeList = ref(['text', 'view', 'tree', 'code', 'form']); // 可选模式

const remarkValidate = () => {
	let newjsonstr = JSON.stringify(jsonobj.value);
	if (jsonstr.value == newjsonstr) {
		console.log('no change');
	} else {
		jsonstr.value = newjsonstr;
		emits('changeValue', JSON.parse(jsonstr.value));
	}
};

watch(() => props.data, (val) => {
	if (val) {
		jsonstr.value = val;
		jsonobj.value = JSON.parse(val);
	} else {
		jsonstr.value = '{}';
		jsonobj.value = JSON.parse(jsonstr.value);
	}
}, { deep: true, immediate: true });

watch(() => props.isDetail, (val) => {
	currentMode.value = props.isDetail ? 'view' : 'code';
}, { deep: true, immediate: true });
</script>

<style lang="less" scoped>
.json-editor-box {
	:deep(.jsoneditor-menu){
		display: none;
	}
	:deep(.jsoneditor-outer.has-main-menu-bar.has-status-bar) {
		margin: 0 !important;
		padding: 0 !important;
	}
}
</style>
