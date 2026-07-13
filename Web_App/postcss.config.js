module.exports = (context) => ({
  plugins: {
    'postcss-import': {},
    tailwindcss: {},
    autoprefixer: {},
    cssnano: context.env === 'production' ? { preset: 'default' } : false,
  },
});
