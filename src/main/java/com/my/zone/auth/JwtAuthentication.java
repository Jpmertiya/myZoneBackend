package com.my.zone.auth;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.my.zone.Repo.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthentication extends OncePerRequestFilter {

	Logger logger = LoggerFactory.getLogger(JwtAuthentication.class);

	@Autowired
	JwtService jwtService;

	@Autowired
	UserDetailsService detailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token;
		String userName;
		logger.info("validating token (1/4)");
		if (authHeader == null || !authHeader.startsWith("Bearer")) {
			filterChain.doFilter(request, response);
			return;
		}
		token = authHeader.substring(7);
		userName = jwtService.getUserName(token);
		logger.info("validating username (2/4)");
		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails user = detailsService.loadUserByUsername(userName);
			logger.info("checking token expiration (3/4)");
			if (jwtService.isTokenExpired(user, token)) {
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						user.getUsername(), null, user.getAuthorities());
				logger.info("token validated successfully (4/4)");
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}

		}
		filterChain.doFilter(request, response);
	}

}
