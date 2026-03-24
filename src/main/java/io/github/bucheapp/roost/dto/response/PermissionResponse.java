package io.github.bucheapp.roost.dto.response;

import java.util.Set;

public class PermissionResponse {
	Set<String> permissions;
	
	public PermissionResponse(Set<String> permissions) {
		this.permissions = permissions;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}
}
