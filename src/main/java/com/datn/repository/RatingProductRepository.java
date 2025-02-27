package com.datn.repository;

import com.datn.entity.RatingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RatingProductRepository extends JpaRepository<RatingProduct, Long> {
    Optional<RatingProduct> findByUserIdAndProductId(Long userId, Long productId);
}
