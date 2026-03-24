package io.github.bucheapp.roost.services;

import java.util.Set;

import io.github.bucheapp.roost.dto.request.CreateAdminUserRequest;
import io.github.bucheapp.roost.dto.request.CreateUserRequest;
import io.github.bucheapp.roost.dto.request.LoginRequest;
import io.github.bucheapp.roost.dto.request.PermissionRequest;
import io.github.bucheapp.roost.dto.request.SignupRequest;
import io.github.bucheapp.roost.dto.response.LoginResponse;
import io.github.bucheapp.roost.dto.response.SignupResponse;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.User;

public interface UserService {
	SignupResponse register(SignupRequest req);
	User getUserById(long id);
	User getUserByPublicId(long publicId);
	User updateEmailById(long id,String email);
	User updatePasswordById(long id,String email);
	void deleteUserById(long id);
	LoginResponse login(LoginRequest req);
	void logout(String refreshTokenText);
	String refresh(String refreshTokenText);
	void setFrozen(long publicId,boolean frozen);
	void createUser(long id,CreateUserRequest req);
	void createAdminUser(CreateAdminUserRequest req);
	Set<Permission> getPermissions(long publicId);
	void grantPermissions(long publicId,long id,PermissionRequest req);
	void revokePermissions(long publicId,long id,PermissionRequest req);
}
