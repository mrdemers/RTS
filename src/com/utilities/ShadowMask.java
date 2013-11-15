package com.utilities;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class ShadowMask {
	private Image[] shadows;
	
	
	public static ShadowMask instance = new ShadowMask();
	
	private ShadowMask() {
		try {
			SpriteSheet shadows1 = new SpriteSheet("shadows1.png", 50, 50);
			shadows = new Image[16];
			shadows[0] = shadows1.getSprite(1, 1);
			shadows[1] = shadows1.getSprite(2, 2);
			shadows[2] = shadows1.getSprite(0, 2);
			shadows[3] = shadows1.getSprite(1, 2);
			shadows[4] = shadows1.getSprite(2, 0);
			shadows[5] = shadows1.getSprite(2, 1);
			shadows[6] = shadows1.getSprite(3, 0);
			shadows[7] = shadows1.getSprite(3, 0);
			shadows[8] = shadows1.getSprite(0, 0);
			shadows[9] = shadows1.getSprite(1, 1); // For error checking. If I see this will re think
			shadows[10] = shadows1.getSprite(0, 1);
			shadows[11] = shadows1.getSprite(5, 0);
			shadows[12] = shadows1.getSprite(1, 0);
			shadows[13] = shadows1.getSprite(3, 2);
			shadows[14] = shadows1.getSprite(5, 2);
			shadows[15] = shadows1.getSprite(4, 1);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	public static ShadowMask getInstance() {
		return instance;
	}
	
	public Image getMask(int corners) {
		return shadows[corners];
	}

}
