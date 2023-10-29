package com.cozyhome.onlineshop.dto.productcard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ColorQuantityStatusDto {
	private String id;
    private String name;
    private String quantityStatus;
    private boolean isFavorite;
    
    public ColorQuantityStatusDto(String id, String name, String quantityStatus) {
    	this.id = id;
    	this.name = name;
    	this.quantityStatus = quantityStatus;
    }
}
