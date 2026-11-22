# Bazaar-e-Hunar shared backend integration

This backend is the single API server for BOTH applications:
- Customer storefront: http://localhost:5173
- Admin panel: http://localhost:5174
- Shared API: http://localhost:8081/api

Both frontends must use this same backend process and the same MongoDB database.
Do not start a second backend from the old admin ZIP on port 8081.

## Integrated endpoints
- Public products: GET /api/products
- Public categories: GET /api/categories
- Admin products: existing /api/products routes, JWT admin role required for writes
- Admin categories: /api/admin/categories
- Customer contact: POST /api/contact
- Admin messages: /api/admin/messages
- Customer custom order: POST /api/custom-orders
- Admin custom orders: /api/admin/custom-orders
- Customer checkout: POST /api/orders
- Admin orders/dashboard: /api/orders and /api/admin/dashboard

Contact messages and custom-order requests are saved to MongoDB by the same backend the admin panel reads.
The customer homepage and product catalogue refresh from the API every 12 seconds.

## CORS
The backend accepts the storefront origin from FRONTEND_ORIGIN (default localhost:5173)
and admin origin from ADMIN_ORIGIN (default localhost:5174). 127.0.0.1 variants are also accepted.

## Start
Run `.un-backend.ps1` from this backend folder. Requires Java 17+, Maven, and a reachable MongoDB URI.
Keep the existing `.env` values for MongoDB and Cloudinary. Do not share the .env file publicly.
