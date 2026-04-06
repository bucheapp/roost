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
import io.github.bucheapp.roost.dto.request.PermissionRequest;
import io.github.bucheapp.roost.dto.request.UpdateUserRequest;
import io.github.bucheapp.roost.dto.request.UpdateUserStateRequest;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.PermissionRepository;
import io.github.bucheapp.roost.repositories.RoleRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.AuthContext;
import io.github.bucheapp.roost.util.MessageUtil;
import io.github.bucheapp.roost.util.Snowflake;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Autowired
	private AuthContext authContext;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public User getCurrentUser() {
		long userId = authContext.getCurrentUserId();
		
		return userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
	}
	
	@Override
	@Transactional
	public User updateCurrentUser(UpdateUserRequest req) {
		long userId = authContext.getCurrentUserId();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		if(req.getEmail() != null) {
			if (userRepository.existsByEmail(req.getEmail())) {
				throw new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.get("email.conflict"));
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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));

		userRepository.delete(user);
	}
	
	@Override
	public User getUser(long publicId) {
		return userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
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
			throw new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.get("email.conflict"));
		}

		if (userRepository.existsByName(name)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.get("name.conflict"));
		}
		
		User currentUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
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
		
		//自分が持ってる権限の範囲内しか付与できない
		if (!creatorPermissions.containsAll(requestedPermissions)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("permissions.cannot.be.granted"));
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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("permission.notfound"))))
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
		
		if (email != null && !email.isBlank() && userRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.get("email.conflict"));
		}

		if (userRepository.existsByName(name)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, messageUtil.get("name.conflict"));
		}
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hashedPassword = encoder.encode(rawPassword);
		
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);

		User user = new User(name, email, hashedPassword);
		user.setRole(roleRepository.findByName("ADMIN")
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("role.notfound"))));
		
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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		Set<String> userPermissions = user.getPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
		Set<String> currentUserPermissions = currentUser.getPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		
		//自分が持ってる権限の範囲内しか付与できない
		if (!currentUserPermissions.containsAll(userPermissions)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("permissions.cannot.be.granted"));
		}
		
		user.setState(req.getState());
		
		return userRepository.save(user);
	}
	
	@Override
	public Set<Permission> getPermissions(long publicId) {
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		return user.getPermissions();
	}
	
	@Override
	@Transactional
	public void grantPermissions(long publicId,PermissionRequest req) {
		long userId = authContext.getCurrentUserId();
		
		User currentUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
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
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("permissions.cannot.be.granted"));
		}
		
		//自分の権限は変更できない
		if(currentUser.getId() == user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("permissions.cannot.be.granted"));
		}
		
		Set<Permission> permissions = requestedPermissions.stream()
				.map(permissionName -> permissionRepository.findByName(permissionName)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"permission.notfound")))
				.collect(Collectors.toSet());
		
		user.getPermissions().addAll(permissions);
		
		userRepository.save(user);
	}
	
	@Override
	@Transactional
	public void revokePermissions(long publicId,PermissionRequest req) {
		long userId = authContext.getCurrentUserId();
		
		User currentUser = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
		User user = userRepository.findByPublicId(publicId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("user.notfound")));
		
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
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, messageUtil.get("permissions.cannot.be.revoked"));
		}
		
		//自分の権限は変更できない
		if (currentUser.getId() == user.getId()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "permissions.cannot.be.revoked");
		}
		
		user.getPermissions().removeIf(p ->
			requestedPermissions.contains(p.getName())
		);

		userRepository.save(user);
	}
}
