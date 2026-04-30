package com.cashinvoice.order.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cashinvoice.order.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
public class FileToQueueRoute extends RouteBuilder {

    @Value("${order.file.input}")
    private String inputPath;

    @Value("${order.file.error}")
    private String errorPath;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
            .handled(true)
            .process(exchange -> {
                Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
                
                log.error("ERROR processing file: {} | {}", fileName, cause.getMessage());
                
                File sourceFile = exchange.getIn().getBody(File.class);
                if (sourceFile != null && sourceFile.exists()) {
                    Path errorDir = Paths.get(errorPath);
                    if (!Files.exists(errorDir)) {
                        Files.createDirectories(errorDir);
                    }
                    Path targetPath = errorDir.resolve(sourceFile.getName());
                    Files.move(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    log.info("Moved failed file to: {}", targetPath);
                }
            })
            .end();

        
        from("file:" + inputPath + "?delete=true&delay=5000&include=.*\\.json$")
            .routeId("file-to-artemis")
            .log("Picked up file: ${header.CamelFileName}")
            .process(exchange -> {
                String json = exchange.getIn().getBody(String.class);
                String fileName = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
                
                Order order = objectMapper.readValue(json, Order.class);
                
                if (order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
                    throw new IllegalArgumentException("orderId is required in file: " + fileName);
                }
                if (order.getCustomerId() == null || order.getCustomerId().trim().isEmpty()) {
                    throw new IllegalArgumentException("customerId is required in file: " + fileName);
                }
                if (order.getAmount() <= 0) {
                    throw new IllegalArgumentException("amount must be greater than 0 in file: " + fileName);
                }
                
                log.info("File validated | File={} | OrderId={}", fileName, order.getOrderId());
                exchange.getIn().setBody(json);
            })
            .to("artemis:queue:ORDER.CREATED.QUEUE")  //
            .log("Message sent to ORDER.CREATED.QUEUE");
    }
}