package com.datn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Address;
import com.datn.entity.User;
import com.datn.request.AddressRequest;
import com.datn.service.AddressService;
import com.datn.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/address")
public class AddressController {
	@Autowired
	private AddressService addressService;
	
	 @Autowired
	   private UserService userService;
	
	@PostMapping
    public ResponseEntity<?> addAddress(@RequestHeader("Authorization") String jwt, 
                                        @RequestBody AddressRequest addressRequest) throws Exception {
        // Lấy user từ JWT
        User user = userService.findUserByJwtToken(jwt);

        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid token.");
        }

        // Thêm mới địa chỉ
        Address newAddress = addressService.addAddress(user, addressRequest);

        return ResponseEntity.ok(newAddress);
    }
	
	@GetMapping
    public Page<Address> getUserAddresses(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(defaultValue = "0") int page) throws Exception {
        // Lấy User từ JWT
        User user = userService.findUserByJwtToken(jwt);

        if (user == null) {
            throw new IllegalArgumentException("Unauthorized: Invalid token.");
        }

        // Lấy danh sách địa chỉ với phân trang
        return addressService.getAddressesByUser(user, page, 5); // Size = 5
    }
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAddress(
	        @RequestHeader("Authorization") String jwt,
	        @PathVariable int id) throws Exception {
	    // Lấy User từ JWT
	    User user = userService.findUserByJwtToken(jwt);
	    if (user == null) {
	        return ResponseEntity.status(401).body("Unauthorized: Invalid token.");
	    }

	    // Gọi service để xóa địa chỉ
	    addressService.deleteAddress(user, id);
	    return ResponseEntity.ok("Address deleted successfully.");
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateAddress(
	        @RequestHeader("Authorization") String jwt,
	        @PathVariable int id,
	        @RequestBody AddressRequest addressRequest) throws Exception {
	    // Lấy User từ JWT
	    User user = userService.findUserByJwtToken(jwt);
	    if (user == null) {
	        return ResponseEntity.status(401).body("Unauthorized: Invalid token.");
	    }

	    // Gọi service để cập nhật địa chỉ
	    Address updatedAddress = addressService.updateAddress(user, id, addressRequest);
	    return ResponseEntity.ok(updatedAddress);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getAddressById(
	        @RequestHeader("Authorization") String jwt,
	        @PathVariable int id) throws Exception {
	    // Lấy User từ JWT
	    User user = userService.findUserByJwtToken(jwt);
	    if (user == null) {
	        return ResponseEntity.status(401).body("Unauthorized: Invalid token.");
	    }

	    // Gọi service để lấy địa chỉ
	    Address address = addressService.getAddressById(user, id);
	    return ResponseEntity.ok(address);
	}
	
	@GetMapping("/all")
	public ResponseEntity<?> getAllUserAddresses(@RequestHeader("Authorization") String jwt) throws Exception {
	    // Lấy User từ JWT
	    User user = userService.findUserByJwtToken(jwt);
	    if (user == null) {
	        return ResponseEntity.status(401).body("Unauthorized: Invalid token.");
	    }

	    // Lấy danh sách tất cả các địa chỉ của User
	    return ResponseEntity.ok(addressService.getAllAddressesByUser(user));
	}


}
