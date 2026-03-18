package io.github.bucheapp.roost.services;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class SecretKeyServiceImpl implements SecretKeyService {
	@Override
	public String generateSecretKey(int byteLength) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] keyBytes = new byte[byteLength];
		secureRandom.nextBytes(keyBytes);
		return Base64.getEncoder().encodeToString(keyBytes);
	}

	@Override
	public String getAndCreateSecretKey() {
		ClassPathResource resource = new ClassPathResource("secret_key.json");
		File secretKeyFile;
		String secretKey = null;
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			secretKeyFile = resource.getFile();
			if(!secretKeyFile.exists()) {
				secretKey = generateSecretKey(32);
	            Map<String, String> jsonMap = new HashMap<>();
	            jsonMap.put("secretKey", secretKey);
	            mapper.writeValue(secretKeyFile, jsonMap);
	        } else {
	            Map<?, ?> jsonMap = mapper.readValue(secretKeyFile, Map.class);
	            secretKey = (String) jsonMap.get("secretKey");
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}

        return secretKey;
	}
}
