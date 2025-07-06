package org.inventory.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inventory.management.dto.OrderRequestDTO;
import org.inventory.management.dto.OrderResponseDTO;
import org.inventory.management.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(OrderControllerTest.MockedBeans.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setup() {
        reset(orderService);
    }

    static class MockedBeans {
        @Bean
        public OrderService orderService() {
            return mock(OrderService.class);
        }
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        OrderRequestDTO request = new OrderRequestDTO(List.of());
        OrderResponseDTO response = OrderResponseDTO.builder()
                .orderId(1L)
                .status("PENDING")
                .items(List.of())
                .build();

        when(orderService.createOrder(any())).thenReturn(response);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void updateStatus_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/orders/1/status")
                        .param("status", "COMPLETED"))
                .andExpect(status().isNoContent());

        verify(orderService).updateOrderStatus(1L, "COMPLETED");
    }

    @Test
    void getOrderSummary_ShouldReturnSummaryMap() throws Exception {
        Map<String, BigDecimal> summary = Map.of("SKU001", new BigDecimal("1999.98"));
        when(orderService.summarizeOrderValuePerProduct()).thenReturn(summary);

        mockMvc.perform(get("/orders/orders/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.SKU001").value("1999.98"));
    }
}