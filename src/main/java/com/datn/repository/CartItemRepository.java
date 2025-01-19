package com.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
