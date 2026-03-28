package com.example.demo.controller;

import com.example.demo.dto.CartRequest;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ── User-facing endpoints (visible in Swagger) ────────────────────────

    @PostMapping("/cart")
    public String addToCart(@RequestBody CartRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.addToCart(email, request);
    }

    @GetMapping("/cart")
    public List<?> viewCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.viewCart(email);
    }

    @PostMapping("/place")
    public Order placeOrder() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.placeOrder(email);
    }

    @GetMapping("/my")
    public List<Order> myOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.getMyOrders(email);
    }

    // ── Internal endpoints — admin-service Feign only, hidden from Swagger ─

    @Hidden
    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Hidden
    @PutMapping("/{id}/status")
    public Order updateStatus(@PathVariable Long id,
                              @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }
}