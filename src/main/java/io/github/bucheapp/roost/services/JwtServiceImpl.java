package io.github.bucheapp.roost.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.models.User;
import io.github.bucheapp.roost.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
	@Autowired
	private SecretKeyService secretKeyService;
	
	@Autowired
	private UserRepository userRepository;
	
	public final static long ACCESSTOKEN_VALIDITY = 60 * 60 * 1000;
	public final static long REFRESHTOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;
	
	@Override
	public String generateAccessToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + ACCESSTOKEN_VALIDITY);
		
		SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyService.getAndCreateSecretKey().getBytes(StandardCharsets.UTF_8));
		
		return Jwts.builder()
				.setSubject(String.valueOf(user.getId()))
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(secretKey)
				.compact();
	}

	@Override
	public String generateRefreshToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + REFRESHTOKEN_VALIDITY);
		
		SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyService.getAndCreateSecretKey().getBytes(StandardCharsets.UTF_8));
		
		return Jwts.builder()
				.setSubject(String.valueOf(user.getId()))
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(secretKey)
				.compact();
	}

	@Override
	public boolean validateToken(String token) {
		SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyService.getAndCreateSecretKey().getBytes(StandardCharsets.UTF_8));
		
		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			
			Long userId = Long.parseLong(claimsJws.getBody().getSubject());
			
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new UsernameNotFoundException("ユーザが存在しません"));
			
			if (user.isFrozen()) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public boolean isTokenValid(String token, UserDetails userDetails) {
	    return validateToken(token) && extractUserId(token) == Long.parseLong(userDetails.getUsername());
	}

	@Override
	public long extractUserId(String token) {
		SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyService.getAndCreateSecretKey().getBytes(StandardCharsets.UTF_8));
		String subject = Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		
		return Long.parseLong(subject);
	}
	
}
