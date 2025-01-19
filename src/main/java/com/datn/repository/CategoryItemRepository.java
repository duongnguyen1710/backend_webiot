package com.datn.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.CategoryItem;

public interface CategoryItemRepository extends JpaRepository<CategoryItem, Long> {
	List<CategoryItem> findByRestaurantId(Long id);
	
	List<CategoryItem> findByCategoryIdAndRestaurantId(Long categoryId, Long restaurantId);
	
	Page<CategoryItem> findByRestaurantId(Long restaurantId, Pageable pageable);
}
