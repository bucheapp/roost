package io.github.bucheapp.roost.services;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.dto.request.LoginRequest;
import io.github.bucheapp.roost.dto.request.SignupRequest;
import io.github.bucheapp.roost.dto.response.LoginResponse;
import io.github.bucheapp.roost.dto.response.SignupResponse;
import io.github.bucheapp.roost.models.RefreshToken;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.RefreshTokenRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.util.Snowflake;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Override
	@Transactional
	public SignupResponse register(SignupRequest req) {

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

		long datacenterId = Long.parseLong(System.getenv("DATACENTER_ID"));
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		user.setPublicId(snowflake.nextId());

		User saved = userRepository.saveAndFlush(user);

		String accessTokenText = jwtService.generateAccessToken(saved);
		String refreshTokenText = jwtService.generateRefreshToken(saved);

		RefreshToken refreshToken =
				new RefreshToken(refreshTokenText, saved, JwtServiceImpl.REFRESHTOKEN_VALIDITY);

		refreshTokenRepository.save(refreshToken);

		return new SignupResponse(accessTokenText, refreshTokenText);
	}
	
	public User getUserById(long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
	}
	
	public User getUserByPublicId(long publicId) {
		return userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
	}
	
	@Override
	@Transactional
	public User updateEmailById(long id, String newEmail) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));

		user.setEmail(newEmail);
		return userRepository.save(user);
	}
	
	@Override
	@Transactional
	public User updatePasswordById(long id, String newPassword) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(newPassword);

		user.setPassword(hashedPassword);
		return userRepository.save(user);
	}
	
	@Override
	@Transactional
	public void deleteUserById(long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));

		userRepository.delete(user);
	}
	
	@Override
	@Transactional
	public void setFrozen(long publicId,boolean frozen) {
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		
		user.setFrozen(frozen);

		userRepository.save(user);
	}
  
	@Override
	public LoginResponse login(LoginRequest req) {
		User user = userRepository.findByName(req.name)
				.orElseThrow(() -> new RuntimeException("ユーザが存在しません"));

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (!encoder.matches(req.password, user.getPassword())) {
			throw new RuntimeException("パスワードが違います");
		}

		String accessTokenText = jwtService.generateAccessToken(user);
		String refreshTokenText = jwtService.generateRefreshToken(user);

		RefreshToken refreshToken =
				new RefreshToken(refreshTokenText, user, JwtServiceImpl.REFRESHTOKEN_VALIDITY);

		refreshTokenRepository.save(refreshToken);

		return new LoginResponse(accessTokenText, refreshTokenText);
	}
  
	@Override
	public void logout(String refreshTokenText) {
		refreshTokenRepository.deleteByToken(refreshTokenText);
	}
	
	@Override
	public String refresh(String refreshTokenText) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenText)
				.orElseThrow(() -> new RuntimeException("トークンが存在しない"));

		return jwtService.generateAccessToken(refreshToken.getUser());
	}
}
