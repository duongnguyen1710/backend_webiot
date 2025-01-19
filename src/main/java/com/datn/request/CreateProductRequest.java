package com.datn.request;

import java.util.List;

import com.datn.entity.Category;
import com.datn.entity.CategoryItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
	private String name;
	private String description;
	private Long price;
	
	private Category category; 
	private CategoryItem categoryItem; 
	private List<String> images;
	
	private Long restaurantId;
	
	
}
