package com.cashinvoice.order.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.cashinvoice.order.model.Order;

@Component
public class InMemoryOrderStore {

    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();

    public void save(Order order) {
        orderMap.put(order.getOrderId(), order);
    }

    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orderMap.get(orderId));
    }

    public List<Order> findByCustomerId(String customerId) {
        return orderMap.values().stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Order> findAll() {
        return new ArrayList<>(orderMap.values());
    }
}

