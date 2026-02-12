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
      colors: {
        // Neon color palette for futuristic gym theme
        neon: {
          cyan: '#00f3ff',
          blue: '#0066ff',
          purple: '#b537f2',
          pink: '#ff00ff',
          green: '#39ff14',
          yellow: '#ffff00',
          orange: '#ff6600',
        },
        // Dark backgrounds for contrast
        cyber: {
          900: '#0a0a0f',
          800: '#12121a',
          700: '#1a1a2e',
          600: '#25254a',
          500: '#2d2d5f',
        },
      },
      backgroundImage: {
        'gradient-neon': 'linear-gradient(135deg, rgba(0, 243, 255, 0.1) 0%, rgba(181, 55, 242, 0.1) 100%)',
        'gradient-cyber': 'linear-gradient(135deg, #0a0a0f 0%, #1a1a2e 100%)',
        'gradient-glow': 'radial-gradient(circle at center, rgba(0, 243, 255, 0.15), transparent 70%)',
      },
      boxShadow: {
        'neon-cyan': '0 0 10px rgba(0, 243, 255, 0.5), 0 0 20px rgba(0, 243, 255, 0.3)',
        'neon-purple': '0 0 10px rgba(181, 55, 242, 0.5), 0 0 20px rgba(181, 55, 242, 0.3)',
        'neon-green': '0 0 10px rgba(57, 255, 20, 0.5), 0 0 20px rgba(57, 255, 20, 0.3)',
        'neon-pink': '0 0 10px rgba(255, 0, 255, 0.5), 0 0 20px rgba(255, 0, 255, 0.3)',
        'glow-sm': '0 0 5px rgba(0, 243, 255, 0.3)',
        'glow-md': '0 0 10px rgba(0, 243, 255, 0.4), 0 0 20px rgba(0, 243, 255, 0.2)',
        'glow-lg': '0 0 15px rgba(0, 243, 255, 0.5), 0 0 30px rgba(0, 243, 255, 0.3)',
      },
      animation: {
        'pulse-neon': 'pulse-neon 2s ease-in-out infinite',
        'glow': 'glow 3s ease-in-out infinite',
        'slide-in': 'slide-in 0.3s ease-out',
        'fade-in': 'fade-in 0.5s ease-out',
      },
      keyframes: {
        'pulse-neon': {
          '0%, 100%': { opacity: '1', boxShadow: '0 0 10px rgba(0, 243, 255, 0.5)' },
          '50%': { opacity: '0.8', boxShadow: '0 0 20px rgba(0, 243, 255, 0.8)' },
        },
        'glow': {
          '0%, 100%': { filter: 'brightness(1) drop-shadow(0 0 5px rgba(0, 243, 255, 0.5))' },
          '50%': { filter: 'brightness(1.2) drop-shadow(0 0 10px rgba(0, 243, 255, 0.8))' },
        },
        'slide-in': {
          '0%': { transform: 'translateY(-10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        'fade-in': {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
      },
    },
  },
  plugins: [require('tailwind-scrollbar')],
};
