package com.revantis.vosdroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by ReVanTis on 2014/09/02.
 * this class is to show progress bar
 */
public class ProgressBar extends Actor
{
	public float Progress;//int smaller than 100
	private Texture mTexture;
	private TextureRegion mBarRegion;
	private Sprite mBar;
	private float height;
	public ProgressBar(Texture _Texture)
	{
		Progress=0;
		mTexture=_Texture;
		mBarRegion=new TextureRegion(mTexture,0,20,200,20);
		mBar=new Sprite(mBarRegion);
		height=Gdx.graphics.getWidth()*0.8f*0.1f;
		mBar.setSize(Gdx.graphics.getWidth()*0.8f,height);
		mBar.setCenter(Gdx.graphics.getWidth()/2+1,mBar.getHeight()/2);
	}
	@Override
	public void draw(Batch batch,float parentAlpha)
	{
		mBar.setSize(Gdx.graphics.getWidth()*0.8f*Progress/100f,height);
		mBar.draw(batch);
	}
}
