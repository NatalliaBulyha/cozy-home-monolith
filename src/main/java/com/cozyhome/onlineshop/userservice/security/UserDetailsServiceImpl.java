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
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Optional<User> user = userRepository.getByEmail(email);
		if(!user.isPresent()) {
			log.warn("[ON loadUserByUsername]:: user with username::[ {} ] not found", email);
            throw new UsernameNotFoundException("User not found for username - " + email);
		}
		log.info("[ON loadUserByUsername]:: loaded user with username [ {} ] and roles [ {} ]", user.get().getEmail(),
                user.get().getRoles());
        return new AuthenticatedUserDetails(user.get());
	}

}
