package com.revantis.vosdroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by ReVanTis on 2014/09/02.
 * this class is to show progress bar
 */
public class ProgressBar extends Actor
{
	public float Progress;//int smaller than 100

	private Texture mTexture;
	private TextureRegion mBarRegion;

	private Image mBar;
	private float height;

	private TextureRegion mFrameRegion;
	private Image mFrame;

	public ProgressBar(Texture _Texture)
	{
		Progress=0;
		mTexture =_Texture;

		mFrameRegion=new TextureRegion(mTexture,0,0,200,20);
		mFrame=new Image(mFrameRegion);
		height=Gdx.graphics.getWidth()*0.8f*0.1f;
		mFrame.setSize(Gdx.graphics.getWidth()*0.8f,height);
		mFrame.setCenterPosition(Gdx.graphics.getWidth()/2,mFrame.getHeight()/2);

		mBarRegion=new TextureRegion(mTexture,0,20,200,20);
		mBar=new Image(mBarRegion);
		height=Gdx.graphics.getWidth()*0.8f*0.1f;
		mBar.setSize(Gdx.graphics.getWidth()*0.8f,height);
		mBar.setCenterPosition(Gdx.graphics.getWidth()/2+1,mBar.getHeight()/2);
	}
	@Override
	public void draw(Batch batch,float parentAlpha)
	{
		mFrame.draw(batch,parentAlpha);
		mBar.setSize(Gdx.graphics.getWidth()*0.8f*Progress/100f,height);
		mBar.draw(batch,parentAlpha);

	}
}
