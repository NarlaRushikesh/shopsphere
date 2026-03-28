package com.example.demo.controller;

// FILE LOCATION:
// admin-service/src/test/java/com/example/demo/controller/AdminControllerTest.java

import com.example.demo.dto.*;
import com.example.demo.service.AdminService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    // ─────────────────────────────────────────────
    // Product endpoints
    // ─────────────────────────────────────────────

    @Test
    void getProducts_returnsOk_withProductPageResponse() {
        ProductPageResponse response = new ProductPageResponse();
        response.setContent(List.of(new ProductDTO()));
        when(adminService.getAllProducts(0, 100, "id")).thenReturn(response);

        ResponseEntity<ProductPageResponse> result = adminController.getProducts(0, 100, "id");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getContent().size());
    }

    @Test
    void createProduct_returnsCreated_withSavedProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Samsung Galaxy");
        when(adminService.createProduct(dto)).thenReturn(dto);

        ResponseEntity<ProductDTO> result = adminController.createProduct(dto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Samsung Galaxy", result.getBody().getName());
    }

    @Test
    void updateProduct_returnsOk_withUpdatedProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Updated");
        when(adminService.updateProduct(1L, dto)).thenReturn(dto);

        ResponseEntity<ProductDTO> result = adminController.updateProduct(1L, dto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Updated", result.getBody().getName());
    }

    @Test
    void updateStock_returnsOk_withUpdatedProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setStock(25);
        when(adminService.updateStock(1L, 25)).thenReturn(dto);

        ResponseEntity<ProductDTO> result = adminController.updateStock(1L, 25);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(25, result.getBody().getStock());
    }

    @Test
    void deleteProduct_returnsOk_withConfirmationMessage() {
        doNothing().when(adminService).deleteProduct(1L);

        ResponseEntity<String> result = adminController.deleteProduct(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().contains("deleted"));
    }

    // ─────────────────────────────────────────────
    // Category endpoints
    // ─────────────────────────────────────────────

    @Test
    void getAllCategories_returnsOk_withCategoryPageResponse() {
        CategoryPageResponse response = new CategoryPageResponse();
        response.setContent(List.of(new CategoryDTO()));
        when(adminService.getAllCategories(null, 0, 10, "id", "asc")).thenReturn(response);

        ResponseEntity<CategoryPageResponse> result =
                adminController.getAllCategories(null, 0, 10, "id", "asc");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(1, result.getBody().getContent().size());
    }

    @Test
    void createCategory_returnsCreated_withSavedCategory() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Books");
        when(adminService.createCategory(dto)).thenReturn(dto);

        ResponseEntity<CategoryDTO> result = adminController.createCategory(dto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Books", result.getBody().getName());
    }

    @Test
    void updateCategory_returnsOk_withUpdatedCategory() {
        CategoryDTO dto = new CategoryDTO();
        dto.setName("Updated Category");
        when(adminService.updateCategory(2L, dto)).thenReturn(dto);

        ResponseEntity<CategoryDTO> result = adminController.updateCategory(2L, dto);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Updated Category", result.getBody().getName());
    }

    @Test
    void deleteCategory_returnsOk_withConfirmationMessage() {
        doNothing().when(adminService).deleteCategory(2L);

        ResponseEntity<String> result = adminController.deleteCategory(2L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
    }

    // ─────────────────────────────────────────────
    // Order endpoints
    // ─────────────────────────────────────────────

    @Test
    void getOrders_returnsOk_withOrderList() {
        when(adminService.getAllOrders()).thenReturn(List.of(new Object(), new Object()));

        ResponseEntity<List<Object>> result = adminController.getOrders();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void updateOrderStatus_returnsOk_withUpdatedOrder() {
        Object updated = new Object();
        when(adminService.updateOrderStatus(5L, "DELIVERED")).thenReturn(updated);

        ResponseEntity<Object> result = adminController.updateOrderStatus(5L, "DELIVERED");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(updated, result.getBody());
    }
}