package com.datn.repository;

import com.datn.entity.ZaloPayTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZaloPayRepository extends JpaRepository<ZaloPayTransaction, Long> {
    ZaloPayTransaction findByAppTransId(String appTransId);
}
