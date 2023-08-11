/** @type {import("@types/prettier").Options} */
// Extracted from `webdev-configs` (https://github.com/JulianCataldo/web-garden/tree/develop/configs)

module.exports = {
	printWidth: 80,
	semi: false,
	singleQuote: true,
    jsxSingleQuote: true,
	tabWidth: 2,
	trailingComma: 'none',

	// See: https://github.com/prettier/prettier/issues/7475
	useTabs: true,

	plugins: ['./node_modules/prettier-plugin-astro'],
	overrides: [
		{
			files: '*.astro',
			semi: true,
			options: {
				parser: 'astro',
				printWidth: 80,
			},
		},
	],
    endOfLine: 'lf',
};
