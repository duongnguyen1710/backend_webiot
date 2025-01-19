package com.datn.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
	private String fullName;
    private String phone;
    private String fullAddress;
    private String street;
    private String city;
    private String province;
}
