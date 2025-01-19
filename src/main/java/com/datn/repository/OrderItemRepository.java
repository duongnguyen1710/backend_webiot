package com.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	
}
