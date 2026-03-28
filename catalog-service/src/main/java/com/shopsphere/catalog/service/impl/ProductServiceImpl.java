package com.shopsphere.catalog.service.impl;

import com.shopsphere.catalog.dto.ProductDTO;
import com.shopsphere.catalog.entity.Category;
import com.shopsphere.catalog.entity.Product;
import com.shopsphere.catalog.exception.ResourceNotFoundException;
import com.shopsphere.catalog.repository.CategoryRepository;
import com.shopsphere.catalog.repository.ProductRepository;
import com.shopsphere.catalog.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

    private ProductDTO toDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setSku(p.getSku());
        dto.setBrand(p.getBrand());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        dto.setImageUrl(p.getImageUrl());
        dto.setActive(p.isActive());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getUpdatedAt());
        if (p.getCategory() != null) {
            dto.setCategoryId(p.getCategory().getId());
            dto.setCategoryName(p.getCategory().getName());
        }
        return dto;
    }

    private void applyDTO(ProductDTO dto, Product p) {
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setSku(dto.getSku());
        p.setBrand(dto.getBrand());
        p.setPrice(dto.getPrice());
        if (dto.getStock() != null) p.setStock(dto.getStock());
        p.setImageUrl(dto.getImageUrl());
        p.setActive(dto.isActive());
        if (dto.getCategoryId() != null) {
            Category cat = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + dto.getCategoryId()));
            p.setCategory(cat);
        }
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return toDTO(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id)));
    }

    @Override
    public Page<ProductDTO> getAllProducts(int page, int size, String sortBy) {
        return productRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy))).map(this::toDTO);
    }

    @Override
    public Page<ProductDTO> searchProducts(String keyword, int page, int size) {
        return productRepository
                .findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
                        keyword, keyword, PageRequest.of(page, size))
                .map(this::toDTO);
    }

    @Override
    public Page<ProductDTO> filterProducts(Long categoryId, String brand, Double minPrice,
                                           Double maxPrice, Boolean active,
                                           int page, int size, String sortBy) {
        return productRepository
                .filterProducts(categoryId, brand, minPrice, maxPrice, active,
                        PageRequest.of(page, size, Sort.by(sortBy)))
                .map(this::toDTO);
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size, String sortBy) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
        return productRepository
                .findByCategoryId(categoryId, PageRequest.of(page, size, Sort.by(sortBy)))
                .map(this::toDTO);
    }

    @Override
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = new Product();
        applyDTO(dto, product);
        return toDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        applyDTO(dto, product);
        return toDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO updateStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        product.setStock(quantity);
        return toDTO(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id))
            throw new ResourceNotFoundException("Product not found: " + id);
        productRepository.deleteById(id);
    }
}