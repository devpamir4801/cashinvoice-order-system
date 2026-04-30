package com.cashinvoice.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "customerId must not be null or empty")
    private String customerId;

    @NotBlank(message = "product must not be null or empty")
    private String product;

    @Positive(message = "amount must be greater than 0")
    private double amount;
}

