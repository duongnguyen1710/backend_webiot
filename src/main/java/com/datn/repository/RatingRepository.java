package com.datn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long>{

}
