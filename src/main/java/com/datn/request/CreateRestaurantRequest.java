package com.datn.request;

import java.util.List;

import com.datn.entity.Address;
import com.datn.entity.ContactInformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantRequest {
	private Long id;
	private String name;
	private String description;
	//private String cuisineType;
	private Address address;
	private ContactInformation contactInformation;
	private String opningHours;
	private List<String> images;
}
