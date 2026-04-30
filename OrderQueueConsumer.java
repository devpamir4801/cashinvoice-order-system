package com.cashinvoice.order.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cashinvoice.order.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderQueueConsumer extends RouteBuilder {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configure() throws Exception {
        
        
        from("artemis:queue:ORDER.CREATED.QUEUE")
            .routeId("artemis-consumer")
            .process(exchange -> {
                String json = exchange.getIn().getBody(String.class);
                
                Order order = objectMapper.readValue(json, Order.class);
                
                log.info("Order processed | OrderId={} | CustomerId={} | Amount={}", 
                        order.getOrderId(),
                        order.getCustomerId(),
                        order.getAmount());
            })
            .log("Order acknowledged from queue successfully");
    }
}