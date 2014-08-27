package com.revantis.vosdroid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.revantis.vosdroid.lib.VosPlayNote;

/**
 * Created by ReVanTis on 2014/08/27.
 */
public class Note extends Actor
{
	VosPlayNote vosNote;
	float speed;
	private Sprite mSprite;
	private Texture mTexture;
	private TextureRegion mTextureRegion;
	float scaler;
	public Note(VosPlayNote tnote,Texture nTexture)
	{
		vosNote=tnote;
		mTexture=nTexture;
		speed= Gdx.graphics.getHeight()*0.2f;

		mTextureRegion = new TextureRegion(mTexture, 0, 0, 32, 8);
		mSprite = new Sprite(mTextureRegion);
		scaler = Gdx.graphics.getWidth()/mTexture.getWidth();
		mSprite.scale(scaler);
		mSprite.setPosition(12+vosNote.Track*33, Gdx.graphics.getHeight());
	}
	@Override
	public void draw(Batch batch,float parentAlpha)
	{
		mSprite.translate(0, Gdx.graphics.getDeltaTime() * (-speed));
		mSprite.draw(batch);
	}
}
