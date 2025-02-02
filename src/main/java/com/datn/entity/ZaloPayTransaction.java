package com.datn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZaloPayTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String appTransId; // Mã giao dịch của ứng dụng

    @Column(nullable = false)
    private Long appId; // ID của ứng dụng

    @Column(nullable = false)
    private Integer status; // Trạng thái thanh toán (1 = thành công)

    @Column(nullable = false)
    private Long amount; // Số tiền thanh toán

    @Column(nullable = false)
    private String bankCode; // Mã ngân hàng

    @Column(nullable = false)
    private String pmcId; // Mã phương thức thanh toán

    @Column(nullable = false)
    private Long discountAmount; // Số tiền giảm giá

    @Column(nullable = false, unique = true)
    private String checksum; // Mã xác thực giao dịch

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Thời gian tạo giao dịch
}
