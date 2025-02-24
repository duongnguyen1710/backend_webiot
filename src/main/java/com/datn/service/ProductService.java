package com.datn.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;
import com.datn.entity.Product;
import com.datn.entity.Restaurant;
import com.datn.request.CreateProductRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
	public Product createProduct(CreateProductRequest req, Category category, CategoryItem categoryItem, Restaurant restaurant);

	public Product createProduct1(CreateProductRequest req, Category category, CategoryItem categoryItem,
								 Restaurant restaurant, List<MultipartFile> imageFiles) throws IOException;

	public List<Product> getProduct(Long restaurantId);
	
	public Product findProductById(Long productId) throws Exception;
	
	//public List<Product> findProductsByCategory(Long restaurantId, Long categoryId);
	
	public List<Product> getNewProduct(Long restaurantId);
	
	public List<Product> getProductsByCategoryAndRestaurant(Long categoryId, Long restaurantId);
	
	public List<Product> searchProducts(String query);
	
	public Page<Product> getProductsByCategoryAndRestaurant(Long categoryId, Long restaurantId, int page, int size);
	
	public Product save(Product product);
	
	public List<Product> getProductsByCategoryItem1(Long categoryItemId, Double minPrice, Double maxPrice);

    void deleteProductById(Long id) throws Exception;
    
    Page<Product> getProductsByRestaurantWithPagination(Long restaurantId, int page, int size);

	public Page<Product> getAllProducts(int page, int size);

}
