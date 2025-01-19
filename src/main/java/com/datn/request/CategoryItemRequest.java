package com.datn.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryItemRequest {
	private String name;
	private Long categoryId;
	private Long restaurantId;
}
