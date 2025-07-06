package org.inventory.management.transactional;

import org.inventory.management.entity.Order;
import org.inventory.management.entity.Product;
import org.inventory.management.exception.InsufficientStockException;
import org.inventory.management.repository.OrderRepository;
import org.inventory.management.repository.ProductRepository;
import org.inventory.management.service.OrderService;
import org.inventory.management.dto.OrderRequestDTO;
import org.inventory.management.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(OrderServiceImpl.class)
class OrderTransactionalTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void createOrder_WithInsufficientStock_ShouldRollbackTransaction() {
        Product p1 = productRepository.save(Product.builder()
                .name("Laptop")
                .sku("L1")
                .stock(10)
                .price(new BigDecimal("1000"))
                .build());

        Product p2 = productRepository.save(Product.builder()
                .name("Mouse")
                .sku("M1")
                .stock(2)
                .price(new BigDecimal("50"))
                .build());

        OrderRequestDTO request = OrderRequestDTO.builder()
                .items(List.of(
                        OrderRequestDTO.Item.builder().productId(p1.getId()).quantity(1).build(),
                        OrderRequestDTO.Item.builder().productId(p2.getId()).quantity(10).build()
                ))
                .build();

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(request));

        List<Order> orders = orderRepository.findAll();
        assertEquals(0, orders.size());

        entityManager.clear();
        
        assertEquals(10, productRepository.findById(p1.getId()).get().getStock());
        assertEquals(2, productRepository.findById(p2.getId()).get().getStock());
    }
}