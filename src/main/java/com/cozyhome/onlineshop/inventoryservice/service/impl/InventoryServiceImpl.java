package com.cozyhome.onlineshop.inventoryservice.service.impl;

import com.cozyhome.onlineshop.dto.inventory.CheckingProductAvailableAndStatusDto;
import com.cozyhome.onlineshop.dto.inventory.InventoryForBasketDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.dto.inventory.QuantityStatusDto;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.inventoryservice.model.Inventory;
import com.cozyhome.onlineshop.inventoryservice.model.enums.ProductQuantityStatus;
import com.cozyhome.onlineshop.inventoryservice.repository.InventoryRepository;
import com.cozyhome.onlineshop.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryServiceImpl implements InventoryService {
	private final InventoryRepository inventoryRepository;

	@Override
	public int getQuantityByProductColor(ProductColorDto request) {
		int quantity = -1;
		Optional<Inventory> inventory = inventoryRepository.findByProductColorProductSkuCodeAndProductColorColorHex(
				request.getProductSkuCode(), request.getColorHex());
		if (inventory.isPresent()) {
			quantity = inventory.get().getQuantity();
			log.info("GET QUANTITY [" + quantity + "] for product skuCode [" + request.getProductSkuCode()
					+ "] and color hex [" + request.getColorHex() + "].");
		}
		return quantity;
	}

	@Override
	public String getQuantityStatusByProductColor(ProductColorDto request) {
		int productQuantity = getQuantityByProductColor(request);
		return convertQuantityToQuantityStatus(productQuantity);
	}

	@Override
	public Map<String, String> getQuantityStatusBySkuCodeList(List<String> productSkuCodeList) {
		List<Inventory> inventoryList = inventoryRepository.findByProductColorProductSkuCodeIn(productSkuCodeList);
		if (inventoryList.isEmpty()) {
			log.error("[ON getQuantityStatusBySkuCodeList]:: Product quantity information not found.");
			throw new DataNotFoundException("Product quantity information not found.");
		}

		Map<String, String> map = new HashMap<>();
		for (Inventory inventory : inventoryList) {
			String productSkuCode = inventory.getProductColor().getProductSkuCode();
			String quantityStatus = convertQuantityToQuantityStatus(inventory.getQuantity());
			map.put(productSkuCode, quantityStatus);
		}
		return map;
	}

	private String convertQuantityToQuantityStatus(int productQuantity) {
		String status = ProductQuantityStatus.getStatusByQuantity(productQuantity);
		log.info("QUANTITY STATUS [" + status + "] FOR QUANTITY " + productQuantity);
		return status;
	}

	public QuantityStatusDto getProductCardColorQuantityStatus(String productSkuCode) {
		QuantityStatusDto result = new QuantityStatusDto();
		List<Inventory> inventoryList = inventoryRepository.findByProductColorProductSkuCode(productSkuCode);
		if (inventoryList.isEmpty()) {
			log.error("[ON getProductCardColorQuantityStatus]:: Product quantity information not found.");
			throw new DataNotFoundException("Product quantity information not found.");
		}
		Map<String, String> map = new HashMap<>();
		for (Inventory inventory : inventoryList) {
			String quantityStatus = ProductQuantityStatus.getStatusByQuantity(inventory.getQuantity());
			map.put(inventory.getProductColor().getColorHex(),quantityStatus);
		}
		int quantity = inventoryList.stream().mapToInt(Inventory::getQuantity).sum();
		result.setColorQuantityStatus(map);
		result.setStatus(ProductQuantityStatus.getStatusByQuantity(quantity));
		log.info("Get quantity status dto for product [" + productSkuCode + "]");
		return result;
	}

	@Override
	public List<InventoryForBasketDto> getProductAvailableStatus(List<ProductColorDto> productColorDto) {
		List<InventoryForBasketDto> checkAvailableAndStatus = new ArrayList<>();
		for (ProductColorDto productColor : productColorDto) {
			Optional<Integer> inventory = inventoryRepository.findQuantityByProductSkuCodeAndColorHex(productColor.getProductSkuCode(),
					productColor.getColorHex());

			if (inventory.isPresent()) {
				checkAvailableAndStatus.add(new InventoryForBasketDto(productColor, new CheckingProductAvailableAndStatusDto(inventory.get(),
						ProductQuantityStatus.getStatusByQuantity(inventory.get()))));
				log.info("Get availableProductQuantity and quantityStatus for product with skuCode = "
						+ productColor.getProductSkuCode() + ", and color hex = " + productColor.getColorHex()
						+ ". class: InventoryServiceImpl, method: getProductAvailableStatus");
			}
		}

		return checkAvailableAndStatus;
	}
}
