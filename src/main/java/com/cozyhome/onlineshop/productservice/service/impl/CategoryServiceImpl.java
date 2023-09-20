package com.cozyhome.onlineshop.productservice.service.impl;

import com.cozyhome.onlineshop.dto.CategoryWithIconDto;
import com.cozyhome.onlineshop.dto.CategoryWithPhotoDto;
import com.cozyhome.onlineshop.dto.CategoryWithSubCategoriesDto;
import com.cozyhome.onlineshop.productservice.model.Category;
import com.cozyhome.onlineshop.productservice.model.ImageCategory;
import com.cozyhome.onlineshop.productservice.repository.CategoryRepository;
import com.cozyhome.onlineshop.productservice.repository.ImageCategoryRepository;
import com.cozyhome.onlineshop.productservice.service.CategoryService;
import com.cozyhome.onlineshop.productservice.service.builder.CategoryBuilder;
import com.cozyhome.onlineshop.productservice.wrapper.IdWrapper;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
    private final ImageCategoryRepository imageCategoryRepository;
    private final CategoryBuilder categoryBuilder;
	private final ModelMapper modelMapper;

	@Override
	public List<CategoryWithIconDto> getCategoryWithIcon() {
		List<Category> categories = categoryRepository.findAllByActiveAndParentIdNull(true);
		return categories.stream().map(category -> modelMapper.map(category, CategoryWithIconDto.class)).toList();
	}

	@Override
	public List<ObjectId> getCategoriesIdsByParentId(String parentId) {
		List<ObjectId> ids = categoryRepository.findAllIdsOnlyByActiveAndParentId(true, new ObjectId(parentId)).stream()
				.map(IdWrapper::getId).toList();
		return ids;
	}

    @Override
    public List<CategoryWithSubCategoriesDto> getCategoriesWithPhoto() {
        List<Category> categories = categoryRepository.findAllByActive(true);
        List<ObjectId> ids = categories.stream().map(Category::getId).toList();
        List<ImageCategory> images = imageCategoryRepository.findAllByCatalogAndCategoryIn(true, ids);
        return categoryBuilder.buildParentCategoryWithCategoriesDtoList(categories, images);
    }

	@Override
	public List<CategoryWithPhotoDto> getCategoriesForHomepage() {
		List<ImageCategory> imageCategories = imageCategoryRepository.findAllByCatalogFalse();
		return imageCategories.stream().map(categoryBuilder::mapToCategoryForHomepageDto).toList();
	}

}
