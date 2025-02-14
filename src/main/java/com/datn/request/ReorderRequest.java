package com.datn.request;

import lombok.Data;

@Data
public class ReorderRequest {
    private String paymentMethod;
    private int addressId;
}
