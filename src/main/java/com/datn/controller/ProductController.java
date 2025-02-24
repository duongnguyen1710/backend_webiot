package com.datn.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Category;
import com.datn.entity.Product;
import com.datn.entity.Restaurant;
import com.datn.service.CategoryService;
import com.datn.service.ProductService;
import com.datn.service.RestaurantService;

@RestController
@RequestMapping("/product")
@CrossOrigin
public class ProductController {
	@Autowired
	private ProductService productService;
	
	@Autowired
	private RestaurantService restaurantService;
	
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/restaurant/{restaurantId}")
	public ResponseEntity<List<Product>> getProductsByRestaurant(@PathVariable Long restaurantId) {
		List<Product> products = productService.getProduct(restaurantId);
		return ResponseEntity.ok(products);
	}

//	@GetMapping("/category")
//	public ResponseEntity<List<Product>> getProductsByRestaurantAndCategory(@RequestParam Long restaurantId,
//			@RequestParam Long categoryId) {
//		List<Product> products = productService.findProductsByCategory(restaurantId, categoryId);
//		return new ResponseEntity<>(products, HttpStatus.OK);
//	}

	@GetMapping("/newProduct/{restaurantId}")
	public ResponseEntity<List<Product>> getTopProduct(@PathVariable Long restaurantId) {
		List<Product> newProducts = productService.getNewProduct(restaurantId);
		return ResponseEntity.ok(newProducts);
	}

	@GetMapping("/{productId}")
	public ResponseEntity<?> getProductById(@PathVariable Long productId) {
		try {
			Product product = productService.findProductById(productId);
			return ResponseEntity.ok(product);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
	
	@GetMapping("/{categoryId}/{restaurantId}")
    public List<Product> getProducts(@PathVariable Long categoryId, @PathVariable Long restaurantId) {
        return productService.getProductsByCategoryAndRestaurant(categoryId, restaurantId);
    }
	
	@GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        return productService.searchProducts(query);
    }

	@GetMapping("/page/{categoryId}/{restaurantId}")
	public Page<Product> getProducts(
			@PathVariable Long categoryId,
			@PathVariable Long restaurantId,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		return productService.getProductsByCategoryAndRestaurant(categoryId, restaurantId, page, size);
	}

	@GetMapping("/filter")
	public ResponseEntity<List<Product>> getProductsByCategoryItem1(
			@RequestParam(required = false) Long categoryItemId,
			@RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice) {

		if (categoryItemId == null) {
			return ResponseEntity.badRequest().body(Collections.emptyList());
		}

		List<Product> products = productService.getProductsByCategoryItem1(categoryItemId, minPrice, maxPrice);
		return ResponseEntity.ok(products);
	}

	@GetMapping("/all")
	public ResponseEntity<Page<Product>> getAllProducts(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<Product> products = productService.getAllProducts(page, size);
		return ResponseEntity.ok(products);
	}



}
