package com.cozyhome.onlineshop.dto.user;

import com.cozyhome.onlineshop.validation.ValidId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressIdDto {
    @ValidId
    private String id;
}
