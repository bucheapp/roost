package io.github.bucheapp.roost.services;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class SecretKeyServiceImpl implements SecretKeyService {
	@Value("${app.data-dir}")
	private String dataDirPath;

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public String generateSecretKey(int byteLength) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] keyBytes = new byte[byteLength];
		secureRandom.nextBytes(keyBytes);
		return Base64.getEncoder().encodeToString(keyBytes);
	}

	@Override
	public String getAndCreateSecretKey() {
		Path dataDir = Paths.get(dataDirPath);

		Path settingsFile = dataDir.resolve("settings.json");
		String secretKey;
		
		Map<String, Object> jsonMap = mapper.readValue(settingsFile.toFile(),
				new TypeReference<Map<String, Object>>() {});

		if (jsonMap.get("secretKey") == null) {
			secretKey = generateSecretKey(32);
			jsonMap.put("secretKey", secretKey);
			mapper.writeValue(settingsFile.toFile(), jsonMap);
		} else {
			secretKey = (String) jsonMap.get("secretKey");
		}

		return secretKey;
	}
}
