package com.datn.controller;

import java.util.List;
import java.util.Optional;

import com.datn.entity.*;
import com.datn.repository.ProductRepository;
import com.datn.service.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.datn.request.CreateProductRequest;
import com.datn.service.ProductService;
import com.datn.service.RestaurantService;
import com.datn.service.UserService;
import org.springframework.web.multipart.MultipartFile;

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

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CloudinaryService cloudinaryService;

	@PostMapping("/he")
	public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest req,
			@RequestHeader("Authorization") String jwt) throws Exception {
		User user = userService.findUserByJwtToken(jwt);
		Restaurant restaurant = restaurantService.getRestaurantByUserId(user.getId());
		Product product = productService.createProduct(req, req.getCategory(), req.getCategoryItem(), restaurant);

		return new ResponseEntity<>(product, HttpStatus.CREATED);
	}

	@PostMapping(consumes = {"multipart/form-data"})
	public ResponseEntity<Product> createProduct(
			@RequestPart("request") String requestJson,
			@RequestPart(value = "images", required = false) List<MultipartFile> images,
			@RequestHeader("Authorization") String jwt) throws Exception {

		// Chuyển JSON từ chuỗi thành object CreateProductRequest
		ObjectMapper objectMapper = new ObjectMapper();
		CreateProductRequest req = objectMapper.readValue(requestJson, CreateProductRequest.class);

		// Lấy user và restaurant từ JWT
		User user = userService.findUserByJwtToken(jwt);
		Restaurant restaurant = restaurantService.getRestaurantByUserId(user.getId());

		// Gọi service để tạo sản phẩm
		Product product = productService.createProduct1(req, req.getCategory(), req.getCategoryItem(), restaurant, images);

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
	
//	@PutMapping("/{id}")
//    public ResponseEntity<Product> updateProduct(
//            @PathVariable Long id,
//            @RequestBody Product updatedProduct) {
//        try {
//            Product product = productService.findProductById(id);
//
//            product.setName(updatedProduct.getName());
//            product.setDescription(updatedProduct.getDescription());
//            product.setPrice(updatedProduct.getPrice());
//            product.setCategory(updatedProduct.getCategory());
//            product.setCategoryItem(updatedProduct.getCategoryItem());
//            product.setImages(updatedProduct.getImages());
//            product.setStatus(updatedProduct.getStatus());
//
//            Product savedProduct = productService.save(product);
//
//            return ResponseEntity.ok(savedProduct);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }

	@PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
	public ResponseEntity<Product> updateProduct(
			@PathVariable Long id,
			@RequestPart("request") String requestJson,
			@RequestPart(value = "images", required = false) List<MultipartFile> images) {
		try {
			// Chuyển JSON từ chuỗi thành object
			ObjectMapper objectMapper = new ObjectMapper();
			CreateProductRequest updatedProduct = objectMapper.readValue(requestJson, CreateProductRequest.class);

			Product product = productService.findProductById(id);

			product.setName(updatedProduct.getName());
			product.setDescription(updatedProduct.getDescription());
			product.setPrice(updatedProduct.getPrice());
			product.setCategory(updatedProduct.getCategory());
			product.setCategoryItem(updatedProduct.getCategoryItem());

			// ✅ Nếu có ảnh mới, tải lên Cloudinary và cập nhật danh sách ảnh
			if (images != null && !images.isEmpty()) {
				List<String> imageUrls = cloudinaryService.uploadImages(images);
				product.setImages(imageUrls);
			}

			Product savedProduct = productService.save(product);
			return ResponseEntity.ok(savedProduct);

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}


	@PutMapping("/{productId}/update-status")
	public ResponseEntity<?> updateProductStatus(@PathVariable Long productId, @RequestParam int status) {
		Optional<Product> optionalProduct = productRepository.findById(productId);

		if (!optionalProduct.isPresent()) {
			return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm!");
		}

		if (status != 1 && status != 0) {
			return ResponseEntity.badRequest().body("Trạng thái không hợp lệ! Chỉ chấp nhận 1 (Còn hàng) hoặc 2 (Hết hàng).");
		}

		Product product = optionalProduct.get();
		product.setStatus(status);
		productRepository.save(product);

		return ResponseEntity.ok("Cập nhật trạng thái sản phẩm thành công! Trạng thái mới: " + (status == 1 ? "Còn hàng" : "Hết hàng"));
	}

	@GetMapping("/countAvailable")
	public long countAvailableProducts() {
		return productRepository.countByStatus(1);
	}

	@GetMapping("/countTotal")
	public long countTotalProducts() {
		return productRepository.count();
	}

}
