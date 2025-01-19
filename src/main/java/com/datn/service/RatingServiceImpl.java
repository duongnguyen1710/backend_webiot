package com.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.entity.Rating;
import com.datn.repository.RatingRepository;

@Service
public class RatingServiceImpl implements RatingService {

	@Autowired
    private RatingRepository ratingRepository;

    public Rating save(Rating rating) {
        return ratingRepository.save(rating);
    }
	
}
