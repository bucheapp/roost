package io.github.bucheapp.roost.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.github.bucheapp.roost.security.CustomUserDetails;
import io.github.bucheapp.roost.util.MessageUtil;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByName(username)
				.orElseThrow(() -> new UsernameNotFoundException(messageUtil.get("user.notfound") + ": " + username));
		return new CustomUserDetails(user);
	}
	
	public UserDetails loadUserById(long id) throws UsernameNotFoundException {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UsernameNotFoundException(messageUtil.get("user.notfound") + ": " + id));
		return new CustomUserDetails(user);
	}
}
