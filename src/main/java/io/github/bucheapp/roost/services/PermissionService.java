package io.github.bucheapp.roost.services;

import java.util.Set;

import io.github.bucheapp.roost.dto.request.RolePermissionRequest;
import io.github.bucheapp.roost.models.Permission;

public interface PermissionService {
	Set<Permission> getAllPermissions();
	Set<Permission> getRolePermissions(RolePermissionRequest req);
}
