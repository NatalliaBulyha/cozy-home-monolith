package com.cozyhome.onlineshop.userservice.repository;

import com.cozyhome.onlineshop.userservice.model.Address;

import java.util.Optional;

public interface AddressRepositoryCustom {
    Optional<Address> findAddressByFields(Address address);
}
