package com.tai.landing.screens;

import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class myPolygonShape extends PolygonShape {
	
	public float minx = Float.MAX_VALUE;
	public float miny = Float.MAX_VALUE;
	public float maxx = 0;
	public float maxy = 0;
	
	public myPolygonShape(TiledObject o)
	{
		super();

		String[] strp = o.polygon.split(" ");
		Vector2[] apoints = new Vector2[strp.length];
		for (int i = 0; i < strp.length; i++) {
			float x = Float.parseFloat(strp[i].split(",")[0]); 
			x = x * myBody.WORLD_TO_BOX; 
		float y = -Float.parseFloat(strp[i].split(",")[1]); 
			y = y * myBody.WORLD_TO_BOX; 

			apoints[i] = new Vector2(x, y);
		}
		
		this.set(apoints);
		InitMinMax();
		
	}
	
	public float getWidth()
	{
		return maxx - minx;
	}
	
	public float getHeight()
	{
		return maxy - miny;
	}
	
	private void InitMinMax()
	{
		for (int i = 0; i < this.getVertexCount(); i++)
		{
			Vector2 v = new Vector2();
			this.getVertex(i, v);
			if (minx > v.x) minx = v.x;
			if (miny > v.y) miny = v.y;
			if (maxx < v.x) maxx = v.x; 
			if (maxy < v.y) maxy = v.y; 
		}
	}
	
	

}
