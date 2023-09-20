package com.cozyhome.onlineshop.dto.request;

import com.cozyhome.onlineshop.validation.ValidSkuCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProductColorDto {

    @ValidSkuCode
    private String productSkuCode;
    @NotNull
    @Pattern(regexp = "^#[A-Za-z0-9]{3,8}+$", message = "Invalid Hex. Hex must start with # and then 3 to 8 characters.")
    private String colorHex;
}
