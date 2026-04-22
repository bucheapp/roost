package io.github.bucheapp.roost.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import io.github.bucheapp.roost.dto.request.UpdatePasswordRequest;
import io.github.bucheapp.roost.dto.request.UpdateUserRequest;
import io.github.bucheapp.roost.dto.request.UpdateUserStateRequest;
import io.github.bucheapp.roost.dto.response.CommunitiesResponse;
import io.github.bucheapp.roost.dto.response.PermissionResponse;
import io.github.bucheapp.roost.dto.response.UserPrivateResponse;
import io.github.bucheapp.roost.dto.response.UserPublicResponse;
import io.github.bucheapp.roost.dto.response.UserResponse;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.User;
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
		User user = userService.getUser(publicId);
		UserPublicResponse userResponse = new UserPublicResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserPrivateResponse> getMe() {
		User user = userService.getCurrentUser();
		UserPrivateResponse userResponse = new UserPrivateResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
	
	@PatchMapping("/me")
	public ResponseEntity<UserResponse> updateUser(
			@Valid @RequestBody UpdateUserRequest req) {
		User user = userService.updateCurrentUser(req);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);

		return ResponseEntity.ok(userResponse);
	}
	
	@PatchMapping("/me/password")
	public ResponseEntity<UserResponse> updatePassword(
			@Valid @RequestBody UpdatePasswordRequest req) {
		User user = userService.updatePassword(req);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);

		return ResponseEntity.ok(userResponse);
	}
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteUser() {
		userService.deleteCurrentUser();
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAuthority('CREATE_USER')")
	@PostMapping
	public ResponseEntity<UserResponse> createUser(
			@Valid @RequestBody CreateUserRequest req) {
		User user = userService.createUser(req);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
	
	@PreAuthorize("hasAuthority('CREATE_ADMINUSER')")
	@PostMapping("/admin")
	public ResponseEntity<UserResponse> createAdminUser(
			@Valid @RequestBody CreateAdminUserRequest req) {
		User user = userService.createAdminUser(req);
		
		UserPrivateResponse userResponse = new UserPrivateResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
	
	@GetMapping("/me/permissions")
	public ResponseEntity<PermissionResponse> getPermissions() {
		Set<String> permissions = userService.getCurrentPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
		PermissionResponse permissionResponse = new PermissionResponse(permissions);
		
		return ResponseEntity.ok(permissionResponse);
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
		@RequestBody PermissionRequest req) {
		
		userService.grantPermissions(publicId, req);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAuthority('REVOKE_PERMISSION')")
	@DeleteMapping("/{publicId}/permissions")
	public ResponseEntity<Void> revokePermissions(
		@PathVariable long publicId,
		@RequestBody PermissionRequest req) {
		userService.revokePermissions(publicId, req);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAuthority('UPDATE_USERSTATE')")
	@PatchMapping("/{publicId}/state")
	public ResponseEntity<UserPublicResponse> updateUserState(
			@PathVariable long publicId,
			@RequestBody UpdateUserStateRequest req
			) {
		User user = userService.updateUserState(publicId, req);
		
		UserPublicResponse userResponse = new UserPublicResponse(user);
		
		return ResponseEntity.ok(userResponse);
	}
	
	@GetMapping("/me/communities")
	public ResponseEntity<CommunitiesResponse> getCurrentCommunities(
			Pageable pageable
			) {
		Page<Community> communityPage = userService.getCurrentCommunities(pageable);
		List<Community> communities = communityPage.getContent();
		CommunitiesResponse communitiesResponse = new CommunitiesResponse(communities);
		
		return ResponseEntity.ok(communitiesResponse);
	}
	
	@GetMapping("/{publicId}/communities")
	public ResponseEntity<CommunitiesResponse> getCommunities(
			@PathVariable long publicId,
			Pageable pageable
			) {
		Page<Community> communityPage = userService.getCommunities(publicId,pageable);
		List<Community> communities = communityPage.getContent();
		CommunitiesResponse communitiesResponse = new CommunitiesResponse(communities);
		
		return ResponseEntity.ok(communitiesResponse);
	}
}
