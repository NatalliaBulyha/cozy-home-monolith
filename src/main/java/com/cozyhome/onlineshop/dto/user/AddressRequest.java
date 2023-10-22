package com.cozyhome.onlineshop.dto.user;

import com.cozyhome.onlineshop.validation.ValidHouse;
import com.cozyhome.onlineshop.validation.ValidName;
import com.cozyhome.onlineshop.validation.ValidOptionalBoolean;
import com.cozyhome.onlineshop.validation.ValidOptionalNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {
    @ValidName(message = "Invalid city. City must be not null, greater than or equal to 2 and less than or equal" +
            "to 32, letters only.")
    private String city;
    @ValidName(message = "Invalid street. Street must be not null, greater than or equal to 2 and less than or equal" +
            "to 32, letters only.")
    private String street;
    @ValidHouse
    private String house;
    @ValidOptionalNumber(message = "Invalid apartment. Apartment must be not null and contains numbers only.")
    private String apartment;
    @ValidOptionalNumber(message = "Invalid entrance. Entrance must be not null and contains numbers only.")
    private String entrance;
    @ValidOptionalNumber(message = "Invalid floor. Floor must be not null and contains numbers only.")
    private String floor;
    @ValidOptionalBoolean
    private String withLift;
}
