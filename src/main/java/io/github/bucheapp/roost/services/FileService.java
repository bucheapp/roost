package io.github.bucheapp.roost.services;

import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	public UUID createImage(String path,MultipartFile imageFile) throws IOException;
	public void deleteImage(String imagePath);
	void checkByte(MultipartFile file,long max);
}
