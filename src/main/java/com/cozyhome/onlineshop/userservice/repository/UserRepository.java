package com.cozyhome.onlineshop.userservice.repository;

import java.util.List;
import java.util.Optional;

import com.cozyhome.onlineshop.userservice.model.RoleE;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.cozyhome.onlineshop.userservice.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

	Optional<User> getByEmail(String email);

	Optional<User> getUserById(String id);
	
	boolean existsByEmail(String username);

	List<User> findByRoles(RoleE role);
	
	Optional<User> findByEmail(String email);

	Optional<User> findById(String userId);
}
