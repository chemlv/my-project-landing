package com.tai.landing.customs;


import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.tai.landing.screens.BaseScreen;
import com.tai.landing.screens.PhysicsGame;
import com.tai.landing.screens.StartScreen;

public class ResultNote extends Group {
	
	public final static int WIN = 0;
	public final static int LOSE = 1;
	public final static int COMP = 2;
	private int status;
	
	final static int WIDTH = 533;
	final static int HEIGHT = 399;
	
	private Image frame; //hình nền của group
	final private PhysicsGame a;
	
	
    public ResultNote(PhysicsGame aa, int status)
    {
    	super();
    	this.a = aa;
    	this.status = status;
    	this.setX(BaseScreen.VIEWPORT_WIDTH/2 - WIDTH / 2);
    	this.setY(BaseScreen.VIEWPORT_HEIGHT + 50);
    	this.setWidth(WIDTH);
    	this.setHeight(HEIGHT);
    	
    	
    	//Nạp hình nền cho Group
    	frame = new Image(a.getAtlas().findRegion("noteboard"));
    	frame.setWidth(WIDTH);
    	frame.setHeight(HEIGHT);
    	this.addActor(frame);
    	
    	LoadActors();
    	
    }
    
    private void LoadActors()
    {
    	TextureRegion region;
    	if (status == WIN) region = a.getAtlas().findRegion("victory");
    	else region = a.getAtlas().findRegion("failure");
    	Image img= new Image(region);
    	img.setPosition(WIDTH/2 - img.getWidth()/2, HEIGHT - img.getHeight() - 100);
    	this.addActor(img);
    	
    	
    	if (status != COMP)
    	{
	    	String str;
	    	if (status == WIN) str = "Next Level";
	    	else str = "Start Again";
			final TextButton btnew = new TextButton(str, a.getSkin());
			btnew.setSize(200, 50);
			btnew.setPosition(WIDTH/2 - btnew.getWidth()/2, 140);
			btnew.addListener(new ClickListener() {
				@Override
				public void touchUp(InputEvent event, float x, float y,
						int pointer, int button) {
					if (x >= 0 && x < btnew.getWidth() && y >= 0 && y < btnew.getHeight()) 
					{
						if (status == WIN)  a.NextLevel() ;
						else a.StartAgain();
						ResultNote.this.remove();
					}
					super.touchUp(event, x, y, pointer, button);
				}
	
			});
			this.addActor(btnew);
    	}

		final TextButton btselect = new TextButton("Level Select", a.getSkin());
		btselect.setSize(200, 50);
		btselect.setPosition(WIDTH/2 - btselect.getWidth()/2, 80);
		btselect.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (x >= 0 && x < btselect.getWidth() && y >= 0 && y < btselect.getHeight()) 
				{
					a.LevelSelect();
				}
				super.touchUp(event, x, y, pointer, button);
			}
		});
		this.addActor(btselect);
    	
		final TextButton btmenu = new TextButton("Back to Menu", a.getSkin());
		btmenu.setSize(200, 50);
		btmenu.setPosition(WIDTH/2 - btmenu.getWidth()/2, 20);
		btmenu.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if ( x >= 0 && x < btmenu.getWidth() && y >= 0 && y < btmenu.getHeight())
				{	
					a.BackToMenu();
				}
				super.touchUp(event, x, y, pointer, button);
			}
			
		});
		this.addActor(btmenu);
    	
    }
    
    public void Moving()
    {
    	Preferences prefs = Gdx.app.getPreferences("mypreferences");
		boolean isSoundOn = prefs.getBoolean("SoundOn", true);
    	if (isSoundOn)
    	{
    		BaseScreen.bg_music.stop();
	    	if (status == WIN) BaseScreen.winner.play();
	    	else if (status == LOSE) BaseScreen.lose.play();
	    	else if (status == COMP) BaseScreen.winner.play();
    	}
    	
    	float x = BaseScreen.VIEWPORT_WIDTH/2 - WIDTH / 2;
    	float y = BaseScreen.VIEWPORT_HEIGHT/2 - HEIGHT / 2;
    	this.addAction(moveTo(x, y, 0.5f));    	
    }
    
}
