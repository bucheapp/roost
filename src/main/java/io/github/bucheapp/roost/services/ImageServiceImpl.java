package io.github.bucheapp.roost.services;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageServiceImpl implements ImageService {
	@Value("${app.data-dir}")
	private String dataDirPath;

	@Override
	public UUID createIconImage(MultipartFile iconFile) throws IOException {
		BufferedImage originalImage = ImageIO.read(iconFile.getInputStream());
		BufferedImage resizedImage = new BufferedImage(160, 160, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = resizedImage.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.drawImage(originalImage, 0, 0, 160, 160, null);
		g.dispose();
		
		File dataDir = new File(dataDirPath,"image/icon");
		dataDir.mkdirs();
		
		UUID uuid = UUID.randomUUID();
		
		File outputFile = new File(dataDir,uuid.toString() + ".jpg");
		
		boolean success = ImageIO.write(resizedImage, "jpg", outputFile);

		if (!success) {
			throw new RuntimeException("画像書き込み失敗");
		}
		
		return uuid;
	}
	
	@Override
	public void deleteIconImage(String iconPath) {
		File dataDir = new File(dataDirPath,"image/icon");
		File imageFile = new File(dataDir,iconPath);
		
		imageFile.delete();
	}
}
