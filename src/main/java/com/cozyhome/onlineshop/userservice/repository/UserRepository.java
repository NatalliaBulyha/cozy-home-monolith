package com.cozyhome.onlineshop.userservice.repository;

import java.util.List;
import java.util.Optional;

import com.cozyhome.onlineshop.userservice.model.UserStatusE;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cozyhome.onlineshop.userservice.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

	Optional<User> findByEmailAndStatus(String email, UserStatusE status);

	Optional<User> findById(String id);
	
	boolean existsByEmailAndStatus(String username, UserStatusE status);

	Optional<User> findByIdAndStatus(String userId, UserStatusE status);
}
