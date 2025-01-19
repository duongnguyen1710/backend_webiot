package com.datn.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactInformation {
	private String email;
	private String mobile;
	private String twitter;
	private String instagram;
}
