package io.github.bucheapp.roost.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.LoginRequest;
import io.github.bucheapp.roost.dto.request.SignupRequest;
import io.github.bucheapp.roost.dto.response.LoginResponse;
import io.github.bucheapp.roost.dto.response.RefreshResponse;
import io.github.bucheapp.roost.dto.response.SignupResponse;
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
		
		ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
				.httpOnly(true)
				.secure(true)
				.path("/")
				.maxAge(0)
				.build();

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.build();
	}
	
	@GetMapping("/refresh")
	public ResponseEntity<RefreshResponse> refresh(@CookieValue String refreshToken) {
		String accessToken = userService.refresh(refreshToken);
		
		RefreshResponse refreshResponse = new RefreshResponse(accessToken);

		return ResponseEntity.ok(refreshResponse);
	}
}
