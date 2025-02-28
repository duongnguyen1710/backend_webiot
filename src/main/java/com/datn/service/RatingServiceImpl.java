package com.datn.service;

import com.datn.entity.Product;
import com.datn.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.entity.Rating;
import com.datn.repository.RatingRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingServiceImpl implements RatingService {

	@Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ProductRepository productRepository;

    public Rating save(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Transactional
    public Rating addRating(Long productId, Rating rating) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        // Gán sản phẩm vào đánh giá
        rating.setProduct(product);

        // Lưu đánh giá
        Rating savedRating = ratingRepository.save(rating);

        // Cập nhật lại số sao trung bình
        product.getRatings().add(savedRating);
        product.updateRatingTotal();
        productRepository.save(product);

        return savedRating;
    }
	
}
