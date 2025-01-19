package com.datn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;
import com.datn.service.CategoryService;

@RestController
@CrossOrigin
@RequestMapping("/category")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping("/restaurant/{id}/category")
	public ResponseEntity<List<Category>> getCategory(
			@PathVariable Long id) throws Exception{
		List<Category> items = categoryService.findCategoryByRestaurantId(id);
		return new ResponseEntity<>(items, HttpStatus.OK);
	}
	
	@GetMapping("/{categoryId}/{restaurantId}")
    public List<CategoryItem> getCategoryItemsByCategoryIdAndRestaurantId(@PathVariable Long categoryId, @PathVariable Long restaurantId) {
        return categoryService.getCategoryItemsByCategoryIdAndRestaurantId(categoryId, restaurantId);
    }
}
