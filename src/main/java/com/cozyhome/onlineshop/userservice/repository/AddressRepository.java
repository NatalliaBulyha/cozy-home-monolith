package com.cozyhome.onlineshop.userservice.repository;

import com.cozyhome.onlineshop.userservice.model.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AddressRepository extends MongoRepository<Address, String> {
    List<Address> findAllByUserId(String userId);
}
