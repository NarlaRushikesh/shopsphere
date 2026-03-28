package com.example.demo.service.impl;

// FILE LOCATION:
// admin-service/src/test/java/com/example/demo/service/impl/AdminServiceImplTest.java

import com.example.demo.dto.*;
import com.example.demo.feign.CatalogClient;
import com.example.demo.feign.OrderClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock private CatalogClient catalogClient;
    @Mock private OrderClient   orderClient;

    @InjectMocks
    private AdminServiceImpl adminService;

    // ─────────────────────────────────────────────
    // Product operations
    // ─────────────────────────────────────────────

    @Test
    void getAllProducts_delegatesToCatalogClient_andReturnsResponse() {
        ProductPageResponse response = new ProductPageResponse();
        response.setContent(List.of(new ProductDTO()));
        when(catalogClient.getAllProducts(0, 100, "id")).thenReturn(response);

        ProductPageResponse result = adminService.getAllProducts(0, 100, "id");

        assertSame(response, result);
        verify(catalogClient).getAllProducts(0, 100, "id");
    }

    @Test
    void createProduct_delegatesToCatalogClient_andReturnsDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setName("iPhone");
        when(catalogClient.createProduct(dto)).thenReturn(dto);

        ProductDTO result = adminService.createProduct(dto);

        assertEquals("iPhone", result.getName());
        verify(catalogClient).createProduct(dto);
    }

    @Test
    void updateProduct_delegatesToCatalogClient_andReturnsDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Updated");
        when(catalogClient.updateProduct(1L, dto)).thenReturn(dto);

        ProductDTO result = adminService.updateProduct(1L, dto);

        assertEquals("Updated", result.getName());
        verify(catalogClient).updateProduct(1L, dto);
    }

    @Test
    void updateStock_delegatesToCatalogClient_andReturnsDTO() {
        ProductDTO dto = new ProductDTO();
        dto.setStock(50);
        when(catalogClient.updateStock(1L, 50)).thenReturn(dto);

        ProductDTO result = adminService.updateStock(1L, 50);

        assertEquals(50, result.getStock());
        verify(catalogClient).updateStock(1L, 50);
    }

    @Test
    void deleteProduct_delegatesToCatalogClient() {
        adminService.deleteProduct(1L);

        verify(catalogClient).deleteProduct(1L);
    }

    // ─────────────────────────────────────────────
    // Category operations
    // ─────────────────────────────────────────────

    @Test
    void getAllCategories_delegatesToCatalogClient_andReturnsResponse() {
        CategoryPageResponse response = new CategoryPageResponse();
        response.setContent(List.of(new CategoryDTO()));
        when(catalogClient.getAllCategories(null, 0, 10, "id", "asc")).thenReturn(response);

        CategoryPageResponse result = adminService.getAllCategories(null, 0, 10, "id", "asc");

        assertSame(response, result);
    }

    @Test
    void createCategory_delegatesToCatalogClient_andReturnsDTO() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Electronics");
        when(catalogClient.createCategory(dto)).thenReturn(dto);

        CategoryDTO result = adminService.createCategory(dto);

        assertEquals("Electronics", result.getName());
    }

    @Test
    void updateCategory_delegatesToCatalogClient_andReturnsDTO() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Updated Category");
        when(catalogClient.updateCategory(2L, dto)).thenReturn(dto);

        CategoryDTO result = adminService.updateCategory(2L, dto);

        assertEquals("Updated Category", result.getName());
        verify(catalogClient).updateCategory(2L, dto);
    }

    @Test
    void deleteCategory_delegatesToCatalogClient() {
        adminService.deleteCategory(3L);

        verify(catalogClient).deleteCategory(3L);
    }

    // ─────────────────────────────────────────────
    // Order operations
    // ─────────────────────────────────────────────

    @Test
    void getAllOrders_delegatesToOrderClient_andReturnsList() {
        List<Object> orders = List.of(new Object(), new Object());
        when(orderClient.getAllOrders()).thenReturn(orders);

        List<Object> result = adminService.getAllOrders();

        assertEquals(2, result.size());
        verify(orderClient).getAllOrders();
    }

    @Test
    void updateOrderStatus_delegatesToOrderClient_andReturnsResult() {
        Object updated = new Object();
        when(orderClient.updateStatus(10L, "SHIPPED")).thenReturn(updated);

        Object result = adminService.updateOrderStatus(10L, "SHIPPED");

        assertSame(updated, result);
        verify(orderClient).updateStatus(10L, "SHIPPED");
    }
}