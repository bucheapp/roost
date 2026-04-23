package io.github.bucheapp.roost.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucheapp.roost.dto.request.RolePermissionRequest;
import io.github.bucheapp.roost.dto.response.PermissionResponse;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.services.PermissionService;

@RestController
public class PermissionController {
	@Autowired
	private PermissionService permissionService;
	
	@GetMapping("api/permissions")
	public ResponseEntity<PermissionResponse> getAllPermissions() {
		Set<String> permissions = permissionService.getAllPermissions()
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		PermissionResponse permissionResponse = new PermissionResponse(permissions);
		
		return ResponseEntity.ok(permissionResponse);
	}
	
	@GetMapping("api/role/permissions")
	public ResponseEntity<PermissionResponse> getRolePermission(
			@RequestBody RolePermissionRequest req
		) {
		Set<String> permissions = permissionService.getRolePermissions(req)
				.stream()
				.map(Permission::getName)
				.collect(Collectors.toSet());
		PermissionResponse permissionResponse = new PermissionResponse(permissions);
		
		return ResponseEntity.ok(permissionResponse);
	}
}
