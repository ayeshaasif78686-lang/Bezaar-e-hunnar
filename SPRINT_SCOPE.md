# Bazaar-e-Hunar: Current Working Scope

This ZIP continues the existing project incrementally. Existing Home, Login, Register, Product Detail, Dashboard, JWT/BCrypt authentication, roles, local JSON user storage, Proxy Pattern, heritage theme, launcher scripts and existing assets are preserved.

## Completed foundation

### Sprint 0: Foundation
- React + Vite frontend
- Java Spring Boot backend
- REST API connection
- CORS and health endpoint
- Existing heritage visual system

### Sprint 1: Authentication + Access Control
- Buyer/Seller registration
- Login with BCrypt + JWT
- BUYER, SELLER, ADMIN roles
- Protected profile/dashboard flow
- Proxy Pattern for role-based dashboard access

## Added in this update

### Marketplace Product Listing
- `/products`
- Reference-style collection hero
- Search
- Category filters
- Price range
- Artisan location filters
- Material filters
- Rating filters
- Sorting
- Pagination
- Add to Cart
- Wishlist toggle
- Product detail navigation
- Backend GET `/api/products` with local frontend fallback

### Artisan Profile
- `/artisans/:artisanId`
- Reference-style artisan hero
- Artisan profile/stats
- About/story section
- Featured artisan products
- Product navigation and cart actions
- Backend GET `/api/artisans/{id}` with local fallback

### Cart
- `/cart`
- Persistent browser cart
- Quantity increase/decrease
- Remove item
- Free shipping threshold
- Order summary
- `HUNAR10` and `WELCOME10` coupon support
- Recommended products
- Continue Shopping
- Cart state persists in localStorage
- Backend pricing preview endpoint at POST `/api/cart/preview`

### Checkout UI Flow
- `/checkout`
- `/payment`
- `/order-confirmed`
- Selected cart items are carried into the checkout flow.
- Address and delivery method are validated before payment.
- Card, mobile wallet, bank transfer and cash-on-delivery choices are available for the local demo.
- Coupon and delivery totals are shown in the Payment order summary.
- Order confirmation includes a local demo order number, delivery estimate, details toggle and order journey.

## Reference UI asset refinement

The three marketplace screens now use the locally bundled reference backgrounds supplied with this update. The collection hero, Meet Our Artisans hero, Artisan Support banner and Cart newsletter use project assets rather than remote image links. Home, Login, Register and Product Detail have not been visually replaced.

Additional UI behavior added in this refinement:
- Artisan tabs switch content without leaving the page.
- Follow state persists in browser storage.
- View All filters the product listing by selected artisan.
- Cart checkboxes update the visible pricing summary for selected items.
- Checkout, Payment and Order Confirmation screens are now connected to the Cart flow.

## Design patterns implemented and preserved

### Existing
1. Proxy

### Catalog patterns added
2. Factory Method
3. Composite
4. Flyweight
5. Iterator

### Cart patterns added because the Cart screen was explicitly requested in this milestone
6. Strategy
7. Command
8. Memento

The remaining GoF patterns are still reserved for their mapped future modules in the Master Development Plan. They should not be added as fake or disconnected classes.

## Current storage note

This runnable university demo build intentionally keeps the error-free local JSON user store from the previous working ZIP because MongoDB Atlas TLS problems prevented reliable local execution on the current machine. Product catalog data is seeded in the backend service and mirrored as frontend fallback data for resilient demos.

## Routes
- `/`
- `/products`
- `/products/:productId`
- `/artisans/:artisanId`
- `/cart`
- `/login`
- `/register`
- `/dashboard`
