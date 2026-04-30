package com.cashinvoice.order.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.cashinvoice.order.dto.OrderRequest;
import com.cashinvoice.order.dto.OrderResponse;
import com.cashinvoice.order.exception.OrderNotFoundException;
import com.cashinvoice.order.model.Order;
import com.cashinvoice.order.storage.InMemoryOrderStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final InMemoryOrderStore orderStore;

    public OrderResponse createOrder(OrderRequest request, String username) {
        String orderId = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.now();

        Order order = Order.builder()
                .orderId(orderId)
                .customerId(request.getCustomerId())
                .product(request.getProduct())
                .amount(request.getAmount())
                .status("CREATED")
                .createdAt(createdAt)
                .createdBy(username)
                .build();

        orderStore.save(order);
        log.info("Order created | OrderId={} | CustomerId={} | CreatedBy={}", 
                orderId, request.getCustomerId(), username);

        return new OrderResponse(orderId, "CREATED");
    }

    public Order getOrderById(String orderId, String username, String role) {
        Order order = orderStore.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        if ("ROLE_USER".equals(role) && !order.getCreatedBy().equals(username)) {
            throw new OrderNotFoundException("Order not found: " + orderId);
        }

        return order;
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        return orderStore.findByCustomerId(customerId);
    }
}