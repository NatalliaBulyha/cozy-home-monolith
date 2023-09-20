package com.cozyhome.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ColorQuantityStatusInvDto {
	
	private String colorHex;
	private String quantityStatus;
}
