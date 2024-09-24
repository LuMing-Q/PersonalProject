<template>
	<div class="home_bg">
		<el-carousel :interval="time">
			<el-carousel-item>
				1
			</el-carousel-item>
			<el-carousel-item>
				2
			</el-carousel-item>
			<el-carousel-item>
				3
			</el-carousel-item>
		</el-carousel>
		<div class="serch_box">
			<div class="top">
				<el-input v-model="search" placeholder="搜索共享资源" />
				<el-button type="primary" :icon="Search" class="search" @click="router.push({ path: '/home/search', query: { param: search } })">搜索</el-button>
			</div>
			<div class="bottom"><span v-for="(item, index) in hotList" :key="index" class="hot_key">{{ item }}</span></div>
		</div>
	</div>
	<div class="contentWrap">
		<div class="listWrap" style="margin-top: 0;">
			<div class="title_box">
				共享资源-分类搜索
			</div>
			<div class="content_box">
				<div class="index-content">
					<div class="img_box">
						1
					</div>
					<div class="right_box">
						<span class="name_box">地理信息</span>
						<span class="value_box">123</span>
					</div>
				</div>
				<div class="index-content">
					<div class="img_box">
						2
					</div>
					<div class="right_box">
						<span class="name_box">自然资源调查监测</span>
						<span class="value_box">886</span>
					</div>
				</div>
				<div class="index-content">
					<div class="img_box">
						3
					</div>
					<div class="right_box">
						<span class="name_box">国土空间规划</span>
						<span class="value_box">668</span>
					</div>
				</div>
				<div class="index-content">
					<div class="img_box">
						4
					</div>
					<div class="right_box">
						<span class="name_box">自然资源管理</span>
						<span class="value_box">1569</span>
					</div>
				</div>
			</div>
		</div>
		<div class="listWrap">
			<div class="title_box">
				最新动态-共享资源
				<span class="more">查看更多 →</span>
			</div>
		</div>
		<div class="listWrap">
			<div class="title_box">
				下载专区
				<span class="more">查看更多 →</span>
			</div>
		</div>
	</div>
</template>

<script setup name="Home">
import { ref, getCurrentInstance } from 'vue';
import day from '@/utils/day';
import { Search } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const time = ref();
const { proxy } = getCurrentInstance();
time.value = proxy.global.time + 2000;
const search = ref();
const hotList = ref(['热门关键词:', '矿产', '耕地', '空间规划', '调查监控', '遥感']);
const hypervisorArray = ref([
	{
		name: '测试空间服务',
		type: '地理信息',
		created_at: '2024-05-10T07:34:58.000000',
		gong: '省林业和草原局'
	}, {
		name: '2024矿产勘测',
		type: '自然资源管理地理信息',
		created_at: '2024-05-10T07:34:58.000000',
		gong: '省林业和草原局'
	}, {
		name: '2023耕地监测',
		type: '国土空间规划',
		created_at: '2024-05-10T07:34:58.000000',
		gong: '省林业和草原局'
	}, {
		name: '2023矿产勘测',
		type: '自然资源管理',
		created_at: '2024-05-10T07:34:58.000000',
		gong: '省林业和草原局'
	}, {
		name: '2023耕地监测',
		type: '自然资源调查监测',
		created_at: '2024-05-10T07:34:58.000000',
		gong: '省林业和草原局'
	}
]);

</script>

<style lang="less" scoped>
.home_bg {
	height: 260px; 
	width: 100%;
	position: relative;

	.serch_box {
		width: 700px;
		height: 70px;
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);

		.top {
			display: flex;
			justify-content: center;
			margin-bottom: 12px;

			.search {
				width: 92px; 
				height: 40px; 
				margin-left: 8px;
			}
		}

		.bottom {
			padding-left: 4px;
			height: 14px;
			line-height: 14px;
			font-size: 14px;
			text-align: left;
			color: #FFFFFF;

			.hot_key {
				margin-right: 24px;
				cursor: pointer;
				-webkit-user-select: none;
				-moz-user-select: none;
				-ms-user-select: none;
			}
		}
	}
}
</style>
<style lang="less">
.home_bg {
	.el-carousel--horizontal {
		height: 100%;

		.el-carousel__container {
			height: 100%;
		}
	}
}
</style>