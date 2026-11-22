BAZAAR-E-HUNAR ADMIN + CUSTOMER SHARED BACKEND
================================================

This archive contains:
1. Admin panel frontend (admin-panel/)
2. The integrated backend (backend/)

Use the backend in THIS archive for both the admin panel and the customer website.
Do not run a second backend from an older archive. Both frontends call
http://localhost:8081/api and this backend uses the supplied customer backend's
MongoDB and Cloudinary configuration.

Customer frontend source is supplied separately in customer-src-integrated.zip.
Replace only the customer's existing src/ folder with that archive.

Start order:
1. backend: cd backend ; .\run-backend.ps1
2. admin: cd admin-panel ; npm install ; npm run dev
3. customer: from the customer's existing project root ; npm run dev

The backend allows the customer origin (default localhost:5173) and admin origin
(default localhost:5174). Both must connect to the same MongoDB database.
See backend/SHARED-ADMIN-STOREFRONT-INTEGRATION.md and
backend/VERIFY-SHARED-INTEGRATION.ps1.
