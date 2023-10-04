package com.cozyhome.onlineshop.userservice.repository;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cozyhome.onlineshop.userservice.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

	Optional<User> getByEmail(String email);
	
	boolean existsByEmail(String username);

}
