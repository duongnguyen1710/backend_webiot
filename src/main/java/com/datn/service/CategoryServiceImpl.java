package com.datn.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;
import com.datn.entity.Restaurant;
import com.datn.repository.CategoryItemRepository;
import com.datn.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CategoryItemRepository categoryItemRepository;

	@Autowired
	private RestaurantService restaurantService;

	@Override
	public Category createCategory(String name, Long restaurantId) throws Exception {
		Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

		Category category = new Category();
		category.setRestaurant(restaurant);
		category.setName(name);

		return categoryRepository.save(category);
	}

	@Override
	public Category findCategoryById(Long id) throws Exception {
		Optional<Category> opt = categoryRepository.findById(id);
		
		if(opt.isEmpty()) {
			throw new Exception("Không tìm thấy danh mục cha");
		}
		return opt.get();
	}

	@Override
	public List<Category> findCategoryByRestaurantId(Long id) throws Exception {
		restaurantService.findRestaurantById(id);
		return categoryRepository.findByRestaurantId(id);
	}

	@Override
	public CategoryItem createCategoryItem(Long restaurantId, String categoryName, Long categoryId) throws Exception {
		Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
		Category category = findCategoryById(categoryId);
		
		CategoryItem item = new CategoryItem();
		item.setName(categoryName);
		item.setRestaurant(restaurant);
		item.setCategory(category);
		
		CategoryItem categoryItem = categoryItemRepository.save(item);
		category.getCategoryItems().add(categoryItem);
		
		return categoryItem;
	}

	@Override
	public List<CategoryItem> findRestaurantsCategory(Long restaurantId) {
		return categoryItemRepository.findByRestaurantId(restaurantId);
	}

	@Override
	public List<CategoryItem> getCategoryItemsByCategoryIdAndRestaurantId(Long categoryId, Long restaurantId) {
		 return categoryItemRepository.findByCategoryIdAndRestaurantId(categoryId, restaurantId);
	}

	@Override
	public void deleteCategoryById(Long id) throws Exception {
		if (!categoryRepository.existsById(id)) {
            throw new Exception("Category not found");
        }
        categoryRepository.deleteById(id);
		
	}

	@Override
	public Category saveCategory(Category category) throws Exception {
		 return categoryRepository.save(category);
	}

	@Override
	public List<Category> searchCategoriesByName(String name) throws Exception {
		return categoryRepository.findByNameContainingIgnoreCase(name);
	}

	@Override
	public Page<CategoryItem> findRestaurantsCategory(Long restaurantId, Pageable pageable) {
		return categoryItemRepository.findByRestaurantId(restaurantId, pageable);
	}

	@Override
	public Page<Category> findCategoryByRestaurantId(Long restaurantId, Pageable pageable) {
		return categoryRepository.findByRestaurantId(restaurantId, pageable);
	}

	@Override
	public CategoryItem findCategoryItemById(Long id) throws Exception {
		return categoryItemRepository.findById(id)
				.orElseThrow(() -> new Exception("CategoryItem not found with id: " + id));
	}

	@Override
	public CategoryItem saveCategoryItem(CategoryItem categoryItem) throws Exception {
		return categoryItemRepository.save(categoryItem);
	}

	@Override
	public void deleteCategoryItemById(Long id) throws Exception {
		CategoryItem categoryItem = findCategoryItemById(id);
		categoryItemRepository.delete(categoryItem);
	}


//	@Override
//	public CategoryItem updateStock(Long id) throws Exception {
//		Optional<CategoryItem> optionalCategoryItem = categoryItemRepository.findById(id);
//		if(optionalCategoryItem.isEmpty()) {
//			throw new Exception("Không tìm thấy nguyên liệu");
//		}
//		CategoryItem categoryItem = optionalCategoryItem.get();
//		categoryItem.setInStoke(!ingredientsItem.isInStoke());
//		return ingredientItemRepository.save(ingredientsItem);
//	}
}
