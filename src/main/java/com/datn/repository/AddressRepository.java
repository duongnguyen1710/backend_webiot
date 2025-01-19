package com.datn.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.datn.entity.Address;
import com.datn.entity.User;


public interface AddressRepository extends JpaRepository<Address, Integer> {
	Page<Address> findByCustomer(User customer, Pageable pageable);
	
	List<Address> findByCustomer(User customer);

}
