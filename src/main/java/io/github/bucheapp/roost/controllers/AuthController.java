package io.github.bucheapp.roost.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.LoginRequest;
import io.github.bucheapp.roost.dto.request.SignupRequest;
import io.github.bucheapp.roost.dto.response.AccessTokenResponse;
import io.github.bucheapp.roost.dto.response.LoginResponse;
import io.github.bucheapp.roost.dto.response.RefreshTokenResponse;
import io.github.bucheapp.roost.dto.response.SignupResponse;
import io.github.bucheapp.roost.services.AuthService;
import io.github.bucheapp.roost.util.MessageUtil;

@RestController
@RequestMapping("api/auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@PostMapping("/signup")
	public ResponseEntity<AccessTokenResponse> signup(
			@Valid @RequestBody SignupRequest req,
			HttpServletRequest servletReq,
			HttpServletResponse servletRes) {
		
		Cookie[] cookies = servletReq.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					 new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already logged in");
				}
			}
		}

		SignupResponse res = authService.register(req);

		Cookie cookie = new Cookie("refreshToken", res.getRefreshToken());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24 * 7);

		servletRes.addCookie(cookie);
		
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse(res.getAccessToken());

		return ResponseEntity.ok(accessTokenResponse);
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(
			@Valid @RequestBody LoginRequest req,
			HttpServletRequest servletReq,
			HttpServletResponse servletRes) {

		Cookie[] cookies = servletReq.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					 new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already logged in");
				}
			}
		}

		LoginResponse res = authService.login(req);

		Cookie cookie = new Cookie("refreshToken", res.getRefreshToken());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60 * 24 * 7);

		servletRes.addCookie(cookie);

		return ResponseEntity.ok(new LoginResponse(res.getAccessToken(), null));
	}
	
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@CookieValue String refreshToken) {
		authService.logout(refreshToken);
		
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

	@GetMapping("/me")
	public ResponseEntity<RefreshTokenResponse> me(
			HttpServletRequest servletReq
			) {
		Cookie[] cookies = servletReq.getCookies();
		RefreshTokenResponse refreshTokenResponse = null;
		
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refreshToken".equals(cookie.getName())) {
					String refreshToken = cookie.getValue();
					if(!authService.checkRefreshToken(refreshToken)) {
						throw new ResponseStatusException(
								HttpStatus.UNAUTHORIZED,
								messageUtil.get("token.invalid")
							);
					}
					refreshTokenResponse = new RefreshTokenResponse(refreshToken);
				}
			}
		}
		
		return ResponseEntity.ok(refreshTokenResponse);
	}
	
	@GetMapping("/refresh")
	public ResponseEntity<AccessTokenResponse> refresh(@CookieValue String refreshToken) {
		String accessToken = authService.refresh(refreshToken);
		
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse(accessToken);

		return ResponseEntity.ok(accessTokenResponse);
	}
}
