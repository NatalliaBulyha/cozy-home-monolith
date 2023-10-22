package com.cozyhome.onlineshop.userservice.security.service;

import com.cozyhome.onlineshop.dto.user.AddressIdDto;
import com.cozyhome.onlineshop.dto.user.AddressRequest;
import com.cozyhome.onlineshop.dto.user.AddressResponse;

public interface AddressService {
    AddressResponse saveAddress(AddressRequest addressRequest, String userId);
    void deleteAddress(AddressIdDto addressId, String userId);
}