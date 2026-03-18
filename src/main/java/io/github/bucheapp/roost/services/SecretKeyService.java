package io.github.bucheapp.roost.services;

public interface SecretKeyService {
	String generateSecretKey(int byteLength);
	String getAndCreateSecretKey();
}
