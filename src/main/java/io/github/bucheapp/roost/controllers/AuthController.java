package io.github.bucheapp.roost.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.LoginRequest;
import io.github.bucheapp.roost.dto.LoginResponse;
import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import io.github.bucheapp.roost.services.UserService;

@RestController
@RequestMapping("api/auth")
public class AuthController {
	@Autowired
	private UserService userService;
	
	@PostMapping("/signup")
	public ResponseEntity<SignupResponse> signup(
			@RequestBody SignupRequest req,
			HttpServletResponse response) {

		SignupResponse res = userService.register(req);

		Cookie cookie = new Cookie("refreshToken", res.getRefreshToken());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24 * 7);

		response.addCookie(cookie);

		return ResponseEntity.ok(new SignupResponse(res.getAccessToken(), null));
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
			@RequestBody LoginRequest req,
			HttpServletResponse response) {

		LoginResponse res = userService.login(req);

		Cookie cookie = new Cookie("refreshToken", res.getRefreshToken());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24 * 7);

		response.addCookie(cookie);

		return ResponseEntity.ok(new LoginResponse(res.getAccessToken(), null));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@CookieValue String refreshToken) {
		userService.logout(refreshToken);

		return ResponseEntity.ok().build();
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<String> refresh(@CookieValue String refreshToken) {
		String accessToken = userService.refresh(refreshToken);

		return ResponseEntity.ok(accessToken);
	}
}
