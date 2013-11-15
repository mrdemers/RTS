package com;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Art {
	/**
	 * @return the image from the file path
	 */
	public static Image loadImage(String fileName) {
		Image image = null;
		try {
			image = new Image(fileName);
		} catch (SlickException e) {
			System.out.println("Could not load " + fileName);
			e.printStackTrace();
		}
		return image;
	}
	
	/**
	 * @return the rgb value of the color
	 */
	public static int getRGB(Color color) {
		return color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
	}
}
