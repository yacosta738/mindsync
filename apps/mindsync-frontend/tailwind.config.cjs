/* eslint-disable global-require */
/** @type {import('tailwindcss').Config} */
module.exports = {
	content: [
		//
		'./src/**/*.{astro,html,js,jsx,md,mdx,svelte,ts,tsx,vue}',
		'./node_modules/flowbite/**/*.js',
	],

	darkMode: 'class',

	theme: {
		extend: {
			colors: {
				primary: {
					50: '#f4f4fa',
					100: '#e8e5f4',
					200: '#d4d1ec',
					300: '#b6b2de',
					400: '#948dcd',
					500: '#8071c0',
					600: '#745eb2',
					700: '#6b53a2',
					800: '#5b4783',
					900: '#4b3d6b',
					950: '#312942',
				},
			},
			fontFamily: {
				sans: [
					'Inter',
					'ui-sans-serif',
					'system-ui',
					'-apple-system',
					'system-ui',
					'Segoe UI',
					'Roboto',
					'Helvetica Neue',
					'Arial',
					'Noto Sans',
					'sans-serif',
					'Apple Color Emoji',
					'Segoe UI Emoji',
					'Segoe UI Symbol',
					'Noto Color Emoji',
				],
				body: [
					'Inter',
					'ui-sans-serif',
					'system-ui',
					'-apple-system',
					'system-ui',
					'Segoe UI',
					'Roboto',
					'Helvetica Neue',
					'Arial',
					'Noto Sans',
					'sans-serif',
					'Apple Color Emoji',
					'Segoe UI Emoji',
					'Segoe UI Symbol',
					'Noto Color Emoji',
				],
				mono: [
					'ui-monospace',
					'SFMono-Regular',
					'Menlo',
					'Monaco',
					'Consolas',
					'Liberation Mono',
					'Courier New',
					'monospace',
				],
			},
			transitionProperty: {
				width: 'width',
			},
			textDecoration: ['active'],
			minWidth: {
				kanban: '28rem',
			},
		},
	},

	safelist: [
		// In Markdown (README…)
		'justify-evenly',
		'overflow-hidden',
		'rounded-md',

		// From the Hugo Dashboard
		'w-64',
		'w-1/2',
		'rounded-l-lg',
		'rounded-r-lg',
		'bg-gray-200',
		'grid-cols-4',
		'grid-cols-7',
		'h-6',
		'leading-6',
		'h-9',
		'leading-9',
		'shadow-lg',
		'bg-opacity-50',
		'dark:bg-opacity-80',

		// For Astro one
		'grid',
	],

	plugins: [
		//
		require('flowbite/plugin'),
		require('flowbite-typography'),
		require('tailwind-scrollbar')({ nocompatible: true }),
	],
};
