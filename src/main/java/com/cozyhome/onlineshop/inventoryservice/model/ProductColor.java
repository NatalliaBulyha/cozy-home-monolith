package com.cozyhome.onlineshop.inventoryservice.model;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "product_color")
public class ProductColor {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

	@Column(name = "product_skucode")
    private String productSkuCode;
	
	@Column(name = "color_hex")
    private String colorHex;
	
	@JsonIgnore
	@ToStringExclude
	@OneToOne(mappedBy = "productColor")
	private Inventory inventory;
}
