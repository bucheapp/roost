package io.github.bucheapp.roost.controllers;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
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

import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import io.github.bucheapp.roost.dto.ProfileUpdateRequest;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.services.UserService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/users")
public class UserController {
	@Autowired
	private UserService userService;
	
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
		//TODO Userの取得
	}
	
	@PatchMapping("/me/email")
	public Mono<ResponseEntity<Void>> updateEmail(
			@RequestHeader("Authorization") String authHeader,
			@RequestBody Map<String, String> body) {
		//TODO emaillの更新
	}
	
	@DeleteMapping("/me")
	public Mono<ResponseEntity<Void>> deleteUser(
			@RequestHeader("Authorization") String authHeader) {
		//TODO Userの削除
	}
	
	//UserProfile
	
	@GetMapping("/{id}/profile")
	public Mono<ResponseEntity<Profile>> getProfile(
			@PathVariable String id,
			@RequestHeader("Authorization") String authHeader) {
		//TODO Profileの取得
	}
	
	 @PatchMapping("/me/profile")
	 public Mono<ResponseEntity<Void>> updateProfile(
			 @RequestHeader("Authorization") String authHeader,
			 @RequestBody ProfileUpdateRequest body) {
		 //TODO Profilのe更新
	 }
}
