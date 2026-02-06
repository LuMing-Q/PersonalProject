<template>
	123
</template>

<script setup name="Home">
import { ref, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
const router = useRouter();
// @click="router.push({ path: '/home/search', query: { param: search } })"
const time = ref();
const { proxy } = getCurrentInstance();
time.value = proxy.global.time + 2000;

</script>

<style lang="less" scoped>

</style>