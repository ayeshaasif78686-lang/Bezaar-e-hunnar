# Fixes and additions

## Errors fixed
| Problem | Fix |
|---|---|
| Expired / missing token returned **403**, so the panel stayed open and every page showed "Request failed" | Backend now answers **401** (JSON); the panel signs out and shows the login page (`SecurityConfig`, `api.js`) |
| Product image upload failed for any image over **1 MB** ("Unexpected server error") although the form allows 5 MB | `spring.servlet.multipart.max-file-size=6MB`; backend also checks type (PNG/JPG/WEBP) and 5 MB |
| Every unexpected error was swallowed: generic "Unexpected server error" and **nothing in the console** | `GlobalExceptionHandler` now logs the stack trace and returns proper 400/404/405/413/409/502 codes with a readable message (bad JSON, missing parameter, Cloudinary failure ...) |
| A deleted user's token caused a **500** on every request; a **deactivated** user's token kept working until it expired | `JwtAuthenticationFilter` treats both as signed out |
| `GET /api/products?q=...` crashed (NullPointerException) when a product had no artisan / region / material | Null-safe search |
| Products could be saved with an empty name / category / negative price; editing could wipe the image | Validation + keep old values when blank (`CatalogService`) |
| Category rename / delete missed products whose category differed only in letter case (e.g. `Khusa` vs `khusa`) | Case-insensitive matching |
| Categories added in the panel were missing from the storefront category tree | `/api/categories/tree` now includes them |
| Reports crashed on an order without items | Guarded |
| `http://127.0.0.1:5174` was blocked by CORS (only `localhost` was allowed) | Both are allowed |
| Backend only read `.env` when started from inside `backend/` | Also reads `backend/.env` |
| Sidebar "Logout" could be cut off on short screens | Link list scrolls |
| Stray duplicate folder `backend/src/src` and stale `backend/target` | Removed |

## Added (to make the panel complete)
* **Messages** page (Contact-page messages) and **Custom Orders** page (made-to-order requests): search, status tabs, status changes, reply by email, delete (messages).
* Bell menu now also lists **new customers** when "New customer alerts" is on in Settings.
* Low-stock alerts ignore deactivated products.
* `ADMIN_RESET_PASSWORD=true` (in `backend/.env`) resets the admin password to `ADMIN_PASSWORD` on the next start - use it once if you cannot log in, then remove it.

## Important: security
`backend/.env` contains your real MongoDB password and Cloudinary secret. Anyone who has this zip has them.
Rotate them (Atlas -> Database Access, Cloudinary -> API Keys) and never share or commit `.env` (a `.gitignore` is included).
