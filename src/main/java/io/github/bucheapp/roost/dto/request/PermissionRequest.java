package io.github.bucheapp.roost.dto.request;

import java.util.Set;

public class PermissionRequest {
	private Set<String> permissions;

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}
}
