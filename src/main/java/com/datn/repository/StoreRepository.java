package com.datn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.datn.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
	@Query("SELECT r FROM Store r WHERE lower(r.name) LIKE lower(concat('%', :query, '%'))")
	List<Store> findBySearchQuery(String query);

	Store findByOwnerId(Long userId);
}
