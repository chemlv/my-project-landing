package com.tai.landing.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
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


	}


	@Override
	public void render(float delta) {

		// Mỗi lần nhấn lên màn hình là tạo mới một dynamic body...
		if (Gdx.input.justTouched()) {
			
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