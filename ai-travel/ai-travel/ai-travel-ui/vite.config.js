import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api/user': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/api/ai': {
        target: 'http://localhost:8085',
        changeOrigin: true
      },
      '/api/plan': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api/order': {
        target: 'http://localhost:8084',
        changeOrigin: true
      },
      '/api/product': {
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  }
})
