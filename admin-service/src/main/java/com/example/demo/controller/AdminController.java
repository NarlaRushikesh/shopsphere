package com.example.demo.controller;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.CategoryPageResponse;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductPageResponse;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/products")
    public ResponseEntity<ProductPageResponse> getProducts(
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id")  String sortBy) {
        return ResponseEntity.ok(adminService.getAllProducts(page, size, sortBy));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createProduct(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @RequestBody ProductDTO product) {
        return ResponseEntity.ok(adminService.updateProduct(id, product));
    }

    @PatchMapping("/products/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(@PathVariable Long id,
                                                  @RequestParam int quantity) {
        return ResponseEntity.ok(adminService.updateStock(id, quantity));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        adminService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

    @GetMapping("/categories/all")
    public ResponseEntity<CategoryPageResponse> getAllCategories(
            @RequestParam(required = false)     String name,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "10")  int size,
            @RequestParam(defaultValue = "id")  String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(adminService.getAllCategories(name, page, size, sortBy, direction));
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createCategory(category));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                      @RequestBody CategoryDTO category) {
        return ResponseEntity.ok(adminService.updateCategory(id, category));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted (or deactivated if it has products)");
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Object>> getOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Object> updateOrderStatus(@PathVariable Long id,
                                                    @RequestParam String status) {
        return ResponseEntity.ok(adminService.updateOrderStatus(id, status));
    }
}