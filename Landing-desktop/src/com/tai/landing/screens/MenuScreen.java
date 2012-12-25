package com.tai.landing.screens;

import java.awt.Label;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
		//Image im = new Image(new TextureRegionDrawable( getAtlas().findRegion("dynamic", 1)));
		//im.setSize(60, 60);
		
		TextButton tb = new TextButton("1", getSkin(), "selectlevel");
		tb.setSize(48, 48);

		//stage.addActor(im);
		stage.addActor(tb);
		
		/*Table table = new Table();
		table.setSize(BaseScreen.VIEWPORT_WIDTH, BaseScreen.VIEWPORT_HEIGHT);
		for (int i = 1; i <= 5; i++)
		{
			Label lb = new Label();
			lb.setText("" + i);
			table.add(lb);
		}
		
		stage.addActor(table);*/
	}
	
	private void CreateButton()
	{
		
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
