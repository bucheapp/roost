package io.github.bucheapp.roost.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.Role;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.PermissionRepository;
import io.github.bucheapp.roost.repositories.RoleRepository;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.services.WorkerIdProvider;
import io.github.bucheapp.roost.util.Snowflake;

@Component
public class DataInitializer implements CommandLineRunner {
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Value("${ADMIN_USERNAME}")
	private String adminUsername;

	@Value("${ADMIN_PASSWORD}")
	private String adminPassword;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public void run(String... args) throws Exception {
		Permission updateUserState = getOrCreatePermission("UUPDATE_USERSTATE");
		Permission createUser = getOrCreatePermission("CREATE_USER");
		Permission getPermission = getOrCreatePermission("GET_PERMISSION");
		Permission grantPermission = getOrCreatePermission("GRANT_PERMISSION");
		Permission revokePermission = getOrCreatePermission("REVOKE_PERMISSION");
		Permission createCommunity = getOrCreatePermission("CREATE_COMMUNITY");
		Permission updateCommunity = getOrCreatePermission("UPDATE_COMMUNITY");
		Permission updateCommunityState = getOrCreatePermission("UPDATE_COMMUNITYSTATE");
		Permission updateCommunityProperty = getOrCreatePermission("UPDATE_COMMUNITYPROPERTY");
		Permission createRoom = getOrCreatePermission("CREATE_ROOM");
		Permission updateRoom = getOrCreatePermission("UPDATE_ROOM");
		Permission deleteRoom = getOrCreatePermission("DELETE_ROOM");
		Permission createChat = getOrCreatePermission("CREATE_CHAT");
		Permission updateChat = getOrCreatePermission("UPDATE_CHAT");
		Permission deleteChat = getOrCreatePermission("DELETE_CHAT");
		getOrCreatePermission("CREATE_ADMINUSER");
		
		Role superAdmin = getOrCreateRole("SUPER_ADMIN");
		superAdmin.setPermissions(Set.copyOf(permissionRepository.findAll()));
		roleRepository.save(superAdmin);
		
		Role admin = getOrCreateRole("ADMIN");
		admin.setPermissions(Set.of(updateUserState,createUser,getPermission,
				grantPermission,revokePermission,createCommunity,
				updateCommunity,updateCommunityState,updateCommunityProperty,
				createRoom,updateRoom,deleteRoom,createChat,updateChat,deleteChat));
		roleRepository.save(admin);
		
		Role user = getOrCreateRole("USER");
		user.setPermissions(Set.of(createCommunity));
		roleRepository.save(user);
		
		if (userRepository.findByName(adminUsername).isEmpty()) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			
			Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
			
			User adminUser = new User(adminUsername,"admin@example.com",encoder.encode(adminPassword));
			adminUser.setRole(superAdmin);
			adminUser.setPublicId(snowflake.nextId());
			
			userRepository.save(adminUser);
		}
	}
	
	private Role getOrCreateRole(String name) {
		return roleRepository.findByName(name)
			.orElseGet(() -> {
				Role role = new Role();
				role.setName(name);
				return roleRepository.save(role);
			});
	}
	
	private Permission getOrCreatePermission(String name) {
		return permissionRepository.findByName(name)
			.orElseGet(() -> {
				Permission p = new Permission();
				p.setName(name);
				return permissionRepository.save(p);
			});
	}
}
