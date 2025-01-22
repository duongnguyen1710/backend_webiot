package com.datn.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fullName;
	private String email;
	private String password;
	private String avatar; // URL của ảnh đại diện
	private boolean verified = false;
	
	private Role role=Role.ROLE_CUSTOMER;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "customer")
	private List<Orders> orders = new ArrayList<>();
	
//	@ElementCollection
//	private List<RestaurantDto> favories = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Address> addresses = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<Rating> ratings = new ArrayList<>(); // Liên kết với đánh giá
	
	 @JsonIgnore
	    @ElementCollection
	    private List<Long> ratedProductIds = new ArrayList<>(); // Lưu danh sách ID sản phẩm đã đánh giá
}
