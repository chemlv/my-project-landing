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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.tai.landing.gamelogic.Landing;

public class PhysicsGame extends BaseScreen {

	// ---- Tiled Map ------------
	TiledMap tiledMap;
	TileAtlas tileAtlas;
	TileMapRenderer tileMapRenderer;

	// -------Box2d--------------
	World world = new World(new Vector2(0, -10), true);

	// camera.
	OrthographicCamera camera;
	static final float BOX_STEP = 1 / 45f;

	// Cái 6,2 này làm lơ đi bạn, nếu cứ khăng khắng muốn hiểu thì vào Box2d
	// document mà đọc (trích lời của libgdx).
	static final int BOX_VELOCITY_ITERATIONS = 6;
	static final int BOX_POSITION_ITERATIONS = 2;

	Group group = new Group();
	Group pgroup = new Group();
	
	public PhysicsGame(Landing game) {
		super(game);
	}

	@Override
	public void show() {

		super.show();

		// Nạp TiledMap
		tiledMap = TiledLoader.createMap(Gdx.files.internal("mapx/xmas.tmx"));
		tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("mapx"));
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

	private PolygonShape getPolygon(TiledObject o)
	{
		PolygonShape polygon = new PolygonShape();
		String[] strp = o.polygon.split(" ");
		Vector2[] apoints = new Vector2[strp.length];
		for (int i = 0; i < strp.length; i++) {
			float x = Float.parseFloat(strp[i].split(",")[0]); 
			x = x * myBody.WORLD_TO_BOX; 
			float y = -Float.parseFloat(strp[i].split(",")[1]); 
			y = y * myBody.WORLD_TO_BOX; 

			apoints[i] = new Vector2(x, y);
		}
		polygon.set(apoints);
		return polygon;
	}
	
	private void CreateStaticBody(TiledObject o)
	{
		TextureRegion tr = getAtlas().findRegion("static", Integer.parseInt(o.name));
		myBody mb = new myBody(tr, o.width, o.height, world, BodyType.StaticBody, o.x,  tileMapRenderer.getMapHeightUnits() - o.y - o.height);
		mb.CreateFixture(getPolygon(o));
		
		mb.setTouchable(Touchable.disabled);
		group.addActor(mb);
	}
	
	private void CreateDynamicBody(TiledObject o)
	{
		TextureRegion tr = getAtlas().findRegion("dynamic", Integer.parseInt(o.name));
		myBody mb = new myBody(tr, o.width, o.height, world, BodyType.StaticBody, o.x,  tileMapRenderer.getMapHeightUnits() - o.y - o.height);
		mb.CreateFixture(getPolygon(o), 1f, 0.5f, 0.5f);
		
		mb.setTouchable(Touchable.enabled);
		group.addActor(mb);
	}
	
	private void CreatePlayerBody(TiledObject o)
	{
		TextureRegion tr = getAtlas().findRegion("player", Integer.parseInt(o.name));
		myBody mb = new myBody(tr, o.width, o.height, world, BodyType.StaticBody, o.x,  tileMapRenderer.getMapHeightUnits() - o.y - o.height);
		mb.CreateFixture(getPolygon(o), 1f, 0.5f, 0.5f);
		
		mb.setTouchable(Touchable.enabled);
		pgroup.addActor(mb);
	}
	
	private void Remove(float x, float y)
	{
		myBody mb = (myBody) stage.hit(x, y, true);
		if (mb != null)
		{
			world.destroyBody(mb.body);
			mb.remove();
		}
	}
	
	@Override
	public void render(float delta) {

		if (Gdx.input.justTouched()) {
			float x = Gdx.input.getX();
			float y = Gdx.input.getY();
			Remove(x, y);
		}

		stage.act(delta);

		// clear the screen with the given RGB color (black)
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		tileMapRenderer.render(camera);
	
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
			myBody mb = (myBody) group.getChildren().get(i);
			mb.UpdateFromBody();
			
			//kiem tra tat ca nhan vat deu duoi dat va khong bi nghieng
			
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