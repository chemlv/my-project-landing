package com.tai.landing.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tai.landing.gamelogic.Landing;

public class PhysicsGame extends BaseScreen {

	private int level;
	
	// ---- Tiled Map ------------
	TiledMap tiledMap;
	TileAtlas tileAtlas;
	TileMapRenderer tileMapRenderer;

	// -------Box2d--------------
	World world = new World(new Vector2(0, -10), true);
	//Box2DDebugRenderer debugRenderer; 

	// camera.
	OrthographicCamera camera;
	static final float BOX_STEP = 1 / 45f;

	// Cái 6,2 này làm lơ đi bạn, nếu cứ khăng khắng muốn hiểu thì vào Box2d
	// document mà đọc (trích lời của libgdx).
	static final int BOX_VELOCITY_ITERATIONS = 6;
	static final int BOX_POSITION_ITERATIONS = 2;

	Group group = new Group();
	Group pgroup = new Group();
	
	public PhysicsGame(Landing game, int level) {
		super(game);
		this.level = level;
		//debugRenderer = new Box2DDebugRenderer();  
	}

	@Override
	public void show() {

		super.show();

		// Nạp TiledMap
		tiledMap = TiledLoader.createMap(Gdx.files.internal("data/level/level" + level + ".tmx"));
		tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("data/map"));
		tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 8, 8);

		// Lấy camera là của stage, định lại kích thước viewport và chĩa ống
		// kính vào giữa...
		camera = (OrthographicCamera) stage.getCamera();
		camera.viewportHeight = BaseScreen.VIEWPORT_HEIGHT;
		camera.viewportWidth = BaseScreen.VIEWPORT_WIDTH;
		camera.position.set(camera.viewportWidth * .5f,
				camera.viewportHeight * .5f, 0f);
		camera.update();
		
		stage.addActor(group);
		stage.addActor(pgroup);
		
		LoadActor();

	}
	
	private void Reset()
	{
			world.dispose();
			group.clear();
			pgroup.clear();
			world = new World(new Vector2(0, -10), true);
			tileAtlas.dispose();
			tileMapRenderer.dispose();
			
			tiledMap = TiledLoader.createMap(Gdx.files.internal("data/level/level" + level + ".tmx"));
			tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("data/map"));
			tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 8, 8);
			
			LoadActor();
	}
	
	private void LoadActor() {
		for (int i = 0; i < tiledMap.objectGroups.size(); i++) {
			TiledObjectGroup group = tiledMap.objectGroups.get(i);

			if ("static".equals(group.name)) 
			{
				for (int j = 0; j < group.objects.size(); j++) {
					TiledObject object = group.objects.get(j);
					CreateStaticBody(object);
				}
			}
			else if ("dynamic".equals(group.name))
			{
				for (int j = 0; j < group.objects.size(); j++) {
					TiledObject object = group.objects.get(j);
					CreateDynamicBody(object);
				}
			}
			else if ("player".equals(group.name))
			{
				for (int j = 0; j < group.objects.size(); j++) {
					TiledObject object = group.objects.get(j);
					CreatePlayerBody(object);
				}
			}
		}
	}

	private void CreateStaticBody(TiledObject o)
	{
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyType.StaticBody;

		float x = o.x;
		x = x * myBody.WORLD_TO_BOX;
		float y = BaseScreen.VIEWPORT_HEIGHT - o.y - o.height;
		y = y * myBody.WORLD_TO_BOX;

		groundBodyDef.position.set(x, y);
		Body groundBody = world.createBody(groundBodyDef);

		groundBody.createFixture(new myPolygonShape(o), 0.0f);		
	}
	
	private void CreateDynamicBody(TiledObject o)
	{
		TextureRegion tr = getAtlas().findRegion("dynamic", Integer.parseInt(o.name));
		myBody mb = new myBody(tr, world, BodyType.DynamicBody, o.x,  BaseScreen.VIEWPORT_HEIGHT - o.y - o.height);
		
		
		myPolygonShape poly= new myPolygonShape(o);
		mb.CreateFixture(poly, 1f, 0.5f, 0f);
		
		mb.setTouchable(Touchable.enabled);
		group.addActor(mb);
	}
	
	private void CreatePlayerBody(TiledObject o)
	{
		TextureRegion tr = getAtlas().findRegion("player", Integer.parseInt(o.name));
		myBody mb = new myBody(tr, world, BodyType.DynamicBody, o.x,  BaseScreen.VIEWPORT_HEIGHT - o.y - o.height);
		
		myPolygonShape poly= new myPolygonShape(o);
		mb.CreateFixture(poly, 1f, 0.5f, 0.5f);
		
		mb.setTouchable(Touchable.disabled);
		pgroup.addActor(mb);
	}
	
	
	@Override
	public void render(float delta) {

		stage.act(delta);

		// clear the screen with the given RGB color (black)
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//camera.update();
		tileMapRenderer.render(camera);
		//debugRenderer.render(world, camera.combined);  
	
		// Thục thi các hoạt động của các vật thể trong thế giới Box2D
		world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);
		world.clearForces();
	
		for (int i = 0; i < group.getChildren().size; i++)
		{
			myBody mb = (myBody) group.getChildren().get(i);
			mb.UpdateFromBody();
		}

		for (int i = 0; i < pgroup.getChildren().size; i++)
		{
			myBody mb = (myBody) pgroup.getChildren().get(i);
			mb.UpdateFromBody();
			
			//kiem tra tat ca nhan vat deu duoi dat va khong bi nghieng
			if (!mb.body.isAwake())
			{
				if (group.getChildren().size == 0) 
				{
					if (mb.getRotation() > -90 && mb.getRotation() < 90)
					{
						level = 2;
						Reset();
					}
					else
					{
						level = 1;
						Reset();
					}
				}
			}
			
		}
		
		
		stage.draw();

	}
	
	@Override
    public void dispose()
    {
		super.dispose();
		world.dispose();
		tileAtlas.dispose();
		tileMapRenderer.dispose();
		
    }
}