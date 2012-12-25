package com.tai.landing.customs;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ButtonLevel extends TextButton {

	private static int WIDTH = 60;
	private static int HEIGHT = 60;
	
	private int id;
	
	private onClickListener listener;
	public interface onClickListener {
		public void onUp(int id);
	}
	
	public ButtonLevel(int id, Skin skin, onClickListener listener) {
		super("" + id, skin, "selectlevel");
		
		this.setSize(WIDTH, HEIGHT);
		this.id = id;
		this.listener = listener;
		SetOnClick();
	}

	private void SetOnClick()
	{
		this.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if ( x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT)
				{	
					if (listener!=null) listener.onUp(id);
				}
				super.touchUp(event, x, y, pointer, button);
			}
			
		});
	}
}
