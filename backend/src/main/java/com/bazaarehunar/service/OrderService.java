package com.bazaarehunar.service;

import com.bazaarehunar.dto.OrderRequest;
import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.model.Order;
import com.bazaarehunar.patterns.behavioral.chainofresponsibility.OrderCheck;
import com.bazaarehunar.patterns.behavioral.chainofresponsibility.OrderValidationChain;
import com.bazaarehunar.patterns.behavioral.chainofresponsibility.OrderValidationHandler;
import com.bazaarehunar.patterns.behavioral.chainofresponsibility.ValidationResult;
import com.bazaarehunar.patterns.behavioral.observer.CustomerEmailObserver;
import com.bazaarehunar.patterns.behavioral.observer.InventoryObserver;
import com.bazaarehunar.patterns.behavioral.observer.OrderStatusSubject;
import com.bazaarehunar.patterns.behavioral.observer.SellerDashboardObserver;
import com.bazaarehunar.patterns.behavioral.state.OrderLifecycle;
import com.bazaarehunar.patterns.behavioral.strategy.DiscountPricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.PricingStrategy;
import com.bazaarehunar.patterns.behavioral.strategy.RegularPricingStrategy;
import com.bazaarehunar.patterns.behavioral.visitor.ReadyMadeLine;
import com.bazaarehunar.patterns.behavioral.visitor.ReceiptVisitor;
import com.bazaarehunar.patterns.behavioral.visitor.TotalPriceVisitor;
import com.bazaarehunar.patterns.creational.abstractfactory.CheckoutFactory;
import com.bazaarehunar.patterns.creational.abstractfactory.DomesticCheckoutFactory;
import com.bazaarehunar.patterns.creational.abstractfactory.ShippingOption;
import com.bazaarehunar.patterns.creational.singleton.MarketplaceSettings;
import com.bazaarehunar.patterns.structural.bridge.EmailChannel;
import com.bazaarehunar.patterns.structural.bridge.OrderConfirmationNotification;
import com.bazaarehunar.patterns.structural.bridge.ShippingUpdateNotification;
import com.bazaarehunar.patterns.structural.bridge.WhatsAppChannel;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/** Customer orders stored in MongoDB (collection "orders"). */
@Service
public class OrderService {
    public static final Set<String> STATUSES = Set.of("PLACED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");

    private final MongoTemplate mongoTemplate;
    private final SecureRandom random = new SecureRandom();

    // ---- Design patterns used by the real order flow (see PATTERNS.md) ----
    /** Chain of Responsibility: items -> address -> stock -> payment -> fraud limit. */
    private final OrderValidationHandler validationChain = OrderValidationChain.standard();
    /** Abstract Factory: matching shipping + payment family (domestic checkout). Fees come from the Singleton. */
    private final CheckoutFactory checkoutFactory = new DomesticCheckoutFactory();
    /** Observer: reacts whenever an order is placed or its status changes. */
    private final OrderStatusSubject statusSubject = new OrderStatusSubject();
    private final CustomerEmailObserver emailObserver = new CustomerEmailObserver();
    private final InventoryObserver inventoryObserver = new InventoryObserver();
    private final SellerDashboardObserver dashboardObserver = new SellerDashboardObserver();
    /** Bridge: last messages sent (notification type x channel), kept in memory for the admin activity view. */
    private final Deque<String> sentMessages = new ArrayDeque<>();

    private final StoreSettingsService storeSettings;

    public OrderService(MongoTemplate mongoTemplate, StoreSettingsService storeSettings) {
        this.mongoTemplate = mongoTemplate;
        this.storeSettings = storeSettings;
        statusSubject.subscribe(emailObserver);
        statusSubject.subscribe(inventoryObserver);
        statusSubject.subscribe(dashboardObserver);
    }

    public Order create(OrderRequest request, AppUser user) {
        Order.Address address = request.getAddress();
        checkPaymentEnabled(request.getPaymentMethod());
        // Chain of Responsibility: validate shape first (total is checked again below once prices are known).
        Map<String, Integer> stock = loadStock(request);
        validate(request, address, 0, stock);

        boolean express = "express".equalsIgnoreCase(request.getDelivery());
        Order order = new Order();
        int subtotal = 0;
        for (OrderRequest.Item requested : request.getItems()) {
            if (isBlank(requested.getProductId())) {
                throw new IllegalArgumentException("Every order item needs a product id");
            }
            if (requested.getQuantity() < 1 || requested.getQuantity() > 100) {
                throw new IllegalArgumentException("Item quantity must be between 1 and 100");
            }
            // The price comes from the catalog in MongoDB whenever the product exists.
            CatalogProduct product = mongoTemplate.findById(requested.getProductId(), CatalogProduct.class);
            int price = product != null ? product.getPrice() : requested.getPrice();
            if (price < 0) {
                throw new IllegalArgumentException("Invalid item price");
            }

            Order.Item item = new Order.Item();
            item.setProductId(requested.getProductId());
            item.setName(product != null ? product.getName() : firstNonBlank(requested.getName(), requested.getProductId()));
            item.setImage(product != null ? product.getImage() : requested.getImage());
            item.setArtisan(product != null ? product.getArtisan() : null);
            item.setArtisanId(product != null ? product.getArtisanId() : null);
            item.setPrice(price);
            item.setQuantity(requested.getQuantity());
            order.getItems().add(item);
            subtotal += price * requested.getQuantity();
        }

        String coupon = request.getCoupon() == null ? "" : request.getCoupon().trim();
        // Abstract Factory + Singleton: shipping option (and its fee) comes from the checkout factory.
        ShippingOption shippingOption = checkoutFactory.createShipping(express);
        int shipping = shippingOption.fee();
        // Strategy: a coupon applies the 10% discount strategy, otherwise the regular (no discount) strategy.
        PricingStrategy pricing = coupon.isEmpty() ? new RegularPricingStrategy() : new DiscountPricingStrategy(10);
        int discount = Math.max(0, subtotal - pricing.calculate(subtotal));
        // Chain of Responsibility again, now with the real total (fraud / manual review limit).
        validate(request, address, subtotal + shipping - discount, stock);

        order.setOrderNumber(newOrderNumber());
        order.setUserId(user.getId());
        order.setUserEmail(user.getEmail());
        order.setAddress(address);
        order.setDelivery(express ? "express" : "standard");
        order.setDeliveryLabel((express ? "Express Delivery, " : "Standard Delivery, ") + shippingOption.estimate());
        order.setEstimatedDelivery(shippingOption.estimate());
        order.setPaymentMethod(request.getPaymentMethod().trim());
        order.setCoupon(coupon.isEmpty() ? null : coupon);
        order.setSubtotal(subtotal);
        order.setShipping(shipping);
        order.setDiscount(discount);
        order.setTax(0);
        order.setTotal(Math.max(0, subtotal + shipping - discount));
        order.setStatus("PLACED");
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(order.getCreatedAt());
        Order saved = mongoTemplate.insert(order);
        adjustStock(saved, -1);
        // Observer + Bridge: tell the observers and send the order confirmation (email channel).
        statusSubject.changeStatus(saved.getOrderNumber(), "PLACED");
        remember(new OrderConfirmationNotification(new EmailChannel(), saved.getOrderNumber(), saved.getTotal())
                .send(saved.getUserEmail()));
        return saved;
    }

    public List<Order> findByUser(String email) {
        Query query = new Query(Criteria.where("userEmail").is(email)).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        return mongoTemplate.find(query, Order.class);
    }

    public List<Order> findAll() {
        return mongoTemplate.find(new Query().with(Sort.by(Sort.Direction.DESC, "createdAt")), Order.class);
    }

    public Optional<Order> findByOrderNumber(String orderNumber) {
        return Optional.ofNullable(mongoTemplate.findOne(
                new Query(Criteria.where("orderNumber").is(orderNumber)), Order.class));
    }

    public Order updateStatus(String orderNumber, String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase();
        if (!STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("Status must be one of " + STATUSES);
        }
        Order order = findByOrderNumber(orderNumber).orElseThrow(() -> new IllegalArgumentException("Order not found"));
        // State: only legal transitions are allowed (PLACED -> PROCESSING -> SHIPPED -> DELIVERED, or CANCELLED).
        OrderLifecycle lifecycle = OrderLifecycle.fromStatus(order.getStatus());
        if (!normalized.equals(lifecycle.status())) {
            try {
                switch (normalized) {
                    case "PROCESSING" -> lifecycle.process();
                    case "SHIPPED" -> lifecycle.ship();
                    case "DELIVERED" -> lifecycle.deliver();
                    case "CANCELLED" -> lifecycle.cancel();
                    default -> throw new IllegalStateException("Cannot move an order back to " + normalized);
                }
            } catch (IllegalStateException ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        }
        String previous = order.getStatus();
        order.setStatus(normalized);
        order.setUpdatedAt(Instant.now());
        Order saved = mongoTemplate.save(order);
        // Cancelling an order puts its items back in stock.
        if ("CANCELLED".equals(normalized) && !"CANCELLED".equals(previous)) adjustStock(saved, 1);
        // Observer: e-mail / inventory / seller-dashboard observers react to the change.
        statusSubject.changeStatus(saved.getOrderNumber(), normalized);
        if ("SHIPPED".equals(normalized)) {
            ShippingOption option = checkoutFactory.createShipping("express".equals(saved.getDelivery()));
            remember(new ShippingUpdateNotification(new WhatsAppChannel(), saved.getOrderNumber(), option.carrier())
                    .send(saved.getAddress() != null ? saved.getAddress().getPhone() : saved.getUserEmail()));
        }
        return saved;
    }

    /** Visitor: receipt and total computed over the order's cart lines. */
    public Map<String, Object> receipt(Order order) {
        ReceiptVisitor receipt = new ReceiptVisitor();
        TotalPriceVisitor total = new TotalPriceVisitor();
        for (Order.Item item : order.getItems()) {
            ReadyMadeLine line = new ReadyMadeLine(item.getName(), item.getPrice(), item.getQuantity());
            line.accept(receipt);
            line.accept(total);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderNumber", order.getOrderNumber());
        result.put("currency", MarketplaceSettings.getInstance().currency());
        result.put("lines", receipt.lines());
        result.put("itemsTotal", total.total());
        result.put("shipping", order.getShipping());
        result.put("discount", order.getDiscount());
        result.put("total", order.getTotal());
        result.put("status", order.getStatus());
        return result;
    }

    /** What the Observer / Bridge patterns have recorded since the server started (admin view). */
    public Map<String, Object> activity() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("statusCounters", dashboardObserver.counters());
        result.put("customerEmails", tail(emailObserver.sentEmails()));
        result.put("restockedOrders", inventoryObserver.restockedOrders());
        synchronized (sentMessages) {
            result.put("messagesSent", List.copyOf(sentMessages));
        }
        return result;
    }

    /** Stock per product id for the Stock handler; products that are not in the database count as unlimited. */
    private Map<String, Integer> loadStock(OrderRequest request) {
        Map<String, Integer> stock = new LinkedHashMap<>();
        stock.put("", Integer.MAX_VALUE);
        if (request.getItems() == null) return stock;
        for (OrderRequest.Item item : request.getItems()) {
            String id = item.getProductId();
            if (id == null || id.isBlank() || stock.containsKey(id)) continue;
            CatalogProduct product = mongoTemplate.findById(id, CatalogProduct.class);
            stock.put(id, product == null ? Integer.MAX_VALUE : (product.isActive() ? product.getStock() : 0));
        }
        return stock;
    }

    /** direction -1 = take items out of stock (new order), +1 = put them back (cancelled order). */
    private void adjustStock(Order order, int direction) {
        for (Order.Item item : order.getItems()) {
            CatalogProduct product = mongoTemplate.findById(item.getProductId(), CatalogProduct.class);
            if (product == null) continue;
            product.setStock(product.getStock() + direction * item.getQuantity());
            mongoTemplate.save(product);
        }
    }

    private void validate(OrderRequest request, Order.Address address, int total, Map<String, Integer> stock) {
        Map<String, Integer> quantities = new LinkedHashMap<>();
        if (request.getItems() != null) {
            for (OrderRequest.Item item : request.getItems()) {
                quantities.merge(item.getProductId() == null ? "" : item.getProductId(), item.getQuantity(), Integer::sum);
            }
        }
        ValidationResult result = validationChain.handle(new OrderCheck(quantities, stock,
                address == null ? null : address.getFullName(), address == null ? null : address.getPhone(),
                address == null ? null : address.getAddress(), address == null ? null : address.getCity(),
                address == null ? null : address.getProvince(), request.getPaymentMethod(), total));
        if (!result.valid()) {
            throw new IllegalArgumentException(result.reason());
        }
    }

    /** Payment methods can be switched off in Admin > Settings > Payment. */
    private void checkPaymentEnabled(String method) {
        if (isBlank(method)) return; // the validation chain reports "Choose a payment method"
        String m = method.toLowerCase();
        boolean allowed;
        if (m.contains("cash")) allowed = storeSettings.getBoolean("payCod");
        else if (m.contains("jazz") || m.contains("easy")) {
            allowed = storeSettings.getBoolean("payJazzCash") || storeSettings.getBoolean("payEasyPaisa");
        } else if (m.contains("card")) allowed = storeSettings.getBoolean("payCard");
        else allowed = true;
        if (!allowed) throw new IllegalArgumentException(method.trim() + " is currently unavailable. Please choose another payment method");
    }

    private void remember(String message) {
        synchronized (sentMessages) {
            sentMessages.addLast(message);
            while (sentMessages.size() > 50) sentMessages.removeFirst();
        }
    }

    private static List<String> tail(List<String> all) {
        return all.size() <= 50 ? all : all.subList(all.size() - 50, all.size());
    }

    private String newOrderNumber() {
        for (int attempt = 0; attempt < 20; attempt++) {
            String number = "BEH" + (10000000 + random.nextInt(90000000));
            if (!mongoTemplate.exists(new Query(Criteria.where("orderNumber").is(number)), Order.class)) {
                return number;
            }
        }
        return "BEH" + System.currentTimeMillis();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String firstNonBlank(String first, String fallback) {
        return isBlank(first) ? fallback : first;
    }
}
