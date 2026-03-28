package com.shopsphere.catalog.service.impl;

// FILE LOCATION:
// catalog-service/src/test/java/com/shopsphere/catalog/service/impl/ProductServiceImplTest.java

import com.shopsphere.catalog.dto.ProductDTO;
import com.shopsphere.catalog.entity.Category;
import com.shopsphere.catalog.entity.Product;
import com.shopsphere.catalog.exception.ResourceNotFoundException;
import com.shopsphere.catalog.repository.CategoryRepository;
import com.shopsphere.catalog.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    // ── helpers ──────────────────────────────────────────────────────────

    private Product buildProduct(Long id, String name) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setSku("SKU-" + id);
        p.setBrand("BrandX");
        p.setPrice(99.99);
        p.setStock(10);
        p.setActive(true);
        return p;
    }

    private ProductDTO buildDto(String name, Long categoryId) {
        ProductDTO dto = new ProductDTO();
        dto.setName(name);
        dto.setSku("SKU-NEW");
        dto.setBrand("BrandY");
        dto.setPrice(49.99);
        dto.setStock(5);
        dto.setActive(true);
        dto.setCategoryId(categoryId);
        return dto;
    }

    // ─────────────────────────────────────────────
    // getProductById()
    // ─────────────────────────────────────────────

    @Test
    void getProductById_success_returnsMappedDTO() {
        Product p = buildProduct(1L, "iPhone");
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        ProductDTO result = productService.getProductById(1L);

        assertEquals("iPhone", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void getProductById_throwsResourceNotFoundException_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductById(99L));
    }

    // ─────────────────────────────────────────────
    // getAllProducts()
    // ─────────────────────────────────────────────

    @Test
    void getAllProducts_returnsMappedPage() {
        Product p = buildProduct(1L, "Laptop");
        Page<Product> page = new PageImpl<>(List.of(p));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ProductDTO> result = productService.getAllProducts(0, 10, "id");

        assertEquals(1, result.getContent().size());
        assertEquals("Laptop", result.getContent().get(0).getName());
    }

    // ─────────────────────────────────────────────
    // searchProducts()
    // ─────────────────────────────────────────────

    @Test
    void searchProducts_returnsMappedPage_forKeyword() {
        Product p = buildProduct(2L, "MacBook");
        p.setBrand("Apple");
        Page<Product> page = new PageImpl<>(List.of(p));

        when(productRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
                eq("mac"), eq("mac"), any(Pageable.class))).thenReturn(page);

        Page<ProductDTO> result = productService.searchProducts("mac", 0, 10);

        assertEquals(1, result.getContent().size());
        assertEquals("MacBook", result.getContent().get(0).getName());
    }

    @Test
    void searchProducts_returnsEmptyPage_whenNoMatch() {
        when(productRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
                anyString(), anyString(), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<ProductDTO> result = productService.searchProducts("xyz", 0, 10);

        assertTrue(result.isEmpty());
    }

    // ─────────────────────────────────────────────
    // filterProducts()
    // ─────────────────────────────────────────────

    @Test
    void filterProducts_delegatesToRepository_andReturnsMappedPage() {
        Product p = buildProduct(3L, "Samsung TV");
        Page<Product> page = new PageImpl<>(List.of(p));

        when(productRepository.filterProducts(
                eq(1L), eq("Samsung"), eq(100.0), eq(1000.0), eq(true), any(Pageable.class)))
                .thenReturn(page);

        Page<ProductDTO> result = productService.filterProducts(1L, "Samsung", 100.0, 1000.0, true, 0, 10, "price");

        assertEquals(1, result.getContent().size());
    }

    // ─────────────────────────────────────────────
    // getProductsByCategory()
    // ─────────────────────────────────────────────

    @Test
    void getProductsByCategory_success_returnsMappedPage() {
        Category category = new Category();
        category.setId(1L);

        Product p = buildProduct(4L, "Nike Shoes");
        Page<Product> page = new PageImpl<>(List.of(p));

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.findByCategoryId(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<ProductDTO> result = productService.getProductsByCategory(1L, 0, 10, "id");

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getProductsByCategory_throwsResourceNotFoundException_whenCategoryNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.getProductsByCategory(99L, 0, 10, "id"));
    }

    // ─────────────────────────────────────────────
    // createProduct()
    // ─────────────────────────────────────────────

    @Test
    void createProduct_success_withCategory() {
        Category cat = new Category();
        cat.setId(1L);
        cat.setName("Electronics");

        ProductDTO dto = buildDto("iPhone 15", 1L);

        Product saved = buildProduct(10L, "iPhone 15");
        saved.setCategory(cat);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductDTO result = productService.createProduct(dto);

        assertEquals("iPhone 15", result.getName());
        assertEquals("Electronics", result.getCategoryName());
    }

    @Test
    void createProduct_throwsResourceNotFoundException_whenCategoryNotFound() {
        ProductDTO dto = buildDto("Item", 99L);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.createProduct(dto));
    }

    @Test
    void createProduct_success_withoutCategory() {
        ProductDTO dto = buildDto("Generic Item", null);
        Product saved = buildProduct(11L, "Generic Item");

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductDTO result = productService.createProduct(dto);

        assertEquals("Generic Item", result.getName());
        verifyNoInteractions(categoryRepository);
    }

    // ─────────────────────────────────────────────
    // updateProduct()
    // ─────────────────────────────────────────────

    @Test
    void updateProduct_success_updatesFieldsAndSaves() {
        Product existing = buildProduct(1L, "OldName");
        ProductDTO dto = buildDto("NewName", null);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductDTO result = productService.updateProduct(1L, dto);

        assertEquals("NewName", result.getName());
    }

    @Test
    void updateProduct_throwsResourceNotFoundException_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(99L, new ProductDTO()));
    }

    // ─────────────────────────────────────────────
    // updateStock()
    // ─────────────────────────────────────────────

    @Test
    void updateStock_success_updatesStockValue() {
        Product p = buildProduct(1L, "Laptop");
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        ProductDTO result = productService.updateStock(1L, 50);

        assertEquals(50, result.getStock());
    }

    @Test
    void updateStock_throwsResourceNotFoundException_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateStock(99L, 10));
    }

    // ─────────────────────────────────────────────
    // deleteProduct()
    // ─────────────────────────────────────────────

    @Test
    void deleteProduct_success_callsDeleteById() {
        when(productRepository.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> productService.deleteProduct(1L));
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_throwsResourceNotFoundException_whenNotFound() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> productService.deleteProduct(99L));
        verify(productRepository, never()).deleteById(any());
    }
}