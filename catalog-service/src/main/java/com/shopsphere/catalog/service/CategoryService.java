package com.shopsphere.catalog.service;

import com.shopsphere.catalog.dto.CategoryDTO;
import org.springframework.data.domain.Page;

public interface CategoryService {

    Page<CategoryDTO> getActiveCategories(int page, int size);
    CategoryDTO getCategoryById(Long id);
    Page<CategoryDTO> getAllCategories(String name, int page, int size,
                                       String sortBy, String direction);
    // Admin
    CategoryDTO createCategory(CategoryDTO dto);
    CategoryDTO updateCategory(Long id, CategoryDTO dto);
    void deleteCategory(Long id);
}