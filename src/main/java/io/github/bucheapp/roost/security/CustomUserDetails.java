package io.github.bucheapp.roost.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.github.bucheapp.roost.models.Permission;
import io.github.bucheapp.roost.models.User;

public class CustomUserDetails implements UserDetails {
	private final User user;
	
	public CustomUserDetails(User user) {
		this.user = user;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<Permission> allPermissions = new HashSet<>();
		if (user.getRole() != null && user.getRole().getPermissions() != null) {
			allPermissions.addAll(user.getRole().getPermissions());
		}
		if (user.getPermissions() != null) {
			allPermissions.addAll(user.getPermissions());
		}

		return allPermissions.stream()
			.map(p -> new SimpleGrantedAuthority(p.getName()))
			.collect(Collectors.toSet());
	}
	
	public long getId() {
		return user.getId();
	}
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getName();
	}
}
