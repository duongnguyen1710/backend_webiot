package com.datn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;
import com.datn.request.CategoryItemRequest;
import com.datn.request.CategoryRequest;
import com.datn.service.CategoryService;

@RestController
@RequestMapping("/api/admin/category")
@CrossOrigin
public class AdminCategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@PostMapping("/category")
	public ResponseEntity<Category> createCategory(
			@RequestBody CategoryRequest req) throws Exception{
		Category item = categoryService.createCategory(req.getName(), req.getRestaurantId());
		return new ResponseEntity<>(item, HttpStatus.CREATED);
	}
	
	@PostMapping()
	public ResponseEntity<CategoryItem> createCategoryItem(
			@RequestBody CategoryItemRequest req) throws Exception{
		CategoryItem item = categoryService.createCategoryItem(req.getRestaurantId(),req.getName(), req.getCategoryId());
		return new ResponseEntity<>(item, HttpStatus.CREATED);
	}
	
//	@PutMapping("/{id}/stoke")
//	public ResponseEntity<IngredientsItem> updateIngredientStock(
//			@PathVariable Long id) throws Exception{
//		IngredientsItem item = ingredientsService.updateStock(id);
//		return new ResponseEntity<>(item, HttpStatus.OK);
//	}
	
	@GetMapping("/restaurant/{id}")
	public ResponseEntity<List<CategoryItem>> getCategoryItem(
			@PathVariable Long id) throws Exception{
		List<CategoryItem> items = categoryService.findRestaurantsCategory(id);
		return new ResponseEntity<>(items, HttpStatus.OK);
	}
	
	@GetMapping("/page/restaurant/{id}")
	public ResponseEntity<Page<CategoryItem>> getCategoryItem(
	        @PathVariable Long id,
	        @RequestParam(defaultValue = "0") int page,  // Trang hiện tại (mặc định là 0)
	        @RequestParam(defaultValue = "10") int size, // Kích thước trang (mặc định là 10)
	        @RequestParam(defaultValue = "id") String sortBy, // Cột để sắp xếp (mặc định là "id")
	        @RequestParam(defaultValue = "asc") String sortDir // Thứ tự sắp xếp (asc hoặc desc)
	) {
	    Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
	    Pageable pageable = PageRequest.of(page, size, sort);

	    Page<CategoryItem> items = categoryService.findRestaurantsCategory(id, pageable);
	    return new ResponseEntity<>(items, HttpStatus.OK);
	}
	
	@GetMapping("/restaurant/{id}/category")
	public ResponseEntity<List<Category>> getCategory(
			@PathVariable Long id) throws Exception{
		List<Category> items = categoryService.findCategoryByRestaurantId(id);
		return new ResponseEntity<>(items, HttpStatus.OK);
	}
	
	@GetMapping("/page/{id}/category")
	public ResponseEntity<Page<Category>> getCategory(
	        @PathVariable Long id,
	        @RequestParam(defaultValue = "0") int page,  // Trang hiện tại (mặc định là 0)
	        @RequestParam(defaultValue = "5") int size, // Kích thước trang (mặc định là 10)
	        @RequestParam(defaultValue = "id") String sortBy, // Cột để sắp xếp (mặc định là "id")
	        @RequestParam(defaultValue = "asc") String sortDir // Thứ tự sắp xếp (asc hoặc desc)
	) {
	    Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
	    Pageable pageable = PageRequest.of(page, size, sort);

	    Page<Category> items = categoryService.findCategoryByRestaurantId(id, pageable);
	    return new ResponseEntity<>(items, HttpStatus.OK);
	}
	
	 @PutMapping("/{id}")
	    public ResponseEntity<Category> updateCategory(
	            @PathVariable Long id, 
	            @RequestBody Category updatedCategory) {
	        try {
	            Category category = categoryService.findCategoryById(id);
	            category.setName(updatedCategory.getName());
	            Category savedCategory = categoryService.saveCategory(category);
	            return ResponseEntity.ok(savedCategory);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }
	    }

	    @DeleteMapping("/{id}")
	    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
	        try {
	            categoryService.deleteCategoryById(id);
	            return ResponseEntity.ok("Category deleted successfully.");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
	        }
	    }
	    
	    @GetMapping("/search")
	    public ResponseEntity<List<Category>> searchCategories(
	            @RequestParam String name) {
	        try {
	            List<Category> categories = categoryService.searchCategoriesByName(name);
	            return ResponseEntity.ok(categories);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }
}
