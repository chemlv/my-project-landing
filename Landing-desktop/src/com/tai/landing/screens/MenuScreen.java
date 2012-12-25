package com.tai.landing.screens;

import java.awt.Label;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.tai.landing.customs.ButtonLevel;
import com.tai.landing.customs.ButtonLevel.onClickListener;
import com.tai.landing.gamelogic.Landing;

public class MenuScreen extends BaseScreen {

	// ---- Tiled Map ------------
	TiledMap tiledMap;
	TileAtlas tileAtlas;
	TileMapRenderer tileMapRenderer;
		
	OrthographicCamera camera;	
	
	public MenuScreen(Landing game) {
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
		
		LoadButtons();
		
		
		
	}
	
	private void LoadButtons()
	{
		ButtonLevel.onClickListener l = new onClickListener() {
			@Override
			public void onUp(int id) {
				game.setScreen(game.getPhysicsGame(id));
			}
		};
		
		Table table = new Table();
		table.setPosition(BaseScreen.VIEWPORT_WIDTH / 2, BaseScreen.VIEWPORT_HEIGHT / 2 + 50);
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				int id = (i * 5) + j;
				ButtonLevel bl = new ButtonLevel(id + 1, getSkin(), l);
				table.add(bl).pad(12, 24, 12, 24);
			}
			table.row();
		}
		stage.addActor(table);

		
		final TextButton btmenu = new TextButton("Back to Menu", getSkin());
		btmenu.setSize(200, 50);
		btmenu.setPosition(BaseScreen.VIEWPORT_WIDTH - 220, 20);
		btmenu.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if ( x >= 0 && x < btmenu.getWidth() && y >= 0 && y < btmenu.getHeight())
				{	
					game.setScreen(game.getStartScreen());
				}
				super.touchUp(event, x, y, pointer, button);
			}
			
		});
		stage.addActor(btmenu);
		
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
