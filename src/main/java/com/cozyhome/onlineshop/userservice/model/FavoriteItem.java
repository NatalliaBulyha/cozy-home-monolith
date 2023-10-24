package com.cozyhome.onlineshop.userservice.model;

import com.cozyhome.onlineshop.inventoryservice.model.ProductColor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "favorite_items")
public class FavoriteItem {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Exclude
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "productColor_id", nullable = false)
	private ProductColor productColor;
	
	@Column(name = "user_id")
	private String userId;
	
	public FavoriteItem(ProductColor productColor, String userId) {
		this.productColor = productColor;
		this.userId = userId;
	}
}
