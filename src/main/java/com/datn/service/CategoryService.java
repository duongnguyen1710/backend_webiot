package com.datn.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;

public interface CategoryService {
	public Category createCategory(String name, Long restaurantId) throws Exception;

	public Category findCategoryById(Long id) throws Exception;

	public List<Category> findCategoryByRestaurantId(Long id) throws Exception;

	public CategoryItem createCategoryItem(Long restaurantId, String categoryName, Long categoryId)
			throws Exception;

	public List<CategoryItem> findRestaurantsCategory(Long restaurantId);
	
	public List<CategoryItem> getCategoryItemsByCategoryIdAndRestaurantId(Long categoryId, Long restaurantId);

	//public CategoryItem updateStock(Long id) throws Exception;
	
	void deleteCategoryById(Long id) throws Exception;
    Category saveCategory(Category category) throws Exception;
    public List<Category> searchCategoriesByName(String name) throws Exception;
    
    Page<CategoryItem> findRestaurantsCategory(Long restaurantId, Pageable pageable);
    
    Page<Category> findCategoryByRestaurantId(Long restaurantId, Pageable pageable);
}
