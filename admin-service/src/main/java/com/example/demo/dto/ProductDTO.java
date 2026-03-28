package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private String sku;
    private String brand;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private boolean active;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductDTO() {}

    public Long getId()                        { return id; }
    public void setId(Long id)                 { this.id = id; }
    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public String getDescription()             { return description; }
    public void setDescription(String d)       { this.description = d; }
    public String getSku()                     { return sku; }
    public void setSku(String sku)             { this.sku = sku; }
    public String getBrand()                   { return brand; }
    public void setBrand(String brand)         { this.brand = brand; }
    public Double getPrice()                   { return price; }
    public void setPrice(Double price)         { this.price = price; }
    public Integer getStock()                  { return stock; }
    public void setStock(Integer stock)        { this.stock = stock; }
    public String getImageUrl()                { return imageUrl; }
    public void setImageUrl(String imageUrl)   { this.imageUrl = imageUrl; }
    public boolean isActive()                  { return active; }
    public void setActive(boolean active)      { this.active = active; }
    public Long getCategoryId()                { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName()            { return categoryName; }
    public void setCategoryName(String n)      { this.categoryName = n; }
    public LocalDateTime getCreatedAt()        { return createdAt; }
    public void setCreatedAt(LocalDateTime t)  { this.createdAt = t; }
    public LocalDateTime getUpdatedAt()        { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)  { this.updatedAt = t; }
}