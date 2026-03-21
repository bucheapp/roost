package io.github.bucheapp.roost.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/users")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtService jwtService;
	
	@GetMapping("/me")
	public Mono<ResponseEntity<User>> getUser(
			@RequestHeader("Authorization") String authHeader) {
		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		
		return userService.getUserById(id)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	@PatchMapping("/me/email")
	public Mono<ResponseEntity<Void>> updateEmail(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody Map<String, String> body) {
		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		String newEmail = body.get("email");
		
		return userService.updateEmailById(id,newEmail)
				.thenReturn(ResponseEntity.ok().<Void>build());
	}
	
	@PatchMapping("/me/password")
	public Mono<ResponseEntity<Void>> updatePassword(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody Map<String, String> body) {
		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		String newPassword = body.get("password");
		
		return userService.updateEmailById(id,newPassword)
				.thenReturn(ResponseEntity.ok().<Void>build());
	}
	
	@DeleteMapping("/me")
	public Mono<ResponseEntity<Void>> deleteUser(
			@RequestHeader("Authorization") String authHeader) {
		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		
		return userService.deleteUserById(id)
				.thenReturn(ResponseEntity.ok().<Void>build());
	}
}
