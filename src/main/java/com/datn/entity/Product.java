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
	private Restaurant restaurant;
	
	private Date creationDate;
	
	private LocalDateTime createAt;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
	@JsonIgnore
	private List<Rating> ratings = new ArrayList<>(); // Danh sách đánh giá liên kết với sản phẩm

	private int numberOfRatings; // Số lượng đã đánh giá
	
	private double ratingTotal;
	
	public void updateRatingTotal() {
        if (ratings.isEmpty()) {
            this.ratingTotal = 0.0;
        } else {
            this.ratingTotal = ratings.stream()
                    .mapToDouble(Rating::getStars)
                    .average()
                    .orElse(0.0);
        }
    }
	
	private int status;
}
