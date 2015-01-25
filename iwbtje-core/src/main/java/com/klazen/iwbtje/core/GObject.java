package com.klazen.iwbtje.core;

public class GObject {
	GSprite sprite;
	GSprite mask;
	
	float x,y;
	
	public void draw() {
		sprite.draw(x, y);
	}
	
	public void setSprite(SpriteType type) {
		sprite = SpriteManager.getInstance().getSprite(type);
	}
}
