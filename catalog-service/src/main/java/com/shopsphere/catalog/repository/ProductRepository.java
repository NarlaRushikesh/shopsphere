package com.shopsphere.catalog.repository;

import com.shopsphere.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search by name or brand (used for the /search endpoint)
    Page<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
            String name, String brand, Pageable pageable);

    // Browse all products under a specific category
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // Filter: all optional — any combination of category, brand, price range, active status
    @Query("""
        SELECT p FROM Product p
        WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:brand      IS NULL OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :brand, '%')))
          AND (:minPrice   IS NULL OR p.price >= :minPrice)
          AND (:maxPrice   IS NULL OR p.price <= :maxPrice)
          AND (:active     IS NULL OR p.active = :active)
    """)
    Page<Product> filterProducts(
            @Param("categoryId") Long categoryId,
            @Param("brand")      String brand,
            @Param("minPrice")   Double minPrice,
            @Param("maxPrice")   Double maxPrice,
            @Param("active")     Boolean active,
            Pageable pageable
    );
}