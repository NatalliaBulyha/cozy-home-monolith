package com.cozyhome.onlineshop.productservice.controller.impl;

import com.cozyhome.onlineshop.productservice.controller.ImageController;
import com.cozyhome.onlineshop.dto.ImageDto;
import com.cozyhome.onlineshop.dto.PopUpImageDto;
import com.cozyhome.onlineshop.dto.productcard.ProductCardImageDto;
import com.cozyhome.onlineshop.dto.request.ProductColorDto;
import com.cozyhome.onlineshop.productservice.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.webjars.NotFoundException;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.basePath}/image")
public class ImageControllerImpl implements ImageController {
	private final ResourceLoader resourceLoader;
	private final ImageService imageService;

	@Value("${imageContentType}")
	private String imageContentType;

    @GetMapping
    @Validated
	public ResponseEntity<Resource> getImage(@RequestParam String imageName) {
		Resource resource = resourceLoader.getResource("classpath:images/" + imageName);
		if (!resource.exists()) {
			throw new NotFoundException("Image with name: " + imageName + " not found.");
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(imageContentType)).body(resource);
	}

    @PostMapping("/product_color")
	public ResponseEntity<ImageDto> getPreviewImageForProductByColor(@RequestBody @Valid ProductColorDto productColor) {
		return ResponseEntity.ok().body(imageService.getPreviewImageForProductByColor(productColor));
	}

    @PostMapping ("/pop_up_image")
    public ResponseEntity<List<PopUpImageDto>> getPopUpImageForProductByColor(@RequestBody @Valid ProductColorDto productColor) {
        return ResponseEntity.ok().body(imageService.getPopUpImageForProductByColor(productColor));
    }

    @PostMapping ("/product_card_image")
	public ResponseEntity<List<ProductCardImageDto>> getProductCardImagesByColor(@RequestBody @Valid ProductColorDto productColor){
		return ResponseEntity.ok().body(imageService.getProductCardImagesByColor(productColor));
	}
}
