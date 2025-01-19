package com.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	public User findByEmail(String username);
	
}
