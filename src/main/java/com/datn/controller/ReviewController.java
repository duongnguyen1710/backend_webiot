package com.datn.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Product;
import com.datn.entity.Rating;
import com.datn.entity.User;
import com.datn.request.RatingRequest;
import com.datn.service.ProductService;
import com.datn.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/ratings")
public class ReviewController {
	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	 
	 	@GetMapping("/{productId}")
	    public ResponseEntity<?> getRatingsByProductId(@PathVariable Long productId) throws Exception {
	        // Lấy thông tin sản phẩm từ productId
	        Product product = productService.findProductById(productId);
	        if (product == null) {
	            return ResponseEntity.status(404).body("Product not found");
	        }

	        // Lấy danh sách đánh giá từ sản phẩm
	        List<Rating> ratings = product.getRatings();

	        return ResponseEntity.ok(ratings);
	    }
}
