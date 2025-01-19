package com.datn.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class Orders {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@ManyToOne
	private User customer;
	@JsonIgnore
	@ManyToOne
	private Store store;
	
	@ManyToOne
	private Restaurant restaurant;
	
	private Long totalAmount;
	
	private String orderStatus;
	
	private Date createAt;
	@ManyToOne
	private Address deliveryAddress;
	@OneToMany
	private List<OrderItem> items;
	
	private int paymentType;
	
	private int statusPayment;
	
	private int totalItem;
	private Long totalPrice;
	
	private int orderType;
	
	public void updateStatusPayment(int statusPayment) {
	    this.statusPayment = statusPayment;
	}
}
