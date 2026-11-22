# Bazaar-e-Hunar Design Pattern Implementation

The project now contains eight explicit GoF patterns. Every pattern is implemented as real Java participants rather than relying on framework internals.

## 1. Proxy Pattern, Access Control
Package: `patterns.structural.proxy`

Participants:
- `DashboardAccess`
- `RealDashboardService`
- `RoleBasedDashboardProxy`

Use: protects Buyer, Seller and Admin dashboard access before delegating to the real dashboard service.

## 2. Factory Method, Catalog Product Creation
Package: `patterns.creational.factorymethod`

Participants:
- `ProductCreator`
- `StandardProductCreator`
- `CustomizableProductCreator`

Use: chooses the correct creation path for normal and customizable marketplace products.

Proof: `ProductCreatorTest` confirms the created product type has the expected customization behavior.

## 3. Composite, Category Tree
Package: `patterns.structural.composite`

Participants:
- `CategoryComponent`
- `CategoryComposite`
- `CategoryLeaf`

Use: represents nested marketplace categories and subcategories as one uniform tree.

Live API: `GET /api/categories/tree`

## 4. Flyweight, Shared Craft Style Metadata
Package: `patterns.structural.flyweight`

Participants:
- `CraftStyleFlyweight`
- `CraftStyleFactory`

Use: products sharing the same region, material and technique reuse one craft-style object in memory.

Proof: `CraftStyleFactoryTest` uses `assertSame` for identical style requests.

## 5. Iterator, Catalog Traversal
Package: `patterns.behavioral.iterator`

Participants:
- `ProductCatalog`
- `ProductIterator`

Use: `CatalogService` traverses products without exposing the catalog's internal collection.

Live API: `GET /api/products?q=pottery`

## 6. Strategy, Cart Pricing
Package: `patterns.behavioral.strategy`

Participants:
- `PricingStrategy`
- `RegularPricingStrategy`
- `DiscountPricingStrategy`
- `BulkPricingStrategy`

Use: cart totals can switch between regular, coupon and bulk pricing at runtime.

Live API: `POST /api/cart/preview`

Example JSON:
```json
{"subtotal": 10000, "coupon": "HUNAR10"}
```

## 7. Command, Cart Actions
Package: `patterns.behavioral.command`

Participants:
- `Command`
- `CartReceiver`
- `AddToCartCommand`
- `RemoveFromCartCommand`

Use: cart mutations are represented as executable objects with undo support.

## 8. Memento, Cart Snapshot/Restore
Package: `patterns.behavioral.memento`

Participants:
- `CartMemento`
- `CartOriginator`
- `CartHistory`

Use: captures and restores a cart state without exposing internal cart state.

Live pattern demo: `GET /api/patterns/cart-demo`

## Catalog pattern demo
`GET /api/patterns/sprint2-demo`

This endpoint reports the Factory Method, Composite, Flyweight and Iterator implementation used by the catalog.

## Testing
Each newly added pattern has a JUnit test under `backend/src/test/java/com/bazaarehunar/patterns/`.
