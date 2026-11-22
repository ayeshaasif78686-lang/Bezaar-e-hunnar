# Bazaar-e-Hunar Admin Panel Setup

## Included
- Complete React/Vite admin panel with the burgundy and cream theme and Bazaar-e-Hunar logo assets.
- Spring Boot backend source with MongoDB, JWT admin login, catalog, orders, categories, customers, sellers, contact messages, custom orders, reports and settings APIs.
- Existing backend data files and Cloudinary sync index.

## Requirements
- JDK 17 or later
- Apache Maven 3.8+
- Node.js 18+ and npm
- Reachable MongoDB Atlas or local MongoDB configured in `backend/.env`

## Start
1. Keep `backend/.env` in place. It contains the existing database and Cloudinary configuration. Do not share it publicly.
2. Double-click `START-ADMIN.bat`, or start each part separately:
   - Backend: PowerShell in `backend`, run `./run-backend.ps1`
   - Admin: PowerShell in `admin-panel`, run `./run-admin.ps1`
3. Open `http://localhost:5174`.

The backend must be running on port 8081. If port 8081 is already occupied, stop the other backend manually first. The launcher will not terminate another process.

## Default admin credentials
- Email: `admin@bazaarehunar.com`
- Password: `Admin123!`

If `ADMIN_EMAIL` and `ADMIN_PASSWORD` are set in `backend/.env`, use those values instead. Existing non-admin accounts are not automatically promoted to admin.

## Verify admin APIs
With the backend running, open a second PowerShell window in this folder and run:

```powershell
.\VERIFY-ADMIN.ps1
```

It checks backend health, admin authentication, and the dashboard, products, orders, categories, customers, sellers, messages, custom orders, settings and notifications endpoints.

## Customer website
This ZIP is the admin panel and its backend only. The separate customer website must point to the same backend base URL, `http://localhost:8081/api`, and use the same MongoDB database for shared products, categories and orders.
