package com.datn.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datn.entity.Address;
import com.datn.entity.Store;
import com.datn.entity.User;
import com.datn.repository.AddressRepository;
import com.datn.repository.StoreRepository;
import com.datn.repository.UserRepository;
import com.datn.request.CreateStoreRequest;

@Service
public class StoreServiceImpl implements StoreService{
	
	@Autowired
	private StoreRepository storeRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private UserRepository userRepository; 
	
	@Override
	public Store createStore(CreateStoreRequest req, User user) {


		Address address = addressRepository.save(req.getAddress());
		
		Store store = new Store();
		store.setAddress(address);
		store.setContactInformation(req.getContactInformation());
		//Store.setCuisineType(req.getCuisineType());
		store.setDescription(req.getDescription());
		store.setImages(req.getImages());
		store.setName(req.getName());
		store.setOpeningHours(req.getOpningHours());
		store.setRegistrationDate(LocalDateTime.now());
		store.setOwner(user);
		
		
		return storeRepository.save(store);
	}

	@Override
	public Store updateStore(Long StoreId, CreateStoreRequest updateStore) throws Exception {
		Store store = findStoreById(StoreId);
		
//		if(Store.getCuisineType()!=null) {
//			Store.setCuisineType(updateStore.getCuisineType());
//		}
		if(store.getDescription()!=null) {
			store.setDescription(updateStore.getDescription());
		}
		if(store.getName()!=null) {
			store.setName(updateStore.getName());
		}
		return storeRepository.save(store);
	}

	@Override
	public void deleteStore(Long StoreId) throws Exception {
		
		Store store = findStoreById(StoreId);
		
		storeRepository.delete(store);
		
	}

	@Override
	public List<Store> getAllStore() {
		return storeRepository.findAll();
	}

	@Override
	public List<Store> searchStore(String keyword) {
		// TODO Auto-generated method stub
		return storeRepository.findBySearchQuery(keyword);
	}

	@Override
	public Store findStoreById(Long id) throws Exception {
		Optional<Store> opt = storeRepository.findById(id);
		
		if(opt.isEmpty()) {
			throw new Exception("Không tìm thấy nhà hàng với mã này" +id);
		}
		return opt.get();
	}

	@Override
	public Store getStoreByUserId(Long userId) throws Exception {
		Store store = storeRepository.findByOwnerId(userId);
		if(store == null) {
			throw new Exception("Không tìm thấy nhà hàng với mã chủ sở hữu này" + userId);
		}
		return store;
	}

//	@Override
//	public StoreDto addToFavories(Long StoreId, User user) throws Exception {
//		
//		Store Store = findStoreById(StoreId);
//		
//		StoreDto dto = new StoreDto();
//		dto.setDescription(Store.getDescription());
//		dto.setImages(Store.getImages());
//		dto.setTitle(Store.getName());
//		dto.setId(StoreId);
//		
//		boolean isFavorited = false;
//		List<StoreDto> favorites = user.getFavories();
//		for (StoreDto favorite : favorites) {
//			if (favorite.getId().equals(StoreId)) {
//				isFavorited = true;
//				break;
//			}
//		}
//		
//		if (isFavorited) {
//			favorites.removeIf(favorite -> favorite.getId().equals(StoreId));
//		}else {
//			favorites.add(dto);
//		}
//		userRepository.save(user);
//		return dto;
//	}

	@Override
	public Store updateStoreStatus(Long id) throws Exception {
		Store store = findStoreById(id);
		store.setOpen(!store.isOpen());
		return storeRepository.save(store);
	}

}
