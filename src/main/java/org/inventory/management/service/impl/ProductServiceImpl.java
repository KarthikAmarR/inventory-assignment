package org.inventory.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.inventory.management.entity.Product;
import org.inventory.management.repository.ProductRepository;
import org.inventory.management.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        productRepository.findBySku(product.getSku())
                .ifPresent(p -> {
                    throw new IllegalArgumentException("SKU already exists: " + product.getSku());
                });
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findAll().stream()
                .filter(product -> product.getStock() < threshold)
                .collect(Collectors.toList());
    }

    public List<Product> getLowStockProducts() {
        return getLowStockProducts(5); 
    }
}