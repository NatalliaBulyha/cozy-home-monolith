package com.cozyhome.onlineshop.userservice.security.JWT;

import java.io.IOException;

import com.cozyhome.onlineshop.userservice.security.AuthenticatedUserDetails;
import com.cozyhome.onlineshop.userservice.security.service.ExtendedUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cozyhome.onlineshop.userservice.security.InvalidTokenException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private ExtendedUserDetailsService userDetailsService;
    @Value("${header.name.user-id}")
    private String userIdAttributeName;
	
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String username = null;
        String jwtToken = jwtTokenUtil.resolveToken(request);
        if (jwtToken != null) {
            log.info("[ON doFilterInternal]:: jwtToken [{}]", jwtToken);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                log.info("[ON doFilterInternal]:: username [ {} ]", username);
            } catch (InvalidTokenException | InvalidCsrfTokenException e) {  
                request.setAttribute("exception", e.getLocalizedMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/api/v1/auth/expired-jwt");
                dispatcher.forward(request, response);
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("[ON doFilterInternal]:: starting token validation...");
            AuthenticatedUserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, user)) {
                request.setAttribute(userIdAttributeName, user.getUser().getId());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
                        user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("[ON doFilterInternal]:: set authentication to SecurityContextHolder - {}", authToken);
            }
        }
        log.info("[ON doFilterInternal]:: filtering request and response by FilterChain");
        filterChain.doFilter(request, response);
    }
	
}
