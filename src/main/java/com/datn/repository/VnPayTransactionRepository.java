package com.datn.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.VnPayTransaction;

public interface VnPayTransactionRepository extends JpaRepository<VnPayTransaction, Long> {

    Optional<VnPayTransaction> findByTxnRef(String txnRef); // Tên trường phải khớp với entity

}
