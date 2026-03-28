package com.example.demo.feign;

import com.example.demo.config.FeignConfig;
import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.CategoryPageResponse;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "CATALOG-SERVICE", configuration = FeignConfig.class)
public interface CatalogClient {

    @GetMapping("/products")
    ProductPageResponse getAllProducts(
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id")  String sortBy);

    @PostMapping("/products")
    ProductDTO createProduct(@RequestBody ProductDTO product);

    @PutMapping("/products/{id}")
    ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductDTO product);

    @PatchMapping("/products/{id}/stock")
    ProductDTO updateStock(@PathVariable Long id, @RequestParam int quantity);

    @DeleteMapping("/products/{id}")
    void deleteProduct(@PathVariable Long id);

    @GetMapping("/categories/all")
    CategoryPageResponse getAllCategories(
            @RequestParam(required = false)     String name,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "10")  int size,
            @RequestParam(defaultValue = "id")  String sortBy,
            @RequestParam(defaultValue = "asc") String direction);

    @PostMapping("/categories")
    CategoryDTO createCategory(@RequestBody CategoryDTO category);

    @PutMapping("/categories/{id}")
    CategoryDTO updateCategory(@PathVariable Long id, @RequestBody CategoryDTO category);

    @DeleteMapping("/categories/{id}")
    void deleteCategory(@PathVariable Long id);
}