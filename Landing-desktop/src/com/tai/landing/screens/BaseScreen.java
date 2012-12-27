package com.tai.landing.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.tai.landing.gamelogic.Landing;

public abstract class BaseScreen implements Screen {
	
    public static final int VIEWPORT_WIDTH = 800, VIEWPORT_HEIGHT = 480;
    public static final int TILE = 16;
    
	private Button btsound;
	private Button btmute;
	
	public static Music bg_music;
	public static Sound remove;
	public static Sound winner;
	public static Sound lose; 	
	
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
        
        bg_music = Gdx.audio.newMusic(Gdx.files.internal("data/sound/bg_music.ogg"));
		bg_music.setLooping(true);
		
		remove = Gdx.audio.newSound(Gdx.files.internal("data/sound/remove.ogg"));
		winner = Gdx.audio.newSound(Gdx.files.internal("data/sound/winner.ogg"));
		lose = Gdx.audio.newSound(Gdx.files.internal("data/sound/lose.ogg"));
        
		btsound = new Button(new TextureRegionDrawable(getAtlas().findRegion("soundup")), new TextureRegionDrawable(getAtlas().findRegion("sounddown")) );
		btsound.setPosition(BaseScreen.VIEWPORT_WIDTH - btsound.getWidth() - 10, BaseScreen.VIEWPORT_HEIGHT - btsound.getHeight() - 10);
		btsound.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) 
			{
				if ( x >= 0 && x < btsound.getWidth() && y >= 0 && y < btsound.getHeight() )
				{
					Preferences prefs = Gdx.app.getPreferences("mypreferences");
					prefs.putBoolean("SoundOn", false);
					prefs.flush();
					
					btsound.setVisible(false);
					btmute.setVisible(true);
					bg_music.stop();
				}
				super.touchUp(event, x, y, pointer, button);
			}
		});
        
		btmute = new Button(new TextureRegionDrawable(getAtlas().findRegion("muteup")), new TextureRegionDrawable(getAtlas().findRegion("mutedown")) );
		btmute.setPosition(BaseScreen.VIEWPORT_WIDTH - btmute.getWidth() - 10, BaseScreen.VIEWPORT_HEIGHT - btmute.getHeight() - 10);
		btmute.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) 
			{
				if ( x >= 0 && x < btmute.getWidth() && y >= 0 && y < btmute.getHeight() )
				{
					Preferences prefs = Gdx.app.getPreferences("mypreferences");
					prefs.putBoolean("SoundOn", true);
					prefs.flush();
					
					btsound.setVisible(true);
					btmute.setVisible(false);
					bg_music.play();
				}
				super.touchUp(event, x, y, pointer, button);
			}
		});		
		
		Preferences prefs = Gdx.app.getPreferences("mypreferences");
		boolean isSoundOn = prefs.getBoolean("SoundOn", true);
		if (isSoundOn)
		{
			btsound.setVisible(true);
			btmute.setVisible(false);
			bg_music.play();
		}
		else
		{
			btsound.setVisible(false);
			btmute.setVisible(true);
			bg_music.stop();
		}		
		
		stage.addActor(btsound);
		stage.addActor(btmute);
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
        bg_music.dispose();
        remove.dispose();
		winner.dispose();
		lose.dispose();

    }
	
}
