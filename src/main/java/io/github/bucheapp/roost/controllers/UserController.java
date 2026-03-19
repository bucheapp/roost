package io.github.bucheapp.roost.controllers;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.LoginRequest;
import io.github.bucheapp.roost.dto.LoginResponse;
import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
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
	
	@PostMapping("/login")
	public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest req,ServerHttpResponse response) {
		return userService.login(req)
				.map(res -> {
					ResponseCookie cookie = ResponseCookie.from("refreshToken", res.getRefreshToken())
							.httpOnly(true)
							.path("/")
							.maxAge(Duration.ofDays(7))
							.build();
					response.addCookie(cookie);
					return ResponseEntity.ok(new LoginResponse(res.getAccessToken(), null));
					});
	}
}
