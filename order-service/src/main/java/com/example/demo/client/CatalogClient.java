package com.example.demo.client;

import com.example.demo.config.FeignConfig;
import com.example.demo.dto.ProductPageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "catalog-service",
        configuration = FeignConfig.class
)
public interface CatalogClient {

    // ✅ FIX: Remove /catalog prefix — Feign calls service directly
    // Catalog controller is mapped to /products/search NOT /catalog/products/search
    @GetMapping("/products/search")
    ProductPageResponse getProductByName(@RequestParam String keyword);
}