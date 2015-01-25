package com.klazen.iwbtje.core;

public class GObject {
	GSprite sprite;
	GSprite mask;
	
	float x,y;
	
	public GObject(float x, float y) {
		this.x=x;
		this.y=y;
	}
	
	public void draw() {
		sprite.draw(x, y);
	}
	
	public void setSprite(SpriteType type) {
		sprite = SpriteManager.getInstance().getSprite(type);
	}
}
