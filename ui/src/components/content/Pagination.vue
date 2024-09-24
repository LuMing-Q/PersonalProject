<template>
	<div class="pageBox">
		<div class="totals">
			共&nbsp;<span class="total-num">{{ total }}</span>&nbsp;条记录
		</div>
		<el-pagination
      v-model:current-page="page"
      v-model:page-size="size"
      :page-sizes="pageSizes"
      :disabled="disabled"
      layout="prev, pager, next, sizes, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"/>
	</div>
</template>
 
<script setup name="pagination">
import { defineProps, defineEmits, toRefs, ref, watch } from 'vue';
const props = defineProps({
	total: { type: Number, default: 0 },
	pageInfo: { type: Number, default: 1 },
	showTotal: { type: Boolean, default: true },
	pageSizes: { type: Array, default: () => [10, 20, 30, 40] }
});

const { total, pageSizes } = toRefs(props);
const page = ref(1);
const size = ref(10);
const emit = defineEmits(['returnPageInfo']);
const handleSizeChange = () => {
	page.value = 1;
	let result = {
		page: page.value,
		size: size.value,
		total: total.value
	};
	emit('returnPageInfo', result);
};
const handleCurrentChange = () => {
	let result = {
		page: page.value,
		size: size.value,
		total: total.value
	};
	emit('returnPageInfo', result);
};

watch(() => props.pageInfo, (val) => {
	if (val) {
		page.value = val;
	}
});
</script>
 
 <style lang="less">
 .pageBox {
	width: 100%;
	height: 32px;
	padding: 27px 0 25px;
	display: flex;
	justify-content: end;
	font-size: 14px;
	color: #5A5A5A;
	font-weight: 400;
	// border-top: 1px solid #E4EFED;

	.totals {
		height: 32px;
		line-height: 32px;

		.total-num {
			color: #42A7FF;
			font-size: 16px;
			font-weight: 700;
		}
	}

.toPage{
	margin-left: 30px;
	height: 32px;
	line-height: 32px;
	display: flex;

	.el-input {
		margin: 0 15px;
		width: 32px;
		height: 32px;
		line-height: 32px;

		.el-input__wrapper {
			padding: 1px 0;

			.el-input__inner {
				text-align: center;
			}
		}
	}
}

	.el-pagination>*.is-first {
		color: #5A5A5A;
	}

	.el-select .el-input__wrapper{
		background: #062435;
	}

	.el-select-dropdown__item.hover, .el-select-dropdown__item:hover{
		background-color: transparent;
		color: #409eff;
		font-weight: bold;
	}
	.el-pagination .el-input__inner {
		color: #5A5A5A;
		font-size: 14px;
		font-weight: 400;
		font-family: "HarmonyOS Sans SC";
	}

	.el-pagination>*.is-last,
	.el-pagination .btn-prev,
	.el-pagination button:disabled,
	.el-pager li {
		background: transparent;
		color: #5A5A5A;
		text-align: center;
		font-size: 14px;
		font-weight: 400;
		font-family: "Microsoft YaHei";
	}

	.el-pager li.is-active {
		text-align: center;
		font-size: 14px;
		font-weight: 400;
		color: #FFFFFF;
		font-family: "Microsoft YaHei";
		width: 32px;
		height: 32px;
		line-height: 32px;
		background: #3E7EE8;
		border-radius: 4px;
	}

	.el-pagination {
		height: 32px;
	}

	.el-pagination button {
		background: transparent;
	}

	.el-select .el-input.is-focus .el-input__wrapper {
		border: 1px solid #83cdff !important;
		box-shadow: inset 0 0 6px 0 #17acf180 !important;
		outline: none !important;
	}

	:deep(.el-select .el-input__wrapper.is-focus) {
		box-shadow: none !important;
		border: 1px solid #486e87 !important;
		outline: none !important;
	}

	.el-select .el-input__wrapper.is-focus {
		box-shadow: none !important;
		border: 1px solid #486e87 !important;
		outline: none !important;
	}
 }

 .el-pagination__editor.el-input {
	width: 32px;
	height: 32px;
	line-height: 32px;
	padding: 1px 0;

	.el-input__wrapper {
		padding: 0;
	}
 }
 </style>