package com.datn.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;
import com.datn.entity.Product;
import com.datn.entity.Restaurant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByRestaurantId(Long restaurantId);
	
	 List<Product> findByCategoryIdAndRestaurantId(Long categoryId, Long restaurantId);

	List<Product> findTop8ByRestaurantIdAndStatusOrderByCreateAtDesc(Long restaurantId, int status);

	List<Product> findByRestaurantAndCategory(Restaurant restaurant, Category category); 
	
	List<Product> findByCategoryItem(CategoryItem categoryItem);
	
	List<Product> findByNameContainingIgnoreCase(String name);

	Page<Product> findByCategoryIdAndRestaurantIdAndStatus(Long categoryId, Long restaurantId, int status, Pageable pageable);

	List<Product> findByCategoryItemIdAndPriceBetween(Long categoryItemId, Double minPrice, Double maxPrice);
		
		List<Product> findByCategoryItemId(Long categoryItemId);
		
		 Page<Product> findByRestaurantId(Long restaurantId, Pageable pageable);

	Page<Product> findByStatus(int status, Pageable pageable);

	long countByStatus(int status);

	@Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
	Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);



}
