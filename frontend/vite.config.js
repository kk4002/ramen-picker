import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// 개발 시 /api 요청을 백엔드(8095)로 프록시
export default defineConfig({
  plugins: [react()],
  // 빌드 산출물을 백엔드 정적 경로로 출력 → 단일 오리진(백엔드)에서 함께 서빙
  build: {
    outDir: '../backend/src/main/resources/static',
    emptyOutDir: true,
  },
  server: {
    port: 5173,
    // 터널(외부 호스트) 접속 허용
    allowedHosts: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8095',
        changeOrigin: true,
      },
    },
  },
})
