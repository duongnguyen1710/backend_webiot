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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Product;
import com.datn.entity.Rating;
import com.datn.entity.User;
import com.datn.request.RatingRequest;
import com.datn.service.ProductService;
import com.datn.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/ratings")
public class RatingController {
	@Autowired
	private UserService userService;

	@Autowired
	private ProductService productService;

	@PostMapping("/{productId}")
	public ResponseEntity<?> rateProduct(@RequestHeader("Authorization") String jwt, @PathVariable Long productId,
	                                     @RequestBody RatingRequest ratingRequest) throws Exception {

	    // Lấy thông tin người dùng từ JWT
	    User user = userService.findUserByJwtToken(jwt);
	    if (user == null) {
	        return ResponseEntity.status(401).body("Unauthorized: Invalid JWT Token");
	    }

	    // Lấy thông tin sản phẩm từ productId
	    Product product = productService.findProductById(productId);
	    if (product == null) {
	        return ResponseEntity.status(404).body("Product not found");
	    }

	    // Kiểm tra nếu sản phẩm đã được người dùng đánh giá
	    if (user.getRatedProductIds().contains(productId)) {
	        return ResponseEntity.badRequest().body("You have already rated this product");
	    }

	    // Kiểm tra số sao hợp lệ
	    if (ratingRequest.getStars() < 1 || ratingRequest.getStars() > 5) {
	        return ResponseEntity.badRequest().body("Stars must be between 1 and 5");
	    }

	    // Tạo đối tượng Rating
	    Rating rating = new Rating();
	    rating.setStars(ratingRequest.getStars());
	    rating.setComment(ratingRequest.getComment());
	    rating.setProduct(product);
	    rating.setUser(user);
	    rating.setCreateAt(LocalDateTime.now());

	    // Lưu đánh giá vào cơ sở dữ liệu
	    product.getRatings().add(rating);
	    product.setNumberOfRatings(product.getRatings().size());
	    product.updateRatingTotal(); // Cập nhật tổng rating
	    productService.save(product);

	    // Thêm productId vào danh sách sản phẩm đã đánh giá
	    user.getRatedProductIds().add(productId);
	    userService.save(user);

	    return ResponseEntity.ok("Rating submitted successfully");
	}



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
