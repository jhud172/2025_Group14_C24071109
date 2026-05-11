/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    './src/main/resources/templates/**/*.html',
    './src/main/java/**/*.java',
    './src/main/resources/static/js/**/*.js',
  ],
  theme: {
    extend: {
      screens: {
        nav: '1125px',
      },
    },
  },
  plugins: [require('tailwind-scrollbar')],
};
