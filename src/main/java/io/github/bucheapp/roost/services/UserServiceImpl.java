package io.github.bucheapp.roost.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.github.bucheapp.roost.dto.LoginRequest;
import io.github.bucheapp.roost.dto.LoginResponse;
import io.github.bucheapp.roost.dto.SignupRequest;
import io.github.bucheapp.roost.dto.SignupResponse;
import io.github.bucheapp.roost.models.RefreshToken;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.RefreshTokenRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import reactor.core.publisher.Mono;

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
		String name = req.name;
		String mail = req.mail;
		String rawPassword = req.password;

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);

		if (userRepository.existsByMail(mail)) {
			return Mono.error(new RuntimeException("既に登録されています"));
		}
		
		if (userRepository.existsByName(name)) {
			return Mono.error(new RuntimeException("既にその名前は存在します"));
		}

		User user = new User(name, mail, hashedPassword);
		User saved = userRepository.saveAndFlush(user);

		String accessTokenText = jwtService.generateAccessToken(saved);
		String refreshTokenText = jwtService.generateRefreshToken(saved);
		
		RefreshToken refreshToken = new RefreshToken(refreshTokenText,saved,JwtServiceImpl.REFRESHTOKEN_VALIDITY);
		refreshTokenRepository.saveAndFlush(refreshToken);

		return Mono.just(new SignupResponse(accessTokenText, refreshTokenText));
	}
	
	@Override
	@Transactional
	public Mono<LoginResponse> login(LoginRequest req) {
		String name = req.name;
		String rawPassword = req.password;
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return userRepository.findByName(name)
				.switchIfEmpty(Mono.error(new RuntimeException("ユーザが存在しません")))
				.flatMap(user -> {
					if (!encoder.matches(rawPassword, user.getPassword())) {
						return Mono.error(new RuntimeException("パスワードが違います"));
					}
					
					String accessTokenText = jwtService.generateAccessToken(user);
					String refreshTokenText = jwtService.generateRefreshToken(user);
					
					RefreshToken refreshToken = new RefreshToken(refreshTokenText,user,JwtServiceImpl.REFRESHTOKEN_VALIDITY);
					refreshTokenRepository.saveAndFlush(refreshToken);
					
					return Mono.just(new LoginResponse(accessTokenText, refreshTokenText));
				});
	}
	
	@Override
	@Transactional
	public Mono<Void> logout(String refreshTokenText) {
		return refreshTokenRepository.deleteByToken(refreshTokenText);
	}
	
	@Override
	public Mono<String> refresh(String refreshTokenText) {
		return refreshTokenRepository.findByToken(refreshTokenText)
		.switchIfEmpty(Mono.error(new RuntimeException("トークンが存在しない")))
		.flatMap(refreshToken -> {
			User user =  refreshToken.getUser();
			return Mono.just(jwtService.generateAccessToken(user));
		});
	}
}
