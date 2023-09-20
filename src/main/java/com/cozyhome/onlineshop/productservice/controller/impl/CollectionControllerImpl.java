package com.cozyhome.onlineshop.productservice.controller.impl;

import com.cozyhome.onlineshop.productservice.controller.CollectionController;
import com.cozyhome.onlineshop.dto.CollectionDto;
import com.cozyhome.onlineshop.productservice.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.basePath}/collection")
public class CollectionControllerImpl implements CollectionController {
    private final CollectionService collectionService;

    @Override
    @GetMapping
    public ResponseEntity<List<CollectionDto>> getCollections() {
        return new ResponseEntity<>(collectionService.getCollections(), HttpStatus.OK);
    }
}
