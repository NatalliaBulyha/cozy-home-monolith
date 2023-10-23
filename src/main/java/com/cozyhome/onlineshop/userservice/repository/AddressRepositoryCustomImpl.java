package com.cozyhome.onlineshop.userservice.repository;

import com.cozyhome.onlineshop.userservice.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class AddressRepositoryCustomImpl implements AddressRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    public Optional<Address> findAddressByFields(Address address) {

        Query query = new Query();

        query.addCriteria(Criteria.where("city").is(address.getCity())
                .and("street").is(address.getStreet())
                .and("house").is(address.getHouse())
                .and("apartment").is(address.getApartment())
                .and("entrance").is(address.getEntrance())
                .and("floor").is(address.getFloor())
                .and("withLift").is(address.isWithLift())
                .and("user").is(address.getUser()));

        return Optional.ofNullable(mongoTemplate.findOne(query, Address.class));
    }
}
