package io.github.bucheapp.roost.config;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.CommunityProperty;
import io.github.bucheapp.roost.models.CommunityState;
import io.github.bucheapp.roost.models.CommunityType;
import io.github.bucheapp.roost.models.Member;
import io.github.bucheapp.roost.models.MemberState;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.Profile;
import io.github.bucheapp.roost.models.Role;
import io.github.bucheapp.roost.models.Room;
import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.models.UserState;
import io.github.bucheapp.roost.repositories.CommunityRepository;
import io.github.bucheapp.roost.repositories.PermissionRepository;
import io.github.bucheapp.roost.repositories.RoleRepository;
import io.github.bucheapp.roost.repositories.RoomRepository;
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
	
	@Autowired
	private CommunityRepository communityRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Value("${ADMIN_USERNAME}")
	private String adminUsername;
	
	@Value("${ADMIN_EMAIL}")
	private String adminEmail;

	@Value("${ADMIN_PASSWORD}")
	private String adminPassword;
	
	@Autowired
	private WorkerIdProvider workerIdProvider;
	
	@Value("${app.snowflake.datacenter-id}")
	private long datacenterId;
	
	@Override
	public void run(String... args) throws Exception {
		Permission updateUserState = getOrCreatePermission("UPDATE_USERSTATE");
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
		Permission deleteChat = getOrCreatePermission("DELETE_CHAT");
		Permission kickMember = getOrCreatePermission("KICK_MEMBER");
		Permission banMember = getOrCreatePermission("BAN_MEMBER");
		getOrCreatePermission("CREATE_ADMINUSER");
		
		Role superAdmin = getOrCreateRole("SUPER_ADMIN");
		superAdmin.setPermissions(Set.copyOf(permissionRepository.findAll()));
		roleRepository.save(superAdmin);
		
		Role admin = getOrCreateRole("ADMIN");
		admin.setPermissions(Set.of(updateUserState,createUser,getPermission,
				grantPermission,revokePermission,createCommunity,
				updateCommunity,updateCommunityState,updateCommunityProperty,
				createRoom,updateRoom,deleteRoom,createChat,deleteChat,
				kickMember,banMember
				));
		roleRepository.save(admin);
		
		Role user = getOrCreateRole("USER");
		user.setPermissions(Set.of(createCommunity,createChat));
		roleRepository.save(user);
		
		User adminUser = null;
		Snowflake snowflake = new Snowflake(workerIdProvider.getWorkerId(), datacenterId);
		
		Optional<User> userOptional = userRepository.findByName(adminUsername);
		
		if (userOptional.isEmpty()) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			
			Profile profile = new Profile(
					LocalDateTime.now()
					);
			
			adminUser = new User(
					snowflake.nextId(),
					adminUsername,
					adminEmail,
					encoder.encode(adminPassword),
					UserState.ACTIVE,
					profile,
					superAdmin
					);
			
			profile.setUser(adminUser);			
			userRepository.save(adminUser);
		} else {
			adminUser = userOptional.get();
		}
		
		//OpenCommunity作成
		if(communityRepository.findByName("OpenCommunity").isEmpty()) {
			Set<CommunityProperty> properties = new HashSet<>();
			properties.add(CommunityProperty.OPEN);
			properties.add(CommunityProperty.FIXED);
			properties.add(CommunityProperty.PERMANENT);
			
			Community community = new Community(
					snowflake.nextId(),
					"OpenCommunity",
					CommunityType.NONE,
					CommunityState.ACTIVE,
					LocalDateTime.now()
					);
			
			community.setProperties(properties);
			community.addHostHistory(adminUser);
			
			Member member = new Member(
					MemberState.ACTIVE,
					LocalDateTime.now(),
					adminUser,
					community
					);
			
			community.addMember(member);
			
			communityRepository.save(community);
			
			roomRepository.save(
					new Room(
							snowflake.nextId(),
							"ようこそ",
							LocalDateTime.now(),
							adminUser,
							community
							)
					);
			
			roomRepository.save(
					new Room(
							snowflake.nextId(),
							"質問",
							LocalDateTime.now(),
							adminUser,
							community
							)
					);
			
			roomRepository.save(
					new Room(
							snowflake.nextId(),
							"雑談",
							LocalDateTime.now(),
							adminUser,
							community
							)
					);
		}
	}
	
	private Role getOrCreateRole(String name) {
		return roleRepository.findByName(name)
			.orElseGet(() -> {
				Role role = new Role(name);
				return roleRepository.save(role);
			});
	}
	
	private Permission getOrCreatePermission(String name) {
		return permissionRepository.findByName(name)
			.orElseGet(() -> {
				Permission p = new Permission(name);
				return permissionRepository.save(p);
			});
	}
}
