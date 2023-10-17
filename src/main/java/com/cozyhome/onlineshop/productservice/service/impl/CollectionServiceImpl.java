package com.cozyhome.onlineshop.productservice.service.impl;

import com.cozyhome.onlineshop.dto.CollectionDto;
import com.cozyhome.onlineshop.exception.DataNotFoundException;
import com.cozyhome.onlineshop.productservice.model.Collection;
import com.cozyhome.onlineshop.productservice.repository.CollectionRepository;
import com.cozyhome.onlineshop.productservice.service.CollectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {
    private final CollectionRepository collectionRepository;
    private final ModelMapper modelMapper;
    @Override
    public List<CollectionDto> getCollections() {
        List<Collection> collections = collectionRepository.findAllByActive(true);
        if (collections.isEmpty()) {
            log.error("[ON getCollections]:: Collections not found.");
            throw new DataNotFoundException("Collections not found.");
        }
        return collections.stream().map(collection -> modelMapper.map(collection, CollectionDto.class)).toList();
    }
}
