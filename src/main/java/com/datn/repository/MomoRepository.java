package com.datn.repository;

import com.datn.entity.MomoTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MomoRepository extends JpaRepository<MomoTransaction, Long> {
    MomoTransaction findByOrderId(String orderId);

    Optional<MomoTransaction> findTopByOrderIdOrderByIdDesc(String orderId);
}
