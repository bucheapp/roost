package io.github.bucheapp.roost.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public Mono<ResponseEntity<SignupResponse>> signup(@RequestBody SignupRequest req) {
		return userService.register(req)
			.map(ResponseEntity::ok);
	}
}
