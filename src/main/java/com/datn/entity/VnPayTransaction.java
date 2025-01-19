package com.datn.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VnPayTransaction {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String txnRef; // Tên trường chính xác

    private Long amount;
    private String orderInfo;
    private String transactionStatus;
    private String secureHash;
    private String responseCode;
    private String bankCode;
    private String bankTranNo;
    private String transactionNo;
    private String cardType;
    private LocalDateTime payDate;
    private LocalDateTime createdAt;
	
}
