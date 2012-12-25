package com.tai.landing.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.tai.landing.gamelogic.Landing;

public abstract class BaseScreen implements Screen {
	
    public static final int VIEWPORT_WIDTH = 800, VIEWPORT_HEIGHT = 480;
    public static final int TILE = 16;
    
	protected final Landing game;
	protected final Stage stage;

	private BitmapFont font;
	private SpriteBatch batch;
	private TextureAtlas atlas;
	private Skin skin;

	public BaseScreen(Landing game) {
		this.game = game;
		this.stage = new Stage( 0, 0, false );
	}

	public BitmapFont getFont() {
		if (font == null) {
			font = new BitmapFont();
			font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			//font.setScale(6);
		}
		return font;
	}

	public SpriteBatch getBatch() {
		if (batch == null) {
			batch = new SpriteBatch();
		}
		return batch;
	}

	public TextureAtlas getAtlas() {
		if (atlas == null) {
			atlas = new TextureAtlas(Gdx.files.internal("data/MyDataPacker/FileMoTa.txt"));
		}
		return atlas;
	}
	
	public Skin getSkin() {
		if (skin == null) {
			skin = new Skin(Gdx.files.internal("data/skin/uiskin.json"), new TextureAtlas(Gdx.files.internal("data/skin/uiskin.atlas")));
			//skin.addRegions(getAtlas());
		}
		return skin;
	}
	
	@Override
    public void show()
    {
        // set the stage as the input processor
        Gdx.input.setInputProcessor( stage );
    }

    @Override
    public void resize(
        int width,
        int height )
    {
        // resize the stage
        stage.setViewport( BaseScreen.VIEWPORT_WIDTH, BaseScreen.VIEWPORT_HEIGHT, false );
    }

    @Override
    public void render(
        float delta )
    {
        // (1) process the game logic

        // update the actors
        stage.act( delta );

        // (2) draw the result

        // clear the screen with the given RGB color (black)
        Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        // draw the actors
        stage.draw();
    }

    @Override
    public void hide()
    {
        // dispose the resources by default
        dispose();
    }

    @Override
    public void pause()
    {
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void dispose()
    {
        //stage.dispose();
        if( font != null ) font.dispose();
        if( batch != null ) batch.dispose();
        if( atlas != null ) atlas.dispose();
        if (skin != null) skin.dispose();
    }
	
}
