package com.klazen.iwbtje.core;

public enum SpriteType {
	player("");
	
	String filename;
	
	private SpriteType(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}
}
