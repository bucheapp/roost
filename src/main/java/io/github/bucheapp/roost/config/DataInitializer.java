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
	
	@Override
	public void run(String... args) throws Exception {
		Permission freezeUser = getOrCreatePermission("FREEZE_USER");
		Permission createUser = getOrCreatePermission("CREATE_USER");
		Permission getPermission = getOrCreatePermission("GET_PERMISSION");
		Permission grantPermission = getOrCreatePermission("GRANT_PERMISSION");
		Permission revokePermission = getOrCreatePermission("REVOKE_PERMISSION");
		getOrCreatePermission("CREATE_ADMINUSER");
		
		Role superAdmin = getOrCreateRole("SUPER_ADMIN");
		superAdmin.setPermissions(Set.copyOf(permissionRepository.findAll()));
		roleRepository.save(superAdmin);
		
		Role admin = getOrCreateRole("ADMIN");
		admin.setPermissions(Set.of(freezeUser,createUser,getPermission,grantPermission,revokePermission));
		roleRepository.save(admin);
		
		Role user = getOrCreateRole("USER");
		user.setPermissions(Set.of());
		roleRepository.save(user);
		
		if (userRepository.findByName(adminUsername).isEmpty()) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			
			User adminUser = new User(adminUsername,"admin@example.com",encoder.encode(adminPassword));
			adminUser.setRole(superAdmin);
			
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
