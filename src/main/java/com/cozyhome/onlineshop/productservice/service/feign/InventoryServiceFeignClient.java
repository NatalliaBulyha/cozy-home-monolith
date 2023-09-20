package com.cozyhome.onlineshop.productservice.service.feign;

import com.cozyhome.onlineshop.dto.InventoryForBasketDto;
import com.cozyhome.onlineshop.dto.QuantityStatusDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

//@FeignClient(value = "inventory-service", url = "http://localhost:8081/api/v1/inventory")
@FeignClient(value = "inventory-service", url = "${app.inventory.render.server.url}/inventory")
public interface InventoryServiceFeignClient {

	//for product card
	@GetMapping(path = "/color_quantity_status/product_skuCode", consumes = "application/json")
	QuantityStatusDto getColorQuantityStatusBySkuCode(@RequestParam String productSkuCode);

	//for review
	@PostMapping(path = "/quantity_status_map/product_skuCode_list", consumes = "application/json")
	Map<String, String> getProductQuantityStatusMap(@RequestBody List<String> productSkuCodeList);

    // for shopping card
    @PostMapping(path = "/shopping-card-info", consumes = "application/json")
    List<InventoryForBasketDto> getProductAvailableStatus(@RequestBody List<ProductColorDto> productColorDto);

}
