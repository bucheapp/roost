package io.github.bucheapp.roost.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {
	public long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		return userDetails.getId();
	}
	
	public boolean hasAuthority(String permission) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals(permission));
	}
}
