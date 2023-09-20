package com.cozyhome.onlineshop.productservice.controller.impl;

import com.cozyhome.onlineshop.productservice.controller.MaterialController;
import com.cozyhome.onlineshop.dto.MaterialDto;
import com.cozyhome.onlineshop.productservice.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.basePath}/material")
public class MaterialControllerImpl implements MaterialController {
    private final MaterialService materialService;

    @Override
    @GetMapping
    public ResponseEntity<List<MaterialDto>> getMaterials() {
        return new ResponseEntity<>(materialService.getMaterials(), HttpStatus.OK);
    }
}
