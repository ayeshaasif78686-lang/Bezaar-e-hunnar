BAZAAR-E-HUNAR CUSTOMER FRONTEND SOURCE
=======================================

This src folder is configured to use the same backend as the Admin Panel:
http://localhost:8081/api

IMPORTANT: Start only ONE backend. Use the backend folder from the updated
Admin + Shared Backend ZIP. It contains both storefront and admin APIs and
connects both frontends to one MongoDB database.

Changes in this src:
- Homepage and product catalogue fetch current products/categories from MongoDB.
- Product details fetch the selected product by ID from the backend.
- Catalogue refreshes every 12 seconds while open.
- Contact Us sends POST /api/contact, no longer saves only in localStorage.
- Custom Orders sends POST /api/custom-orders, no longer saves only in localStorage.
- Checkout continues to submit POST /api/orders.
- API errors show a useful message if the backend is offline.

Keep the existing customer project's package.json, public assets, Vite config,
and other root files. Replace only its src folder with this src folder.
Set VITE_API_URL=http://localhost:8081/api in the customer project's .env if needed.

Run customer frontend from its project root:
  npm install
  npm run dev

Open http://localhost:5173
