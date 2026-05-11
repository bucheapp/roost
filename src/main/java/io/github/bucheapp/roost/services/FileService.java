package io.github.bucheapp.roost.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
	String createIconImage(String path,MultipartFile imageFile) throws IOException;
	String createImage(String path,MultipartFile imageFile) throws IOException;
	String createImage(String path,MultipartFile imageFile,int width,int height) throws IOException;
	String createVideo(String path,MultipartFile videoFile) throws IOException;
	String createAudio(String path, MultipartFile audioFile) throws IOException;
	void deleteFile(String path);
	void checkByte(MultipartFile file,long max);
}
