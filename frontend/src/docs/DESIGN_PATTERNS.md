# Bazaar-e-Hunar - Design Patterns Documentation

All 23 Gang-of-Four patterns are implemented **in the backend only** (`com.bazaarehunar.patterns.*`).
There is no patterns page in the frontend; this file is the documentation. Details: `backend/PATTERNS.md`.

- `GET /api/patterns` - list of the 23 patterns
- `GET /api/patterns/demo` and `/api/patterns/demo/{key}` - run a pattern demo on the server (try in Postman/browser)
- Real features using them: orders/checkout (Chain, Singleton, Abstract Factory, Strategy, State, Observer, Bridge,
  Visitor), catalog (Factory Method, Prototype, Composite, Flyweight, Iterator, Interpreter, Template Method),
  dashboards (Proxy), cart (Command, Memento, Strategy).

Total **23** = 5 Creational + 7 Structural + 11 Behavioral.

## Creational (5)

| # | Pattern | Package | Marketplace use |
|---|---------|---------|-----------------|
| 1 | Singleton | `creational/singleton` | `MarketplaceSettings` - one shared config (currency, shipping fees, thresholds) |
| 2 | Factory Method | `creational/factorymethod` | `ProductCreator` -> Standard / Customizable product creators *(existing)* |
| 3 | Abstract Factory | `creational/abstractfactory` | `CheckoutFactory` -> domestic or international shipping + payment family |
| 4 | Builder | `creational/builder` | `CatalogProductBuilder` + `CatalogProductDirector` for the 15-field product |
| 5 | Prototype | `creational/prototype` | `ProductTemplate` / registry clone a listing to make variants |

## Structural (7)

| # | Pattern | Package | Marketplace use |
|---|---------|---------|-----------------|
| 6 | Adapter | `structural/adapter` | EasyPaisa & JazzCash legacy clients adapted to `PaymentGateway` |
| 7 | Bridge | `structural/bridge` | Notification type x channel (Email / SMS / WhatsApp) |
| 8 | Composite | `structural/composite` | Category tree *(existing)* |
| 9 | Decorator | `structural/decorator` | Stackable price options: gift wrap, embroidery, festival discount |
| 10 | Facade | `structural/facade` | `CheckoutFacade` - one call for pricing + shipping + payment + notification |
| 11 | Flyweight | `structural/flyweight` | `CraftStyleFactory` shares style objects *(existing)* |
| 12 | Proxy | `structural/proxy` | `RoleBasedDashboardProxy` guards dashboards by role *(existing)* |

## Behavioral (11)

| # | Pattern | Package | Marketplace use |
|---|---------|---------|-----------------|
| 13 | Chain of Responsibility | `behavioral/chainofresponsibility` | Order validation chain (items -> address -> stock -> payment -> fraud limit) |
| 14 | Command | `behavioral/command` | Add/remove cart actions with undo *(existing)* |
| 15 | Interpreter | `behavioral/interpreter` | Search language `category:pottery AND (price<5000 OR rating>=4.5)` |
| 16 | Iterator | `behavioral/iterator` | `ProductCatalog` / `ProductIterator` *(existing)* |
| 17 | Mediator | `behavioral/mediator` | `HaggleSession` between buyer and seller |
| 18 | Memento | `behavioral/memento` | Cart snapshots *(existing)* |
| 19 | Observer | `behavioral/observer` | Order status -> e-mail, inventory, seller dashboard observers |
| 20 | State | `behavioral/state` | Order lifecycle PLACED -> PROCESSING -> SHIPPED -> DELIVERED / CANCELLED |
| 21 | Strategy | `behavioral/strategy` | Regular / bulk / coupon pricing *(existing)* |
| 22 | Template Method | `behavioral/templatemethod` | `CatalogReport` skeleton: CSV, text, featured-only variants |
| 23 | Visitor | `behavioral/visitor` | Total, receipt and preparation-time visitors over cart line types |
