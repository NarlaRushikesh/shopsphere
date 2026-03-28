package com.example.demo.service;

import com.example.demo.dto.CartRequest;
import com.example.demo.entity.Order;

import java.util.List;

public interface OrderService {

    String addToCart(String email, CartRequest request);

    List<?> viewCart(String email);

    Order placeOrder(String email);

    List<Order> getMyOrders(String email);

    Order updateStatus(Long id, String status);

    List<Order> getAllOrders();  // ✅ NEW — for admin
}