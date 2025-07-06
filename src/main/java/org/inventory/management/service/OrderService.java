package org.inventory.management.service;

import java.math.BigDecimal;
import java.util.Map;

import org.inventory.management.dto.OrderRequestDTO;
import org.inventory.management.dto.OrderResponseDTO;

public interface OrderService {
    
    OrderResponseDTO createOrder(OrderRequestDTO request);

    void updateOrderStatus(Long orderId, String status);
    
    Map<String, BigDecimal> summarizeOrderValuePerProduct();
}