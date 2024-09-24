module.exports = {
	'env': {
		'browser': true,
		'es2021': true
	},
	'extends': [
		'eslint:recommended',
		'plugin:vue/vue3-essential'
	],
	'globals': {
		CONFIG: true,
		BASE_URL: true,
		WEBSOCKET_URL: true,
		__dirname: true,
		ElMessage: true,
	},
	'parserOptions': {
		ecmaVersion: 6,
		sourceType: 'module',
		ecmaFeatures: {
			modules: true
		},
		requireConfigFile: false,
		parser: '@babel/eslint-parser'
	},
	'plugins': [
		'vue'
	],
	'rules': {
		'import/no-extraneous-dependencies': 0,
		'prettier/prettier': 0,
		'require-yield': [0],
		'no-extend-native': [0],
		'no-unused-vars': [1, { vars: 'all', args: 'all' }],
		'no-redeclare': [0],
		'jsx-a11y/href-no-hash': [0],
		'comma-dangle': [0],
		'comma-spacing': 2, //  逗号前不能有空格,逗号后要有空格
		'no-underscore-dangle': [1], //  变量名禁用下划线
		'linebreak-style': [0, 'windows'], //  换行风格
		'generator-star-spacing': [0], //  allow async-await
		'consistent-return': [0],
		'quotes': [2, 'single'], //  强制使用单引号
		'no-console': 0,
		'no-var': 2,
		'semi': 2, // 强制使用分号
		'no-mixed-spaces-and-tabs': 0, //  禁止混用tab和空格
		'no-trailing-spaces': 0, //  一行结束后面有空格就发出警告
		'no-const-assign': 2, //  禁止修改const声明的变量
		'no-duplicate-case': 2, // switch中的case标签不能重复
		'no-dupe-args': 2, //  函数参数不能重复
		'no-empty': 2, //  块语句中的内容不能为空
		'no-func-assign': 2, // 禁止重复的函数声明
		'no-invalid-this': 0, //  禁止无效的this，只能用在构造器，类，对象字面量
		'no-spaced-func': 2, //  函数调用时 函数名与()之间不能有空格
		'no-this-before-super': 0, //  在调用super()之前不能使用this或super
		'no-undef': 2, //  不能有未定义的变量
		'no-use-before-define': 2, //  未定义前不能使用
		'no-multi-spaces': 2,
		'key-spacing': [2, { beforeColon: false, afterColon: true }],
		'keyword-spacing': [2, { before: true, after: true }],
		'no-irregular-whitespace': 2,
		'space-infix-ops': 2, //  要求操作符周围有空格
		'space-unary-ops': 2, // 强制在一元操作符前后使用一致的空格
		'spaced-comment': 2, //  强制在注释中 //  或 /* 使用一致的空格
		'object-curly-spacing': [2, 'always'],
		'indent': [2, 'tab', { 'VariableDeclarator': 1 }], // 缩进
		'no-extra-boolean-cast': 0, // 禁止不必要的bool转换
		'import/no-dynamic-require': 0,
		'click-events-have-key-events': 0,
		'global-require': 0,
		//  'eqeqeq': [2, 'always'], //  使用 == 替代 ===
		'no-duplicate-imports': 2,
		'jsx-a11y/click-events-have-key-events': 0,
		'jsx-a11y/no-static-element-interactions': 0,
		'jsx-a11y/label-has-associated-control': 0,
		'import/no-cycle': 0,
		'no-nested-ternary': 0, //  使用嵌套的三元运算
		'no-param-reassign': 0,
		'import/no-unresolved': 0,
		'prefer-destructuring': 0,
		'no-plusplus': 1,
		'no-else-return': 1,
		'compat/compat': 0,
		'no-return-assign': 0,
		'object-shorthand': 2,
		'camelcase': 0, //  强制使用骆驼拼写法命名约定
		'no-shadow': 1,
		'jsx-a11y/anchor-is-valid': 0,
		'import/prefer-default-export': 0,
		'no-tabs': 0,
		'operator-linebreak': [2, 'after'], // 换行时运算符在行尾还是行首
		'object-curly-newline': 0,
		'arrow-parens': 0,
		'no-restricted-syntax': 0,
		'guard-for-in': 0,
		'no-mixed-operators': 0,
		'max-len': 0,
		'no-template-curly-in-string': 0,
		'quote-props': 0, // 对象中的属性名是否需要用引号引起来
		'import/extensions': 0, //  对文件扩展名的验证
		'import/no-named-as-default-member': 0,
		'no-return-await': 0,
		'arrow-body-style': 1,
		// vue
		'vue/max-attributes-per-line': 0,
		'vue/singleline-html-element-content-newline': 0,
		'vue/attributes-order': 2,
		'vue/no-unused-components': 2,
		'vue/multi-word-component-names': 0,
		'vue/no-setup-props-destructure': 0,
		// 'object-curly-newline': ["error", { "multiline": true }],
		'no-useless-concat': 1,
		'no-debugger': 1,
		'vue/no-deprecated-html-element-is': 0,
	}
};
