<template>
	<div class="listWrap">
		
		<div style="width: 75%;">
			<CircularProgressBar />
		</div>
	</div>
</template>
<script setup>
import { onMounted, nextTick, ref, onBeforeUnmount } from 'vue';
onMounted(async () => {

});
// onBeforeUnmount(() => (isContinue.value = false));
</script>
<style lang="less" scoped>
@import '@/assets/styles/list.less';
.listWrap {
	height: calc(100% - 40px);
	overflow-y: auto;
}
</style>