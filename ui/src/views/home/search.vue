<template>
	<div class="listWrap" style="background: #f5f8f8;">
		<empty />
	</div>
</template>
<script setup name="Home">
import { ref } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { useRoute } from 'vue-router';
import { echartsFit } from '@/utils';

const { query } = useRoute();

const param = ref(query.param);
const total = ref(1200);
// 分页
const getPagiation = (res) => {
	console.log(res.page, res.size);
	// params.value.page = res.page
	// params.value.size = res.size
};
</script>
<style lang="less" scoped>

</style>