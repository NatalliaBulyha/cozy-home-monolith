package com.cozyhome.onlineshop.inventoryservice.repository;

import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductColorRepository extends JpaRepository<ProductColor, Integer>{

}
