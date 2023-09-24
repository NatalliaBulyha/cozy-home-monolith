package com.cozyhome.onlineshop.userservice.security.JWT;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

	private JwtTokenUtil jwtTokenUtil;
	private UserDetailsService userDetailsService;
	
	@Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String username = null;
        String jwtToken = null;
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("[ON doFilterInternal]:: header {}", header);
        if (header != null && header.startsWith("Bearer ")) {
            jwtToken = header.substring(7);
            log.info("[ON doFilterInternal]:: jwtToken [ {} ]", jwtToken);
            try {
                username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                log.info("[ON doFilterInternal]:: username [ {} ]", username);
            } catch (InvalidCsrfTokenException e) {
                request.setAttribute("exception", e.getLocalizedMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/expired-jwt");
                dispatcher.forward(request, response);
            }
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("[ON doFilterInternal]:: starting token validation...");
            UserDetails user = userDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(jwtToken, user)) {
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
