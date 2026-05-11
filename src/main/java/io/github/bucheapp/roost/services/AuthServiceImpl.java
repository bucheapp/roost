package io.github.bucheapp.roost.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.LoginRequest;
import io.github.bucheapp.roost.dto.request.SignupRequest;
import io.github.bucheapp.roost.dto.response.LoginResponse;
import io.github.bucheapp.roost.dto.response.SignupResponse;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.models.RefreshToken;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.models.UserState;
import io.github.bucheapp.roost.repositories.RefreshTokenRepository;
import io.github.bucheapp.roost.repositories.RoleRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.util.MessageUtil;
import io.github.bucheapp.roost.util.Snowflake;

@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	@Transactional
	public SignupResponse register(SignupRequest req) {
		String name = req.getName();
		String email = req.getEmail();
		String rawPassword = req.getPassword();

		if (email != null && !email.isBlank() && userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.get("email.conflict"));
		}

		if (userRepository.existsByName(name)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.get("name.conflict"));
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		
		Profile profile = new Profile(
				LocalDateTime.now()
				);

		User user = new User(
				snowflake.nextId(),
				name,
				email,
				hashedPassword,
				UserState.ACTIVE,
				profile,
				roleRepository.findByName("USER")
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("role.notfound")))
				);
		
		profile.setUser(user);

		User savedUser = userRepository.saveAndFlush(user);

		String accessTokenText = jwtService.generateAccessToken(savedUser);
		String refreshTokenText = jwtService.generateRefreshToken(savedUser);

		RefreshToken refreshToken =
				new RefreshToken(
						refreshTokenText,
						JwtServiceImpl.REFRESHTOKEN_VALIDITY,
						savedUser
						);

		refreshTokenRepository.save(refreshToken);

		return new SignupResponse(accessTokenText, refreshTokenText);
	}
  
	@Override
	public LoginResponse login(LoginRequest req) {
		User user = userRepository.findByName(req.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (!encoder.matches(req.getPassword(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, messageUtil.get("incorrect.password"));
		}
		
		if(user.getState() == UserState.FROZEN) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("user.forbidden"));
		}

		String accessTokenText = jwtService.generateAccessToken(user);
		String refreshTokenText = jwtService.generateRefreshToken(user);

		RefreshToken refreshToken =
				new RefreshToken(refreshTokenText,
						JwtServiceImpl.REFRESHTOKEN_VALIDITY,
						user
						);

		refreshTokenRepository.save(refreshToken);

		return new LoginResponse(accessTokenText, refreshTokenText);
	}
  
	@Override
	@Transactional
	public void logout(String refreshTokenText) {
		refreshTokenRepository.deleteByToken(refreshTokenText);
	}
	
	@Override
	public String refresh(String refreshTokenText) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenText)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("token.notfound")));
		
		User user = refreshToken.getUser();
		
		if(user.getState() == UserState.FROZEN) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("user.forbidden"));
		}
		
		if(!checkRefreshToken(refreshTokenText)) {
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					messageUtil.get("token.invalid")
				);
		}

		return jwtService.generateAccessToken(refreshToken.getUser());
	}
	
	@Override
	public boolean checkRefreshToken(String refreshTokenText) {
		if (refreshTokenText == null || refreshTokenText.isBlank()) {
			return false;
		}

		Optional<RefreshToken> tokenOpt = refreshTokenRepository
				.findByToken(refreshTokenText);

		if (tokenOpt.isEmpty()) {
			return false;
		}

		RefreshToken token = tokenOpt.get();

		if (token.getExpiryDate().isBefore(Instant.now())) {
			return false;
		}

		if (token.isRevoked()) {
			return false;
		}

		return true;
	}
}
