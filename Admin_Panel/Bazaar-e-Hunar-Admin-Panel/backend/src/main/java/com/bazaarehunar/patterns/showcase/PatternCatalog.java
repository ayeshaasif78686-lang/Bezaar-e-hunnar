package com.bazaarehunar.patterns.showcase;

import java.util.List;

/** The complete list of all 23 Gang-of-Four patterns implemented in this project. */
public final class PatternCatalog {
    private static final String BASE = "com.bazaarehunar.patterns.";

    private static final List<PatternInfo> ALL = List.of(
            // ---- Creational (5) ----
            new PatternInfo(1, "singleton", "Singleton", "Creational", BASE + "creational.singleton",
                    "MarketplaceSettings: one shared config for currency, shipping fees and thresholds"),
            new PatternInfo(2, "factory-method", "Factory Method", "Creational", BASE + "creational.factorymethod",
                    "ProductCreator subclasses create standard or customizable catalog products"),
            new PatternInfo(3, "abstract-factory", "Abstract Factory", "Creational", BASE + "creational.abstractfactory",
                    "CheckoutFactory creates matching shipping + payment families (domestic / international)"),
            new PatternInfo(4, "builder", "Builder", "Creational", BASE + "creational.builder",
                    "CatalogProductBuilder + Director build the 15-field CatalogProduct step by step"),
            new PatternInfo(5, "prototype", "Prototype", "Creational", BASE + "creational.prototype",
                    "ProductTemplate clones an existing listing so sellers can create variants quickly"),
            // ---- Structural (7) ----
            new PatternInfo(6, "adapter", "Adapter", "Structural", BASE + "structural.adapter",
                    "EasyPaisa/JazzCash legacy clients adapted to one PaymentGateway interface"),
            new PatternInfo(7, "bridge", "Bridge", "Structural", BASE + "structural.bridge",
                    "Notification types (confirmation, shipping) x channels (email, SMS, WhatsApp)"),
            new PatternInfo(8, "composite", "Composite", "Structural", BASE + "structural.composite",
                    "Category tree: categories contain sub-categories and leaf categories"),
            new PatternInfo(9, "decorator", "Decorator", "Structural", BASE + "structural.decorator",
                    "Stackable price options: gift wrap, embroidery, festival discount"),
            new PatternInfo(10, "facade", "Facade", "Structural", BASE + "structural.facade",
                    "CheckoutFacade hides pricing, shipping, payment and notification behind one call"),
            new PatternInfo(11, "flyweight", "Flyweight", "Structural", BASE + "structural.flyweight",
                    "CraftStyleFactory shares one style object per region/material/technique"),
            new PatternInfo(12, "proxy", "Proxy", "Structural", BASE + "structural.proxy",
                    "RoleBasedDashboardProxy guards the real dashboard by user role"),
            // ---- Behavioral (11) ----
            new PatternInfo(13, "chain-of-responsibility", "Chain of Responsibility", "Behavioral",
                    BASE + "behavioral.chainofresponsibility",
                    "Order validation: items -> address -> stock -> payment -> fraud limit"),
            new PatternInfo(14, "command", "Command", "Behavioral", BASE + "behavioral.command",
                    "Add/remove cart actions as objects that can be executed and undone"),
            new PatternInfo(15, "interpreter", "Interpreter", "Behavioral", BASE + "behavioral.interpreter",
                    "Mini search language: category:pottery AND (price<5000 OR rating>=4.5)"),
            new PatternInfo(16, "iterator", "Iterator", "Behavioral", BASE + "behavioral.iterator",
                    "ProductCatalog/ProductIterator traverse products without exposing the list"),
            new PatternInfo(17, "mediator", "Mediator", "Behavioral", BASE + "behavioral.mediator",
                    "HaggleSession routes offers/counter-offers between buyer and seller"),
            new PatternInfo(18, "memento", "Memento", "Behavioral", BASE + "behavioral.memento",
                    "Cart snapshots that can be restored later"),
            new PatternInfo(19, "observer", "Observer", "Behavioral", BASE + "behavioral.observer",
                    "Order status changes notify e-mail, inventory and seller dashboard observers"),
            new PatternInfo(20, "state", "State", "Behavioral", BASE + "behavioral.state",
                    "Order lifecycle PLACED -> PROCESSING -> SHIPPED -> DELIVERED / CANCELLED"),
            new PatternInfo(21, "strategy", "Strategy", "Behavioral", BASE + "behavioral.strategy",
                    "Regular, bulk and coupon pricing strategies for the cart"),
            new PatternInfo(22, "template-method", "Template Method", "Behavioral", BASE + "behavioral.templatemethod",
                    "CatalogReport skeleton with CSV, text and featured-only report variants"),
            new PatternInfo(23, "visitor", "Visitor", "Behavioral", BASE + "behavioral.visitor",
                    "Totals, receipts and preparation time computed over different cart line types")
    );

    private PatternCatalog() { }

    public static List<PatternInfo> all() { return ALL; }

    public static long count(String category) {
        return ALL.stream().filter(p -> p.category().equalsIgnoreCase(category)).count();
    }
}
