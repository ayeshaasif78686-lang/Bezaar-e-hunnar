package com.bazaarehunar.patterns.showcase;

import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.model.Role;
import com.bazaarehunar.patterns.behavioral.chainofresponsibility.OrderCheck;
import com.bazaarehunar.patterns.behavioral.chainofresponsibility.OrderValidationChain;
import com.bazaarehunar.patterns.behavioral.chainofresponsibility.ValidationResult;
import com.bazaarehunar.patterns.behavioral.command.AddToCartCommand;
import com.bazaarehunar.patterns.behavioral.command.CartReceiver;
import com.bazaarehunar.patterns.behavioral.command.RemoveFromCartCommand;
import com.bazaarehunar.patterns.behavioral.interpreter.SearchQueryParser;
import com.bazaarehunar.patterns.behavioral.iterator.ProductCatalog;
import com.bazaarehunar.patterns.behavioral.iterator.ProductIterator;
import com.bazaarehunar.patterns.behavioral.mediator.BuyerParticipant;
import com.bazaarehunar.patterns.behavioral.mediator.HaggleSession;
import com.bazaarehunar.patterns.behavioral.mediator.SellerParticipant;
import com.bazaarehunar.patterns.behavioral.memento.CartHistory;
import com.bazaarehunar.patterns.behavioral.memento.CartOriginator;
import com.bazaarehunar.patterns.behavioral.observer.CustomerEmailObserver;
import com.bazaarehunar.patterns.behavioral.observer.InventoryObserver;
import com.bazaarehunar.patterns.behavioral.observer.OrderStatusSubject;
import com.bazaarehunar.patterns.behavioral.observer.SellerDashboardObserver;
import com.bazaarehunar.patterns.behavioral.state.OrderLifecycle;
import com.bazaarehunar.patterns.behavioral.strategy.BulkPricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.DiscountPricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.PricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.RegularPricingStrategy;
import com.bazaarehunar.patterns.behavioral.templatemethod.CsvCatalogReport;
import com.bazaarehunar.patterns.behavioral.templatemethod.FeaturedCatalogReport;
import com.bazaarehunar.patterns.behavioral.templatemethod.TextCatalogReport;
import com.bazaarehunar.patterns.behavioral.visitor.CartLine;
import com.bazaarehunar.patterns.behavioral.visitor.CustomMadeLine;
import com.bazaarehunar.patterns.behavioral.visitor.GiftBoxLine;
import com.bazaarehunar.patterns.behavioral.visitor.PreparationTimeVisitor;
import com.bazaarehunar.patterns.behavioral.visitor.ReadyMadeLine;
import com.bazaarehunar.patterns.behavioral.visitor.ReceiptVisitor;
import com.bazaarehunar.patterns.behavioral.visitor.TotalPriceVisitor;
import com.bazaarehunar.patterns.creational.abstractfactory.CheckoutFactory;
import com.bazaarehunar.patterns.creational.builder.CatalogProductBuilder;
import com.bazaarehunar.patterns.creational.builder.CatalogProductDirector;
import com.bazaarehunar.patterns.creational.factorymethod.CustomizableProductCreator;
import com.bazaarehunar.patterns.creational.factorymethod.ProductCreator;
import com.bazaarehunar.patterns.creational.factorymethod.StandardProductCreator;
import com.bazaarehunar.patterns.creational.prototype.ProductTemplateRegistry;
import com.bazaarehunar.patterns.creational.singleton.MarketplaceSettings;
import com.bazaarehunar.patterns.structural.adapter.EasyPaisaGatewayAdapter;
import com.bazaarehunar.patterns.structural.adapter.JazzCashGatewayAdapter;
import com.bazaarehunar.patterns.structural.adapter.LegacyEasyPaisaClient;
import com.bazaarehunar.patterns.structural.adapter.LegacyJazzCashSdk;
import com.bazaarehunar.patterns.structural.adapter.PaymentGateway;
import com.bazaarehunar.patterns.structural.bridge.EmailChannel;
import com.bazaarehunar.patterns.structural.bridge.OrderConfirmationNotification;
import com.bazaarehunar.patterns.structural.bridge.ShippingUpdateNotification;
import com.bazaarehunar.patterns.structural.bridge.SmsChannel;
import com.bazaarehunar.patterns.structural.bridge.WhatsAppChannel;
import com.bazaarehunar.patterns.structural.composite.CategoryComposite;
import com.bazaarehunar.patterns.structural.composite.CategoryLeaf;
import com.bazaarehunar.patterns.structural.decorator.BasePricedItem;
import com.bazaarehunar.patterns.structural.decorator.EmbroideryDecorator;
import com.bazaarehunar.patterns.structural.decorator.FestivalDiscountDecorator;
import com.bazaarehunar.patterns.structural.decorator.GiftWrapDecorator;
import com.bazaarehunar.patterns.structural.decorator.PricedItem;
import com.bazaarehunar.patterns.structural.facade.CheckoutFacade;
import com.bazaarehunar.patterns.structural.flyweight.CraftStyleFactory;
import com.bazaarehunar.patterns.structural.flyweight.CraftStyleFlyweight;
import com.bazaarehunar.patterns.structural.proxy.RealDashboardService;
import com.bazaarehunar.patterns.structural.proxy.RoleBasedDashboardProxy;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Runs a tiny, self-contained example of each of the 23 patterns and returns the result as plain data, so
 * the behaviour of every pattern can be shown from one endpoint (GET /api/patterns/demo/{key}).
 * The demos use in-memory sample data only; they never read or write the database.
 */
public final class PatternDemos {
    private static final Map<String, Supplier<Map<String, Object>>> DEMOS = new LinkedHashMap<>();

    static {
        DEMOS.put("singleton", PatternDemos::singleton);
        DEMOS.put("factory-method", PatternDemos::factoryMethod);
        DEMOS.put("abstract-factory", PatternDemos::abstractFactory);
        DEMOS.put("builder", PatternDemos::builder);
        DEMOS.put("prototype", PatternDemos::prototype);
        DEMOS.put("adapter", PatternDemos::adapter);
        DEMOS.put("bridge", PatternDemos::bridge);
        DEMOS.put("composite", PatternDemos::composite);
        DEMOS.put("decorator", PatternDemos::decorator);
        DEMOS.put("facade", PatternDemos::facade);
        DEMOS.put("flyweight", PatternDemos::flyweight);
        DEMOS.put("proxy", PatternDemos::proxy);
        DEMOS.put("chain-of-responsibility", PatternDemos::chain);
        DEMOS.put("command", PatternDemos::command);
        DEMOS.put("interpreter", PatternDemos::interpreter);
        DEMOS.put("iterator", PatternDemos::iterator);
        DEMOS.put("mediator", PatternDemos::mediator);
        DEMOS.put("memento", PatternDemos::memento);
        DEMOS.put("observer", PatternDemos::observer);
        DEMOS.put("state", PatternDemos::state);
        DEMOS.put("strategy", PatternDemos::strategy);
        DEMOS.put("template-method", PatternDemos::templateMethod);
        DEMOS.put("visitor", PatternDemos::visitor);
    }

    private PatternDemos() { }

    public static boolean exists(String key) { return DEMOS.containsKey(key); }

    public static Map<String, Object> run(String key) { return DEMOS.get(key).get(); }

    public static Map<String, Object> runAll() {
        Map<String, Object> all = new LinkedHashMap<>();
        DEMOS.forEach((key, demo) -> all.put(key, demo.get()));
        return all;
    }

    // ------------------------------------------------------------------ sample data

    private static List<CatalogProduct> sampleProducts() {
        CatalogProductDirector director = new CatalogProductDirector(new CatalogProductBuilder());
        List<CatalogProduct> products = new ArrayList<>();
        products.add(new CatalogProductDirector(new CatalogProductBuilder())
                .constructHeritage("rilli-1", "Sindhi Rilli Quilt", "Textiles", 9500, "Sana", "a-sana", "Sindh"));
        products.add(new CatalogProductDirector(new CatalogProductBuilder())
                .constructHandmade("vase-1", "Blue Pottery Vase", "Pottery", 3200, "Bilal", "a-bilal"));
        products.add(director.constructCustomizable("cup-1", "Custom Clay Mug", "Pottery", 900, "Bilal", "a-bilal"));
        return products;
    }

    private static Map<String, Object> map(Object... keyValues) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) result.put((String) keyValues[i], keyValues[i + 1]);
        return result;
    }

    private static CheckoutFacade sampleCheckout() {
        return new CheckoutFacade(CheckoutFactory.forCountry("Pakistan"),
                new EasyPaisaGatewayAdapter(new LegacyEasyPaisaClient()), new SmsChannel());
    }

    // ------------------------------------------------------------------ creational

    private static Map<String, Object> singleton() {
        boolean same = MarketplaceSettings.getInstance() == MarketplaceSettings.getInstance();
        MarketplaceSettings s = MarketplaceSettings.getInstance();
        return map("sameInstance", same, "currency", s.currency(), "freeShippingThreshold", s.freeShippingThreshold(),
                "standardShippingFee", s.standardShippingFee(), "expressShippingFee", s.expressShippingFee());
    }

    private static Map<String, Object> factoryMethod() {
        CraftStyleFlyweight style = new CraftStyleFactory().getStyle("Multan", "Clay", "Blue glaze");
        ProductCreator standard = new StandardProductCreator();
        ProductCreator custom = new CustomizableProductCreator();
        CatalogProduct a = standard.create("f1", "Blue Plate", "Pottery", 1200, 4.5, 10, "Handmade", "", "Bilal", "a-bilal", "Plate", style);
        CatalogProduct b = custom.create("f2", "Custom Plate", "Pottery", 1500, 4.6, 5, "Custom", "", "Bilal", "a-bilal", "Plate", style);
        return map("standardCustomizable", a.isCustomizable(), "customCreatorCustomizable", b.isCustomizable(),
                "region", a.getRegion());
    }

    private static Map<String, Object> abstractFactory() {
        CheckoutFactory domestic = CheckoutFactory.forCountry("Pakistan");
        CheckoutFactory international = CheckoutFactory.forCountry("Germany");
        return map(
                "domestic", map("region", domestic.region(), "shipping", domestic.createShipping(true),
                        "payment", domestic.createPayment().name()),
                "international", map("region", international.region(), "shipping", international.createShipping(true),
                        "payment", international.createPayment().name()));
    }

    private static Map<String, Object> builder() {
        CatalogProduct product = new CatalogProductBuilder().id("b1").name("Ajrak Shawl").category("Textiles")
                .price(4500).artisan("Ayesha", "a-ayesha").region("Sindh").material("Cotton").rating(4.8).build();
        String error;
        try {
            new CatalogProductBuilder().name("Incomplete").build();
            error = "none";
        } catch (IllegalStateException ex) {
            error = ex.getMessage();
        }
        return map("built", product, "validationOnIncompleteBuilder", error);
    }

    private static Map<String, Object> prototype() {
        ProductTemplateRegistry registry = new ProductTemplateRegistry();
        registry.register("shawl", new CatalogProductBuilder().id("shawl-1").name("Ajrak Shawl").category("Textiles")
                .price(4500).artisan("Ayesha", "a-ayesha").region("Sindh").material("Cotton").build());
        CatalogProduct red = registry.create("shawl", "shawl-2", "Ajrak Shawl (Red)");
        CatalogProduct blue = registry.create("shawl", "shawl-3", "Ajrak Shawl (Blue)");
        return map("templates", registry.keys(), "clones", List.of(red, blue));
    }

    // ------------------------------------------------------------------ structural

    private static Map<String, Object> adapter() {
        PaymentGateway easy = new EasyPaisaGatewayAdapter(new LegacyEasyPaisaClient());
        PaymentGateway jazz = new JazzCashGatewayAdapter(new LegacyJazzCashSdk());
        return map("easyPaisa", easy.charge("03001234567", 1500), "jazzCash", jazz.charge("03211234567", 1500),
                "refused", easy.charge("123", 1500));
    }

    private static Map<String, Object> bridge() {
        return map("email", new OrderConfirmationNotification(new EmailChannel(), "BEH100", 4200).send("ali@example.com"),
                "sms", new OrderConfirmationNotification(new SmsChannel(), "BEH100", 4200).send("03001234567"),
                "whatsapp", new ShippingUpdateNotification(new WhatsAppChannel(), "BEH100", "TCS").send("03001234567"));
    }

    private static Map<String, Object> composite() {
        CategoryComposite root = new CategoryComposite("Marketplace")
                .add(new CategoryComposite("Textiles").add(new CategoryLeaf("Shawls")).add(new CategoryLeaf("Rilli")))
                .add(new CategoryComposite("Pottery").add(new CategoryLeaf("Vases")));
        List<String> lines = new ArrayList<>();
        walk(root, 0, lines);
        return map("tree", lines);
    }

    private static void walk(com.bazaarehunar.patterns.structural.composite.CategoryComponent node, int depth, List<String> out) {
        out.add("  ".repeat(depth) + node.name() + (node.leaf() ? "" : "/"));
        node.children().forEach(child -> walk(child, depth + 1, out));
    }

    private static Map<String, Object> decorator() {
        PricedItem plain = new BasePricedItem("Ajrak Shawl", 4000);
        PricedItem fancy = new FestivalDiscountDecorator(
                new GiftWrapDecorator(new EmbroideryDecorator(plain, "Ali")), 10);
        return map("plain", map("description", plain.description(), "price", plain.price()),
                "decorated", map("description", fancy.description(), "price", fancy.price()));
    }

    private static Map<String, Object> facade() {
        List<PricedItem> items = List.of(new GiftWrapDecorator(new BasePricedItem("Blue Vase", 3200)),
                new BasePricedItem("Clay Mug", 900));
        return map("checkout", sampleCheckout().checkout("BEH-DEMO-1", "03001234567", items, "HUNAR10", false));
    }

    private static Map<String, Object> flyweight() {
        CraftStyleFactory factory = new CraftStyleFactory();
        CraftStyleFlyweight a = factory.getStyle("Sindh", "Cotton", "Ajrak");
        CraftStyleFlyweight b = factory.getStyle("Sindh", "Cotton", "Ajrak");
        factory.getStyle("Multan", "Clay", "Blue glaze");
        return map("sameObjectShared", a == b, "cachedStyles", factory.cachedStyleCount());
    }

    private static Map<String, Object> proxy() {
        RoleBasedDashboardProxy proxy = new RoleBasedDashboardProxy(new RealDashboardService());
        AppUser buyer = new AppUser("Sara", "sara@example.com", "x", Role.BUYER);
        String blocked;
        try {
            proxy.openDashboard(buyer, "admin");
            blocked = "allowed";
        } catch (ResponseStatusException ex) {
            blocked = ex.getReason();
        }
        return map("ownDashboard", proxy.openDashboard(buyer, "buyer"), "adminDashboardAsBuyer", blocked);
    }

    // ------------------------------------------------------------------ behavioral

    private static Map<String, Object> chain() {
        var handler = OrderValidationChain.standard();
        Map<String, Integer> stock = Map.of("rug", 3);
        ValidationResult good = handler.handle(new OrderCheck(Map.of("rug", 1), stock, "Ali", "0300", "House 1",
                "Lahore", "Punjab", "COD", 4500));
        ValidationResult noStock = handler.handle(new OrderCheck(Map.of("rug", 9), stock, "Ali", "0300", "House 1",
                "Lahore", "Punjab", "COD", 4500));
        ValidationResult noAddress = handler.handle(new OrderCheck(Map.of("rug", 1), stock, "Ali", "0300", "",
                "Lahore", "Punjab", "COD", 4500));
        return map("validOrder", good, "outOfStock", noStock, "missingAddress", noAddress);
    }

    private static Map<String, Object> command() {
        CartReceiver cart = new CartReceiver();
        AddToCartCommand add = new AddToCartCommand(cart, "handwoven-rug", 2);
        add.execute();
        Map<String, Integer> afterAdd = cart.snapshot();
        add.undo();
        Map<String, Integer> afterUndoAdd = cart.snapshot();
        RemoveFromCartCommand remove = new RemoveFromCartCommand(cart, "blue-vase");
        cart.add("blue-vase", 1);
        remove.execute();
        Map<String, Integer> afterRemove = cart.snapshot();
        remove.undo();
        return map("afterAdd", afterAdd, "afterUndoAdd", afterUndoAdd, "afterRemove", afterRemove,
                "afterUndoRemove", cart.snapshot());
    }

    private static Map<String, Object> interpreter() {
        SearchQueryParser parser = new SearchQueryParser();
        String query = "category:pottery AND (price<5000 OR rating>=4.5) AND NOT name:custom";
        var expression = parser.parse(query);
        List<String> matches = sampleProducts().stream().filter(expression::interpret).map(CatalogProduct::getName).toList();
        return map("query", query, "matches", matches);
    }

    private static Map<String, Object> iterator() {
        ProductIterator it = new ProductCatalog(sampleProducts()).iterator();
        List<String> names = new ArrayList<>();
        while (it.hasNext()) names.add(it.next().getName());
        return map("visited", names);
    }

    private static Map<String, Object> mediator() {
        BuyerParticipant buyer = new BuyerParticipant("Sara");
        SellerParticipant seller = new SellerParticipant("Bilal", 4000);
        HaggleSession session = new HaggleSession("Handwoven Rug", 5000, buyer, seller);
        buyer.offer(3600);
        buyer.acceptCounter();
        return map("status", session.status(), "agreedPrice", session.agreedPrice(), "transcript", session.transcript());
    }

    private static Map<String, Object> memento() {
        CartReceiver cart = new CartReceiver();
        CartOriginator originator = new CartOriginator(cart);
        CartHistory history = new CartHistory();
        cart.add("rug", 1);
        history.push(originator.save());
        cart.add("vase", 3);
        Map<String, Integer> before = cart.snapshot();
        originator.restore(history.pop());
        return map("beforeRestore", before, "afterRestore", cart.snapshot());
    }

    private static Map<String, Object> observer() {
        OrderStatusSubject subject = new OrderStatusSubject();
        CustomerEmailObserver email = new CustomerEmailObserver();
        InventoryObserver inventory = new InventoryObserver();
        SellerDashboardObserver dashboard = new SellerDashboardObserver();
        subject.subscribe(email);
        subject.subscribe(inventory);
        subject.subscribe(dashboard);
        subject.changeStatus("BEH1", "PLACED");
        subject.changeStatus("BEH2", "PLACED");
        subject.changeStatus("BEH1", "CANCELLED");
        return map("emails", email.sentEmails(), "restockedOrders", inventory.restockedOrders(),
                "dashboardCounters", dashboard.counters());
    }

    private static Map<String, Object> state() {
        OrderLifecycle order = new OrderLifecycle().process().ship();
        String illegal;
        try {
            order.cancel();
            illegal = "allowed";
        } catch (IllegalStateException ex) {
            illegal = ex.getMessage();
        }
        order.deliver();
        return map("history", order.history(), "finished", order.finished(), "cancelAfterShipping", illegal);
    }

    private static Map<String, Object> strategy() {
        List<PricingStrategy> strategies = List.of(new RegularPricingStrategy(), new DiscountPricingStrategy(10),
                new BulkPricingStrategy());
        Map<String, Object> results = new LinkedHashMap<>();
        for (PricingStrategy s : strategies) results.put(s.name(), s.calculate(10000));
        return map("subtotal", 10000, "totals", results);
    }

    private static Map<String, Object> templateMethod() {
        List<CatalogProduct> products = sampleProducts();
        return map("csv", new CsvCatalogReport().generate(products),
                "text", new TextCatalogReport().generate(products),
                "featured", new FeaturedCatalogReport().generate(products));
    }

    private static Map<String, Object> visitor() {
        List<CartLine> cart = List.of(new ReadyMadeLine("Clay Mug", 900, 2),
                new CustomMadeLine("Ajrak Shawl", 4000, 1, 500, 7), new GiftBoxLine("Festive box", 300, 1));
        TotalPriceVisitor total = new TotalPriceVisitor();
        PreparationTimeVisitor days = new PreparationTimeVisitor();
        ReceiptVisitor receipt = new ReceiptVisitor();
        for (CartLine line : cart) {
            line.accept(total);
            line.accept(days);
            line.accept(receipt);
        }
        return map("total", total.total(), "preparationDays", days.days(), "receipt", receipt.lines());
    }
}
