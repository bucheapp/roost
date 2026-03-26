package io.github.bucheapp.roost.services;

import java.util.Set;

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
import io.github.bucheapp.roost.models.User;

public interface UserService {
	User getCurrentUser();
	User updateCurrentUser(UpdateUserRequest req);
	void deleteCurrentUser();
	User getUser(long targetPublicId);
	void createUser(CreateUserRequest req);
	void createAdminUser(CreateAdminUserRequest req);
	User updateUserState(long targetPublicId,UpdateUserStateRequest req);
	
	SignupResponse register(SignupRequest req);
	LoginResponse login(LoginRequest req);
	void logout(String refreshTokenText);
	String refresh(String refreshTokenText);
	
	Set<Permission> getPermissions(long targetPublicId);
	void grantPermissions(long targetPublicId,PermissionRequest req);
	void revokePermissions(long targetPublicId,PermissionRequest req);
}
