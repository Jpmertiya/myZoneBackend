package com.my.zone.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class Endpoints {

	@Autowired
	JwtAuthentication authentication;

	@Autowired
	AuthenticationProvider authenticationProvider;

	@Bean
	SecurityFilterChain chain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf((c) -> c.disable()).authorizeHttpRequests((auth) -> {
			auth.requestMatchers("/newsZone-ui").permitAll().requestMatchers("/swagger-ui/**").permitAll()
					.requestMatchers("/newsZone-docs").permitAll().requestMatchers("/myzone/api/v1/login").permitAll().anyRequest().authenticated();
		}).sessionManagement((sess) -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(authentication, UsernamePasswordAuthenticationFilter.class).build();
	}

}
