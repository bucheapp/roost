package io.github.bucheapp.roost.services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.dto.request.RolePermissionRequest;
import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.Role;
import io.github.bucheapp.roost.repositories.PermissionRepository;
import io.github.bucheapp.roost.repositories.RoleRepository;
import io.github.bucheapp.roost.util.MessageUtil;

@Service
public class PermissionServiceImpl implements PermissionService {
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Override
	public Set<Permission> getAllPermissions() {
		Set<Permission> permissions = new HashSet<>(permissionRepository.findAll());
		return permissions;
	}

	@Override
	public Set<Permission> getRolePermissions(RolePermissionRequest req) {
		Role role = roleRepository.findByName(req.getRole())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, messageUtil.get("permission.notfound")));
		
		return role.getPermissions();
	}
}
