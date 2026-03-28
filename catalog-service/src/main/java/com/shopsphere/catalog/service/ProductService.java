package com.shopsphere.catalog.service;

import com.shopsphere.catalog.dto.ProductDTO;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductDTO getProductById(Long id);
    Page<ProductDTO> getAllProducts(int page, int size, String sortBy);
    Page<ProductDTO> searchProducts(String keyword, int page, int size);
    Page<ProductDTO> filterProducts(Long categoryId, String brand, Double minPrice,
                                    Double maxPrice, Boolean active, int page, int size, String sortBy);
    Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size, String sortBy);

    // Admin
    ProductDTO createProduct(ProductDTO dto);
    ProductDTO updateProduct(Long id, ProductDTO dto);
    ProductDTO updateStock(Long id, int quantity);
    void deleteProduct(Long id);
}