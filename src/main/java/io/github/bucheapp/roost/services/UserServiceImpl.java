package io.github.bucheapp.roost.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.UserRepository;
import reactor.core.publisher.Mono;

public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Override
	@Transactional
	public Mono<SignupResponse> register(SignupRequest req) {
		String name = req.name;
		String email = req.email;
		String rawPassword = req.password;

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);
		
		if (userRepository.existsByEmail(email)) {
			return Mono.error(new RuntimeException("既に登録されています"));
		}
		
		User user = new User(name, email, hashedPassword);
		User saved = userRepository.saveAndFlush(user);
		
		String accessToken = jwtService.generateAccessToken(saved);
		String refreshToken = jwtService.generateRefreshToken(saved);
		
		return Mono.just(new SignupResponse(accessToken, refreshToken));
	}
	
	@Override
	public Mono<User> getUserById(long id) {
		return Mono.justOrEmpty(userRepository.findById(id))
				.switchIfEmpty(Mono.error(new RuntimeException("ユーザが見つかりません")));
	}
	
	@Override
	@Transactional
	public Mono<Void> updateEmailById(long id,String newEmail) {
		return Mono.fromRunnable(() -> {
			User user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
			user.setEmail(newEmail);
			userRepository.save(user);
		});
	}
	
	@Override
	@Transactional
	public Mono<Void> updatePasswordById(long id,String newPassword) {
		return Mono.fromRunnable(() -> {
			User user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String hashedPassword = encoder.encode(newPassword);
			user.setEmail(hashedPassword);
			userRepository.save(user);
		});
	}
	
	@Override
	@Transactional
	public Mono<Void> deleteUserById(long id) {
		return Mono.fromRunnable(() -> {
			User user = userRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
			userRepository.delete(user);
		});
	}
}
