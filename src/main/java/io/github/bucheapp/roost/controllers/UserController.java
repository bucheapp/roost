package io.github.bucheapp.roost.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.CreateAdminUserRequest;
import io.github.bucheapp.roost.dto.request.CreateUserRequest;
import io.github.bucheapp.roost.dto.request.UpdateEmailRequest;
import io.github.bucheapp.roost.dto.request.UpdatePasswordRequest;
import io.github.bucheapp.roost.dto.response.UserPrivateResponse;
import io.github.bucheapp.roost.dto.response.UserPublicResponse;
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
		UserPublicResponse userResponse = new UserPublicResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserPrivateResponse> getMe(
			Authentication authentication) {

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		long id = Long.parseLong(userDetails.getUsername());

		User user = userService.getUserById(id);
		UserPrivateResponse userResponse = new UserPrivateResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
	
	@PatchMapping("/me/email")
	public ResponseEntity<UserResponse> updateEmail(
			Authentication authentication,
			@RequestBody UpdateEmailRequest updateEmailRequest) {

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		long id = Long.parseLong(userDetails.getUsername());
		
		String newEmail = updateEmailRequest.getEmail();
		User user = userService.updateEmailById(id, newEmail);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);

		return ResponseEntity.ok(userResponse);
	}
	
	@PatchMapping("/me/password")
	public ResponseEntity<UserResponse> updatePassword(
			Authentication authentication,
			@RequestBody UpdatePasswordRequest updatePasswordRequest) {

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		long id = Long.parseLong(userDetails.getUsername());
		
		String newPassword = updatePasswordRequest.getPassword();

		User user = userService.updatePasswordById(id, newPassword);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);

		return ResponseEntity.ok(userResponse);
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteUser(
			Authentication authentication) {

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		long id = Long.parseLong(userDetails.getUsername());

		userService.deleteUserById(id);

		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasAuthority('CREATE_USER')")
	@PostMapping
	public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest req) {
		userService.createUser(req);
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasAuthority('CREATE_ADMINUSER')")
	@PostMapping("/admin")
	public ResponseEntity<Void> createAdminUser(@RequestBody CreateAdminUserRequest req) {
		userService.createAdminUser(req);
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasAuthority('FREEZE_USER')")
	@PatchMapping("/{publicId}/freeze")
	public ResponseEntity<Void> freezeUser(
			@PathVariable long publicId
			) {
		userService.setFrozen(publicId, true);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAuthority('FREEZE_USER')")
	@PatchMapping("/{publicId}/unfreeze")
	public ResponseEntity<Void> unfreezeUser(
			@PathVariable long publicId
			) {
		userService.setFrozen(publicId, false);
		return ResponseEntity.noContent().build();
	}
}
