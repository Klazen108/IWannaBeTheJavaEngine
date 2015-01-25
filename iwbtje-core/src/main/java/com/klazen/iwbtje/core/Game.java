package com.klazen.iwbtje.core;

import java.util.List;

public class Game {
	List<GObject> objects;
	
	GRoom currentRoom;
	Class<? extends GRoom> nextRoom;
	
	public <T extends GRoom> Game(Class<T> firstRoom) {
		gotoRoom(firstRoom);
	}
	
	/**
	 * Queues a room switch to occur at the end of this step
	 * @param nextRoom
	 */
	public void gotoRoom(Class<? extends GRoom> nextRoom) {
		this.nextRoom = nextRoom;
	}
	
	public void postStep() {
		if (nextRoom != null) {
			try {
				currentRoom = nextRoom.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				throwError(e);
			}
			currentRoom.roomStart();
		}
	}
	
	public void draw() {
		for (GObject cur : objects) {
			cur.draw();
		}
	}
	
	public void throwError(Exception e) {
		System.exit(-1);
	}
}
