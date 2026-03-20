package io.github.bucheapp.roost.controllers;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.ProfileUpdateRequest;
import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.services.JwtService;
import io.github.bucheapp.roost.services.ProfileService;
import io.github.bucheapp.roost.services.UserService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/users")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
	private JwtService jwtService;
	
	@PostMapping("/signup")
	public Mono<ResponseEntity<SignupResponse>> signup(@RequestBody SignupRequest req,ServerHttpResponse response) {
		return userService.register(req)
				.map(res -> {
					ResponseCookie cookie = ResponseCookie.from("refreshToken", res.getRefreshToken())
							.httpOnly(true)
							.path("/")
							.maxAge(Duration.ofDays(7))
							.build();
					response.addCookie(cookie);
					return ResponseEntity.ok(new SignupResponse(res.getAccessToken(), null));
					});
	}
	
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
	
	//UserProfile
	
	@GetMapping("/{id}/profile")
	public Mono<ResponseEntity<Profile>> getProfile(
			@PathVariable long id,
			@RequestHeader("Authorization") String authHeader) {
		return profileService.getProfileById(id)
				.map(ResponseEntity::ok)
				.defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}
	
	 @PatchMapping("/me/profile")
	 public Mono<ResponseEntity<Void>> updateProfile(
			 @RequestHeader("Authorization") String authHeader,
			 @RequestBody ProfileUpdateRequest body) {
		
		String token = authHeader.substring(7);
		long id = jwtService.extractUserId(token);
		
		return profileService.updateProfileById(id,body)
				.thenReturn(ResponseEntity.ok().<Void>build());
	 }
}
