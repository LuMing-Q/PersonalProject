import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';
import eslint from 'vite-plugin-eslint';
// import stylelint from 'vite-plugin-stylelint';
import VueSetupExtend from 'vite-plugin-vue-setup-extend';
import {resolve} from 'path';
import AutoImport from 'unplugin-auto-import/vite';
// import svgLoader from 'vite-svg-loader';
import Components from 'unplugin-vue-components/vite';
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers';
import postcssPxtoRem from 'postcss-pxtorem';

// https://vitejs.dev/config/
export default defineConfig(({ command, mode }) => ({
	build: {
		chunkSizeWarningLimit: 1000, // 触发警告的chunk大小（以 kbs 为单位）
		cssCodeSplit: false, // 样式是否分包
		// Terser 压缩器:减少构建后的文件大小 
		terserOptions: {
			compress: {
				drop_console: true, 	// 清除console
				drop_debugger: true, 	// debugger
			}
		},
	},
	// resolve 路径解析
	resolve: {
		// alias 定义路径别名
		alias: {
			'@': resolve(__dirname, 'src'),
			'~/': `${resolve(__dirname, 'src')}/`,
		},
	},
	// serverv 定义开发服务器选项，包括监听端口、自动打开浏览器等
	server: {
		port: 52693, // 设置开发服务器的端口号
		open: true, // 启动开发服务器时会自动打开浏览器 true 打开
	},
	// 定义要使用的 Vite 插件
	plugins: [
		vue(),
		// 用于自动导入组件和 API插件
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
		// 用于扩展 Vue 组件的 setup 选项，使其支持使用 setup 语法
		VueSetupExtend(), 
	],
	// CSS 处理
	css: {
		//  CSS 预处理器
		preprocessorOptions: {
			// less 预处理器
			less: {
				// additionalData: 设置全局样式文件的路径
				additionalData: '@import \'./src/assets/styles/global.less\';',
			},
			// elementPlus 样式重置
			scss: {
				additionalData: '@use "@/assets/styles/element.scss" as *;',
			},
		},
		// 用于将像素单位转换为 rem 单位的 PostCSS 插件
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

