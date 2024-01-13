package com.my.zone.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.zone.Models.LoginCred;
import com.my.zone.Models.User;
import com.my.zone.Repo.NotesRepository;
import com.my.zone.Repo.UserRepository;
import com.my.zone.auth.AuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/myzone/api/v1")
public class MyController {
	Logger logger = LoggerFactory.getLogger(MyController.class);

	@Autowired
	private UserRepository repository;

	@Autowired
	private NotesRepository notesRepository;

	@Autowired
	AuthenticationService authenticationService;

	@GetMapping("/home")
	public String homeGround() {
		return "not possible";
	}

	@GetMapping("/notes")
	public String Notess() {
		return "notes here";
	}

	@PostMapping("/login")
	public ResponseEntity<?> authen(@RequestBody LoginCred cred) {
		if (cred != null) {
			String token = authenticationService.authenticate(cred.getEmail(), cred.getPassowrd());
			return ResponseEntity.status(HttpStatus.OK).body(token);
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@PostMapping("/reg")
	synchronized public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
		if (!result.hasErrors()) {
			try {
				Optional<User> findById = repository.findById(user.getEmail());
				if (findById.isEmpty()) {
					return ResponseEntity.status(HttpStatus.OK).body(authenticationService.generateToken(user));
				} else {
					logger.info("User already exists");
				}
			} catch (Exception e) {
				logger.info("internal server error occured while fetching and saving user");
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

		} else {

			logger.info(result.toString());

		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

	}

}
