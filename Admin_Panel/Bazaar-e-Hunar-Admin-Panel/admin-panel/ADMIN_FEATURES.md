# Bazaar-e-Hunar Admin Panel

Open **/admin** after logging in with an ADMIN account (admins are sent there automatically after login).
The admin account is created by `AdminSeeder` (see backend `.env` / `.env.example`). Everyone else is redirected away.

## Pages (frontend/src/pages/admin) and the API behind them
| Page | What works | Backend |
|------|-----------|---------|
| Dashboard | totals + growth %, 30-day sales chart, order-status donut, recent orders, low stock | `GET /api/admin/dashboard` |
| Products | search, category filter, pagination, edit, delete, click status to activate/deactivate | `GET/PUT/DELETE /api/products` (`?includeInactive=true` for admin) |
| Add / Edit Product | validation, image upload (PNG/JPG up to 5 MB, Cloudinary) or image URL, stock, seller | `POST /api/products`, `POST /api/products/{id}/images` |
| Orders | status tabs with counts, search, date range, order details, move status forward (State pattern), cancel | `GET /api/orders`, `PATCH /api/orders/{n}/status` |
| Categories | add / rename / delete (delete blocked while products use it) | `/api/admin/categories` |
| Customers | search, details with order history, activate / deactivate account | `/api/admin/customers`, `PATCH /api/admin/users/{id}/status` |
| Sellers | search, add seller, details with products, activate / deactivate | `/api/admin/sellers` |
| Messages | contact-page messages: tabs by status, search, auto-mark read, reply by email, mark replied, delete | `/api/admin/messages` |
| Custom Orders | custom order requests: tabs by status, search, details, change status, email customer | `/api/admin/custom-orders` |
| Reports | week / month / year / all, totals, top selling, sales by category, CSV export | `GET /api/admin/reports` |
| Settings | General, Store Details, Users & Access, Payment, Notifications | `GET/PUT /api/admin/settings` |
| Top bar | bell (pending orders + low stock + new customers), user menu, View Store, Logout | `GET /api/admin/notifications` |

## Settings that really change behaviour
* Currency / store name: shown across the panel.
* Allow registration / seller sign-ups: registration is rejected when off.
* Payment methods: an order with a switched-off method is rejected at checkout.
* Low stock threshold + alerts: drive the dashboard and bell.
* Deactivated customers/sellers cannot log in; deactivated products disappear from the shop and cannot be bought.
* Stock: placing an order reduces stock, cancelling puts it back; ordering more than the stock is rejected.

## Install
* Copy `frontend/src` over your project's `src` (new files + changed `App.jsx`, `LoginPage.jsx`; only the Admin files are new).
* Copy `backend/src` over your backend (keep your own `.env`).
* No new npm / Maven dependencies. Charts are plain SVG.
