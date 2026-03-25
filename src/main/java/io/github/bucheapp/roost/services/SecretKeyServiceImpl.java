package io.github.bucheapp.roost.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class SecretKeyServiceImpl implements SecretKeyService {
	@Value("${app.data-dir}")
	private String dataDirPath;

	private final ObjectMapper mapper = new ObjectMapper();
	private final String secretKeyFileName = "secret_key.json";

	@Override
	public String generateSecretKey(int byteLength) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] keyBytes = new byte[byteLength];
		secureRandom.nextBytes(keyBytes);
		return Base64.getEncoder().encodeToString(keyBytes);
	}

	@Override
	public String getAndCreateSecretKey() {
		try {
			Path dataDir = Paths.get(dataDirPath);
			Files.createDirectories(dataDir);

			Path secretKeyFile = dataDir.resolve(secretKeyFileName);
			String secretKey;

			if (!Files.exists(secretKeyFile)) {
				secretKey = generateSecretKey(32);
				Map<String, String> jsonMap = new HashMap<>();
				jsonMap.put("secretKey", secretKey);
				mapper.writeValue(secretKeyFile.toFile(), jsonMap);
			} else {
				Map<?, ?> jsonMap = mapper.readValue(secretKeyFile.toFile(), Map.class);
				secretKey = (String) jsonMap.get("secretKey");
			}

			return secretKey;
		} catch (IOException e) {
			throw new RuntimeException("secret_key.json の読み書きに失敗しました", e);
		}
	}
}
