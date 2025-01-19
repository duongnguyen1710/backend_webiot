package com.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	public Cart findByCustomerId(Long userId);
}

