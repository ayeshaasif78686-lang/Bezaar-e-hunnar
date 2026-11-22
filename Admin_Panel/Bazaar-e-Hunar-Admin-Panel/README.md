# Bazaar-e-Hunar Admin (ready-to-run folder)

Is folder mein sab kuch hai: `backend/` (aapka original backend + admin APIs, aapki `.env` samet) aur `admin-panel/`.
Original project mein kuch copy nahi karna.

## Chalana (2 terminal)
Terminal 1 - backend (port 8081):
    cd backend
    .\run-backend.ps1

Terminal 2 - admin panel (port 5174):
    cd admin-panel
    npm install
    npm run dev

Browser: http://localhost:5174
Login: admin@bazaarehunar.com / Admin123!  (ya aapki .env ke ADMIN_EMAIL / ADMIN_PASSWORD)

Customer website (apne original folder mein, port 5173) alag chalti rahe; dono isi backend se connected hain.
Agar aap pehle se apna alag backend chala rahe hain to usay band kar dein (dono 8081 use karte hain).


## Storefront live integration
Customer frontend `src` is supplied separately as `customer-src-live-integration.zip`.
Keep the customer site pointed at `http://localhost:8081/api` (or set `VITE_API_URL`).
Both frontends must use this same backend and MongoDB database.

The customer home page and Products page now refresh products/categories every 15 seconds.
Contact Us messages and Custom Orders requests are posted to MongoDB. Admin Messages and
Custom Orders pages refresh every 12 seconds. Admin product/category/order pages and the
dashboard also refresh while open.
