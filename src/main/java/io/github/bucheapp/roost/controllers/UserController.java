package io.github.bucheapp.roost.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.services.JwtService;
import io.github.bucheapp.roost.services.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtService jwtService;
	
	@GetMapping("/me")
	public ResponseEntity<User> getUser(
			@RequestHeader("Authorization") String authHeader) {

		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);

		User user = userService.getUserById(id);

		return ResponseEntity.ok(user);
	}
	
	@PatchMapping("/me/email")
	public ResponseEntity<Void> updateEmail(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody Map<String, String> body) {

		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		String newEmail = body.get("email");

		userService.updateEmailById(id, newEmail);

		return ResponseEntity.ok().build();
	}
	
	@PatchMapping("/me/password")
	public ResponseEntity<Void> updatePassword(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody Map<String, String> body) {

		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		String newPassword = body.get("password");

		userService.updatePasswordById(id, newPassword);

		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteUser(
			@RequestHeader("Authorization") String authHeader) {

		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);

		userService.deleteUserById(id);

		return ResponseEntity.ok().build();
	}
}
