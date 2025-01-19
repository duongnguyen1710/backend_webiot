package com.datn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Product;
import com.datn.entity.Restaurant;
import com.datn.entity.User;
import com.datn.request.CreateProductRequest;
import com.datn.service.ProductService;
import com.datn.service.RestaurantService;
import com.datn.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/admin/product")
public class AdminProductController {
	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private RestaurantService restaurantService;

	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest req,
			@RequestHeader("Authorization") String jwt) throws Exception {
		User user = userService.findUserByJwtToken(jwt);
		Restaurant restaurant = restaurantService.getRestaurantByUserId(user.getId());
		Product product = productService.createProduct(req, req.getCategory(), req.getCategoryItem(), restaurant);

		return new ResponseEntity<>(product, HttpStatus.CREATED);
	}

	@GetMapping("/page/{restaurantId}")
	public ResponseEntity<Page<Product>> getProductsByRestaurant(
	        @PathVariable Long restaurantId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {
	    try {
	        Page<Product> products = productService.getProductsByRestaurantWithPagination(restaurantId, page, size);
	        return ResponseEntity.ok(products);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		try {
			productService.deleteProductById(id);
			return ResponseEntity.ok("Product deleted successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
		}
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updatedProduct) {
        try {
            Product product = productService.findProductById(id);

            product.setName(updatedProduct.getName());
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            product.setCategory(updatedProduct.getCategory());
            product.setCategoryItem(updatedProduct.getCategoryItem());
            product.setImages(updatedProduct.getImages());
            product.setStatus(updatedProduct.getStatus());

            Product savedProduct = productService.save(product);

            return ResponseEntity.ok(savedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
