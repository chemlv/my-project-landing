package com.tai.landing.screens;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class myBody extends Image {
	static final float WORLD_TO_BOX = 1; //0.01f;
	static final float BOX_TO_WORLD = 1; //100f;
	
	Body body;
	
	public myBody(TextureRegion aregion, World world, BodyType type, float x, float y)
	{
		super(aregion);
		this.setOrigin(0, this.getHeight());
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		
		// luôn luôn nhớ chuyển đổi sang kích thước box2D
		x = x * WORLD_TO_BOX;
		y = y * WORLD_TO_BOX;

		bodyDef.position.set(x, y);
		body = world.createBody(bodyDef);
		
		//body.setUserData(this);
	}
	
	public void CreateFixture(myPolygonShape shape, float density, float friction, float restitution)
	{
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density; // độ đậm đặc
		fixtureDef.friction = friction; // độ ma sát
		fixtureDef.restitution = restitution; // độ đàn hồi
		body.createFixture(fixtureDef);
		
		this.setSize(shape.getWidth(), shape.getHeight());
	}
	
	public void UpdateFromBody()
	{
		float x = body.getPosition().x; //- (this.getWidth()); 
		x = x * BOX_TO_WORLD; 
		float y = body.getPosition().y - this.getHeight();
		y = y * BOX_TO_WORLD;

		this.setPosition(x, y);
		this.setRotation(MathUtils.radiansToDegrees * body.getAngle());		
	}
}
