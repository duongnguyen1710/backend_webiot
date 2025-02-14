package com.datn.repository;

import com.datn.entity.MomoTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MomoRepository extends JpaRepository<MomoTransaction, Long> {
    MomoTransaction findByOrderId(String orderId);
}
