package com.shopsphere.catalog.service.impl;

import com.shopsphere.catalog.dto.CategoryDTO;
import com.shopsphere.catalog.entity.Category;
import com.shopsphere.catalog.exception.ResourceNotFoundException;
import com.shopsphere.catalog.repository.CategoryRepository;
import com.shopsphere.catalog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    private CategoryDTO toDTO(Category c) {
        return new CategoryDTO(c.getId(), c.getName(), c.getDescription(), c.getImageUrl(), c.isActive());
    }

    private void applyDTO(CategoryDTO dto, Category c) {
        c.setName(dto.getName());
        c.setDescription(dto.getDescription());
        c.setImageUrl(dto.getImageUrl());
        c.setActive(dto.isActive());
    }

    @Override
    public Page<CategoryDTO> getActiveCategories(int page, int size) {
        return categoryRepository
                .findByActiveTrue(PageRequest.of(page, size, Sort.by("name").ascending()))
                .map(this::toDTO);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return toDTO(categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id)));
    }

    @Override
    public Page<CategoryDTO> getAllCategories(String name, int page, int size,
                                              String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        if (name != null && !name.isBlank())
            return categoryRepository.findByNameContainingIgnoreCase(name, pageable).map(this::toDTO);
        return categoryRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO dto) {
        Category c = new Category();
        applyDTO(dto, c);
        return toDTO(categoryRepository.save(c));
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        applyDTO(dto, c);
        return toDTO(categoryRepository.save(c));
    }

    @Override
    public void deleteCategory(Long id) {
        Category c = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        if (c.getProducts() != null && !c.getProducts().isEmpty()) {
            c.setActive(false);   // soft-delete if products exist
            categoryRepository.save(c);
        } else {
            categoryRepository.deleteById(id);
        }
    }
}