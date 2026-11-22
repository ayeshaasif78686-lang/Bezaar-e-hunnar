# Bazaar-e-Hunar Admin Panel (separate project)

A standalone React + Vite app. It does **not** touch the storefront project; it only talks to the same backend.

| App | Folder | Port |
|-----|--------|------|
| Storefront (original, unchanged) | your existing frontend | 5173 |
| **Admin panel (this)** | `admin-panel/` | **5174** |
| Backend (shared) | `backend/` | 8081 |

## Run
1. Start the backend (use the `backend/` from this zip: it adds `/api/admin/*` and allows port 5174).
2. In this folder: `npm install` then `npm run dev`.
3. Open http://localhost:5174 and sign in with the admin account
   (default `admin@bazaarehunar.com` / `Admin123!`, or your `ADMIN_EMAIL` / `ADMIN_PASSWORD`).

`.env` (already created, edit if your ports differ):
```
VITE_API_URL=http://localhost:8081/api
VITE_STORE_URL=http://localhost:5173
```
Only ADMIN accounts can sign in. The admin session is stored under its own key, so it never logs you in or out of the storefront.

Production build: `npm run build` (output in `dist/`). If you deploy it, add its URL to the backend with `ADMIN_ORIGIN=https://your-admin-url`.
