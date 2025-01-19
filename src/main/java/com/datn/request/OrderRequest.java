package com.datn.request;

import com.datn.entity.Address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
	private Long restaurantId;
	private Address deliveryAddress;
	private String paymentMethod;
}
