package com.klazen.iwbtje.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

public class SpriteManager {
	Map<SpriteType,GSprite> sprites;
	
	public static final int INITIAL_MAP_SIZE = 32;
	
	static SpriteManager instance;

	private SpriteManager() {
		sprites = new HashMap<>(INITIAL_MAP_SIZE);
	}
	
	public static SpriteManager getInstance() {
		if (instance==null) instance = new SpriteManager();
		return instance;
	}
	
	public GSprite getSprite(SpriteType type) {
		if (sprites.containsKey(type)) return sprites.get(type);
		else {
			int tex = loadTexture(loadImage(type.getFilename()));
			GSprite spr = new GSprite(tex);
			sprites.put(type, spr);
			return spr;
		}
	}
	
    private static final int BYTES_PER_PIXEL = 4;//3 for RGB, 4 for RGBA
    
    public static int loadTexture(BufferedImage image) {
    	int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL); //4 for RGBA, 3 for RGB
        for(int y = 0; y < image.getHeight(); y++){
            for(int x = 0; x < image.getWidth(); x++){
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));    // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));     // Green component
                buffer.put((byte) (pixel & 0xFF));            // Blue component
                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
            }
        }
        buffer.flip(); //FOR THE LOVE OF GOD DO NOT FORGET THIS

        int textureID = glGenTextures(); //Generate texture ID
        glBindTexture(GL_TEXTURE_2D, textureID); //Bind texture ID

        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        //Return the texture ID so we can bind it later again
      return textureID;
   }

   public static BufferedImage loadImage(String loc)
   {
        try {
           return ImageIO.read(SpriteManager.class.getResource("/src/main/resources/sprites/"+loc));
        } catch (IOException e) {
            //Error Handling Here
        	e.printStackTrace();
        }
       return null;
   }
}
