package com.cashinvoice.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cashinvoice.order.dto.OrderRequest;
import com.cashinvoice.order.dto.OrderResponse;
import com.cashinvoice.order.model.Order;
import com.cashinvoice.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            Authentication auth) {

        log.debug("Create order request from user: {}", auth.getName());
        OrderResponse response = orderService.createOrder(request, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Order> getOrderById(
            @PathVariable String orderId,
            Authentication auth) {

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("");

        log.debug("Get order request - OrderId={} | User={} | Role={}", orderId, auth.getName(), role);
        
        Order order = orderService.getOrderById(orderId, auth.getName(), role);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<Order>> listOrders(
            @RequestParam String customerId,
            Authentication auth) {

        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("");
        
        String username = auth.getName();
        
        log.debug("List orders request - CustomerId={} | User={} | Role={}", customerId, username, role);
        
        List<Order> userOrders = orderService.getOrdersByCustomer(customerId);
        
        if ("ROLE_USER".equals(role)) {
            boolean hasAccess = userOrders.stream()
                    .anyMatch(order -> order.getCreatedBy().equals(username));
            
            if (!hasAccess) {
                log.warn("User {} attempted to access orders for customerId {} without authorization", 
                        username, customerId);
                return ResponseEntity.ok(List.of());
            }
        }
        
        return ResponseEntity.ok(userOrders);
    }
}