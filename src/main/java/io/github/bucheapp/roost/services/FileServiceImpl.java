package io.github.bucheapp.roost.services;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucheapp.roost.util.MessageUtil;

@Service
public class FileServiceImpl implements FileService {
	@Value("${app.data-dir}")
	private String dataDirPath;
	
	@Autowired
	private MessageUtil messageUtil;

	@Override
	public String createIconImage(String path,MultipartFile imageFile) throws IOException {
		return createImage(path,imageFile,160,160);
	}
	
	@Override
	public String createImage(String path,MultipartFile imageFile) throws IOException {
		BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
		return createImage(path,imageFile,originalImage.getWidth(),originalImage.getHeight());
	}
	
	@Override
	public String createImage(String path,MultipartFile imageFile,int width,int height) throws IOException {
		BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
		BufferedImage resizedImage = new BufferedImage(160, 160, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = resizedImage.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
		
		File dataDir = new File(dataDirPath,path);
		dataDir.mkdirs();
		
		UUID uuid = UUID.randomUUID();
		
		String newPath = uuid + ".jpg";
		File outputFile = new File(dataDir,newPath);
		
		boolean success = ImageIO.write(resizedImage, "jpg", outputFile);

		if (!success) {
			throw new RuntimeException(messageUtil.get("image.writing.failure"));
		}
		
		return newPath;
	}
	
	@Override
	public String createVideo(String path, MultipartFile videoFile) throws IOException {
		if(!isMp4(videoFile)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,messageUtil.get("video.format.not.mp4"));
		}
		
		UUID uuid = UUID.randomUUID();
		String newPath = uuid + ".mp4";
		File parentFile = new File(dataDirPath,path);
		Path savePath = Paths.get(parentFile.getPath(),newPath);
		Files.createDirectories(savePath.getParent());
		
		videoFile.transferTo(savePath.toFile());
		
		return newPath;
	}
	
	@Override
	public String createAudio(String path, MultipartFile audioFile) throws IOException {
		String ext = null;
		
		if(isMp3(audioFile)) {
			ext = ".mp3";
		} else if(isM4a(audioFile)) {
			ext = ".m4a";
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,messageUtil.get("audio.format.not.mp3orm4a"));
		}
		
		UUID uuid = UUID.randomUUID();
		String newPath = uuid + ext;
		File parentFile = new File(dataDirPath,path);
		Path savePath = Paths.get(parentFile.getPath(),newPath);
		Files.createDirectories(savePath.getParent());
		
		audioFile.transferTo(savePath.toFile());
		
		return newPath;
	}
	
	@Override
	public void deleteFile(String path) {
		File file = new File(dataDirPath,path);
		
		boolean success = file.delete();
		
		if (!success) {
			throw new RuntimeException(messageUtil.get("file.deletion.failed"));
		}
	}

	@Override
	public void checkByte(MultipartFile file, long max) {
		if(file.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageUtil.get("file.isempty"));
		}
		
		if (file.getSize() > max) {
			long size = max / (1024 * 1024);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageUtil.get("file.size.exceeds",size));
		}
	}
	
	private boolean isMp4(MultipartFile file) {
		try (InputStream is = file.getInputStream()) {
			byte[] buffer = new byte[12];
			if (is.read(buffer) < 12) return false;
			String boxType = new String(buffer, 4, 4);
			return "ftyp".equals(boxType);
		} catch (Exception e) {
			return false;
		}
	}
	
	public boolean isMp3(MultipartFile file) {
		try (InputStream is = file.getInputStream()) {
			byte[] buffer = new byte[3];

			if (is.read(buffer) < 3) {
				return false;
			}

			if (buffer[0] == 'I' && buffer[1] == 'D' && buffer[2] == '3') {
				return true;
			}

			int first = buffer[0] & 0xFF;
			int second = buffer[1] & 0xE0;

			return first == 0xFF && second == 0xE0;

		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isM4a(MultipartFile file) {

		try (InputStream is = file.getInputStream()) {

			byte[] buffer = new byte[32];

			if (is.read(buffer) < 32) {
				return false;
			}

			if (!(buffer[4] == 'f'
				&& buffer[5] == 't'
				&& buffer[6] == 'y'
				&& buffer[7] == 'p')) {
				return false;
			}

			String brand = new String(buffer, 8, 4);

			return brand.contains("M4A")
				|| brand.contains("mp4a")
				|| brand.contains("isom")
				|| brand.contains("mp42");

		} catch (Exception e) {
			return false;
		}
	}
}
