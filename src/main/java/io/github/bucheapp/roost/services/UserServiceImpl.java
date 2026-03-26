package io.github.bucheapp.roost.services;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.dto.request.CreateAdminUserRequest;
import io.github.bucheapp.roost.dto.request.CreateUserRequest;
import io.github.bucheapp.roost.dto.request.LoginRequest;
import io.github.bucheapp.roost.dto.request.PermissionRequest;
import io.github.bucheapp.roost.dto.request.SignupRequest;
import io.github.bucheapp.roost.dto.request.UserStateUpdateRequest;
import io.github.bucheapp.roost.dto.response.LoginResponse;
import io.github.bucheapp.roost.dto.response.SignupResponse;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.RefreshToken;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.PermissionRepository;
import io.github.bucheapp.roost.repositories.RefreshTokenRepository;
import io.github.bucheapp.roost.repositories.RoleRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.util.Snowflake;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	@Transactional
	public SignupResponse register(SignupRequest req) {
		String name = req.getName();
		String email = req.getEmail();
		String rawPassword = req.getPassword();

		if (userRepository.existsByEmail(email)) {
			throw new RuntimeException("既に登録されています");
		}

		if (userRepository.existsByName(name)) {
			throw new RuntimeException("既にその名前は存在します");
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);

		User user = new User(name, email, hashedPassword);

		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		
		user.setPublicId(snowflake.nextId());
		user.setRole(roleRepository.findByName("USER")
				.orElseThrow(() -> new RuntimeException("ロールが存在しません")));

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
	public User updateUserState(long publicId,UserStateUpdateRequest req) {
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		
		user.setState(req.getState());
		
		return userRepository.save(user);
	}
  
	@Override
	public LoginResponse login(LoginRequest req) {
		User user = userRepository.findByName(req.getName())
				.orElseThrow(() -> new RuntimeException("ユーザが存在しません"));

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (!encoder.matches(req.getPassword(), user.getPassword())) {
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
	@Transactional
	public void logout(String refreshTokenText) {
		refreshTokenRepository.deleteByToken(refreshTokenText);
	}
	
	@Override
	public String refresh(String refreshTokenText) {
		RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenText)
				.orElseThrow(() -> new RuntimeException("トークンが存在しない"));

		return jwtService.generateAccessToken(refreshToken.getUser());
	}
	
	@Override
	@Transactional
	public void createUser(long id,CreateUserRequest req) {
		String name = req.getName();
		String email = req.getEmail();
		String rawPassword = req.getPassword();
		Set<String> requestedPermissions = req.getPermissions();
		
		if (userRepository.existsByEmail(email)) {
			throw new RuntimeException("既に登録されています");
		}

		if (userRepository.existsByName(name)) {
			throw new RuntimeException("既にその名前は存在します");
		}
		
		User currentUser = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザが存在しません"));
		
		Set<String> creatorPermissions = currentUser.getPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
		creatorPermissions.addAll(
				Optional.ofNullable(currentUser.getRole())
					.map(role -> role.getPermissions())
					.orElse(Collections.emptySet())
					.stream()
					.map(Permission::getName)
					.toList()
			);
		
		if (!creatorPermissions.containsAll(requestedPermissions)) {
			throw new RuntimeException("権限を付与できません");
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);

		User user = new User(name, email, hashedPassword);
		user.setPublicId(snowflake.nextId());
		
		Set<Permission> permissions = requestedPermissions.stream()
				.map(permissionName -> permissionRepository.findByName(permissionName)
				.orElseThrow(() -> new RuntimeException("権限がありません: " + permissionName)))
				.collect(Collectors.toSet());
		
		user.setPermissions(permissions);
		userRepository.save(user);
	}
	
	@Override
	@Transactional
	public void createAdminUser(CreateAdminUserRequest req) {
		String name = req.getName();
		String email = req.getEmail();
		String rawPassword = req.getPassword();
		
		if (userRepository.existsByEmail(email)) {
			throw new RuntimeException("既に登録されています");
		}

		if (userRepository.existsByName(name)) {
			throw new RuntimeException("既にその名前は存在します");
		}
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);

		User user = new User(name, email, hashedPassword);
		user.setRole(roleRepository.findByName("ADMIN")
				.orElseThrow(() -> new RuntimeException("ロールが存在しません")));
		user.setPublicId(snowflake.nextId());
		userRepository.save(user);
	}
	
	@Override
	public Set<Permission> getPermissions(long publicId) {
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		return user.getPermissions();
	}
	
	@Override
	@Transactional
	public void grantPermissions(long id,long publicId,PermissionRequest req) {
		User currentUser = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		
		Set<String> requestedPermissions = req.getPermissions();
		
		Set<String> currentUserPermissions = currentUser.getPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
		currentUserPermissions.addAll(
				Optional.ofNullable(currentUser.getRole())
					.map(role -> role.getPermissions())
					.orElse(Collections.emptySet())
					.stream()
					.map(Permission::getName)
					.toList()
			);
		
		if (!currentUserPermissions.containsAll(requestedPermissions)) {
			throw new RuntimeException("権限を付与できません");
		}
		
		if(currentUser.getId() == user.getId()) {
			throw new RuntimeException("自分には権限を付与できません");
		}
		
		Set<Permission> permissions = requestedPermissions.stream()
				.map(permissionName -> permissionRepository.findByName(permissionName)
				.orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName)))
				.collect(Collectors.toSet());
		
		user.getPermissions().addAll(permissions);
		
		userRepository.save(user);
	}
	
	@Override
	@Transactional
	public void revokePermissions(long id,long publicId,PermissionRequest req) {
		User currentUser = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new RuntimeException("ユーザが見つかりません"));
		
		Set<String> requestedPermissions = req.getPermissions();
		
		Set<String> currentUserPermissions = currentUser.getPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
		currentUserPermissions.addAll(
				Optional.ofNullable(currentUser.getRole())
					.map(role -> role.getPermissions())
					.orElse(Collections.emptySet())
					.stream()
					.map(Permission::getName)
					.toList()
			);
		
		if (!currentUserPermissions.containsAll(requestedPermissions)) {
			throw new RuntimeException("権限を剥奪できません");
		}
		
		if (currentUser.getId() == user.getId()) {
			throw new RuntimeException("自分の権限は変更できません");
		}
		
		user.getPermissions().removeIf(p ->
			requestedPermissions.contains(p.getName())
		);

		userRepository.save(user);
	}
}
