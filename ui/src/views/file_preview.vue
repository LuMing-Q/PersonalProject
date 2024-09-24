<template>
	<div class="file-preview-box">
		<file-preview-template :filename="filename"/>
	</div>
</template>

<script setup>
import { useRoute } from 'vue-router';
const filename = useRoute().query.fileName;
</script>

<style lang="less" scoped>
.file-preview-box{
	// height: calc(~"100vh - 60px - 40px");
	height: 100%;
}
</style>
