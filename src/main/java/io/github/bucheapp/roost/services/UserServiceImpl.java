package io.github.bucheapp.roost.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.dto.LoginRequest;
import io.github.bucheapp.roost.dto.LoginResponse;
import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import io.github.bucheapp.roost.models.RefreshToken;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.RefreshTokenRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Override
	@Transactional
	public Mono<SignupResponse> register(SignupRequest req) {
		return Mono.fromCallable(() -> {
			String name = req.name;
			String email = req.email;
			String rawPassword = req.password;

			if (userRepository.existsByEmail(email)) {
				throw new RuntimeException("既に登録されています");
			}

			if (userRepository.existsByName(name)) {
				throw new RuntimeException("既にその名前は存在します");
			}
			
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			String hashedPassword = encoder.encode(rawPassword);
			User user = new User(name, email, hashedPassword);
			User saved = userRepository.saveAndFlush(user);

			String accessTokenText = jwtService.generateAccessToken(saved);
			String refreshTokenText = jwtService.generateRefreshToken(saved);

			RefreshToken refreshToken = new RefreshToken(refreshTokenText, saved, JwtServiceImpl.REFRESHTOKEN_VALIDITY);
			refreshTokenRepository.save(refreshToken);

			return new SignupResponse(accessTokenText, refreshTokenText);
		});
	}
	
	@Override
	public Mono<User> getUserById(long id) {
		return Mono.fromCallable(() -> userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません")));
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
			user.setPassword(hashedPassword);
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
  
	@Override
	public Mono<LoginResponse> login(LoginRequest req) {
		return Mono.fromCallable(() -> {
			User user = userRepository.findByName(req.name)
					.orElseThrow(() -> new RuntimeException("ユーザが存在しません"));
			
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			

			if (!encoder.matches(req.password, user.getPassword())) {
				throw new RuntimeException("パスワードが違います");
			}

			String accessTokenText = jwtService.generateAccessToken(user);
			String refreshTokenText = jwtService.generateRefreshToken(user);

			RefreshToken refreshToken = new RefreshToken(refreshTokenText, user, JwtServiceImpl.REFRESHTOKEN_VALIDITY);
			refreshTokenRepository.save(refreshToken);

			return new LoginResponse(accessTokenText, refreshTokenText);
		});
	}
  
	@Override
	public Mono<Void> logout(String refreshTokenText) {
		return Mono.fromRunnable(() -> refreshTokenRepository.deleteByToken(refreshTokenText));
	}
	
	@Override
	public Mono<String> refresh(String refreshTokenText) {
		return Mono.fromCallable(() -> {
			RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenText)
					.orElseThrow(() -> new RuntimeException("トークンが存在しない"));
			return jwtService.generateAccessToken(refreshToken.getUser());
		});
	}
}
