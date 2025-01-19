package com.datn.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private double stars; // Số sao đánh giá (1-5)

    private String comment; // Nội dung đánh giá

    @ManyToOne
    private Product product; // Sản phẩm được đánh giá

    @ManyToOne
    private User user; // Người dùng đánh giá

    private LocalDateTime createAt; // Thời gian đánh giá
}
