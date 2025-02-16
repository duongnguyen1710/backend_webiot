package com.datn.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private Long productId;
    private String productName;
    private Long userId;
    private String userName;
    private double stars;
    private String comment;
    private LocalDateTime createAt;
}
