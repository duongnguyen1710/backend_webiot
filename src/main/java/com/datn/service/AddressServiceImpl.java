package com.datn.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.datn.entity.Address;
import com.datn.entity.User;
import com.datn.repository.AddressRepository;
import com.datn.request.AddressRequest;

@Service
public class AddressServiceImpl implements AddressService {
	@Autowired
    private AddressRepository addressRepository;
	
	@Autowired
    private UserService userService;

	@Override
	public Address addAddress(User user, AddressRequest addressRequest) {
		// Tạo đối tượng Address
        Address address = new Address();
        address.setFullName(addressRequest.getFullName());
        address.setPhone(addressRequest.getPhone());
        address.setFullAddress(addressRequest.getFullAddress());
        address.setStreet(addressRequest.getStreet());
        address.setCity(addressRequest.getCity());
        address.setProvince(addressRequest.getProvince());
        address.setCustomer(user); // Gán customer từ user

        // Lưu vào database
        return addressRepository.save(address);
	}

	@Override
	public Page<Address> getAddressesByUser(User user, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
        return addressRepository.findByCustomer(user, pageable);
	}

	@Override
	public Address updateAddress(User user, int addressId, AddressRequest addressRequest) {
	    // Kiểm tra xem địa chỉ có thuộc về User không
	    Address address = addressRepository.findById(addressId)
	            .orElseThrow(() -> new IllegalArgumentException("Address not found."));
	    
	    if (!address.getCustomer().equals(user)) {
	        throw new IllegalArgumentException("Address does not belong to the user.");
	    }

	    // Cập nhật thông tin địa chỉ
	    address.setFullName(addressRequest.getFullName());
	    address.setPhone(addressRequest.getPhone());
	    address.setFullAddress(addressRequest.getFullAddress());
	    address.setStreet(addressRequest.getStreet());
	    address.setCity(addressRequest.getCity());
	    address.setProvince(addressRequest.getProvince());

	    return addressRepository.save(address);
	}

	@Override
	public void deleteAddress(User user, int addressId) {
	    // Kiểm tra xem địa chỉ có thuộc về User không
	    Address address = addressRepository.findById(addressId)
	            .orElseThrow(() -> new IllegalArgumentException("Address not found."));

	    if (!address.getCustomer().equals(user)) {
	        throw new IllegalArgumentException("Address does not belong to the user.");
	    }

	    // Xóa địa chỉ
	    addressRepository.delete(address);
	}

	@Override
	public Address getAddressById(User user, int addressId) {
	    // Tìm địa chỉ theo ID
	    Address address = addressRepository.findById(addressId)
	            .orElseThrow(() -> new IllegalArgumentException("Address not found."));

	    // Kiểm tra xem địa chỉ có thuộc về User không
	    if (!address.getCustomer().equals(user)) {
	        throw new IllegalArgumentException("Address does not belong to the user.");
	    }

	    return address;
	}

	@Override
	public List<Address> getAllAddressesByUser(User user) {
		return addressRepository.findByCustomer(user);
	}
	
	
	
}
