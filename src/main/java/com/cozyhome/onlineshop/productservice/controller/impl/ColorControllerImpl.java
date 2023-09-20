package com.cozyhome.onlineshop.productservice.controller.impl;

import com.cozyhome.onlineshop.productservice.controller.ColorController;
import com.cozyhome.onlineshop.dto.ColorDto;
import com.cozyhome.onlineshop.productservice.service.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.basePath}/colors")
public class ColorControllerImpl implements ColorController {
    private final ColorService colorService;

    @Override
    @GetMapping
    public ResponseEntity<List<ColorDto>> getColors() {
        return new ResponseEntity<>(colorService.getColors(), HttpStatus.OK);
    }
}
