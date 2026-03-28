package com.shopsphere.catalog.controller;

import com.shopsphere.catalog.dto.ProductDTO;
import com.shopsphere.catalog.service.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // ── Public read endpoints (visible in Swagger) ────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, sortBy));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam                      String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.searchProducts(keyword, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductDTO>> filterProducts(
            @RequestParam(required = false)       Long categoryId,
            @RequestParam(required = false)       String brand,
            @RequestParam(required = false)       Double minPrice,
            @RequestParam(required = false)       Double maxPrice,
            @RequestParam(required = false)       Boolean active,
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "10")    int size,
            @RequestParam(defaultValue = "price") String sortBy) {
        return ResponseEntity.ok(productService.filterProducts(
                categoryId, brand, minPrice, maxPrice, active, page, size, sortBy));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<ProductDTO>> getByCategory(
            @PathVariable                      Long categoryId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId, page, size, sortBy));
    }

    // ── Internal write endpoints — admin-service Feign only ───────────────
    // Hidden from Swagger: these should only appear in the Admin Service docs,
    // not here. They are still callable internally by Feign with an ADMIN JWT.

    @Hidden
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto));
    }

    @Hidden
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                                                    @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @Hidden
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductDTO> updateStock(@PathVariable Long id,
                                                  @RequestParam int quantity) {
        return ResponseEntity.ok(productService.updateStock(id, quantity));
    }

    @Hidden
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}