package com.example.demo.service;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.CategoryPageResponse;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductPageResponse;
import java.util.List;

public interface AdminService {

    ProductPageResponse getAllProducts(int page, int size, String sortBy);
    ProductDTO createProduct(ProductDTO product);
    ProductDTO updateProduct(Long id, ProductDTO product);
    ProductDTO updateStock(Long id, int quantity);
    void deleteProduct(Long id);

    CategoryPageResponse getAllCategories(String name, int page, int size, String sortBy, String direction);
    CategoryDTO createCategory(CategoryDTO category);
    CategoryDTO updateCategory(Long id, CategoryDTO category);
    void deleteCategory(Long id);

    List<Object> getAllOrders();
    Object updateOrderStatus(Long id, String status);
}