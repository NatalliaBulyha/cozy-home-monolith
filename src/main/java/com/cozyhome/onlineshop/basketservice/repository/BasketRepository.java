package com.cozyhome.onlineshop.basketservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozyhome.onlineshop.basketcartservice.model.BasketRecord;

import jakarta.transaction.Transactional;

public interface BasketRepository extends JpaRepository<BasketRecord, Integer>{
	
	List<BasketRecord> findByUserId(String userId);

	@Transactional
	void deleteAllByUserId(String userId);
}
