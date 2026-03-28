package com.example.demo.dto;

import java.util.List;

public class ProductPageResponse {

    private List<ProductDTO> content;

    public List<ProductDTO> getContent() {
        return content;
    }

    public void setContent(List<ProductDTO> content) {
        this.content = content;
    }
}