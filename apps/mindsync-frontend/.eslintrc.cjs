require('@rushstack/eslint-patch/modern-module-resolution');

module.exports = {
	root: true,
	env: {
		node: true,
		es2022: true,
		browser: true
	},
	plugins: ['@typescript-eslint', 'import', 'prettier'],
	extends: [
		'airbnb-typescript/base',
		'prettier',
		'plugin:@typescript-eslint/recommended',
		'plugin:import/typescript'
	],
	parser: '@typescript-eslint/parser',
	parserOptions: {
		'project': './tsconfig.eslint.json',
		'ecmaVersion': 'latest',
		'sourceType': 'module'
	},
	settings: {
		'import/resolver': {
			'typescript': {},
			'alias': {
				'map': [
					['@', './src'],
					['@components/*', './src/components/*'],
					['@views/*', './src/views/*'],
					['@router/*', './src/router/*'],
					['@store/*', './src/store/*'],
					['@assets/*', './src/assets/*'],
					['@atoms/*', './src/components/atoms/*'],
					['@molecules/*', './src/components/molecules/*'],
					['@organisms/*', './src/components/organisms/*'],
					['@templates/*', './src/components/templates/*']
				]
			}
		}
	},
	rules: {
		'import/no-extraneous-dependencies': 'off',
		'import/no-unresolved': 'off',
		'import/extensions': 'off',
		'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
		'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off'
	},
	overrides: [
		{
			files: '*.vue',
			parser: 'vue-eslint-parser',
			extends: [
				'plugin:vue/vue3-recommended',
				'eslint:recommended',
				'plugin:import/recommended',
				'@vue/eslint-config-typescript',
				'prettier'
			],
			plugins: [
				'vue',
				'@typescript-eslint',
				'eslint-plugin-import',
				'prettier'
			],
			rules: {
				// FIXME: Causes problems with imports from Vue
				'import/named': 'off',
				'import/no-extraneous-dependencies': 'off',
				'import/no-unresolved': 'off',
				'import/extensions': 'off',
				'vue/multi-word-component-names': 'off',
			},
			parserOptions: {
				'parser': '@typescript-eslint/parser',
				'project': './tsconfig.json',
				'ecmaVersion': 'latest'
			}
		},
		{
			files: ['*.astro'],
			extends: ['plugin:astro/recommended', 'plugin:astro/jsx-a11y-strict'],
			parser: 'astro-eslint-parser',
			parserOptions: {
				'parser': '@typescript-eslint/parser',
				'extraFileExtensions': ['.astro'],
				'sourceType': 'module'
			},
			rules: {
				// prevent XSS attack
				'astro/no-set-html-directive': 'error',
				'astro/no-set-text-directive': 'error',
				'astro/no-unused-css-selector': 'off',
				'astro/prefer-class-list-directive': 'error',
				'no-unused-vars': ['error', { 'varsIgnorePattern': 'Props' }]
			}
		}
	]
}
