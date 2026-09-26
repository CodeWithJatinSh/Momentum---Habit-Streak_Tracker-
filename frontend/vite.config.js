import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    // Proxy all /api requests from React dev server (port 3000) to Spring Boot (port 8080)
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  },
  build: {
    // Default to 'dist' for Vercel and standard hosting platforms;
    // can be overridden to output to Spring Boot static directory
    outDir: process.env.BUILD_TARGET === 'spring' ? '../src/main/resources/static' : 'dist',
    emptyOutDir: true,
  }
})
