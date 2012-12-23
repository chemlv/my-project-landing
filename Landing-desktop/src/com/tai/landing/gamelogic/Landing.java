package com.tai.landing.gamelogic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.tai.landing.screens.PhysicsDemo;

public class Landing extends Game {

	public PhysicsDemo getPhysicsDemo()
	{
		return new PhysicsDemo(this);
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
           setScreen( getPhysicsDemo() );
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
