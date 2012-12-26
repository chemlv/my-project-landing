package com.tai.landing.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tai.landing.gamelogic.Landing;

public class StartScreen extends BaseScreen {

	// ---- Tiled Map ------------
	TiledMap tiledMap;
	TileAtlas tileAtlas;
	TileMapRenderer tileMapRenderer;
	
	OrthographicCamera camera;
	
	public StartScreen(Landing game) {
		super(game);
	}

	@Override
	public void show() {

		super.show();
		
		// Nạp TiledMap
		tiledMap = TiledLoader.createMap(Gdx.files.internal("data/level/level1.tmx"));
		tileAtlas = new TileAtlas(tiledMap, Gdx.files.internal("data/map"));
		tileMapRenderer = new TileMapRenderer(tiledMap, tileAtlas, 8, 8);

		// Lấy camera là của stage, định lại kích thước viewport và chĩa ống
		// kính vào giữa...
		camera = (OrthographicCamera) stage.getCamera();
		camera.viewportHeight = BaseScreen.VIEWPORT_HEIGHT;
		camera.viewportWidth = BaseScreen.VIEWPORT_WIDTH;
		camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
		camera.update();	
		
		LoadButton();
		
	}
	
	
	private void LoadButton()
	{
		Image title = new Image(getAtlas().findRegion("landing"));
		title.setSize(212, 71);
		title.setPosition(BaseScreen.VIEWPORT_WIDTH/2 - 212/2, 320);
		
		Image pig = new Image(getAtlas().findRegion("player", 1));
		pig.setSize(99, 108);
		pig.setPosition(BaseScreen.VIEWPORT_WIDTH/2 - 99/2, 170);
		
		
		//Button btnew = new Button(new TextureRegionDrawable( getAtlas().findRegion("btnew")));
		final TextButton btnew = new TextButton("New Game", getSkin());
		btnew.setSize(200, 50);
		btnew.setPosition(BaseScreen.VIEWPORT_WIDTH/2 - btnew.getWidth()/2, 120);
		btnew.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if ( x >= 0 && x < btnew.getWidth() && y >= 0 && y < btnew.getHeight())
				{	
					game.setScreen(game.getPhysicsGame(1));
				}
				super.touchUp(event, x, y, pointer, button);
			}
			
		});

		//Button btselect = new Button(new TextureRegionDrawable( getAtlas().findRegion("btselect")));
		final TextButton btselect = new TextButton("Level Select", getSkin());
		btselect.setSize(200, 50);
		btselect.setPosition(BaseScreen.VIEWPORT_WIDTH/2 - btselect.getWidth()/2, 60);
		btselect.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) 
			{
				if ( x >= 0 && x < btselect.getWidth() && y >= 0 && y < btselect.getHeight() )
				{
					game.setScreen(game.getMenuScreen());
				}
				super.touchUp(event, x, y, pointer, button);
			}
		});
		
		stage.addActor(title);
		stage.addActor(pig);
		stage.addActor(btnew);
		stage.addActor(btselect);
		
	}
	

	@Override
	public void render(float delta) {
		
		stage.act(delta);

		// clear the screen with the given RGB color (black)
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		tileMapRenderer.render(camera);
	
		stage.draw();

	}
	
	@Override
    public void dispose()
    {
		super.dispose();
		tileAtlas.dispose();
		tileMapRenderer.dispose();
		
    }	
	
	
}
