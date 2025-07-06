package org.inventory.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory.management.dto.ProductRequestDTO;
import org.inventory.management.dto.ProductResponseDTO;
import org.inventory.management.mapper.ProductMapper;
import org.inventory.management.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        var product = productService.createProduct(ProductMapper.toEntity(request));
        var response = ProductMapper.toDTO(product);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public List<ProductResponseDTO> getAllProducts() {
        return productService.getAllProducts().stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/low-stock")
    public List<ProductResponseDTO> getLowStock(@RequestParam int threshold) {
        return productService.getLowStockProducts(threshold).stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
    }
}