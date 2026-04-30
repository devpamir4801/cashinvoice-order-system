package com.cashinvoice.order.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DirectorySetupRunner implements ApplicationRunner {

    @Value("${order.file.input}")
    private String inputPath;

    @Value("${order.file.error}")
    private String errorPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Path inputDir = Paths.get(inputPath);
        Path errorDir = Paths.get(errorPath);
        
        if (!Files.exists(inputDir)) {
            Files.createDirectories(inputDir);
            log.info("Created input directory: {}", inputDir.toAbsolutePath());
        }
        
        if (!Files.exists(errorDir)) {
            Files.createDirectories(errorDir);
            log.info("Created error directory: {}", errorDir.toAbsolutePath());
        }
        
        log.info("Order Processing Application started successfully");
        log.info("Input directory: {}", inputDir.toAbsolutePath());
        log.info("Error directory: {}", errorDir.toAbsolutePath());
    }
}