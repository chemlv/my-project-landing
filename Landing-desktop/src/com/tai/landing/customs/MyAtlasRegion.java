package com.tai.landing.customs;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MyAtlasRegion extends AtlasRegion {
	
	private Drawable drawable;

	public MyAtlasRegion(AtlasRegion aregion) {
		super(aregion);
		drawable = new TextureRegionDrawable( aregion );
	}
	
	public Drawable getDrawable()
	{
		return drawable;
	}

}
