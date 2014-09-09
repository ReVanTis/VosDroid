package com.revantis.vosdroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.revantis.vosdroid.lib.VosPlayNote;

/**
 * Created by ReVanTis on 2014/08/27.
 */
public class Note extends Actor
{
	public boolean bblink=false;
	float judgement_line_pos;
	VosPlayNote vosNote;
	float speed;
	private Image mSprite;
	private Image circleSprite;
	private Texture mCircleTexutre;
	private Texture mTexture;
	private TextureRegion mTextureRegion;
	private TextureRegion mCircleTextureRegion;
	float bubblesize=256f;
	float appeartime=0.1f;
	float scaler;
	public Note(VosPlayNote tnote,Texture nTexture,Texture nctexutre,float _judgement_line_pos)
	{
		judgement_line_pos=_judgement_line_pos;
		vosNote=tnote;
		mTexture=nTexture;
		mCircleTexutre=nctexutre;
		speed= Gdx.graphics.getHeight()*judgement_line_pos/512f/1f;
		mTextureRegion = new TextureRegion(mTexture, 0, vosNote.Color*8, 32, 8);
		mSprite = new Image(mTextureRegion);
		mCircleTextureRegion=new TextureRegion(mCircleTexutre,0,0,128,128);
		circleSprite = new Image(mCircleTextureRegion);
		circleSprite.setSize(1,1);
		scaler = Gdx.graphics.getWidth()/mTexture.getWidth();
		if(!vosNote.LongNote)
		{
			mSprite.setSize(Gdx.graphics.getWidth()/256f*mSprite.getWidth(),Gdx.graphics.getWidth()/256f*mSprite.getWidth()/4f);
		}
		else
		{
			mSprite.setSize(Gdx.graphics.getWidth()/256f*mSprite.getWidth(),((float)vosNote.DurationTime)/1000f*speed);
		}
		mSprite.setPosition((13f+vosNote.Track*33f)/256f*Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bubblesize=mSprite.getWidth()*2;
	}
	@Override
	public void draw(Batch batch,float parentAlpha)
	{
		if(!bblink)
		{
			circleSprite.setCenterPosition(mSprite.getX() + mSprite.getWidth() / 2, mSprite.getY() + mSprite.getHeight() / 2);
			//Gdx.app.log("d","note at" + circleSprite.getX()+","+circleSprite.getY());
			mSprite.setY(mSprite.getY()+Gdx.graphics.getDeltaTime() * (-speed));
			if(mSprite.getY()+mSprite.getHeight()/2< (Gdx.graphics.getHeight()-speed))
			{
				bblink=true;
				//Gdx.app.log("d","note blink true");
			}
			mSprite.draw(batch,parentAlpha);
		}
		else
		{

			if (circleSprite.getWidth() < bubblesize)
			{
			float tempx=circleSprite.getCenterX();
			float tempy=circleSprite.getCenterY();
			circleSprite.setSize(circleSprite.getWidth()+Gdx.graphics.getDeltaTime()/appeartime * bubblesize,circleSprite.getHeight()+Gdx.graphics.getDeltaTime()/appeartime * bubblesize);
			circleSprite.setCenterPosition(tempx,tempy);
				//Gdx.app.log("d","blink size is:"+circleSprite.getHeight());
				circleSprite.draw(batch,parentAlpha);
			}
		}
	}
}
