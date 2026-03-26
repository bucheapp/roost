package io.github.bucheapp.roost.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import io.github.bucheapp.roost.dto.request.PermissionRequest;
import io.github.bucheapp.roost.dto.request.UpdateEmailRequest;
import io.github.bucheapp.roost.dto.request.UpdatePasswordRequest;
import io.github.bucheapp.roost.dto.request.UserStateUpdateRequest;
import io.github.bucheapp.roost.dto.response.PermissionResponse;
import io.github.bucheapp.roost.dto.response.UserPrivateResponse;
import io.github.bucheapp.roost.dto.response.UserPublicResponse;
import io.github.bucheapp.roost.dto.response.UserResponse;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.security.CustomUserDetails;
import io.github.bucheapp.roost.services.UserService;

@RestController
@RequestMapping("api/users")
public class UserController {
	@Autowired
	private UserService userService;
	
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

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long id = userDetails.getId();

		User user = userService.getUserById(id);
		UserPrivateResponse userResponse = new UserPrivateResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
	
	@PatchMapping("/me/email")
	public ResponseEntity<UserResponse> updateEmail(
			Authentication authentication,
			@RequestBody UpdateEmailRequest updateEmailRequest) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long id = userDetails.getId();
		
		String newEmail = updateEmailRequest.getEmail();
		User user = userService.updateEmailById(id, newEmail);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);

		return ResponseEntity.ok(userResponse);
	}
	
	@PatchMapping("/me/password")
	public ResponseEntity<UserResponse> updatePassword(
			Authentication authentication,
			@RequestBody UpdatePasswordRequest updatePasswordRequest) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long id = userDetails.getId();
		
		String newPassword = updatePasswordRequest.getPassword();

		User user = userService.updatePasswordById(id, newPassword);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);

		return ResponseEntity.ok(userResponse);
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteUser(
			Authentication authentication) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long id = userDetails.getId();

		userService.deleteUserById(id);

		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAuthority('CREATE_USER')")
	@PostMapping
	public ResponseEntity<Void> createUser(
			@RequestBody CreateUserRequest req,
			Authentication authentication) {
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long id = userDetails.getId();
		
		userService.createUser(id,req);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAuthority('CREATE_ADMINUSER')")
	@PostMapping("/admin")
	public ResponseEntity<Void> createAdminUser(@RequestBody CreateAdminUserRequest req) {
		userService.createAdminUser(req);
		return ResponseEntity.ok().build();
	}
	
	@PreAuthorize("hasAuthority('GET_PERMISSION')")
	@GetMapping("/{publicId}/permissions")
	public ResponseEntity<PermissionResponse> getPermissions(
		@PathVariable long publicId) {
		Set<String> permmisions = userService.getPermissions(publicId)
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
		PermissionResponse permmisionResponse = new PermissionResponse(permmisions);
		
		return ResponseEntity.ok(permmisionResponse);
	}
	
	@PreAuthorize("hasAuthority('GRANT_PERMISSION')")
	@PostMapping("/{publicId}/permissions")
	public ResponseEntity<Void> grantPermissions(
		@PathVariable long publicId,
		@RequestBody PermissionRequest req,
		Authentication authentication) {
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long id = userDetails.getId();
		
		userService.grantPermissions(id,publicId, req);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAuthority('REVOKE_PERMISSION')")
	@DeleteMapping("/{publicId}/permissions")
	public ResponseEntity<Void> revokePermissions(
		@PathVariable long publicId,
		@RequestBody PermissionRequest req,
		Authentication authentication) {
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		long id = userDetails.getId();
		
		userService.revokePermissions(id,publicId, req);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAuthority('UPDATE_USERSTATE')")
	@PatchMapping("/{publicId}/state")
	public ResponseEntity<UserPublicResponse> freezeUser(
			@PathVariable long publicId,
			@RequestBody UserStateUpdateRequest req
			) {
		User user = userService.updateUserState(publicId, req);
		
		UserPublicResponse userResponse = new UserPublicResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
}
