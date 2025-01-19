package com.datn.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Store;
import com.datn.entity.User;
import com.datn.request.CreateStoreRequest;
import com.datn.response.MessageResponse;
import com.datn.service.StoreService;
import com.datn.service.UserService;

@RestController
@RequestMapping("/api/admin/store")
@CrossOrigin
public class AdminStoreController {
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private UserService userService;
	
	@PostMapping()
	public ResponseEntity<Store> createStore(
			@RequestBody CreateStoreRequest req,
			@RequestHeader ("Authorization") String jwt
	)throws Exception{
		User user = userService.findUserByJwtToken(jwt);
		
		Store Store = storeService.createStore(req, user);
		return new ResponseEntity<>(Store, HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Store> updateStore(
			@RequestBody CreateStoreRequest req,
			@RequestHeader ("Authorization") String jwt,
			@PathVariable Long id
	)throws Exception{
		User user = userService.findUserByJwtToken(jwt);
		
		Store Store = storeService.updateStore(id, req);
		return new ResponseEntity<>(Store, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<MessageResponse> deleteStore(
			@RequestHeader ("Authorization") String jwt,
			@PathVariable Long id
	)throws Exception{
		User user = userService.findUserByJwtToken(jwt);
		
		storeService.deleteStore(id);
		
		MessageResponse res = new MessageResponse();
		res.setMessage("Nhà hàng đã được xóa");
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	@PutMapping("/{id}/status")
	public ResponseEntity<Store> updateStoreStatus(
			@RequestHeader ("Authorization") String jwt,
			@PathVariable Long id
	)throws Exception{
		User user = userService.findUserByJwtToken(jwt);
		
		Store Store = storeService.updateStoreStatus(id);
		
		return new ResponseEntity<>(Store, HttpStatus.OK);
	}
	
	@GetMapping("/user")
	public ResponseEntity<Store> findStoreByUserId(
			@RequestHeader ("Authorization") String jwt
	)throws Exception{
		User user = userService.findUserByJwtToken(jwt);
		
		Store Store = storeService.getStoreByUserId(user.getId());
		
		return new ResponseEntity<>(Store, HttpStatus.OK);
	}
}
