package com.datn.repository;

import com.datn.entity.ZaloPayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZaloPayRepository extends JpaRepository<ZaloPayTransaction, Long> {
    Optional<ZaloPayTransaction> findTopByAppTransIdOrderByIdDesc(String appTransId);
}
