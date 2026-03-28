package com.example.demo.service.impl;

import com.example.demo.client.CatalogClient;
import com.example.demo.dto.CartRequest;
import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ProductPageResponse;
import com.example.demo.entity.Cart;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.event.OrderEvent;
import com.example.demo.exception.EmptyCartException;
import com.example.demo.exception.InvalidOrderStatusException;
import com.example.demo.exception.OrderNotFoundException;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.messaging.OrderProducer;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private static final List<String> VALID_STATUSES =
            List.of("PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");

    @Autowired private CartRepository  cartRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private CatalogClient   catalogClient;
    @Autowired private OrderProducer   orderProducer;

    @Override
    public String addToCart(String email, CartRequest request) {

        if (request.getProductName() == null || request.getProductName().isBlank())
            throw new IllegalArgumentException("Product name must not be blank");
        if (request.getQuantity() <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0");

        ProductPageResponse response = catalogClient.getProductByName(request.getProductName());

        if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
            throw new ProductNotFoundException(request.getProductName());
        }

        ProductDTO product = response.getContent().get(0);

        Cart cart = new Cart();
        cart.setUserEmail(email);
        cart.setProductId(product.getId());
        cart.setProductName(product.getName());
        cart.setQuantity(request.getQuantity());
        cartRepository.save(cart);

        return "Added to cart: " + product.getName();
    }

    @Override
    public List<?> viewCart(String email) {
        return cartRepository.findByUserEmail(email);
    }

    @Transactional
    @Override
    public Order placeOrder(String email) {

        List<Cart> cartItems = cartRepository.findByUserEmail(email);

        // 400 — cart empty
        if (cartItems.isEmpty()) {
            throw new EmptyCartException(email);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (Cart cart : cartItems) {
            OrderItem item = new OrderItem();
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProductName());
            item.setQuantity(cart.getQuantity());
            orderItems.add(item);
        }

        Order order = new Order();
        order.setUserEmail(email);
        order.setStatus("PLACED");
        order.setTotalAmount(total);
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartRepository.deleteAll(cartItems);

        orderProducer.sendOrderEvent(new OrderEvent(
                savedOrder.getId(),
                savedOrder.getUserEmail(),
                savedOrder.getTotalAmount(),
                savedOrder.getStatus()
        ));

        return savedOrder;
    }

    @Override
    public List<Order> getMyOrders(String email) {
        return orderRepository.findByUserEmail(email);
    }

    @Override
    public Order updateStatus(Long id, String status) {

        // 400 — invalid status value
        if (!VALID_STATUSES.contains(status.toUpperCase())) {
            throw new InvalidOrderStatusException(status);
        }

        // 404 — order not found
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        order.setStatus(status.toUpperCase());
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}