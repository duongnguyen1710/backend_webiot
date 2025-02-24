package com.datn.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;
import com.datn.entity.Product;
import com.datn.entity.Restaurant;
import com.datn.repository.ProductRepository;
import com.datn.request.CreateProductRequest;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CloudinaryService cloudinaryService;

	@Override
	public Product createProduct(CreateProductRequest req, Category category, CategoryItem categoryItem,
			Restaurant restaurant) {
		Product product = new Product();
		product.setCategory(category);
		product.setCategoryItem(categoryItem);
		product.setRestaurant(restaurant);
		product.setName(req.getName());
		product.setDescription(req.getDescription());
		product.setPrice(req.getPrice());
		product.setImages(req.getImages());
		product.setCreateAt(LocalDateTime.now());
		product.setStatus(1); //Còn hàng 

		Product saveProduct = productRepository.save(product);
		restaurant.getProducts().add(saveProduct);
		return saveProduct;
	}

	@Override
	public Product createProduct1(CreateProductRequest req, Category category, CategoryItem categoryItem, Restaurant restaurant, List<MultipartFile> imageFiles) throws IOException {
		Product product = new Product();
		product.setCategory(category);
		product.setCategoryItem(categoryItem);
		product.setRestaurant(restaurant);
		product.setName(req.getName());
		product.setDescription(req.getDescription());
		product.setPrice(req.getPrice());
		product.setCreateAt(LocalDateTime.now());
		product.setStatus(1); // Còn hàng

		// Xử lý ảnh
		List<String> imageUrls;
		if (imageFiles == null || imageFiles.isEmpty()) {
			// Nếu không có ảnh tải lên, dùng ảnh mặc định
			imageUrls = List.of("https://res.cloudinary.com/your_cloud_name/image/upload/v1700000000/default-product.jpg");
		} else {
			// Upload ảnh lên Cloudinary
			imageUrls = cloudinaryService.uploadImages(imageFiles);
		}

		product.setImages(imageUrls); // Lưu danh sách URL vào database
		return productRepository.save(product);
	}

	@Override
	public List<Product> getProduct(Long restaurantId) {
		return productRepository.findByRestaurantId(restaurantId);
	}

	@Override
	public Product findProductById(Long productId) throws Exception {
		Optional<Product> optionalProduct = productRepository.findById(productId);
		if (optionalProduct.isEmpty()) {
			throw new Exception("Sản phẩm không tồn tại");
		}
		return optionalProduct.get();
	}

//	@Override
//	public List<Product> findProductsByCategory(Long categoryId, Long restaurantId) {
//		return productRepository.findByRestaurantIdAndCategoryId(restaurantId, categoryId);
//	}

	@Override
	public List<Product> getNewProduct(Long restaurantId) {
		return productRepository.findTop8ByRestaurantIdAndStatusOrderByCreateAtDesc(restaurantId, 1); // 🔥 Lọc theo status = 1
	}

	@Override
	public List<Product> getProductsByCategoryAndRestaurant(Long categoryId, Long restaurantId) {
		return productRepository.findByCategoryIdAndRestaurantId(categoryId, restaurantId);
	}

	@Override
	public List<Product> searchProducts(String query) {
		return productRepository.findByNameContainingIgnoreCase(query);
	}

	@Override
	public Page<Product> getProductsByCategoryAndRestaurant(Long categoryId, Long restaurantId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findByCategoryIdAndRestaurantIdAndStatus(categoryId, restaurantId, 1, pageable); // 🔥 Lọc theo status = 1
	}

	@Override
	public Product save(Product product) {
		return productRepository.save(product);
	}

	@Override
	public List<Product> getProductsByCategoryItem1(Long categoryItemId, Double minPrice, Double maxPrice) {
	    if (minPrice != null && maxPrice != null) {
	        System.out.println("Querying with price range: " + minPrice + " - " + maxPrice);
	        return productRepository.findByCategoryItemIdAndPriceBetween(categoryItemId, minPrice, maxPrice);
	    } else {
	        System.out.println("Querying without price range.");
	        return productRepository.findByCategoryItemId(categoryItemId);
	    }
	}

	@Override
	public void deleteProductById(Long id) throws Exception {
		if (!productRepository.existsById(id)) {
            throw new Exception("Product not found");
        }
        productRepository.deleteById(id);
	}

	@Override
	public Page<Product> getProductsByRestaurantWithPagination(Long restaurantId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createAt")); // 🔥 Sắp xếp theo ngày tạo mới nhất

		return productRepository.findByRestaurantId(restaurantId, pageable);
	}

	@Override
	public Page<Product> getAllProducts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productRepository.findAll(pageable);
	}

}
