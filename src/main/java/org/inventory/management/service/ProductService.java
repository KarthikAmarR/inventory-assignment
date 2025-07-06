package org.inventory.management.service;

import org.inventory.management.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product createProduct(Product product);

    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    List<Product> getLowStockProducts(int threshold);
}