package com.datn.request;

import lombok.Data;

@Data
public class RatingProductRequest {
    private int productId;
    private int stars;
    private String comment;
}
