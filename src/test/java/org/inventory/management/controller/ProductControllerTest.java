package org.inventory.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inventory.management.dto.ProductRequestDTO;
import org.inventory.management.entity.Product;
import org.inventory.management.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(ProductControllerTest.MockedBeans.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Product testProduct;

    @BeforeEach
    void setup() {
        testProduct = Product.builder()
                .id(1L)
                .name("Phone")
                .sku("PH001")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .build();
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        ProductRequestDTO request = ProductRequestDTO.builder()
                .name("Phone")
                .sku("PH001")
                .price(new BigDecimal("999.99"))
                .stock(10)
                .build();

        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Phone"))
                .andExpect(jsonPath("$.sku").value("PH001"))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void getAllProducts_ShouldReturnList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(testProduct));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sku").value("PH001"));
    }

    @Test
    void getLowStock_ShouldReturnFilteredList() throws Exception {
        testProduct.setStock(3);
        when(productService.getLowStockProducts(5)).thenReturn(List.of(testProduct));

        mockMvc.perform(get("/products/low-stock?threshold=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sku").value("PH001"))
                .andExpect(jsonPath("$[0].stock").value(3));
    }

    @TestConfiguration
    static class MockedBeans {
        @Bean
        public ProductService productService() {
            return Mockito.mock(ProductService.class);
        }
    }
}