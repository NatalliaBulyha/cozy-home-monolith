package com.cozyhome.onlineshop.dto.auth;

import java.util.List;

public class JwtResponse {
	
	private String token;
	private String type;
	private String id;
	private String email;
	private List<String> roles;

}
