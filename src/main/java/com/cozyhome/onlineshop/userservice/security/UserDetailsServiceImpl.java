package com.cozyhome.onlineshop.userservice.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cozyhome.onlineshop.userservice.model.User;
import com.cozyhome.onlineshop.userservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = userRepository.getByUsername(username);
		if(!user.isPresent()) {
			log.warn("[ON loadUserByUsername]:: user with username::[ {} ] not found", username);
            throw new UsernameNotFoundException("User not found for username - " + username);
		}
		log.info("[ON loadUserByUsername]:: loaded user with username [ {} ] and roles [ {} ]", user.get().getUsername(),
                user.get().getRole());
        return new AuthenticatedUser(user.get());
	}

}
