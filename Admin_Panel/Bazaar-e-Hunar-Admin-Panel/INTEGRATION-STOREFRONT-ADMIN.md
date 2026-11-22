# Storefront and Admin Integration

Both frontends use the same Spring Boot API at `http://localhost:8081/api` and the same MongoDB database.

- Storefront catalog: `GET /products`
- Public category directory: `GET /categories`
- Contact form: `POST /contact`
- Custom order form: `POST /custom-orders`
- Customer checkout: authenticated `POST /orders`
- Admin catalog and management: authenticated admin endpoints under `/admin`, `/products`, and `/orders`

The storefront's Contact and Custom Orders forms no longer save submissions only in browser localStorage. They submit to the backend. The storefront catalog and home page load products and categories from the backend, while retaining the supplied static catalogue as a temporary fallback if the backend is unavailable.

Run backend first, then customer frontend and admin panel. Keep the same backend `.env` and MongoDB connection for both applications.
