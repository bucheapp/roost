package io.github.bucheapp.roost.services;

import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
	public UUID createIconImage(MultipartFile iconFile) throws IOException;
	public void deleteIconImage(String iconPath);
}
