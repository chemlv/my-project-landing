package com.tai.landing.gamelogic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.tai.landing.screens.PhysicsGame;

public class Landing extends Game {

	public PhysicsGame getPhysicsGame(int level)
	{
		return new PhysicsGame(this, level);
	}

	@Override
	public void create() {
	}

	@Override
    public void resize(
        int width,
        int height )
    {
        super.resize( width, height );

        // show the splash screen when the game is resized for the first time;
        // this approach avoids calling the screen's resize method repeatedly
        if( getScreen() == null ) {
           setScreen( getPhysicsGame(1) );
        }
    }

    @Override
    public void render()
    {
        super.render();
    }

    @Override
    public void pause()
    {
        super.pause();
    }

    @Override
    public void resume()
    {
        super.resume();
    }

    @Override
    public void setScreen(Screen screen )
    {
        super.setScreen( screen );
    }

    @Override
    public void dispose()
    {
        super.dispose();
    }	
		
}
