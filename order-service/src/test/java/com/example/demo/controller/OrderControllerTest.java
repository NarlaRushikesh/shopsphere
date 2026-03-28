package com.example.demo.controller;

// FILE LOCATION:
// order-service/src/test/java/com/example/demo/controller/OrderControllerTest.java

import com.example.demo.dto.CartRequest;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private static final String USER_EMAIL = "user@example.com";

    @BeforeEach
    void setSecurityContext() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(USER_EMAIL, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ─────────────────────────────────────────────
    // addToCart()
    // ─────────────────────────────────────────────

    @Test
    void addToCart_delegatesToService_withEmailFromContext() {
        CartRequest req = new CartRequest();
        req.setProductName("Laptop");
        req.setQuantity(1);

        when(orderService.addToCart(USER_EMAIL, req)).thenReturn("Added to cart: Laptop");

        String result = orderController.addToCart(req);

        assertEquals("Added to cart: Laptop", result);
        verify(orderService).addToCart(USER_EMAIL, req);
    }

    // ─────────────────────────────────────────────
    // viewCart()
    // ─────────────────────────────────────────────

    @Test
    void viewCart_delegatesToService_andReturnsItems() {
        Cart cart = new Cart();
        cart.setProductName("Laptop");
        // Use ArrayList (raw List) to avoid wildcard capture mismatch with List<?>
        ArrayList<Object> items = new ArrayList<>();
        items.add(cart);
        doReturn(items).when(orderService).viewCart(USER_EMAIL);

        List<?> result = orderController.viewCart();

        assertEquals(1, result.size());
        verify(orderService).viewCart(USER_EMAIL);
    }

    @Test
    void viewCart_returnsEmptyList_whenCartIsEmpty() {
        doReturn(new ArrayList<>()).when(orderService).viewCart(USER_EMAIL);

        List<?> result = orderController.viewCart();

        assertTrue(result.isEmpty());
    }

    // ─────────────────────────────────────────────
    // placeOrder()
    // ─────────────────────────────────────────────

    @Test
    void placeOrder_delegatesToService_andReturnsOrder() {
        Order order = new Order();
        order.setStatus("PLACED");
        when(orderService.placeOrder(USER_EMAIL)).thenReturn(order);

        Order result = orderController.placeOrder();

        assertEquals("PLACED", result.getStatus());
        verify(orderService).placeOrder(USER_EMAIL);
    }

    // ─────────────────────────────────────────────
    // myOrders()
    // ─────────────────────────────────────────────

    @Test
    void myOrders_delegatesToService_andReturnsOrderList() {
        Order order = new Order();
        order.setUserEmail(USER_EMAIL);
        when(orderService.getMyOrders(USER_EMAIL)).thenReturn(List.of(order));

        List<Order> result = orderController.myOrders();

        assertEquals(1, result.size());
    }

    // ─────────────────────────────────────────────
    // getAllOrders()
    // ─────────────────────────────────────────────

    @Test
    void getAllOrders_returnsAllOrders() {
        when(orderService.getAllOrders()).thenReturn(List.of(new Order(), new Order()));

        List<Order> result = orderController.getAllOrders();

        assertEquals(2, result.size());
    }

    // ─────────────────────────────────────────────
    // updateStatus()
    // ─────────────────────────────────────────────

    @Test
    void updateStatus_delegatesToService_andReturnsUpdatedOrder() {
        Order updated = new Order();
        updated.setStatus("SHIPPED");
        when(orderService.updateStatus(1L, "SHIPPED")).thenReturn(updated);

        Order result = orderController.updateStatus(1L, "SHIPPED");

        assertEquals("SHIPPED", result.getStatus());
        verify(orderService).updateStatus(1L, "SHIPPED");
    }
}