package com.datn.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetryPaymentRequest {
    private String paymentMethod;
    private int addressId;
}
