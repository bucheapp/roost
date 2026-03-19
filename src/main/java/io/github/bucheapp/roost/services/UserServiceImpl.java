package io.github.bucheapp.roost.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

		User user = new User(name, mail, hashedPassword);
		User saved = userRepository.saveAndFlush(user);

		String accessTokenText = jwtService.generateAccessToken(saved);
		String refreshTokenText = jwtService.generateRefreshToken(saved);
		
		RefreshToken refreshToken = new RefreshToken(refreshTokenText,saved,JwtServiceImpl.REFRESHTOKEN_VALIDITY);
		refreshTokenRepository.saveAndFlush(refreshToken);

		return Mono.just(new SignupResponse(accessTokenText, refreshTokenText));
	}
}
