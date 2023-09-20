package com.cozyhome.onlineshop.productservice.controller.impl;

import com.cozyhome.onlineshop.productservice.controller.CategoryController;
import com.cozyhome.onlineshop.dto.CategoryWithIconDto;
import com.cozyhome.onlineshop.dto.CategoryWithPhotoDto;
import com.cozyhome.onlineshop.dto.CategoryWithSubCategoriesDto;
import com.cozyhome.onlineshop.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.basePath}/category")
public class CategoryControllerImpl implements CategoryController {
	private final CategoryService categoryService;

    @Override
    @GetMapping
	public ResponseEntity<List<CategoryWithIconDto>> getCategoryWithIcon() {
		return new ResponseEntity<>(categoryService.getCategoryWithIcon(), HttpStatus.OK);
	}

	@Override
    @GetMapping("/categories")	public ResponseEntity<List<CategoryWithSubCategoriesDto>> getCategoriesWithSubcategoriesAndPhoto() {
		return new ResponseEntity<>(categoryService.getCategoriesWithPhoto(), HttpStatus.OK);
	}

	@Override
    @GetMapping("/homepage/categories")
	public ResponseEntity<List<CategoryWithPhotoDto>> getCategoriesWithPhoto() {
		return new ResponseEntity<>(categoryService.getCategoriesForHomepage(), HttpStatus.OK);
	}
}
