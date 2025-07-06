package org.inventory.management.config;

import lombok.RequiredArgsConstructor;
import org.inventory.management.entity.Order;
import org.inventory.management.entity.OrderItem;
import org.inventory.management.entity.Product;
import org.inventory.management.entity.enums.OrderStatus;
import org.inventory.management.repository.OrderRepository;
import org.inventory.management.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            List<Product> products = seedProducts();
            seedOrders(products);
        };
    }

    private List<Product> seedProducts() {
        List<Product> products = List.of(
                createProduct("Laptop", "LAP123", 1000, 5),
                createProduct("Mouse", "MOU456", 25, 20),
                createProduct("Keyboard", "KEY789", 50, 10),
                createProduct("Monitor", "MON101", 200, 3),
                createProduct("Phone", "PHN102", 800, 7),
                createProduct("Charger", "CHR103", 30, 15),
                createProduct("Tablet", "TAB104", 500, 4),
                createProduct("Printer", "PRT105", 150, 2),
                createProduct("Camera", "CAM106", 600, 6),
                createProduct("Speaker", "SPK107", 100, 12)
        );

        return productRepository.saveAll(products);
    }

    private void seedOrders(List<Product> products) {
        Order order1 = createOrder(OrderStatus.PENDING, List.of(
                createOrderItem(products.get(0), 1),
                createOrderItem(products.get(1), 2)
        ));

        Order order2 = createOrder(OrderStatus.COMPLETED, List.of(
                createOrderItem(products.get(2), 3)
        ));

        orderRepository.saveAll(List.of(order1, order2));
    }

    private Product createProduct(String name, String sku, double price, int stock) {
        return Product.builder()
                .name(name)
                .sku(sku)
                .price(BigDecimal.valueOf(price))
                .stock(stock)
                .build();
    }

    private OrderItem createOrderItem(Product product, int quantity) {
        return OrderItem.builder()
                .product(product)
                .quantity(quantity)
                .build();
    }

    private Order createOrder(OrderStatus status, List<OrderItem> items) {
        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .status(status)
                .build();

        items.forEach(item -> item.setOrder(order));
        order.setItems(items);
        return order;
    }
}