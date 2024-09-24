import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import eslint from 'vite-plugin-eslint';
// import stylelint from 'vite-plugin-stylelint';
import VueSetupExtend from 'vite-plugin-vue-setup-extend';
import { resolve } from 'path';
import AutoImport from 'unplugin-auto-import/vite';
// import svgLoader from 'vite-svg-loader';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
import postcssPxtoRem from 'postcss-pxtorem';

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => ({
	build: {
		chunkSizeWarningLimit: 1000, // 触发警告的chunk大小（以 kbs 为单位）
		cssCodeSplit: false, // 样式是否分包
		// 清除console和debugger
		terserOptions: {
			compress: {
				drop_console: true,
				drop_debugger: true,
			}
		},
	},
	resolve: {
		alias: {
			'@': resolve(__dirname, 'src'),
			'~/': `${resolve(__dirname, 'src')}/`,
		},
	},
	server: {
		// port: 8080,
		open: true,
	},
	plugins: [
		vue(),
		AutoImport({
			resolvers: [ElementPlusResolver()],
		}),
		Components({
			resolvers: [
				ElementPlusResolver({ importStyle: 'sass' }),
			],
		}),
		eslint({
			cache: false,
			include: ['src/**/*.js', 'src/**/*.vue', 'src/*.js', 'src/*.vue'],
		}),
		VueSetupExtend(), 
	],
	css: {
		preprocessorOptions: {
			// define global scss variable
			less: {
				additionalData: '@import \'./src/assets/styles/global.less\';',
			},
			// elementPlus 样式重置
			scss: {
				additionalData: '@use "@/assets/styles/element.scss" as *;',
			},
		},
		postcss: {
			plugins: [
				postcssPxtoRem({
					rootValue: 192, // 按照自己的设计稿修改 1920/10
					unitPrecision: 5, // 保留到5位小数
					selectorBlackList: ['ignore', 'tab-bar', 'tab-bar-item'], // 忽略转换正则匹配项
					propList: ['*'],
					replace: true,
					mediaQuery: false,
					minPixelValue: 0
				})
			]
		}
	},
}));

