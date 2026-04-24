package io.github.bucheapp.roost.services;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
	public UUID createImage(String path,MultipartFile imageFile) throws IOException {
		BufferedImage originalImage = ImageIO.read(imageFile.getInputStream());
		BufferedImage resizedImage = new BufferedImage(160, 160, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = resizedImage.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.drawImage(originalImage, 0, 0, 160, 160, null);
		g.dispose();
		
		File dataDir = new File(dataDirPath,path);
		dataDir.mkdirs();
		
		UUID uuid = UUID.randomUUID();
		
		File outputFile = new File(dataDir,uuid.toString() + ".jpg");
		
		boolean success = ImageIO.write(resizedImage, "jpg", outputFile);

		if (!success) {
			throw new RuntimeException(messageUtil.get("image.writing.failure"));
		}
		
		return uuid;
	}
	
	@Override
	public void deleteImage(String imagePath) {
		File imageFile = new File(dataDirPath,imagePath);
		
		boolean success = imageFile.delete();
		
		if (!success) {
			throw new RuntimeException(messageUtil.get("image.deletion.failed"));
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
}
