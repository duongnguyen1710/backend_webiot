package com.datn.service;

import java.util.List;

import com.datn.entity.Store;
import com.datn.entity.User;
import com.datn.request.CreateStoreRequest;

public interface StoreService {
	public Store createStore(CreateStoreRequest req, User user);
	
	public Store updateStore(Long StoreId, CreateStoreRequest updateStore) throws Exception;

	public void deleteStore(Long StoreId) throws Exception;
	
	public List<Store> getAllStore();
	
	public List<Store> searchStore(String keyword);
	
	public Store findStoreById(Long id) throws Exception;
	
	public Store getStoreByUserId(Long userId) throws Exception;
	
	//public StoreDto addToFavories(Long StoreId, User user) throws Exception;
	
	public Store updateStoreStatus (Long id) throws Exception;
}
