<!-- 
	搜索传参配置
	const searchConfig = reactive([
		{ type: 'input', field: 'name', label: '名称' },
		{ type: 'select', field: 'type', label: '下拉', options: list }, // list 为数组
		{ type: 'dataPicker', field: 'time', label: '时间选择：', defaultVal: '' }
	]);
	// 接收时间参数
	param.value.start_push_time = day.getDay(e.publishTime[0]);
	param.value.end_push_time = day.getDay(e.publishTime[1]);
-->
<template>
	<div class="tableHeaderWrap" :style="icon ? 'justify-content: space-between' : ''">
		<div v-if="isShowCreate" class="createWrap">
			<el-button v-if="!icon" type="primary" :icon="Plus" @click="handleCreate">{{ handleType }}{{ type }}</el-button>
			<el-button v-else type="primary" @click="handleCreate">
				<Icon :name="icon" color="#FFFFFF" class="iconSize14 icon-margin-right"/>
				{{ handleType }}{{ type }}
			</el-button>
		</div>
		<div v-if="props.searchConfig && props.searchConfig.length" class="searchWrap">
			<template v-for="item in props.searchConfig" :key="`${item.field}_${index}`">
				<div class="search">
					<div class="labelWrap">{{ item.label }}</div>
					<div class="valueWrap">
						<!-- 下拉选择 -->
						<el-select v-if="item.type === 'select'" v-model="ruleForm[item.field]" clearable :placeholder="`请选择${item.label}`">
							<el-option v-for="el in item.options" :key="el.value" :label="el.label || el.name" :value="el.value || el.id" />
						</el-select>
						<!-- 日期选择器 -->
						<el-date-picker
							v-else-if="item.type === 'dataPicker'"
							v-model="ruleForm[item.field]"
							type="datetimerange"
							range-separator="-"
							start-placeholder="开始时间"
							end-placeholder="结束时间" />
						<!-- 输入框 -->
						<el-input v-else 
							v-model="ruleForm[item.field]" 
							:maxlength="64" :placeholder="`请输入${item.label}`" 
							@blur="ruleForm[item.field]=$event.target.value.trim()">
						</el-input>
					</div>
				</div>
			</template>
			<div class="btnGroup">
        <div class="searchBox">
          <el-button type="primary" class="searchBtn" 
					:style="{background: '#285FBB'}" :icon="Search" @click="getSearchData">搜索</el-button>
				</div>
				<!-- 刷新按钮 -->
				<div v-if="isShowRefresh" class="searchBox" @click="handleRefresh">
					<el-button type="primary" class="refreshBtn" >
						<Icon name="icon-zhongzhi" color="#285FBB" class="iconSize16 icon-margin-right"></icon>
					</el-button>
				</div>
      </div>
		</div>
	</div>
</template>

<script setup name="TableHeader">
import { ref, defineProps, defineExpose } from 'vue';
import { isEmpty } from 'lodash';
import { Plus, Search } from '@element-plus/icons-vue';

const props = defineProps({
	// 是否包含创建（）
	isShowCreate: { type: Boolean, default: true }, 
	/**
	 * icon 、handleType 和 type 三者组成一个按钮
	 */
	// 操作类型
	handleType: { type: String, default: '创建' }, 
	// 菜单名  eg: 角色
	type: { type: String, default: '' },
	// 图标
	icon: { type: String, default: '' },
	isShowRefresh: { type: Boolean, default: false }, // 是否包含刷新
	/**
	 * 搜索配置
	 *     type：展示类型
	 * 		 field：接受字
	 * 	 	 label：展示内
	 * 		 defaultVal：默认值（列表无时表示可以清除）
	 * 		 options：下拉选择备选项（类型为数组 label：字段可为 name；value：字段可为 id）
	 */
	searchConfig: {
		type: Array,
		default: () => [
			{ type: 'input', field: 'name', label: '输入名称' },
			{ type: 'select', field: 'tag', label: '下拉选择',
				options: [
					{ label: '显示', value: '0' },
					{ label: '不显示', value: '1' }
				]
			}
		]
	},
});

const emits = defineEmits(['onCreate', 'onSearch', 'onRefresh', 'onOpen', 'onClose']);
const ruleForm = ref({});

// 创建
const handleCreate = () => {
	if (props.icon === 'daochuExcel') {
		const searchData = {};
		if (!isEmpty(ruleForm.value)) {
			Object.keys(ruleForm.value).forEach(el => {
				if (ruleForm.value[el]) {
					searchData[el] = ruleForm.value[el];
				}
			});
		}
		emits('onCreate', searchData);
		return searchData;
	}
	emits('onCreate');
};

// 获取搜索数据
const getSearchData = () => {
	const searchData = {};
	if (!isEmpty(ruleForm.value)) {
		Object.keys(ruleForm.value).forEach(el => {
			if (ruleForm.value[el]) {
				searchData[el] = ruleForm.value[el];
			}
		});
	}
	emits('onSearch', searchData);
	return searchData;
};

// 刷新
const handleRefresh = () => {
	ruleForm.value = {};
	emits('onRefresh');
};

defineExpose({
	getSearchData,
	handleRefresh
});
</script>

<style lang="less" scoped>
.refreshBtn{
	background: #EEF5FF;
	border: 1px solid #DEEBFE;
	color:#285FBB;
	width: 0px;
}
	.tableHeaderWrap {
		height: 32px;
		line-height: 32px;
		display: flex;
		align-items: center;
		.createWrap {
			display: flex;
			align-items: center;
			height: 32px;
			margin-right: 24px;

			.icon-margin-right {
				margin-right: 6px;
			}
		}
		.searchWrap {
			display: flex;
			height: 32px;
			align-items: center;

			:deep(.el-button) {
				margin-left: 24px;
				height: 32px;
			}

      .btnGroup {
        flex: 1;
				height: 32px;
        display: flex;
				align-items: center;

				.searchBox, .closeBox, .openBox {
					height: 32px;
					display: flex;
					align-items: center;	
				}

        .rightBox{
          flex: 1;
          display: flex;
          flex-direction: row-reverse;
        }
      }
			
			.search {
				display: flex;
				justify-content: flex-start;

				& > div{
					flex-shrink: 0;
				}

				& + .search{
					margin-left: 40px;
				}
			}

			.labelWrap {
				display: flex;
				align-items: center;
				margin-right: 12px;
				height: 32px;
				line-height: 32px;
				font-size: 16px;
				font-weight: 400;
				color: #30333A;
			}

			.valueWrap {
				height: 32px;
				display: flex;
				align-items: center;
				width: 208px;
				.el-date-editor{
					min-width: 312px;
				}
			}
		}
	}
</style>
