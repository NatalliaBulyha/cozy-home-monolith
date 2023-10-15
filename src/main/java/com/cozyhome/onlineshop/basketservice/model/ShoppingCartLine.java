package com.cozyhome.onlineshop.basketservice.model;

import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
@Table(name = "shopping_cart_lines")
public class ShoppingCartLine {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "productColor_id", nullable = false)
	private ProductColor productColor;
	
	private int quantity;

}