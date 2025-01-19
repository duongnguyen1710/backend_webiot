package com.datn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datn.entity.Store;
import com.datn.entity.User;
import com.datn.service.StoreService;
import com.datn.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/store")
public class StoreController {
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/search")
	public ResponseEntity<List<Store>> searchstore(
			@RequestHeader ("Authorization") String jwt,
			@RequestParam String keyword
	)throws Exception{
		User user = userService.findUserByJwtToken(jwt);
		
		List<Store> store = storeService.searchStore(keyword);
		return new ResponseEntity<>(store, HttpStatus.OK);
	}
	
	@GetMapping()
	public ResponseEntity<List<Store>> getAllstore(
			@RequestHeader ("Authorization") String jwt
	)throws Exception{
		User user = userService.findUserByJwtToken(jwt);
		
		List<Store> store = storeService.getAllStore();
		return new ResponseEntity<>(store, HttpStatus.OK);
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Store> findstoreById(
			@RequestHeader ("Authorization") String jwt,
			@PathVariable Long id	
	)throws Exception{
		User user = userService.findUserByJwtToken(jwt);
		
		Store store = storeService.findStoreById(id);
		return new ResponseEntity<>(store, HttpStatus.OK);
	}
}
