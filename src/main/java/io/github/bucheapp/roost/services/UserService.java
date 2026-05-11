package io.github.bucheapp.roost.services;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.github.bucheapp.roost.dto.request.CreateAdminUserRequest;
import io.github.bucheapp.roost.dto.request.CreateUserRequest;
import io.github.bucheapp.roost.dto.request.PermissionRequest;
import io.github.bucheapp.roost.dto.request.UpdatePasswordRequest;
import io.github.bucheapp.roost.dto.request.UpdateUserRequest;
import io.github.bucheapp.roost.dto.request.UpdateUserStateRequest;
import io.github.bucheapp.roost.models.Community;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.User;

public interface UserService {
	User getCurrentUser();
	User updateCurrentUser(UpdateUserRequest req);
	User updatePassword(UpdatePasswordRequest req);
	void deleteCurrentUser();
	User getUser(long targetPublicId);
	User createUser(CreateUserRequest req);
	User createAdminUser(CreateAdminUserRequest req);
	User updateUserState(long targetPublicId,UpdateUserStateRequest req);
	Page<Community> getCurrentCommunities(Pageable pageable);
	Page<Community> getCommunities(long publicId);
	
	String hideEmail(String email);

	Set<Permission> getPermissions(long targetPublicId);
	Set<Permission> getCurrentPermissions();
	void grantPermissions(long targetPublicId,PermissionRequest req);
	void revokePermissions(long targetPublicId,PermissionRequest req);
}
