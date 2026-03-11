/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    './src/main/resources/templates/**/*.html',
    './src/main/java/**/*.java',
    './src/main/resources/static/js/**/*.js',
  ],
  safelist: [
    {
      pattern: /profile-banner-pill--(none|aurora|sunset|ocean|rose|carbon|lagoon|meadow|midnight)/,
    },
    {
      pattern: /profile-avatar-ring--(none|neon_dual|solar_flare|crystal|starry_spark|aurora_pulse|comet_trail|ember_crown|king_crown|cyber_arms|ufo_beam)/,
    },
    {
      pattern: /profile-card-back-theme--(none|glass|topo|carbon|matrix|nebula|circuit|sunburst|retro_grid)/,
    },
    {
      pattern: /profile-preview-card-back--(none|glass|topo|carbon|matrix|nebula|circuit|sunburst|retro_grid)/,
    },
  ],
  theme: {
    extend: {
      screens: {
        nav: '1125px',
      },
      colors: {
        // Professional color palette: Emerald green, Golden orange, White, Black, Light blue
        brand: {
          emerald: '#10b981',    // Primary emerald green
          emeraldDark: '#059669',  // Darker emerald for hover states
          orange: '#f59e0b',     // Golden orange accent
          orangeDark: '#d97706', // Darker orange
          lightBlue: '#7dd3fc',  // Light blue for secondary accents
          lightBlueDark: '#0ea5e9', // Sky blue for hover
        },
        // Keep neon colors but muted for backwards compatibility
        neon: {
          cyan: '#7dd3fc',       // Changed to light blue
          blue: '#3b82f6',       // Changed to regular blue
          purple: '#8b5cf6',     // Changed to violet
          pink: '#ec4899',       // Changed to pink
          green: '#10b981',      // Changed to emerald
          yellow: '#fbbf24',     // Changed to amber
          orange: '#f59e0b',     // Changed to golden orange
        },
        // Dark backgrounds - cleaner for professional look
        cyber: {
          900: '#0f172a',
          800: '#1e293b',
          700: '#334155',
          600: '#475569',
          500: '#64748b',
        },
      },
      backgroundImage: {
        'gradient-neon': 'linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(245, 158, 11, 0.1) 100%)',
        'gradient-cyber': 'linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%)',
        'gradient-glow': 'radial-gradient(circle at center, rgba(16, 185, 129, 0.15), transparent 70%)',
      },
      boxShadow: {
        'neon-cyan': '0 0 10px rgba(125, 211, 252, 0.3), 0 0 20px rgba(125, 211, 252, 0.15)',
        'neon-blue': '0 0 10px rgba(59, 130, 246, 0.3), 0 0 20px rgba(59, 130, 246, 0.15)',
        'neon-purple': '0 0 10px rgba(139, 92, 246, 0.3), 0 0 20px rgba(139, 92, 246, 0.15)',
        'neon-green': '0 0 10px rgba(16, 185, 129, 0.3), 0 0 20px rgba(16, 185, 129, 0.15)',
        'neon-pink': '0 0 10px rgba(236, 72, 153, 0.3), 0 0 20px rgba(236, 72, 153, 0.15)',
        'glow-sm': '0 0 5px rgba(16, 185, 129, 0.2)',
        'glow-md': '0 0 10px rgba(16, 185, 129, 0.25), 0 0 20px rgba(16, 185, 129, 0.12)',
        'glow-lg': '0 0 15px rgba(16, 185, 129, 0.3), 0 0 30px rgba(16, 185, 129, 0.15)',
      },
      animation: {
        'pulse-neon': 'pulse-neon 2s ease-in-out infinite',
        'glow': 'glow 3s ease-in-out infinite',
        'slide-in': 'slide-in 0.3s ease-out',
        'fade-in': 'fade-in 0.5s ease-out',
      },
      keyframes: {
        'pulse-neon': {
          '0%, 100%': { opacity: '1', boxShadow: '0 0 10px rgba(16, 185, 129, 0.3)' },
          '50%': { opacity: '0.8', boxShadow: '0 0 20px rgba(16, 185, 129, 0.5)' },
        },
        'glow': {
          '0%, 100%': { filter: 'brightness(1) drop-shadow(0 0 5px rgba(16, 185, 129, 0.3))' },
          '50%': { filter: 'brightness(1.1) drop-shadow(0 0 10px rgba(16, 185, 129, 0.5))' },
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
