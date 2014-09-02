package com.revantis.vosdroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by ReVanTis on 2014/09/02.
 * this class is to show progress bar's frame
 */
public class ProgressBarFrame extends Actor
{
	public int Progress;//int smaller than 100
	private Texture mTexture;
	private TextureRegion mFrameRegion;
	private Sprite mFrame;
	private float height;
	public ProgressBarFrame(Texture _Texture)
	{
		Progress=0;
		mTexture=_Texture;
		mFrameRegion=new TextureRegion(mTexture,0,0,200,20);
		mFrame=new Sprite(mFrameRegion);
		height=Gdx.graphics.getWidth()*0.8f*0.1f;
		mFrame.setSize(Gdx.graphics.getWidth()*0.8f,height);
		mFrame.setCenter(Gdx.graphics.getWidth()/2,mFrame.getHeight()/2);
	}
	@Override
	public void draw(Batch batch,float parentAlpha)
	{
		mFrame.draw(batch);
	}
}
