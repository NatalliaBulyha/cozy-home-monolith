package com.cozyhome.onlineshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cozyhome.onlineshop.userservice.model.Role;
import com.cozyhome.onlineshop.userservice.security.UrlAuthenticationSuccessHandler;
import com.cozyhome.onlineshop.userservice.security.UserDetailsServiceImpl;
import com.cozyhome.onlineshop.userservice.security.JWT.JwtAuthenticationEntryPoint;
import com.cozyhome.onlineshop.userservice.security.JWT.JwtTokenFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private UserDetailsServiceImpl userDetailsService;
	private JwtTokenFilter jwtTokenFilter;
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	private static final String LOGIN_URL = "/home/log_in";
	private static final String LOGOUT_URL = "/home/log_out";
	private static final String LOGIN_ERROR_URL = "";
	private static final String ADMIN_PANEL_URL = "/admin";
	
	@Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

	    @Override
	    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	    }

	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        http.csrf().disable().authorizeRequests().mvcMatchers(ADMIN_PANEL_URL)
	                .hasAnyAuthority(Role.UserRole.ADMIN, Role.UserRole.MANAGER).mvcMatchers(LOGIN_URL, LOGIN_ERROR_URL).permitAll().mvcMatchers()
	                .denyAll().and().formLogin().loginPage(LOGIN_URL).successHandler(authenticationSuccessHandler())
	                .failureUrl(LOGIN_ERROR_URL).and().logout().logoutUrl(LOGOUT_URL).logoutSuccessUrl()
	                .invalidateHttpSession(true).deleteCookies("JSESSIONID");
	        http.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);
	        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	    }

	    @Bean
	    public BCryptPasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	    @Bean
	    public AuthenticationSuccessHandler authenticationSuccessHandler() {
	        return new UrlAuthenticationSuccessHandler();
	    }
}
