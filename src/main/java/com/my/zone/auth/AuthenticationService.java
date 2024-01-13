package com.my.zone.auth;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.my.zone.Models.User;
import com.my.zone.Repo.UserRepository;

@Service
public class AuthenticationService {
	Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	@Autowired
	JwtService jwtService;

	@Autowired
	UserRepository repository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	AuthenticationManager manager;

	public String generateToken(User user) {

		user.setDate(LocalDateTime.now());
		user.setPasswor(encoder.encode(user.getPasswor()));
		User save = repository.save(user);
		logger.info(save.toString());

		String generateToken = jwtService.generateToken(user);
		return generateToken;
	}

	public String authenticate(String username, String password) {
		manager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		User users = repository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found exception"));
		String generateToken = jwtService.generateToken(users);
		return generateToken;
	}

}
