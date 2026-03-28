package com.shopsphere.catalog.service.impl;

// FILE LOCATION:
// catalog-service/src/test/java/com/shopsphere/catalog/service/impl/CategoryServiceImplTest.java

import com.shopsphere.catalog.dto.CategoryDTO;
import com.shopsphere.catalog.entity.Category;
import com.shopsphere.catalog.entity.Product;
import com.shopsphere.catalog.exception.ResourceNotFoundException;
import com.shopsphere.catalog.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    // ── helpers ──────────────────────────────────────────────────────────

    private Category buildCategory(Long id, String name) {
        Category c = new Category();
        c.setId(id);
        c.setName(name);
        c.setDescription("Desc for " + name);
        c.setActive(true);
        return c;
    }

    private CategoryDTO buildDto(String name) {
        CategoryDTO dto = new CategoryDTO();
        dto.setName(name);
        dto.setDescription("Desc");
        dto.setActive(true);
        return dto;
    }

    // ─────────────────────────────────────────────
    // getActiveCategories()
    // ─────────────────────────────────────────────

    @Test
    void getActiveCategories_returnsMappedPage() {
        Category c = buildCategory(1L, "Electronics");
        Page<Category> page = new PageImpl<>(List.of(c));
        when(categoryRepository.findByActiveTrue(any(Pageable.class))).thenReturn(page);

        Page<CategoryDTO> result = categoryService.getActiveCategories(0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("Electronics", result.getContent().get(0).getName());
    }

    @Test
    void getActiveCategories_returnsEmptyPage_whenNoneActive() {
        when(categoryRepository.findByActiveTrue(any(Pageable.class))).thenReturn(Page.empty());

        Page<CategoryDTO> result = categoryService.getActiveCategories(0, 10);

        assertTrue(result.isEmpty());
    }

    // ─────────────────────────────────────────────
    // getCategoryById()
    // ─────────────────────────────────────────────

    @Test
    void getCategoryById_success_returnsMappedDTO() {
        Category c = buildCategory(1L, "Books");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(c));

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertEquals("Books", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void getCategoryById_throwsResourceNotFoundException_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(99L));
    }

    // ─────────────────────────────────────────────
    // getAllCategories()
    // ─────────────────────────────────────────────

    @Test
    void getAllCategories_withoutNameFilter_returnsAll() {
        Category c = buildCategory(1L, "Clothing");
        Page<Category> page = new PageImpl<>(List.of(c));
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<CategoryDTO> result = categoryService.getAllCategories(null, 0, 10, "id", "asc");

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllCategories_withNameFilter_usesSearchQuery() {
        Category c = buildCategory(1L, "Clothing");
        Page<Category> page = new PageImpl<>(List.of(c));
        when(categoryRepository.findByNameContainingIgnoreCase(eq("cloth"), any(Pageable.class)))
                .thenReturn(page);

        Page<CategoryDTO> result = categoryService.getAllCategories("cloth", 0, 10, "id", "asc");

        assertEquals(1, result.getContent().size());
        verify(categoryRepository).findByNameContainingIgnoreCase(eq("cloth"), any(Pageable.class));
    }

    @Test
    void getAllCategories_withDescDirection_appliesDescSort() {
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        categoryService.getAllCategories(null, 0, 10, "name", "desc");

        verify(categoryRepository).findAll(argThat((Pageable p) ->
                p.getSort().getOrderFor("name") != null &&
                p.getSort().getOrderFor("name").getDirection() == Sort.Direction.DESC
        ));
    }

    // ─────────────────────────────────────────────
    // createCategory()
    // ─────────────────────────────────────────────

    @Test
    void createCategory_success_savesAndReturnsMappedDTO() {
        CategoryDTO dto = buildDto("Sports");
        Category saved = buildCategory(5L, "Sports");

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryDTO result = categoryService.createCategory(dto);

        assertEquals("Sports", result.getName());
        assertEquals(5L, result.getId());
        verify(categoryRepository).save(any(Category.class));
    }

    // ─────────────────────────────────────────────
    // updateCategory()
    // ─────────────────────────────────────────────

    @Test
    void updateCategory_success_updatesFieldsAndSaves() {
        Category existing = buildCategory(1L, "OldName");
        CategoryDTO dto = buildDto("NewName");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        CategoryDTO result = categoryService.updateCategory(1L, dto);

        assertEquals("NewName", result.getName());
    }

    @Test
    void updateCategory_throwsResourceNotFoundException_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(99L, new CategoryDTO()));
    }

    // ─────────────────────────────────────────────
    // deleteCategory()
    // ─────────────────────────────────────────────

    @Test
    void deleteCategory_hardDeletes_whenNoProducts() {
        Category c = buildCategory(1L, "Empty");
        c.setProducts(Collections.emptyList());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(c));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void deleteCategory_softDeletes_whenCategoryHasProducts() {
        Category c = buildCategory(2L, "WithProducts");
        Product p = new Product();
        c.setProducts(List.of(p));

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(c));
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        categoryService.deleteCategory(2L);

        assertFalse(c.isActive());
        verify(categoryRepository).save(c);
        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void deleteCategory_throwsResourceNotFoundException_whenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(99L));
    }
}