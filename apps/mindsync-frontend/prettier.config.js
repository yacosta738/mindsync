module.exports = {
  plugins: [require('prettier-plugin-tailwindcss'), require('prettier-plugin-astro')],
  semi: true,
  singleQuote: true,
  printWidth: 80,
  jsxSingleQuote: true,
	tabWidth: 2,
	trailingComma: 'none',
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
}
