package com.datn.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByRestaurantId(Long id);
	
	List<Category> findByNameContainingIgnoreCase(String name);
	
	 Page<Category> findByRestaurantId(Long restaurantId, Pageable pageable);
}
