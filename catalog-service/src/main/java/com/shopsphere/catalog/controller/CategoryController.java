package com.shopsphere.catalog.controller;

import com.shopsphere.catalog.dto.CategoryDTO;
import com.shopsphere.catalog.service.CategoryService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // ── Public endpoints (visible in Swagger) ─────────────────────────────

    @GetMapping
    public ResponseEntity<Page<CategoryDTO>> getActiveCategories(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(categoryService.getActiveCategories(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // ── Internal endpoint — admin-service Feign only, hidden from Swagger ─

    @Hidden
    @GetMapping("/all")
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(
            @RequestParam(required = false)     String name,
            @RequestParam(defaultValue = "0")   int page,
            @RequestParam(defaultValue = "10")  int size,
            @RequestParam(defaultValue = "id")  String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(categoryService.getAllCategories(name, page, size, sortBy, direction));
    }
}