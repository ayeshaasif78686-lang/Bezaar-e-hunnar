# Bazaar-e-Hunar, Error-Free Local Demo Build

## Product images and Cloudinary

The supplied product collection is bundled in `frontend/public/assets/products/` and the displayed catalog categories match the supplied folder names. The `khusa` category is retained, but its supplied folder did not contain an image file.

### Upload the supplied 148 product images to Cloudinary

Set your private Cloudinary values in PowerShell, then run the one-time upload command from the project root:

```powershell
$env:CLOUDINARY_CLOUD_NAME="your_cloud_name"
$env:CLOUDINARY_API_KEY="your_api_key"
$env:CLOUDINARY_API_SECRET="your_api_secret"
.\UPLOAD_ALL_PRODUCT_IMAGES.ps1
```

The command uploads all supplied images into `bazaar-e-hunar/products` in Cloudinary, writes their secure URLs to `frontend/public/cloudinary-product-images.js`, and the React website automatically uses those Cloudinary URLs after refresh. Do not share API secrets or commit the generated URL file if it contains private delivery links.

For later single Seller or Admin uploads, use `POST /api/products/{productId}/images` with form-data field `image`.

This build is prepared for easy university demo use on Windows. It keeps the existing React UI, JWT authentication, role-based access, and Proxy design pattern, but removes the MongoDB Atlas runtime dependency that was causing TLS/SSL connection failures on the current machine.

## Technology

- Frontend: React + Vite + JavaScript + CSS
- Backend: Java + Spring Boot + Spring Security
- Storage: local JSON file, created automatically at `backend/data/users.json`
- Authentication: JWT + BCrypt
- Roles: BUYER, SELLER, ADMIN
- Implemented design pattern: Proxy Pattern for role-based dashboard access

## Why this build is easier to run

No MongoDB Atlas account, database URI, IP allow-list, VPN, TLS fix, or local MongoDB installation is required. Registration and login still work and users are saved locally, so they remain available after restart.

## Requirements

Install and verify:

```powershell
java -version
mvn -version
node -v
npm -v
```

Java 17 or newer is supported.

## Run backend

Open the project root in VS Code. In Terminal 1:

```powershell
cd backend
mvn spring-boot:run
```

Backend runs at:

```text
http://localhost:8081
```

Health test:

```text
http://localhost:8081/api/health
```

You can also run:

```powershell
./RUN_BACKEND.ps1
```

from the project root.

## One command Windows launch

From the extracted `bazaar-e-hunar-sprint01` folder, run:

```powershell
.\START_PROJECT.cmd
```

Use the `.cmd` launcher instead of opening `START_PROJECT.ps1` directly. It starts PowerShell with the required process-only execution setting, so an unsigned-script warning does not block the local demo.

## Run frontend

Open Terminal 2:

```powershell
cd frontend
npm install
npm run dev
```

Frontend runs at:

```text
http://localhost:5173
```

You can also run:

```powershell
./RUN_FRONTEND.ps1
```

from the project root.

## Demo accounts and flows

### Admin

A demo admin is created automatically the first time the backend starts:

```text
Email: admin@bazaarehunar.com
Password: Admin123!
```

### Buyer and Seller

Use the Register page and choose BUYER or SELLER. The new account is saved to `backend/data/users.json`.

## Main APIs

Public:

```text
GET  /api/health
POST /api/auth/register
POST /api/auth/login
```

Protected:

```text
GET /api/users/me
GET /api/dashboard/buyer
GET /api/dashboard/seller
GET /api/dashboard/admin
```

Protected routes require:

```text
Authorization: Bearer <JWT_TOKEN>
```

## Design Pattern currently implemented

### Proxy Pattern

Files:

```text
backend/src/main/java/com/bazaarehunar/patterns/structural/proxy/
```

The Proxy checks the logged-in user's role before allowing access to BUYER, SELLER, or ADMIN dashboard areas. The real dashboard service performs the actual operation only after the Proxy allows access.

This is a real behavioral use of Proxy, not a dummy class added only for marks.

## Data reset

To clear all locally registered users, stop the backend and delete:

```text
backend/data/users.json
```

On next start, the admin account is created again automatically.

## Important project rule for future sprints

Do not remove working pages, authentication, role logic, or existing design patterns when adding the next sprint. New features should be added incrementally and the project should remain runnable after every sprint.

## New marketplace screens in this ZIP

```text
http://localhost:5173/products
http://localhost:5173/artisans/ustad-karim
http://localhost:5173/cart
```

### Product catalog features
Search, sorting, category, price, region, material and rating filters are interactive. Product cards open Product Detail, wishlist toggles persist, and Add to Cart updates the persistent cart.

### Reference image update
The supplied heritage image assets are bundled locally under `frontend/public/assets/` and are used on the three marketplace screens:

- `collection-hero.png` for Shop Our Collection
- `meet-our-artisans-hero.png` for the Meet Our Artisans banner
- `artisan-support-banner.png` for the Artisan Support section
- `cart-newsletter-banner.png` for the Cart newsletter section

The original Home, Login, Register and Product Detail visual designs remain unchanged.

### Updated screen interactions
- Artisan Follow state is interactive and persists in the browser.
- Artisan About, Products, Reviews and Store Policy tabs now switch their visible content.
- Artisan View All opens that artisan's filtered product collection.
- Cart item checkboxes now control which items are included in the visible order summary.
- The Cart checkout action opens the Address, Payment and Order Confirmation flow for selected items.

## Checkout flow added in this ZIP

```text
/checkout
/payment
/order-confirmed
```

The Cart button carries selected items into the Checkout screen. The address form validates required delivery information, the payment screen supports card, mobile wallet, bank transfer and cash-on-delivery choices, and the confirmation screen shows an order number, delivery estimate and order journey. The demo stores the pending checkout only in the current browser session and does not make a real payment charge.

### Cart demo coupon codes
```text
HUNAR10
WELCOME10
```
Both apply a 10% demo discount.

### New public/demo APIs
```text
GET  /api/products
GET  /api/products/{id}
GET  /api/categories/tree
GET  /api/artisans/{id}
GET  /api/patterns/sprint2-demo
GET  /api/patterns/cart-demo
POST /api/cart/preview
```

### New GoF patterns
- Factory Method
- Composite
- Flyweight
- Iterator
- Strategy
- Command
- Memento

Proxy from Sprint 1 remains intact. See `PATTERN_IMPLEMENTATION.md` for participants and proof paths.
