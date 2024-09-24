<!-- 左侧目录 -->
<template>
	<div class="left-directory-wrap">
		<div v-if="istranShow" class="istran">
			<div :class="tran ? 'btn' : 'btn1'" @click="changeCatalogue(true)">横向目录</div>
			<div :class="!tran ? 'btn' : 'btn1'" @click="changeCatalogue(false)">纵向录目</div>
		</div>
		<div :class="istranShow ? 'directory' : 'directory directory1'">
			<div v-if="props.isShowHear" class="directory-search">
				<el-input v-model="directoryName" :placeholder="props.headText" clearable class="directory-input" ></el-input>
				<el-button type="primary" class="elbutton" icon="Search" @click="filterNode">搜索</el-button>
			</div>
			<div class="directory-body">
				<el-tree
					v-if="showList.length > 0"
					ref="treeRef"
					highlight-current
					:data="showList"
					:indent="16"
					:check-strictly="true"
					node-key="id"
					:default-expanded-keys="expandedList"
					:current-node-key="defaultChecked"
					:props="defaultProps"
					@node-click="handleNodeClick"
				>
					<template #default="{ data }"> 
						<div class="data-style">
							<bz-icon :name="props.icon" class="icon" />
							<span>
								{{ data.label }}
							</span>
						</div>
					</template>
				</el-tree>
				<empty v-else />
			</div>
		</div>
	</div>
</template>

<script setup>
import { ref, defineProps, defineEmits, watch } from 'vue';

const props = defineProps({
	directoryData: { type: Array, default: () => [] },
	defaultChecked: { type: String, default: '' },
	isShowHear: { type: Boolean, default: true },
	headText: { type: String, default: '目录名称' },
	istran: { type: Boolean, default: true },
	istranShow: { type: Boolean, default: false },
	icon: { type: String, default: 'icon-wenjianjia' }
});

const tran = ref(props.istran);

const flattenList = ref([]);
const expandedList = ref([]);
const showList = ref([]);

const emits = defineEmits(['handleNodeClick']);

const defaultProps = {
	children: 'children',
	label: 'name',
	value: 'id'
};

const treeRef = ref();

const selectedCategory = ref('');
const directoryName = ref('');

// 保存节点Id到selectedCategory变量中
const handleNodeClick = (nodeData) => {
	selectedCategory.value = nodeData.id;
	emits('handleNodeClick', selectedCategory.value);
};

const flatten = tree => {
	let list = [];
	tree.forEach(node => {
		const { children, ...obj } = node;
		list.push(obj);
		if (children && children.length) {
			const childrenList = flatten(children);
			list.push(...childrenList);
		}
	});
	return list;
};

const filterNode = () => {
	expandedList.value = (flattenList.value.filter(r => r.label.indexOf(directoryName.value) !== -1) || []).map(r => r.id);
};

const changeCatalogue = (param) => {
	tran.value = param;
	if (tran.value) {
		showList.value = props.directoryData.filter(r => r.isShar === 1);
	} else {
		showList.value = props.directoryData.filter(r => r.isShar === 0);
	}
};

watch(() => props.directoryData, val => {
	flattenList.value = flatten(val);
	if (tran.value) {
		showList.value = val.filter(r => r.isShar === 1);
	} else {
		showList.value = val.filter(r => r.isShar === 0);
	}
}, { deep: true, immediate: true });
</script>

<style lang="less" scoped>
@import '@/assets/styles/global.less';
.left-directory-wrap{
	height: 100%;
	
	.istran {
		height: 40px;
		line-height: 40px;
		font-size: 16px;
		border-radius: 4px 4px 0 0;
		display: flex;
		overflow: hidden;
		cursor: pointer;
		-webkit-user-select:none;
		-moz-user-select:none;
		-ms-user-select:none;
		user-select:none;

		.btn {
			width: 50%;
			text-align: center;
			background: #285FBB;
			color: #FFFFFF;
		}

		.btn1 {
			width: 50%;
			text-align: center;
			background: #F9FAFC;
			color: #909399;
		}
	}
}

.directory  {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 20px;
	width: calc(100% - 40px);
	height: calc(100% - 20px - 40px - 20px);
	border-radius: 4px 4px 4px 4px;
	background:#FFFFFF;
}

.directory1 {
	height: calc(100% - 20px - 20px);
}

.listSerach{
	height: 72px;
	width:1550px ;
	border-radius: 4px 4px 4px 4px;
	background:#FFFFFF;

}
.search-bar, .directory-search{
	display: flex;
	justify-content: space-between;
	height: 32px;
	align-items: center;
	margin-bottom: 16px;
}
.directory-body {
	width: calc(100% - 5px);
	height: calc(100% - 32px - 16px);
	padding-right: 5px;
	overflow-y: auto;
}
:deep(.el-button.el-button--primary){
	background-color: @mainColor;
}
.data-style{
	height: 36px;
	line-height: 36px;
	display: flex;
	align-items: center;
	.icon {
		margin-right: 8px;
		width: 24px !important;
		height: 21px !important;
	}
}
</style>
