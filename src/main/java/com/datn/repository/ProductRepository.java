package com.datn.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;
import com.datn.entity.Product;
import com.datn.entity.Restaurant;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByRestaurantId(Long restaurantId);
	
	 List<Product> findByCategoryIdAndRestaurantId(Long categoryId, Long restaurantId);
	
	List<Product> findTop8ByRestaurantIdOrderByCreateAtDesc(Long restaurantId);
	 
	List<Product> findByRestaurantAndCategory(Restaurant restaurant, Category category); 
	
	List<Product> findByCategoryItem(CategoryItem categoryItem);
	
	List<Product> findByNameContainingIgnoreCase(String name);
	
	 Page<Product> findByCategoryIdAndRestaurantId(Long categoryId, Long restaurantId, Pageable pageable);
	 
	 List<Product> findByCategoryItemIdAndPriceBetween(Long categoryItemId, Double minPrice, Double maxPrice);
		
		List<Product> findByCategoryItemId(Long categoryItemId);
		
		 Page<Product> findByRestaurantId(Long restaurantId, Pageable pageable);
}
