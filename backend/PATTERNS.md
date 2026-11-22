# Design Patterns in Bazaar-e-Hunar (all 23 GoF patterns)

All pattern code lives under `src/main/java/com/bazaarehunar/patterns/<category>/<pattern>/` and every
pattern has a JUnit test under `src/test/java/...` (same package path).
Nothing from the original project was removed or changed; the 15 missing patterns were **added**.

| # | Pattern | Category | Package (under `patterns/`) | Marketplace use |
|---|---------|----------|-----------------------------|-----------------|
| 1 | Singleton | Creational | `creational/singleton` | `MarketplaceSettings` - one shared config (currency, shipping fees, thresholds) |
| 2 | Factory Method | Creational | `creational/factorymethod` | `ProductCreator` -> Standard / Customizable product creators *(existing)* |
| 3 | Abstract Factory | Creational | `creational/abstractfactory` | `CheckoutFactory` -> domestic or international shipping + payment family |
| 4 | Builder | Creational | `creational/builder` | `CatalogProductBuilder` + `CatalogProductDirector` for the 15-field product |
| 5 | Prototype | Creational | `creational/prototype` | `ProductTemplate` / registry clone a listing to make variants |
| 6 | Adapter | Structural | `structural/adapter` | EasyPaisa & JazzCash legacy clients adapted to `PaymentGateway` |
| 7 | Bridge | Structural | `structural/bridge` | Notification type x channel (Email / SMS / WhatsApp) |
| 8 | Composite | Structural | `structural/composite` | Category tree *(existing)* |
| 9 | Decorator | Structural | `structural/decorator` | Stackable price options: gift wrap, embroidery, festival discount |
| 10 | Facade | Structural | `structural/facade` | `CheckoutFacade` - one call for pricing + shipping + payment + notification |
| 11 | Flyweight | Structural | `structural/flyweight` | `CraftStyleFactory` shares style objects *(existing)* |
| 12 | Proxy | Structural | `structural/proxy` | `RoleBasedDashboardProxy` guards dashboards by role *(existing)* |
| 13 | Chain of Responsibility | Behavioral | `behavioral/chainofresponsibility` | Order validation chain (items -> address -> stock -> payment -> fraud limit) |
| 14 | Command | Behavioral | `behavioral/command` | Add/remove cart actions with undo *(existing)* |
| 15 | Interpreter | Behavioral | `behavioral/interpreter` | Search language `category:pottery AND (price<5000 OR rating>=4.5)` |
| 16 | Iterator | Behavioral | `behavioral/iterator` | `ProductCatalog` / `ProductIterator` *(existing)* |
| 17 | Mediator | Behavioral | `behavioral/mediator` | `HaggleSession` between buyer and seller |
| 18 | Memento | Behavioral | `behavioral/memento` | Cart snapshots *(existing)* |
| 19 | Observer | Behavioral | `behavioral/observer` | Order status -> e-mail, inventory, seller dashboard observers |
| 20 | State | Behavioral | `behavioral/state` | Order lifecycle PLACED -> PROCESSING -> SHIPPED -> DELIVERED / CANCELLED |
| 21 | Strategy | Behavioral | `behavioral/strategy` | Regular / bulk / coupon pricing *(existing)* |
| 22 | Template Method | Behavioral | `behavioral/templatemethod` | `CatalogReport` skeleton: CSV, text, featured-only variants |
| 23 | Visitor | Behavioral | `behavioral/visitor` | Total, receipt and preparation-time visitors over cart line types |

Totals: **5 creational + 7 structural + 11 behavioral = 23**.

## Try them (public endpoints, no login needed)

| Endpoint | Returns |
|----------|---------|
| `GET /api/patterns` | list of all 23 patterns (category, package, purpose) |
| `GET /api/patterns/demo` | runs the demo of all 23 patterns |
| `GET /api/patterns/demo/{key}` | runs one demo, e.g. `/api/patterns/demo/observer` |
| `GET /api/patterns/cart-demo` | the original command + memento + strategy demo (unchanged) |

Keys: `singleton, factory-method, abstract-factory, builder, prototype, adapter, bridge, composite, decorator,
facade, flyweight, proxy, chain-of-responsibility, command, interpreter, iterator, mediator, memento, observer,
state, strategy, template-method, visitor`.

The demos use in-memory sample data only (no MongoDB access). Showcase code:
`patterns/showcase/PatternCatalog.java`, `patterns/showcase/PatternDemos.java`, `controller/PatternShowcaseController.java`.

## How the new patterns work together

`CheckoutFacade` (Facade) uses `CheckoutFactory` (Abstract Factory) for shipping, a `PaymentGateway` (Adapter) for
payment, `MarketplaceSettings` (Singleton) for fees, the existing `PricingStrategy` classes (Strategy) for discounts,
and a `Notification` + `MessageChannel` (Bridge) for the confirmation message. Items can be priced with Decorators.

## Where each pattern is used in the REAL app (not only in `/api/patterns/demo`)

| Pattern | Real feature | Where |
|---------|--------------|-------|
| Singleton | shipping fees / currency for every order and receipt | `OrderService` via `MarketplaceSettings` |
| Abstract Factory | shipping option (carrier, fee, estimate) chosen at checkout | `OrderService.create` -> `DomesticCheckoutFactory` |
| Factory Method | creating catalog products (standard / customizable) | `CatalogService.create` |
| Prototype | `POST /api/products/{id}/clone` duplicates a listing (seller/admin) | `CatalogService.cloneProduct` |
| Composite | category tree `GET /api/categories/tree` | `CatalogService` |
| Flyweight | shared craft-style objects for products | `CatalogService` |
| Proxy | role-based dashboards | `DashboardController` |
| Iterator | product search / advanced search traversal | `CatalogService.search` |
| Chain of Responsibility | order validation (items -> address -> stock -> payment -> fraud limit) | `OrderService.create` |
| Strategy | coupon = 10% discount strategy, else regular pricing | `OrderService.create`, `CartPatternController` |
| State | legal order status transitions (admin `PATCH /api/orders/{n}/status`) | `OrderService.updateStatus` |
| Observer | e-mail / inventory / seller-dashboard observers on order placed & status change; `GET /api/orders/activity` (admin) | `OrderService` |
| Bridge | order-confirmation (e-mail) and shipping-update (WhatsApp) messages | `OrderService` |
| Visitor | `GET /api/orders/{n}/receipt` receipt lines + total | `OrderService.receipt` |
| Interpreter | `GET /api/products/advanced-search?q=category:pottery AND price<5000` | `CatalogService.advancedSearch` |
| Template Method | `GET /api/products/report?format=csv|text|featured` | `CatalogService.report` |
| Command / Memento | cart undo + snapshots | `CartPatternController` |

Still demo-only (implemented + unit-tested, run via `/api/patterns/demo/{key}`, not yet used by a live feature):
Builder, Adapter, Decorator, Facade, Mediator.

Behaviour notes: order statuses can now only move forward (PLACED -> PROCESSING -> SHIPPED -> DELIVERED, or
CANCELLED from PLACED/PROCESSING); orders above PKR 500,000 are rejected for manual review; stock is not tracked in
the catalog, so the Stock handler treats requested quantities as available. Observer/Bridge logs are in memory only.

## Run the tests

```
mvn test
```
