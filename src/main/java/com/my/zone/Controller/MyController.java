package com.my.zone.Controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.zone.Models.User;
import com.my.zone.Repo.NotesRepository;
import com.my.zone.Repo.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/myzone/api/v1")
public class MyController {
	Logger logger = LoggerFactory.getLogger(MyController.class);

	@Autowired
	private UserRepository repository;

	@Autowired
	private NotesRepository notesRepository;

	@GetMapping("/homes")
	public String homeGround() {
		return "not possible";
	}

	@GetMapping("/notes")
	public String Notess() {
		return "notes here";
	}

	@GetMapping("/auth")
	public String authen() {
		return "auth here";
	}

	@PostMapping("/reg")
	synchronized public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
		if (!result.hasErrors()) {

			user.setDate(LocalDateTime.now());

			try {
				Optional<User> findById = repository.findById(user.getEmail());
				if (findById.isEmpty()) {
					User save = repository.save(user);
					logger.info(save.toString());
					return ResponseEntity.status(HttpStatus.OK).body(save);
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
