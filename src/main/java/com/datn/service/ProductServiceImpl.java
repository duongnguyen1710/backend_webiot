package com.datn.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;
import com.datn.entity.Product;
import com.datn.entity.Restaurant;
import com.datn.repository.ProductRepository;
import com.datn.request.CreateProductRequest;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

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
		return productRepository.findTop8ByRestaurantIdOrderByCreateAtDesc(restaurantId);
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
		return productRepository.findByCategoryIdAndRestaurantId(categoryId, restaurantId, pageable);
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
		 Pageable pageable = PageRequest.of(page, size);
	        return productRepository.findByRestaurantId(restaurantId, pageable);
	}

}
