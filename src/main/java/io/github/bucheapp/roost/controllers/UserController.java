package io.github.bucheapp.roost.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.UpdateEmailRequest;
import io.github.bucheapp.roost.dto.request.UpdatePasswordRequest;
import io.github.bucheapp.roost.dto.response.UserPrivateResponse;
import io.github.bucheapp.roost.dto.response.UserResponse;
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
	
	@GetMapping("/{publicId}")
	public ResponseEntity<UserResponse> getUser(
			@PathVariable long publicId
			) {
		User user = userService.getUserByPublicId(publicId);
		UserResponse userResponse = null;
		
		return ResponseEntity.ok(userResponse);
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserPrivateResponse> getMe(
			@RequestHeader("Authorization") String authHeader) {

		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);

		User user = userService.getUserById(id);
		UserPrivateResponse userResponse = new UserPrivateResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
	
	@PatchMapping("/me/email")
	public ResponseEntity<UserResponse> updateEmail(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody UpdateEmailRequest updateEmailRequest) {

		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		String newEmail = updateEmailRequest.getEmail();
		User user = userService.updateEmailById(id, newEmail);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);

		return ResponseEntity.ok(userResponse);
	}
	
	@PatchMapping("/me/password")
	public ResponseEntity<UserResponse> updatePassword(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody UpdatePasswordRequest updatePasswordRequest) {

		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		String newPassword = updatePasswordRequest.getPassword();

		User user = userService.updatePasswordById(id, newPassword);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);

		return ResponseEntity.ok(userResponse);
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteUser(
			@RequestHeader("Authorization") String authHeader) {

		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);

		userService.deleteUserById(id);

		return ResponseEntity.ok().build();
	}
	
	@PatchMapping("/{publicId}/freeze")
	public ResponseEntity<Void> freezeUser(
			@PathVariable long publicId
			) {
	    userService.setFrozen(publicId, true);
	    return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{publicId}/unfreeze")
	public ResponseEntity<Void> unfreezeUser(
			@PathVariable long publicId
			) {
	    userService.setFrozen(publicId, false);
	    return ResponseEntity.noContent().build();
	}
}
