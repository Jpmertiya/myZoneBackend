package com.my.zone.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.zone.Models.LoginCred;
import com.my.zone.Models.Notes;
import com.my.zone.Models.User;
import com.my.zone.Repo.NotesRepository;
import com.my.zone.Repo.UserRepository;
import com.my.zone.auth.AuthenticationService;
import com.my.zone.auth.JwtService;

import jakarta.servlet.http.HttpServletRequest;
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

	@Autowired
	JwtService jwtService;

	@PostMapping("/login")
	public ResponseEntity<?> authen(@RequestBody LoginCred cred) {
		try {
			if (cred != null) {
				String token = authenticationService.authenticate(cred.getEmail(), cred.getPassowrd());
				return ResponseEntity.status(HttpStatus.OK).body(token);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@PostMapping("/reg")
	public ResponseEntity<?> register(@Valid @RequestBody User user, BindingResult result) {
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

	@PostMapping("/getUser")
	public ResponseEntity<?> getUserfromToken(HttpServletRequest request) {
		try {
			String token = request.getHeader("Authorization");
			User user = authenticationService.getUser(token.substring(7));
			return ResponseEntity.status(HttpStatus.OK).body(user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("invalid token");
		}
	}

	@PostMapping("/notes/save")
	public ResponseEntity<?> saveNotes(@Valid @RequestBody Notes notes, BindingResult bindingResult,
			HttpServletRequest request) {

		if (bindingResult.hasErrors()) {
			List<String> errors = bindingResult.getFieldErrors().stream().map(FieldError::getDefaultMessage)
					.collect(Collectors.toList());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		try {
			String token = request.getHeader("Authorization");
			System.out.println(token);
			String userName = jwtService.getUserName(token.substring(7));
			logger.info(userName);
			notes.setId(new ObjectId());
			notes.setUserId(userName);
			notes.setDateTime(LocalDateTime.now());
			logger.info("All values are set successfully");
			Notes save = notesRepository.save(notes);
			return ResponseEntity.status(HttpStatus.OK).body(save);
		} catch (Exception e) {
			logger.info("Problem while saving notes");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GetMapping("/notes/getAllNotes")
	public ResponseEntity<List<Notes>> getAllNotes(HttpServletRequest request) {
		String token = request.getHeader("Authorization").substring(7);
		logger.info(token);
		String userName = jwtService.getUserName(token);
		logger.info(userName);

		try {
			List<Notes> notes = notesRepository.findByUserId(userName);
			System.out.println(notes);
			return ResponseEntity.status(HttpStatus.OK).body(notes);
		} catch (Exception e) {
			logger.info("Problem occur while fetching notes");

		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

	}

	@PutMapping("/notes/updateNotes")
	public ResponseEntity<?> updateNotes(@RequestParam String noteId, @RequestBody Notes notes,
			HttpServletRequest request) {

		String userName = jwtService.getUserName(request.getHeader("Authorization").substring(7));
		ObjectId noteIds = new ObjectId(noteId);
		logger.info("updating notes with id " + noteIds);
		try {
			Optional<Notes> notesId = notesRepository.findById(noteIds);
			Notes note = notesId.get();
			logger.info("Processing...");
			if (note.getUserId().equals(userName)) {
				logger.info("all check verified and now saving ti database");
				notes.setId(note.getId());
				notes.setUserId(note.getUserId());
				notes.setDateTime(LocalDateTime.now());
				logger.info("" + notes);
				notesRepository.save(notes);
				logger.info("saved successfully");
				return ResponseEntity.status(HttpStatus.OK).body(notes);
			} else {
				logger.info("unAuthorize person trying to change notes ...Access Denied");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access Denied!");
			}

		} catch (Exception e) {
			logger.info("error occured either while fetching note or while saving note");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	@DeleteMapping("/notes/deleteNotes")
	public ResponseEntity<?> deletenotes(@RequestParam String id, HttpServletRequest request) {
		ObjectId id2 = new ObjectId(id);
		try {
			Optional<Notes> id3 = notesRepository.findById(id2);
			if (id3 != null) {
				String userName = jwtService.getUserName(request.getHeader("Authorization").substring(7));
				if (userName.equals(id3.get().getUserId())) {
					notesRepository.deleteById(id2);
					return ResponseEntity.status(HttpStatus.OK).body("deleted successfully");
				}
			}

		} catch (Exception e) {
			logger.info("can't delete notes..");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}
}
