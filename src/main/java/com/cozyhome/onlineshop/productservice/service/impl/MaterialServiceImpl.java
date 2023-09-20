package com.cozyhome.onlineshop.productservice.service.impl;

import com.cozyhome.onlineshop.dto.MaterialDto;
import com.cozyhome.onlineshop.productservice.model.Material;
import com.cozyhome.onlineshop.productservice.repository.MaterialRepository;
import com.cozyhome.onlineshop.productservice.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialServiceImpl implements MaterialService {
    private final MaterialRepository materialRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<MaterialDto> getMaterials() {
        List<Material> materials = materialRepository.findAllByActive(true);
        return materials.stream().map(material -> modelMapper.map(material, MaterialDto.class)).toList();
    }
}
