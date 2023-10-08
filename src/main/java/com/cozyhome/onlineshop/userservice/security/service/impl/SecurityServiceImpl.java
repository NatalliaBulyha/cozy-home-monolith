package com.cozyhome.onlineshop.userservice.security.service.impl;

import com.cozyhome.onlineshop.userservice.security.service.SecurityService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SecurityServiceImpl implements SecurityService {

	private final AuthenticationManager authenticationManager;
	
	@Override
	public boolean isAuthenticated(String username, String password) {
		try {
			log.info("[ON isAuthenticated]:: authenticating by UsernamePasswordAuthenticationToken...");
	        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	        log.info("[ON authenticate]:: user with username [ {} ] is authenticated successfully", username);
            return true;
        } catch (AuthenticationException e) {
            log.error("[ON isAuthenticated]:: Exception :: {}.", e);
            throw new BadCredentialsException(e.getMessage());
        }
	}
}
