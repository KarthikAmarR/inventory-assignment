package org.inventory.management.mapper;

import org.inventory.management.dto.OrderResponseDTO;
import org.inventory.management.entity.Order;
import org.inventory.management.entity.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderResponseDTO toDTO(Order order) {
        List<OrderResponseDTO.Item> itemDTOs = order.getItems().stream()
                .map(OrderMapper::toItemDTO)
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .status(order.getStatus().name())
                .items(itemDTOs)
                .build();
    }

    private static OrderResponseDTO.Item toItemDTO(OrderItem item) {
        return OrderResponseDTO.Item.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .build();
    }
}