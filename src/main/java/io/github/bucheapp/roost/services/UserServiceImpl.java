package io.github.bucheapp.roost.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.CreateAdminUserRequest;
import io.github.bucheapp.roost.dto.request.CreateUserRequest;
import io.github.bucheapp.roost.dto.request.LoginRequest;
import io.github.bucheapp.roost.dto.request.PermissionRequest;
import io.github.bucheapp.roost.dto.request.SignupRequest;
import io.github.bucheapp.roost.dto.request.UpdateUserRequest;
import io.github.bucheapp.roost.dto.request.UpdateUserStateRequest;
import io.github.bucheapp.roost.dto.response.LoginResponse;
import io.github.bucheapp.roost.dto.response.SignupResponse;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.models.RefreshToken;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.PermissionRepository;
import io.github.bucheapp.roost.repositories.RefreshTokenRepository;
import io.github.bucheapp.roost.repositories.RoleRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
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
	
	@Autowired
	private AuthContext authContext;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public User getCurrentUser() {
		long userId = authContext.getCurrentUserId();
		
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}
	
	@Override
	@Transactional
	public User updateCurrentUser(UpdateUserRequest req) {
		long userId = authContext.getCurrentUserId();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		if(req.getEmail() != null) {
			if (userRepository.existsByEmail(req.getEmail())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "This email address is already in use");
			}
			
			user.setEmail(req.getEmail());
		}
		
		if(req.getPassword() != null) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String hashedPassword = encoder.encode(req.getPassword());
			user.setPassword(hashedPassword);
		}
		
		return userRepository.save(user);
	}
	
	@Override
	@Transactional
	public void deleteCurrentUser() {
		long userId = authContext.getCurrentUserId();
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		userRepository.delete(user);
	}
	
	@Override
	public User getUser(long publicId) {
		return userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}
	
	@Override
	@Transactional
	public void createUser(CreateUserRequest req) {
		long userId = authContext.getCurrentUserId();
		
		String name = req.getName();
		String email = req.getEmail();
		String rawPassword = req.getPassword();
		Set<String> requestedPermissions = req.getPermissions();
		
		if (userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This email address is already in use");
		}

		if (userRepository.existsByName(name)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This name is already in use");
		}
		
		User currentUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
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
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissions cannot be granted");
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);

		User user = new User(name, email, hashedPassword);
		Profile profile = new Profile();
		profile.setCreatedAt(LocalDateTime.now());
		
		user.setPublicId(snowflake.nextId());
		
		Set<Permission> permissions = requestedPermissions.stream()
				.map(permissionName -> permissionRepository.findByName(permissionName)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found:" + permissionName)))
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
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This email address is already in use");
		}

		if (userRepository.existsByName(name)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This name is already in use");
		}
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);

		User user = new User(name, email, hashedPassword);
		user.setRole(roleRepository.findByName("ADMIN")
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")));
		
		Profile profile = new Profile();
		profile.setCreatedAt(LocalDateTime.now());
		
		user.setPublicId(snowflake.nextId());
		userRepository.save(user);
	}
	
	@Override
	@Transactional
	public User updateUserState(long publicId,UpdateUserStateRequest req) {
		long userId = authContext.getCurrentUserId();
		
		User currentUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		Set<String> userPermissions = user.getPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
		Set<String> currentUserPermissions = currentUser.getPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
		if (!currentUserPermissions.containsAll(userPermissions)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissions cannot be granted");
		}
		
		user.setState(req.getState());
		
		return userRepository.save(user);
	}
	
	@Override
	@Transactional
	public SignupResponse register(SignupRequest req) {
		String name = req.getName();
		String email = req.getEmail();
		String rawPassword = req.getPassword();

		if (userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This email address is already in use");
		}

		if (userRepository.existsByName(name)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "This name is already in use");
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);

		User user = new User(name, email, hashedPassword);
		Profile profile = new Profile();
		profile.setCreatedAt(LocalDateTime.now());
		
		profile.setUser(user);
		user.setProfile(profile);

		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		
		user.setPublicId(snowflake.nextId());
		user.setRole(roleRepository.findByName("USER")
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found")));

		User saved = userRepository.saveAndFlush(user);

		String accessTokenText = jwtService.generateAccessToken(saved);
		String refreshTokenText = jwtService.generateRefreshToken(saved);

		RefreshToken refreshToken =
				new RefreshToken(refreshTokenText, saved, JwtServiceImpl.REFRESHTOKEN_VALIDITY);

		refreshTokenRepository.save(refreshToken);

		return new SignupResponse(accessTokenText, refreshTokenText);
	}
  
	@Override
	public LoginResponse login(LoginRequest req) {
		User user = userRepository.findByName(req.getName())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		if (!encoder.matches(req.getPassword(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not found"));

		return jwtService.generateAccessToken(refreshToken.getUser());
	}
	
	@Override
	public Set<Permission> getPermissions(long publicId) {
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		return user.getPermissions();
	}
	
	@Override
	@Transactional
	public void grantPermissions(long publicId,PermissionRequest req) {
		long userId = authContext.getCurrentUserId();
		
		User currentUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		Set<String> requestedPermissions = req.getPermissions();
		
		Set<String> userPermissions = user.getPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
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
		
		if (!currentUserPermissions.containsAll(requestedPermissions) ||
				!currentUserPermissions.containsAll(userPermissions)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissions cannot be granted");
		}
		
		if(currentUser.getId() == user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot grant permissions to yourself");
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
	public void revokePermissions(long publicId,PermissionRequest req) {
		long userId = authContext.getCurrentUserId();
		
		User currentUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		
		Set<String> requestedPermissions = req.getPermissions();
		
		Set<String> userPermissions = user.getPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
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
		
		if (!currentUserPermissions.containsAll(requestedPermissions) ||
				!currentUserPermissions.containsAll(userPermissions)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permissions could not be revoked");
		}
		
		if (currentUser.getId() == user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot revoke permissions from yourself");
		}
		
		user.getPermissions().removeIf(p ->
			requestedPermissions.contains(p.getName())
		);

		userRepository.save(user);
	}
}
