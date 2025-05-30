import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
      '/basapi': {
        target: 'http://localhost:8080',
        //支持所有ip访问

        changeOrigin: true
      }
    }
  }
}) 