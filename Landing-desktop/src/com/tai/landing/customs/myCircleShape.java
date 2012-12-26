package com.tai.landing.customs;

import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.physics.box2d.CircleShape;

public class myCircleShape extends CircleShape {
	
	static float RADIUS = 41f * myBody.WORLD_TO_BOX;
	
	public myCircleShape(TiledObject o)
	{
		super();
		this.setRadius(RADIUS);
		
	}
	
	public float getWidth()
	{
		return RADIUS * 2;
	}
	
	public float getHeight()
	{
		return RADIUS * 2;
	}
	
}
	