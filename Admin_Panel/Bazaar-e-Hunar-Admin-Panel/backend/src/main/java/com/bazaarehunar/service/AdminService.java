package com.bazaarehunar.service;

import com.bazaarehunar.model.AppUser;
import com.bazaarehunar.model.CatalogProduct;
import com.bazaarehunar.model.Category;
import com.bazaarehunar.model.ContactMessage;
import com.bazaarehunar.model.CustomOrderRequest;
import com.bazaarehunar.model.Order;
import com.bazaarehunar.model.Role;
import com.bazaarehunar.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/** Data and actions behind the admin panel (/api/admin/**). Every method is admin-only (checked in the controller). */
@Service
public class AdminService {
    private static final ZoneId ZONE = ZoneId.systemDefault();
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);

    private final MongoTemplate mongo;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreSettingsService settings;

    public AdminService(MongoTemplate mongo, UserRepository userRepository, PasswordEncoder passwordEncoder,
                        StoreSettingsService settings) {
        this.mongo = mongo;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.settings = settings;
    }

    // ------------------------------------------------------------------ dashboard
    public Map<String, Object> dashboard() {
        List<Order> orders = mongo.findAll(Order.class);
        List<AppUser> users = userRepository.findAll();
        long products = mongo.count(new Query(), CatalogProduct.class);
        Instant now = Instant.now();
        Instant d30 = now.minusSeconds(30L * 86400);
        Instant d60 = now.minusSeconds(60L * 86400);

        long sales = revenue(orders);
        long salesNow = revenue(orders.stream().filter(o -> after(o.getCreatedAt(), d30)).toList());
        long salesPrev = revenue(orders.stream().filter(o -> after(o.getCreatedAt(), d60) && !after(o.getCreatedAt(), d30)).toList());
        long ordersNow = orders.stream().filter(o -> after(o.getCreatedAt(), d30)).count();
        long ordersPrev = orders.stream().filter(o -> after(o.getCreatedAt(), d60) && !after(o.getCreatedAt(), d30)).count();
        long usersNow = users.stream().filter(u -> after(u.getCreatedAt(), d30)).count();
        long usersPrev = users.stream().filter(u -> after(u.getCreatedAt(), d60) && !after(u.getCreatedAt(), d30)).count();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalOrders", orders.size());
        result.put("totalUsers", users.stream().filter(u -> u.getRole() == Role.BUYER).count());
        result.put("totalProducts", products);
        result.put("totalSales", sales);
        result.put("ordersGrowth", growth(ordersNow, ordersPrev));
        result.put("usersGrowth", growth(usersNow, usersPrev));
        result.put("salesGrowth", growth(salesNow, salesPrev));
        result.put("salesSeries", dailySeries(orders, 30));
        result.put("statusCounts", statusCounts(orders));
        result.put("recentOrders", orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5).map(this::orderSummary).toList());
        result.put("lowStock", lowStock());
        return result;
    }

    private List<Map<String, Object>> dailySeries(List<Order> orders, int days) {
        Map<LocalDate, Long> byDay = new TreeMap<>();
        LocalDate today = LocalDate.now(ZONE);
        for (int i = days - 1; i >= 0; i--) byDay.put(today.minusDays(i), 0L);
        for (Order order : orders) {
            if (order.getCreatedAt() == null || "CANCELLED".equals(order.getStatus())) continue;
            LocalDate day = order.getCreatedAt().atZone(ZONE).toLocalDate();
            if (byDay.containsKey(day)) byDay.merge(day, (long) order.getTotal(), Long::sum);
        }
        List<Map<String, Object>> series = new ArrayList<>();
        byDay.forEach((day, total) -> series.add(Map.of("label", day.format(DAY), "value", total)));
        return series;
    }

    private Map<String, Long> statusCounts(List<Order> orders) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (String status : List.of("PLACED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED")) counts.put(status, 0L);
        for (Order order : orders) counts.merge(order.getStatus() == null ? "PLACED" : order.getStatus(), 1L, Long::sum);
        return counts;
    }

    private List<Map<String, Object>> lowStock() {
        int threshold = settings.getInt("lowStockThreshold");
        return mongo.findAll(CatalogProduct.class).stream()
                .filter(p -> p.isActive() && p.getStock() <= threshold)
                .sorted(Comparator.comparingInt(CatalogProduct::getStock))
                .limit(8)
                .map(p -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", p.getId());
                    row.put("name", p.getName());
                    row.put("stock", p.getStock());
                    return row;
                }).toList();
    }

    public Map<String, Object> notifications() {
        List<Order> orders = mongo.findAll(Order.class);
        Map<String, Object> result = new LinkedHashMap<>();
        boolean orderAlerts = settings.getBoolean("notifyNewOrder");
        boolean stockAlerts = settings.getBoolean("notifyLowStock");
        result.put("newOrders", orderAlerts ? orders.stream().filter(o -> "PLACED".equals(o.getStatus()))
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5).map(this::orderSummary).toList() : List.of());
        result.put("pendingCount", orderAlerts ? orders.stream().filter(o -> "PLACED".equals(o.getStatus())).count() : 0);
        result.put("lowStock", stockAlerts ? lowStock() : List.of());
        List<Map<String, Object>> newCustomers = List.of();
        if (settings.getBoolean("notifyNewCustomer")) {
            Instant week = Instant.now().minusSeconds(7L * 86400);
            newCustomers = userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.BUYER && after(u.getCreatedAt(), week))
                    .sorted(Comparator.comparing(AppUser::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(5)
                    .map(u -> {
                        Map<String, Object> row = new LinkedHashMap<>();
                        row.put("id", u.getId());
                        row.put("name", u.getName());
                        row.put("joinedAt", u.getCreatedAt());
                        return row;
                    }).toList();
        }
        result.put("newCustomers", newCustomers);
        return result;
    }

    // ------------------------------------------------------------------ reports
    public Map<String, Object> reports(String period) {
        String normalized = period == null ? "month" : period.toLowerCase(Locale.ROOT);
        LocalDate today = LocalDate.now(ZONE);
        Instant from = switch (normalized) {
            case "week" -> today.minusDays(6).atStartOfDay(ZONE).toInstant();
            case "month" -> today.withDayOfMonth(1).atStartOfDay(ZONE).toInstant();
            case "year" -> today.withDayOfYear(1).atStartOfDay(ZONE).toInstant();
            case "all" -> Instant.EPOCH;
            default -> throw new IllegalArgumentException("Period must be week, month, year or all");
        };
        List<Order> orders = mongo.findAll(Order.class).stream()
                .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().isBefore(from)).toList();
        List<Order> sold = orders.stream().filter(o -> !"CANCELLED".equals(o.getStatus())).toList();

        Map<String, CatalogProduct> products = new LinkedHashMap<>();
        mongo.findAll(CatalogProduct.class).forEach(p -> products.put(p.getId(), p));

        Map<String, long[]> perProduct = new LinkedHashMap<>();   // id -> [sold, revenue]
        Map<String, String[]> productInfo = new LinkedHashMap<>();
        Map<String, Long> perCategory = new LinkedHashMap<>();
        for (Order order : sold) {
            if (order.getItems() == null) continue;
            for (Order.Item item : order.getItems()) {
                long revenue = (long) item.getPrice() * item.getQuantity();
                long[] totals = perProduct.computeIfAbsent(item.getProductId(), k -> new long[2]);
                totals[0] += item.getQuantity();
                totals[1] += revenue;
                productInfo.putIfAbsent(item.getProductId(), new String[]{item.getName(), item.getImage()});
                CatalogProduct product = products.get(item.getProductId());
                String category = product != null && product.getCategory() != null ? product.getCategory() : "Others";
                perCategory.merge(category, revenue, Long::sum);
            }
        }
        List<Map<String, Object>> top = perProduct.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[0], a.getValue()[0])).limit(5)
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("productId", e.getKey());
                    row.put("name", productInfo.get(e.getKey())[0]);
                    row.put("image", productInfo.get(e.getKey())[1]);
                    row.put("sold", e.getValue()[0]);
                    row.put("revenue", e.getValue()[1]);
                    return row;
                }).toList();
        long categoryTotal = perCategory.values().stream().mapToLong(Long::longValue).sum();
        List<Map<String, Object>> byCategory = perCategory.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue())).limit(6)
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("category", e.getKey());
                    row.put("revenue", e.getValue());
                    row.put("percent", categoryTotal == 0 ? 0 : Math.round(e.getValue() * 100.0 / categoryTotal));
                    return row;
                }).toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("period", normalized);
        result.put("totalSales", revenue(sold));
        result.put("totalOrders", orders.size());
        result.put("totalProducts", mongo.count(new Query(), CatalogProduct.class));
        result.put("topSelling", top);
        result.put("salesByCategory", byCategory);
        result.put("statusCounts", statusCounts(orders));
        return result;
    }

    // ------------------------------------------------------------------ categories
    public List<Map<String, Object>> categories() {
        Map<String, Map<String, Object>> byName = new LinkedHashMap<>();
        for (CatalogProduct product : mongo.findAll(CatalogProduct.class)) {
            String name = product.getCategory() == null || product.getCategory().isBlank() ? "Others" : product.getCategory();
            Map<String, Object> row = byName.computeIfAbsent(name.toLowerCase(Locale.ROOT), k -> categoryRow(name, null, false));
            row.put("productCount", (int) row.get("productCount") + 1);
            if (row.get("image") == null && product.getImage() != null) row.put("image", product.getImage());
        }
        for (Category category : mongo.findAll(Category.class)) {
            Map<String, Object> row = byName.computeIfAbsent(category.getName().toLowerCase(Locale.ROOT),
                    k -> categoryRow(category.getName(), null, true));
            row.put("custom", true);
            if (category.getImage() != null && !category.getImage().isBlank()) row.put("image", category.getImage());
        }
        return byName.values().stream().sorted(Comparator.comparing(r -> String.valueOf(r.get("name")))).toList();
    }

    public Map<String, Object> addCategory(String name, String image) {
        String clean = cleanName(name);
        if (categoryExists(clean)) throw new IllegalArgumentException("This category already exists");
        mongo.insert(new Category(clean, blankToNull(image)));
        return categoryRow(clean, blankToNull(image), true);
    }

    public Map<String, Object> renameCategory(String oldName, String newName, String image) {
        if (oldName == null || oldName.isBlank()) throw new IllegalArgumentException("The category to rename is missing");
        String clean = cleanName(newName);
        if (!oldName.trim().equalsIgnoreCase(clean) && categoryExists(clean)) throw new IllegalArgumentException("This category already exists");
        mongo.updateMulti(new Query(categoryIs("category", oldName)),
                new org.springframework.data.mongodb.core.query.Update().set("category", clean), CatalogProduct.class);
        Category stored = mongo.findOne(new Query(categoryIs("name", oldName)), Category.class);
        if (stored == null) stored = new Category(clean, null);
        stored.setName(clean);
        if (image != null) stored.setImage(blankToNull(image));
        mongo.save(stored);
        return categoryRow(clean, stored.getImage(), true);
    }

    public void deleteCategory(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("The category to delete is missing");
        long inUse = mongo.count(new Query(categoryIs("category", name)), CatalogProduct.class);
        if (inUse > 0) throw new IllegalArgumentException("Move or delete the " + inUse + " product(s) in this category first");
        mongo.remove(new Query(categoryIs("name", name)), Category.class);
    }

    /** Case-insensitive exact match on a category name (product categories are free text). */
    private Criteria categoryIs(String field, String name) {
        return Criteria.where(field).regex("^" + java.util.regex.Pattern.quote(name.trim()) + "$", "i");
    }

    private Map<String, Object> categoryRow(String name, String image, boolean custom) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("name", name);
        row.put("image", image);
        row.put("productCount", 0);
        row.put("custom", custom);
        return row;
    }

    private boolean categoryExists(String name) {
        return categories().stream().anyMatch(c -> String.valueOf(c.get("name")).equalsIgnoreCase(name));
    }

    private String cleanName(String name) {
        String clean = name == null ? "" : name.trim();
        if (clean.length() < 2 || clean.length() > 60) throw new IllegalArgumentException("Category name must be 2 to 60 characters");
        return clean;
    }

    // ------------------------------------------------------------------ customers & sellers
    public List<Map<String, Object>> customers(String q) {
        List<Order> orders = mongo.findAll(Order.class);
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.BUYER && matches(u, q))
                .sorted(Comparator.comparing(AppUser::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(u -> {
                    List<Order> mine = orders.stream().filter(o -> u.getEmail().equalsIgnoreCase(o.getUserEmail())).toList();
                    Map<String, Object> row = userRow(u);
                    row.put("orders", mine.size());
                    row.put("totalSpent", revenue(mine));
                    return row;
                }).toList();
    }

    public Map<String, Object> customerDetail(String id) {
        AppUser user = userRepository.findById(id).filter(u -> u.getRole() == Role.BUYER)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        List<Order> mine = mongo.findAll(Order.class).stream()
                .filter(o -> user.getEmail().equalsIgnoreCase(o.getUserEmail()))
                .sorted(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))).toList();
        Map<String, Object> row = userRow(user);
        row.put("orders", mine.size());
        row.put("totalSpent", revenue(mine));
        row.put("orderList", mine.stream().map(this::orderSummary).toList());
        return row;
    }

    public List<Map<String, Object>> sellers(String q) {
        List<CatalogProduct> products = mongo.findAll(CatalogProduct.class);
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.SELLER && matches(u, q))
                .map(u -> {
                    Map<String, Object> row = userRow(u);
                    row.put("products", sellerProducts(u, products).size());
                    return row;
                }).toList();
    }

    public Map<String, Object> sellerDetail(String id) {
        AppUser user = userRepository.findById(id).filter(u -> u.getRole() == Role.SELLER)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        List<CatalogProduct> products = sellerProducts(user, mongo.findAll(CatalogProduct.class));
        Map<String, Object> row = userRow(user);
        row.put("products", products.size());
        row.put("productList", products.stream().map(p -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName());
            item.put("price", p.getPrice());
            item.put("stock", p.getStock());
            return item;
        }).toList());
        return row;
    }

    public Map<String, Object> createSeller(String name, String email, String password, String phone) {
        String cleanName = name == null ? "" : name.trim();
        String cleanEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (cleanName.length() < 2 || cleanName.length() > 80) throw new IllegalArgumentException("Name must be between 2 and 80 characters");
        if (!cleanEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("Please enter a valid email");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("Password must be at least 6 characters");
        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) throw new IllegalArgumentException("An account with this email already exists");
        AppUser seller = new AppUser(cleanName, cleanEmail, passwordEncoder.encode(password), Role.SELLER);
        if (phone != null && !phone.isBlank()) seller.setPhone(phone.trim());
        seller = userRepository.save(seller);
        Map<String, Object> row = userRow(seller);
        row.put("products", 0);
        return row;
    }

    /** Activate / deactivate a customer or seller account (admins cannot be changed here). */
    public Map<String, Object> setUserActive(String id, boolean active) {
        AppUser user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() == Role.ADMIN) throw new IllegalArgumentException("Admin accounts cannot be deactivated");
        user.setActive(active);
        return userRow(userRepository.save(user));
    }

    private List<CatalogProduct> sellerProducts(AppUser seller, List<CatalogProduct> products) {
        return products.stream().filter(p -> (seller.getId() != null && seller.getId().equals(p.getArtisanId()))
                || (p.getArtisan() != null && p.getArtisan().equalsIgnoreCase(seller.getName()))).toList();
    }

    private Map<String, Object> userRow(AppUser u) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", u.getId());
        row.put("name", u.getName());
        row.put("email", u.getEmail());
        row.put("phone", u.getPhone());
        row.put("city", u.getCity());
        row.put("province", u.getProvince());
        row.put("address", u.getAddress());
        row.put("role", u.getRole());
        row.put("active", u.isActive());
        row.put("joinedAt", u.getCreatedAt());
        return row;
    }

    private boolean matches(AppUser user, String q) {
        if (q == null || q.isBlank()) return true;
        String needle = q.trim().toLowerCase(Locale.ROOT);
        return (user.getName() != null && user.getName().toLowerCase(Locale.ROOT).contains(needle))
                || (user.getEmail() != null && user.getEmail().toLowerCase(Locale.ROOT).contains(needle));
    }

    // ------------------------------------------------------------------ contact messages & custom orders
    public static final java.util.Set<String> MESSAGE_STATUSES = java.util.Set.of("NEW", "READ", "REPLIED");
    public static final List<String> CUSTOM_ORDER_STATUSES =
            List.of("Request received", "In review", "Quote sent", "In progress", "Completed", "Declined");

    public List<ContactMessage> messages() {
        return mongo.find(new Query().with(Sort.by(Sort.Direction.DESC, "createdAt")), ContactMessage.class);
    }

    public ContactMessage setMessageStatus(String id, String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if (!MESSAGE_STATUSES.contains(normalized)) throw new IllegalArgumentException("Status must be NEW, READ or REPLIED");
        ContactMessage message = mongo.findById(id, ContactMessage.class);
        if (message == null) throw new IllegalArgumentException("Message not found");
        message.setStatus(normalized);
        return mongo.save(message);
    }

    public void deleteMessage(String id) {
        ContactMessage message = mongo.findById(id, ContactMessage.class);
        if (message == null) throw new IllegalArgumentException("Message not found");
        mongo.remove(message);
    }

    public List<CustomOrderRequest> customOrders() {
        return mongo.find(new Query().with(Sort.by(Sort.Direction.DESC, "createdAt")), CustomOrderRequest.class);
    }

    public CustomOrderRequest setCustomOrderStatus(String id, String status) {
        String chosen = CUSTOM_ORDER_STATUSES.stream().filter(s -> s.equalsIgnoreCase(status == null ? "" : status.trim()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Status must be one of " + CUSTOM_ORDER_STATUSES));
        CustomOrderRequest request = mongo.findById(id, CustomOrderRequest.class);
        if (request == null) throw new IllegalArgumentException("Custom order request not found");
        request.setStatus(chosen);
        return mongo.save(request);
    }

    // ------------------------------------------------------------------ helpers
    private Map<String, Object> orderSummary(Order o) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("orderNumber", o.getOrderNumber());
        row.put("customer", o.getAddress() != null && o.getAddress().getFullName() != null
                ? o.getAddress().getFullName() : o.getUserEmail());
        row.put("total", o.getTotal());
        row.put("status", o.getStatus());
        row.put("createdAt", o.getCreatedAt());
        return row;
    }

    private long revenue(List<Order> orders) {
        return orders.stream().filter(o -> !"CANCELLED".equals(o.getStatus())).mapToLong(Order::getTotal).sum();
    }

    private boolean after(Instant value, Instant limit) {
        return value != null && value.isAfter(limit);
    }

    private int growth(long current, long previous) {
        if (previous == 0) return current > 0 ? 100 : 0;
        return (int) Math.round((current - previous) * 100.0 / previous);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
