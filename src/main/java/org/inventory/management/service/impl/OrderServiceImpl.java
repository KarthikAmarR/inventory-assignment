package org.inventory.management.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.inventory.management.dto.OrderRequestDTO;
import org.inventory.management.dto.OrderResponseDTO;
import org.inventory.management.entity.*;
import org.inventory.management.entity.enums.OrderStatus;
import org.inventory.management.exception.InsufficientStockException;
import org.inventory.management.exception.ResourceNotFoundException;
import org.inventory.management.mapper.OrderMapper;
import org.inventory.management.repository.OrderRepository;
import org.inventory.management.repository.ProductRepository;
import org.inventory.management.service.OrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        List<OrderItem> items = new ArrayList<>();

        for (OrderRequestDTO.Item itemDTO : request.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemDTO.getProductId()));

            if (product.getStock() < itemDTO.getQuantity()) {
                throw new InsufficientStockException("Not enough stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - itemDTO.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemDTO.getQuantity())
                    .build();

            items.add(orderItem);
        }

        Order order = Order.builder()
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .items(items)
                .build();

        items.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.save(order);
        return OrderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }

    @Override
    public Map<String, BigDecimal> summarizeOrderValuePerProduct() {
        return orderRepository.findAll().stream()
                .flatMap(order -> order.getItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getProduct().getSku(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                                BigDecimal::add)));
    }
}