package com.tai.landing.screens;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.tai.landing.customs.ResultNote;
import com.tai.landing.customs.myBody;
import com.tai.landing.customs.myCircleShape;
import com.tai.landing.customs.myPolygonShape;
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

	Group note = new Group();
	Group group = new Group();
	Group pgroup = new Group();
	
	Label lb;
	boolean isplaying = true;
	
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
		
		stage.addActor(note);
		stage.addActor(group);
		stage.addActor(pgroup);
		
		AddButtons();
		
		LoadActor();

	}
	
	private void AddButtons()
	{
		lb = new Label("" + level + " / 25", getSkin(), "level");
		lb.setPosition(BaseScreen.VIEWPORT_WIDTH / 2 - lb.getWidth() / 2, BaseScreen.VIEWPORT_HEIGHT - lb.getHeight() - 10);
		stage.addActor(lb);
		
		final Button bthome = new Button(new TextureRegionDrawable(getAtlas().findRegion("homeup")), new TextureRegionDrawable(getAtlas().findRegion("homedown")) );
		bthome.setPosition(10, BaseScreen.VIEWPORT_HEIGHT - bthome.getHeight() - 10);
		bthome.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if ( x >= 0 && x < bthome.getWidth() && y >= 0 && y < bthome.getHeight())
				{	
					game.setScreen(game.getStartScreen());
				}
				super.touchUp(event, x, y, pointer, button);
			}
			
		});
		stage.addActor(bthome);
		
		final Button btreset = new Button(new TextureRegionDrawable(getAtlas().findRegion("resetup")), new TextureRegionDrawable(getAtlas().findRegion("resetdown")) );
		btreset.setPosition(bthome.getWidth() + 20, BaseScreen.VIEWPORT_HEIGHT - btreset.getHeight() - 10);
		btreset.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if ( x >= 0 && x < btreset.getWidth() && y >= 0 && y < btreset.getHeight())
				{	
					Reset();
				}
				super.touchUp(event, x, y, pointer, button);
			}
			
		});		
		stage.addActor(btreset);

		Button btsound = new Button(new TextureRegionDrawable(getAtlas().findRegion("soundup")), new TextureRegionDrawable(getAtlas().findRegion("sounddown")) );
		btsound.setPosition(BaseScreen.VIEWPORT_WIDTH - btsound.getWidth() - 10, BaseScreen.VIEWPORT_HEIGHT - btsound.getHeight() - 10);
		stage.addActor(btsound);
	
	}
	
	private void Reset()
	{
		lb.setText("" + level + " / 25");
		
		world.dispose();
		note.clear();
		group.clear();
		pgroup.clear();
		world = new World(new Vector2(0, -10), true);
		tileAtlas.dispose();
		tileMapRenderer.dispose();
			
		tiledMap = TiledLoader.createMap(Gdx.files.internal("data/level/level" + level + ".tmx"));
		tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("data/map"));
		tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 8, 8);
			
		LoadActor();
		isplaying = true;
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
		if ("da".equals(o.type))
		{
			TextureRegion tr = getAtlas().findRegion("dynamic", Integer.parseInt(o.name));
			myBody mb = new myBody(tr, world, BodyType.StaticBody, o.x,  BaseScreen.VIEWPORT_HEIGHT - o.y - o.height, false);
			mb.type = o.type;
			
			myPolygonShape poly= new myPolygonShape(o);
			mb.CreateFixture(poly, 1f, 0.5f, 0f);
			
			mb.setTouchable(Touchable.disabled);
			group.addActor(mb);
		}
		else if ("gotron".equals(o.type))
		{
			TextureRegion tr = getAtlas().findRegion("dynamic", Integer.parseInt(o.name));
			myBody mb = new myBody(tr, world, BodyType.DynamicBody, o.x,  BaseScreen.VIEWPORT_HEIGHT - o.y - o.height, true);
			mb.type = o.type;
			
			myCircleShape circle= new myCircleShape(o);
			mb.CreateFixture(circle, 1f, 0.5f, 0f);
			
			mb.setTouchable(Touchable.enabled);
			group.addActor(mb);
		}
		else
		{
			TextureRegion tr = getAtlas().findRegion("dynamic", Integer.parseInt(o.name));
			myBody mb = new myBody(tr, world, BodyType.DynamicBody, o.x,  BaseScreen.VIEWPORT_HEIGHT - o.y - o.height, false);
			mb.type = o.type;
			
			myPolygonShape poly= new myPolygonShape(o);
			mb.CreateFixture(poly, 1f, 0.5f, 0f);
			
			mb.setTouchable(Touchable.enabled);
			group.addActor(mb);
		}
	}
	
	private void CreatePlayerBody(TiledObject o)
	{
		TextureRegion tr = getAtlas().findRegion("player", Integer.parseInt(o.name));
		myBody mb = new myBody(tr, world, BodyType.DynamicBody, o.x,  BaseScreen.VIEWPORT_HEIGHT - o.y - o.height, false);
		mb.type = o.type;
		
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
		}
		
		if (isplaying) // kiem tra tinh trang cua cac chu heo
		{
			int NumWin = 0;
			int NumLose = 0;
			
			for (int i = 0; i < pgroup.getChildren().size; i++)
			{
				myBody mb = (myBody) pgroup.getChildren().get(i);
				
				if ( mb.getY() < -mb.getHeight()) //nếu một con lọt xuống hố, cả bầy cùng thua...
				{
					NumLose ++;
					break;
				}
				else if (!mb.body.isAwake()) // đã nằm yên
				{
					if (isLand(mb)) 
					{
						if (mb.getRotation() > -30 && mb.getRotation() < 30)
						{
							NumWin++;
						}
						else
						{
							NumLose++;
							break;
						}
					}
				}
			}
			
			int status = -1;
			if (NumLose > 0) status = ResultNote.LOSE;
			else if (NumWin >= pgroup.getChildren().size) status = ResultNote.WIN;
			if (status >= 0)
			{
				isplaying = false;
				if (status == ResultNote.WIN)
				{
					Preferences prefs = Gdx.app.getPreferences("mypreferences");
					int sLevel = prefs.getInteger("level", 1);
					
					if (level + 1 > sLevel)
					{
						prefs.putInteger("level", level + 1);
						prefs.flush();
					}
				}
	
				ResultNote result;
				result = new ResultNote(PhysicsGame.this, status);
				note.addActor(result);
				note.setZIndex(stage.getActors().size);
				result.Moving();
	
			}
		}
		
		stage.draw();

	}
	
	private boolean isLand(myBody mb)
	{
		boolean kq = true;
		boolean OnGround = false;
		
		List<Contact> cons = world.getContactList();
		for (int i = 0; i < cons.size(); i++)
		{
			Contact contact = cons.get(i);
			if (contact.isTouching())
			{
				myBody mb1 = (myBody)contact.getFixtureA().getBody().getUserData();
				myBody mb2 = (myBody)contact.getFixtureB().getBody().getUserData();
				
				String t1 = mb1 == null ? "dat" : mb1.type;
				String t2 = mb2 == null ? "dat" : mb2.type;
				
				if ("da".equals(t1) || "da".equals(t2)) continue;
					
				if (mb.equals(mb1))
				{
					if (!"dat".equals(t2))
					{
						kq = false;
						break;
					}
					else
					{
						OnGround = true;
					}
				}
				else if (mb.equals(mb2))
				{
					if (!"dat".equals(t1))
					{
						kq = false;
						break;
					}
					else
					{
						OnGround = true;
					}
				}
			}
		}		
		
		return (kq && OnGround);
	}
	
	public void BackToMenu()
	{
		game.setScreen(game.getStartScreen());
	}
	
	public void NextLevel()
	{
		level++;
		Reset();
	}
	
	public void StartAgain()
	{
		Reset();
	}
	
	public void LevelSelect()
	{
		game.setScreen(game.getMenuScreen());
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