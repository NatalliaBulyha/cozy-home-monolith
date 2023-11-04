package com.cozyhome.onlineshop.dto.order;

import com.cozyhome.onlineshop.orderservice.model.enums.PaymentMethod;
import com.cozyhome.onlineshop.validation.ValidEnum;
import com.cozyhome.onlineshop.validation.ValidName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeliveryDto {
    @ValidName
    private String city;
    @ValidEnum(enumClass = PaymentMethod.class)
    private String paymentMethod;

    private String street;
    private String house;
    private Integer apartment;
    private String comment;

    private String deliveryCompanyName;
    private String region;
    private String postOffice;
}
