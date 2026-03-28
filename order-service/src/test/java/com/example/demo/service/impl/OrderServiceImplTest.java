package com.example.demo.service.impl;

// FILE LOCATION:
// order-service/src/test/java/com/example/demo/service/impl/OrderServiceImplTest.java

import com.example.demo.client.CatalogClient;
import com.example.demo.dto.CartRequest;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductPageResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Order;
import com.example.demo.exception.EmptyCartException;
import com.example.demo.exception.InvalidOrderStatusException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.messaging.OrderProducer;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private CartRepository cartRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private CatalogClient catalogClient;
    @Mock private OrderProducer orderProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    // ─────────────────────────────────────────────
    // addToCart()
    // ─────────────────────────────────────────────

    @Test
    void addToCart_success_savesCartAndReturnsMessage() {
        CartRequest req = new CartRequest();
        req.setProductName("iPhone");
        req.setQuantity(2);

        ProductDTO product = new ProductDTO();
        product.setId(10L);
        product.setName("iPhone");

        ProductPageResponse pageResponse = new ProductPageResponse();
        pageResponse.setContent(List.of(product));

        when(catalogClient.getProductByName("iPhone")).thenReturn(pageResponse);
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        String result = orderService.addToCart("user@example.com", req);

        assertEquals("Added to cart: iPhone", result);
        verify(cartRepository).save(argThat(c ->
                "user@example.com".equals(c.getUserEmail()) &&
                c.getProductId() == 10L &&
                c.getQuantity() == 2
        ));
    }

    @Test
    void addToCart_throwsIllegalArgument_whenProductNameIsBlank() {
        CartRequest req = new CartRequest();
        req.setProductName("  ");
        req.setQuantity(1);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.addToCart("user@example.com", req));
        verifyNoInteractions(catalogClient);
    }

    @Test
    void addToCart_throwsIllegalArgument_whenProductNameIsNull() {
        CartRequest req = new CartRequest();
        req.setProductName(null);
        req.setQuantity(1);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.addToCart("user@example.com", req));
    }

    @Test
    void addToCart_throwsIllegalArgument_whenQuantityIsZero() {
        CartRequest req = new CartRequest();
        req.setProductName("iPhone");
        req.setQuantity(0);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.addToCart("user@example.com", req));
    }

    @Test
    void addToCart_throwsIllegalArgument_whenQuantityIsNegative() {
        CartRequest req = new CartRequest();
        req.setProductName("iPhone");
        req.setQuantity(-3);

        assertThrows(IllegalArgumentException.class,
                () -> orderService.addToCart("user@example.com", req));
    }

    @Test
    void addToCart_throwsProductNotFoundException_whenCatalogReturnsEmpty() {
        CartRequest req = new CartRequest();
        req.setProductName("Ghost");
        req.setQuantity(1);

        ProductPageResponse emptyResponse = new ProductPageResponse();
        emptyResponse.setContent(Collections.emptyList());

        when(catalogClient.getProductByName("Ghost")).thenReturn(emptyResponse);

        assertThrows(ProductNotFoundException.class,
                () -> orderService.addToCart("user@example.com", req));
    }

    @Test
    void addToCart_throwsProductNotFoundException_whenCatalogReturnsNull() {
        CartRequest req = new CartRequest();
        req.setProductName("Ghost");
        req.setQuantity(1);

        when(catalogClient.getProductByName("Ghost")).thenReturn(null);

        assertThrows(ProductNotFoundException.class,
                () -> orderService.addToCart("user@example.com", req));
    }

    // ─────────────────────────────────────────────
    // viewCart()
    // ─────────────────────────────────────────────

    @Test
    void viewCart_returnsCartItems_forUser() {
        Cart cart = new Cart();
        cart.setUserEmail("user@example.com");
        cart.setProductName("iPhone");
        cart.setQuantity(1);

        when(cartRepository.findByUserEmail("user@example.com")).thenReturn(List.of(cart));

        List<?> result = orderService.viewCart("user@example.com");

        assertEquals(1, result.size());
    }

    @Test
    void viewCart_returnsEmptyList_whenCartIsEmpty() {
        when(cartRepository.findByUserEmail("user@example.com")).thenReturn(Collections.emptyList());

        List<?> result = orderService.viewCart("user@example.com");

        assertTrue(result.isEmpty());
    }

    // ─────────────────────────────────────────────
    // placeOrder()
    // ─────────────────────────────────────────────

    @Test
    void placeOrder_success_savesOrderAndClearsCart() {
        Cart cartItem = new Cart();
        cartItem.setUserEmail("user@example.com");
        cartItem.setProductId(1L);
        cartItem.setProductName("iPhone");
        cartItem.setQuantity(2);

        Order savedOrder = new Order();
        savedOrder.setId(100L);
        savedOrder.setUserEmail("user@example.com");
        savedOrder.setStatus("PLACED");

        when(cartRepository.findByUserEmail("user@example.com")).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(orderProducer).sendOrderEvent(any());

        Order result = orderService.placeOrder("user@example.com");

        assertNotNull(result);
        assertEquals("PLACED", result.getStatus());
        assertEquals("user@example.com", result.getUserEmail());
        verify(cartRepository).deleteAll(List.of(cartItem));
        verify(orderProducer).sendOrderEvent(any());
    }

    @Test
    void placeOrder_throwsEmptyCartException_whenCartIsEmpty() {
        when(cartRepository.findByUserEmail("user@example.com")).thenReturn(Collections.emptyList());

        assertThrows(EmptyCartException.class,
                () -> orderService.placeOrder("user@example.com"));
        verifyNoInteractions(orderRepository, orderProducer);
    }

    // ─────────────────────────────────────────────
    // getMyOrders()
    // ─────────────────────────────────────────────

    @Test
    void getMyOrders_returnsOrdersForUser() {
        Order order = new Order();
        order.setUserEmail("user@example.com");

        when(orderRepository.findByUserEmail("user@example.com")).thenReturn(List.of(order));

        List<Order> result = orderService.getMyOrders("user@example.com");

        assertEquals(1, result.size());
    }

    @Test
    void getMyOrders_returnsEmptyList_whenNoOrders() {
        when(orderRepository.findByUserEmail("user@example.com")).thenReturn(Collections.emptyList());

        List<Order> result = orderService.getMyOrders("user@example.com");

        assertTrue(result.isEmpty());
    }

    // ─────────────────────────────────────────────
    // updateStatus()
    // ─────────────────────────────────────────────

    @Test
    void updateStatus_success_updatesAndSavesOrder() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus("PLACED");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.updateStatus(1L, "shipped");

        assertEquals("SHIPPED", result.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateStatus_throwsOrderNotFoundException_whenIdNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateStatus(99L, "CONFIRMED"));
    }

    @Test
    void updateStatus_throwsInvalidOrderStatusException_whenStatusIsInvalid() {
        assertThrows(InvalidOrderStatusException.class,
                () -> orderService.updateStatus(1L, "FLYING"));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void updateStatus_acceptsAllValidStatuses() {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        for (String status : List.of("PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED")) {
            Order result = orderService.updateStatus(1L, status);
            assertEquals(status, result.getStatus());
        }
    }

    // ─────────────────────────────────────────────
    // getAllOrders()
    // ─────────────────────────────────────────────

    @Test
    void getAllOrders_returnsAllOrders() {
        Order o1 = new Order();
        Order o2 = new Order();
        when(orderRepository.findAll()).thenReturn(List.of(o1, o2));

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
    }
}