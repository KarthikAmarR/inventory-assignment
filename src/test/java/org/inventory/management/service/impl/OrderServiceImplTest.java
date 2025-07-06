package org.inventory.management.service.impl;

import org.inventory.management.dto.OrderRequestDTO;
import org.inventory.management.dto.OrderResponseDTO;
import org.inventory.management.entity.Order;
import org.inventory.management.entity.OrderItem;
import org.inventory.management.entity.Product;
import org.inventory.management.entity.enums.OrderStatus;
import org.inventory.management.exception.InsufficientStockException;
import org.inventory.management.exception.ResourceNotFoundException;
import org.inventory.management.repository.OrderItemRepository;
import org.inventory.management.repository.OrderRepository;
import org.inventory.management.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_ShouldReturnResponse_WhenStockIsSufficient() {
        OrderRequestDTO request = OrderRequestDTO.builder()
                .items(List.of(
                        OrderRequestDTO.Item.builder()
                                .productId(1L)
                                .quantity(1)
                                .build()))
                .build();

        Product laptop = Product.builder()
                .id(1L)
                .sku("LAP123")
                .stock(5)
                .price(new BigDecimal("1000"))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));

        Order savedOrder = Order.builder()
                .id(1L)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .items(List.of(
                        OrderItem.builder()
                                .product(laptop)
                                .quantity(1)
                                .build()))
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponseDTO response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(OrderStatus.PENDING.name(), response.getStatus());
    }

    @Test
    void createOrder_ShouldThrowException_WhenStockIsInsufficient() {
        OrderRequestDTO request = OrderRequestDTO.builder()
                .items(List.of(
                        OrderRequestDTO.Item.builder()
                                .productId(1L)
                                .quantity(5)
                                .build()))
                .build();
    
        Product product = Product.builder()
                .id(1L)
                .sku("LAP123")
                .name("Laptop")
                .stock(2)
                .price(new BigDecimal("1000"))
                .build();
    
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
    
        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> orderService.createOrder(request)
        );
    
        assertEquals("Not enough stock for product: Laptop", exception.getMessage());
    }

    @Test
    void updateOrderStatus_ShouldUpdateSuccessfully() {
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.updateOrderStatus(1L, "COMPLETED");

        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_ShouldThrowException_WhenStatusInvalid() {
        Order order = Order.builder().id(1L).status(OrderStatus.PENDING).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderStatus(1L, "INVALID"));

        assertEquals("Invalid order status: INVALID", exception.getMessage());
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotFound() {
        OrderRequestDTO request = OrderRequestDTO.builder()
                .items(List.of(OrderRequestDTO.Item.builder().productId(1L).quantity(1).build()))
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.createOrder(request));

        assertEquals("Product not found with ID: 1", exception.getMessage());
    }

    @Test
    void summarizeOrderValuePerProduct_ShouldReturnCorrectAggregation() {
            Product productA = Product.builder()
                            .id(1L)
                            .sku("A123")
                            .price(new BigDecimal("100"))
                            .build();

            Product productB = Product.builder()
                            .id(2L)
                            .sku("B456")
                            .price(new BigDecimal("200"))
                            .build();

            OrderItem item1 = OrderItem.builder().product(productA).quantity(2).build(); 
            OrderItem item2 = OrderItem.builder().product(productB).quantity(1).build(); 
            OrderItem item3 = OrderItem.builder().product(productA).quantity(1).build();

            Order order1 = Order.builder().items(List.of(item1, item2)).build();
            Order order2 = Order.builder().items(List.of(item3)).build();

            when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

            Map<String, BigDecimal> result = orderService.summarizeOrderValuePerProduct();

            assertEquals(2, result.size());
            assertEquals(new BigDecimal("300"), result.get("A123")); 
            assertEquals(new BigDecimal("200"), result.get("B456"));
    }
}