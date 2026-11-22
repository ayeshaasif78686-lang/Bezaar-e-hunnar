import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// The admin panel runs on its own port (5174); the storefront keeps 5173 and the backend 8081.
export default defineConfig({
  plugins: [react()],
  server: { port: 5174, strictPort: true }
})
