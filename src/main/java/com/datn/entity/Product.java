package com.datn.entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	private Long price;

	@ManyToOne
	private Category category;

	@ManyToOne
	private CategoryItem categoryItem;

	@Column(length = 1000)
	@ElementCollection
	private List<String> images;

	private boolean available;

	@ManyToOne
	private Store store;

	@ManyToOne
	@JsonIgnore
	private Restaurant restaurant;

	private Date creationDate;

	private LocalDateTime createAt;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	@JsonIgnore
	private List<Rating> ratings = new ArrayList<>();

	private int numberOfRatings; // Số lượng đánh giá

	private double ratingTotal; // Tổng số sao

	private double averageRating; // Số sao trung bình

	// Phương thức cập nhật trung bình số sao
	public void updateRatingTotal() {
		if (ratings.isEmpty()) {
			this.ratingTotal = 0.0;
			this.averageRating = 0.0;
		} else {
			this.ratingTotal = ratings.stream()
					.mapToDouble(Rating::getStars)
					.sum();
			this.numberOfRatings = ratings.size();
			this.averageRating = this.ratingTotal / this.numberOfRatings;
		}
	}

	private int status;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	@JsonIgnore
	private List<RatingProduct> ratingProducts = new ArrayList<>();

}
