package com.example.demo.service.impl;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.dto.CategoryPageResponse;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductPageResponse;
import com.example.demo.feign.CatalogClient;
import com.example.demo.feign.OrderClient;
import com.example.demo.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired private CatalogClient catalogClient;
    @Autowired private OrderClient   orderClient;

    @Override
    public ProductPageResponse getAllProducts(int page, int size, String sortBy) {
        return catalogClient.getAllProducts(page, size, sortBy);
    }

    @Override
    public ProductDTO createProduct(ProductDTO product) {
        return catalogClient.createProduct(product);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO product) {
        return catalogClient.updateProduct(id, product);
    }

    @Override
    public ProductDTO updateStock(Long id, int quantity) {
        return catalogClient.updateStock(id, quantity);
    }

    @Override
    public void deleteProduct(Long id) {
        catalogClient.deleteProduct(id);
    }

    @Override
    public CategoryPageResponse getAllCategories(String name, int page, int size,
                                                  String sortBy, String direction) {
        return catalogClient.getAllCategories(name, page, size, sortBy, direction);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO category) {
        return catalogClient.createCategory(category);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO category) {
        return catalogClient.updateCategory(id, category);
    }

    @Override
    public void deleteCategory(Long id) {
        catalogClient.deleteCategory(id);
    }

    @Override
    public List<Object> getAllOrders() {
        return orderClient.getAllOrders();
    }

    @Override
    public Object updateOrderStatus(Long id, String status) {
        return orderClient.updateStatus(id, status);
    }
}