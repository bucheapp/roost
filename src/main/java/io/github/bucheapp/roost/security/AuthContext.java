package io.github.bucheapp.roost.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContext {
	public Long getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		
		Object principal = authentication.getPrincipal();
		
		if (principal instanceof CustomUserDetails userDetails) {
			return userDetails.getId();
		}
		
		return null;
	}
	
	public boolean hasAuthority(String permission) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals(permission));
	}
}
