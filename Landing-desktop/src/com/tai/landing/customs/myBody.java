package com.tai.landing.customs;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class myBody extends Image {
	public static final float WORLD_TO_BOX = 0.01f;
	public static final float BOX_TO_WORLD = 100f;
	
	public Body body;
	public String type = null; 
	
	private boolean isCircle;
	
	public myBody(TextureRegion aregion, World world, BodyType type, float x, float y, boolean isCircle)
	{
		super(aregion);
		
		this.isCircle = isCircle;
		if (isCircle) this.setOrigin(this.getWidth()/2, this.getHeight()/2);
		else this.setOrigin(0, this.getHeight());
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		
		// luôn luôn nhớ chuyển đổi sang kích thước box2D
		x = x * WORLD_TO_BOX;
		y = y * WORLD_TO_BOX;

		bodyDef.position.set(x, y);
		body = world.createBody(bodyDef);
		
		setListener(world);
		body.setUserData(this);
	}
	
	public void CreateFixture(myPolygonShape shape, float density, float friction, float restitution)
	{
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density; // độ đậm đặc
		fixtureDef.friction = friction; // độ ma sát
		fixtureDef.restitution = restitution; // độ đàn hồi
		body.createFixture(fixtureDef);
		
		this.setSize(shape.getWidth() * BOX_TO_WORLD, shape.getHeight() * BOX_TO_WORLD);
	}
	
	public void CreateFixture(myCircleShape shape, float density, float friction, float restitution)
	{
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density; // độ đậm đặc
		fixtureDef.friction = friction; // độ ma sát
		fixtureDef.restitution = restitution; // độ đàn hồi
		body.createFixture(fixtureDef);
		
		this.setSize(shape.getWidth() * BOX_TO_WORLD, shape.getHeight() * BOX_TO_WORLD);
	}
	
	public void UpdateFromBody()
	{
		float x = body.getPosition().x; 
		x = x * BOX_TO_WORLD; 
		float y = body.getPosition().y;
		y = y * BOX_TO_WORLD;

		if (isCircle) this.setPosition(x - this.getWidth()/2, y - this.getHeight()/2);
		else this.setPosition(x, y - this.getHeight());
		
		this.setRotation(MathUtils.radiansToDegrees * body.getAngle());		
	}
	
	private void setListener(final World world)
	{
		this.addListener(new InputListener() {
	        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	        	world.destroyBody(body);
				myBody.this.remove();
	            return true;
	        }
	        
	        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
	        	
	        }
	});		
	}
	
	
}
