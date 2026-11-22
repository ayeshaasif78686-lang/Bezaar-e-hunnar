# MongoDB storage

All backend data now lives in MongoDB (database `bazaar_e_hunar`, change with `MONGODB_DATABASE`).
The connection string is read from `backend/.env` (`MONGODB_URI=...`) automatically.

| Collection | What it stores | Endpoints |
|---|---|---|
| `users` | accounts, roles, profile details | `/api/auth/register`, `/api/auth/login`, `GET/PUT /api/users/me` |
| `products` | catalog (seeded on first run) | `/api/products` (GET public; POST/PUT/DELETE seller/admin) |
| `artisans` | artisan profiles (seeded on first run) | `GET /api/artisans`, `GET /api/artisans/{id}`, POST/PUT seller/admin, DELETE admin |
| `product_reviews` | product reviews | `GET/POST /api/products/{id}/reviews` |
| `orders` | placed orders | `POST /api/orders`, `GET /api/orders/my`, `GET /api/orders/{number}`, admin: `GET /api/orders`, `PATCH /api/orders/{number}/status` |
| `custom_orders` | custom order requests | `POST /api/custom-orders` (public), `GET /api/custom-orders/my`, admin: `GET /api/custom-orders` |
| `contact_messages` | Contact page messages | `POST /api/contact` (public), admin: `GET /api/contact` |
| `counters` | numeric id counter for reviews | internal |

First start: built-in products/artisans are inserted, and old `data/users.json` / `data/reviews.json`
are copied into MongoDB (only while the collection is empty). Those files are not written to anymore.

Atlas: if startup says it cannot connect, add your current IP in Atlas -> Network Access.

Order body for `POST /api/orders` (needs `Authorization: Bearer <token>`):
```json
{
  "items": [{ "id": "ajrak-shawl-1", "quantity": 2 }],
  "address": { "fullName": "Ali", "phone": "0300...", "address": "House 1", "city": "Lahore", "province": "Punjab", "postalCode": "54000" },
  "delivery": "standard",
  "paymentMethod": "Cash on Delivery",
  "coupon": ""
}
```
Prices are taken from the `products` collection. Card numbers are never stored.
