package com.cozyhome.onlineshop.inventoryservice.controller;

import com.cozyhome.onlineshop.dto.inventory.InventoryForBasketDto;
import com.cozyhome.onlineshop.dto.inventory.QuantityStatusDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
@RestController
public class InventoryController {
	private final InventoryService inventoryService;
	
	//for product card
	@GetMapping("/color_quantity_status/product_skuCode")
	public ResponseEntity<QuantityStatusDto> getColorQuantityStatusBySkuCode(@RequestParam String productSkuCode){
		return ResponseEntity.ok(inventoryService.getProductCardColorQuantityStatus(productSkuCode));
	}	
	
	//for preview
	@PostMapping("/quantity_status_map/product_skuCode_list")
	public ResponseEntity<Map<String, String>> getProductQuantityStatusMap(@RequestBody List<String> productSkuCodeList){
		return ResponseEntity.ok(inventoryService.getQuantityStatusBySkuCodeList(productSkuCodeList));
	}

	// for basket
	@PostMapping("/shopping-card-info")
	public ResponseEntity<List<InventoryForBasketDto>> getProductAvailableStatus(@RequestBody List<ProductColorDto> productColorDto) {
		return ResponseEntity.ok(inventoryService.getProductAvailableStatus(productColorDto));
	}
}
