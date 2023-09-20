package com.cozyhome.onlineshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CheckingProductAvailableAndStatusDto {
    private int availableProductQuantity;
    private String quantityStatus;
}
