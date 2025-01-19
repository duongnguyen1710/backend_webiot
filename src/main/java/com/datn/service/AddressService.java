package com.datn.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.datn.entity.Address;
import com.datn.entity.User;
import com.datn.request.AddressRequest;

public interface AddressService {
    
	 public Address addAddress(User user, AddressRequest addressRequest);
	 
	 public Page<Address> getAddressesByUser(User user, int page, int size);
	 
	 public Address updateAddress(User user, int addressId, AddressRequest addressRequest);
	 
	 public void deleteAddress(User user, int addressId);
	 
	 public Address getAddressById(User user, int addressId);
	 
	 public List<Address> getAllAddressesByUser(User user);
	 
	 
}
