package org.inventory.management.mapper;

import org.inventory.management.dto.ProductRequestDTO;
import org.inventory.management.dto.ProductResponseDTO;
import org.inventory.management.entity.Product;

public class ProductMapper {

    public static Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .sku(dto.getSku())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .build();
    }

    public static ProductResponseDTO toDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }
}