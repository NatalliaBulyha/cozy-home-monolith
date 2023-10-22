package com.cozyhome.onlineshop.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private String id;
    private String city;
    private String street;
    private String house;
    private Integer apartment;
    private Integer entrance;
    private Integer floor;
    private Boolean withLift;
}
