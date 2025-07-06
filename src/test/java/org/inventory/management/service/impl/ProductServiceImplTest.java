package org.inventory.management.service.impl;

import org.inventory.management.entity.Product;
import org.inventory.management.repository.ProductRepository;
import org.inventory.management.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void createProduct_WhenSkuIsUnique_ShouldSaveProduct() { 
        Product product = Product.builder()
                .sku("SKU001")
                .name("Laptop")
                .price(new BigDecimal("1000"))
                .stock(10)
                .build();

        when(productRepository.findBySku("SKU001")).thenReturn(Optional.empty());
        when(productRepository.save(product)).thenReturn(product);

        Product saved = productService.createProduct(product);

        assertNotNull(saved);
        assertEquals("Laptop", saved.getName());
        verify(productRepository).save(product);
    }

    @Test
    void createProduct_WhenSkuExists_ShouldThrowException() {
        Product product = Product.builder()
                .sku("SKU001")
                .name("Laptop")
                .build();

        when(productRepository.findBySku("SKU001"))
                .thenReturn(Optional.of(product));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productService.createProduct(product)
        );

        assertEquals("SKU already exists: SKU001", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void getAllProducts_ShouldReturnProductList() {
        List<Product> products = Arrays.asList(
                Product.builder().id(1L).name("Tablet").build(),
                Product.builder().id(2L).name("Mouse").build()
        );

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Tablet", result.get(0).getName());
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        Product product = Product.builder().id(1L).name("Tablet").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertTrue(result.isPresent());
        assertEquals("Tablet", result.get().getName());
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldReturnEmptyOptional() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void getLowStockProducts_ShouldReturnFilteredProducts() {
        List<Product> allProducts = List.of(
                Product.builder().name("Monitor").stock(2).build(),
                Product.builder().name("Printer").stock(1).build(),
                Product.builder().name("TV").stock(10).build());

        when(productRepository.findAll()).thenReturn(allProducts);

        List<Product> result = productService.getLowStockProducts(5);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Monitor")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Printer")));
    }

    @Test
    void getLowStockProducts_ShouldReturnFilteredList() {
        Product p1 = Product.builder().sku("P1").stock(2).build();
        Product p2 = Product.builder().sku("P2").stock(4).build();
        Product p3 = Product.builder().sku("P3").stock(10).build();
    
        when(productRepository.findAll()).thenReturn(List.of(p1, p2, p3));
    
        List<Product> result = productService.getLowStockProducts(5);
    
        assertEquals(2, result.size());
        assertTrue(result.contains(p1));
        assertTrue(result.contains(p2));
    }
}