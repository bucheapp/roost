package io.github.bucheapp.roost.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.bucheapp.roost.models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
	@Autowired
	SecretKeyService secretKeyService;
	
	private final long accessTokenValidity = 60 * 60 * 1000;
	
	private final long refreshTokenValidity = 7 * 24 * 60 * 60 * 1000;
	
	@Override
	public String generateAccessToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + accessTokenValidity);
		
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
		Date expiryDate = new Date(now.getTime() + refreshTokenValidity);
		
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
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String extractUserId(String token) {
		SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyService.getAndCreateSecretKey().getBytes(StandardCharsets.UTF_8));
		return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	
}
