package com.revantis.vosdroid;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.revantis.vosdroid.lib.*;




public class VosDroidGame implements ApplicationListener {
	Stage mStage;


	Texture instrument_texture;
	Texture note_texutre;
	MidiPlayer midiPlayer;
	String vosFilePath;
	Object MusicReady=new Object();
	String filePath;
	BitmapFont font;
	AssetManager assetManager=new AssetManager();
	int midiPlayedCurrentPostion=-5000;
	int notesPlayed=0;
	VosParser vosp;
	Label msg;
	boolean begun=false;

	public VosDroidGame ( )
	{
		super();
	}
	public VosDroidGame (MidiPlayer _midiPlayer,String path)
	{
		super();
		midiPlayer=_midiPlayer;
		filePath=path;
	}
	@Override
	public void create () {
		mStage=new Stage();


		instrument_texture = new Texture("instrument.png");
		Image bgimg=new Image(instrument_texture);
		bgimg.setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		note_texutre = new Texture("note.png");


		assetManager.load("msyahei_en.fnt",BitmapFont.class);
		assetManager.finishLoading();
		font=assetManager.get("msyahei_en.fnt",BitmapFont.class);

		msg = new Label("init",new Label.LabelStyle(font, Color.BLACK));
		msg.setFontScale(2);
		msg.setPosition(Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()-200);

		mStage.addActor(bgimg);
		mStage.addActor(msg);

		double scaler_width=Gdx.graphics.getWidth()/instrument_texture.getWidth();
		double scaler_height=Gdx.graphics.getHeight()/instrument_texture.getHeight();
		double note_width=note_texutre.getWidth()*Gdx.graphics.getWidth()/instrument_texture.getWidth();

		Thread parserThread = new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				vosFilePath="vos/";
				FileHandle vosFile=new FileHandle(filePath);
				FileHandle midFile=new FileHandle(filePath.replace(".vos",".mid"));
				try
				{
					Gdx.app.log("d","parserThread:start parse");

					vosp = new VosParser(vosFile.file());
					Gdx.app.log("d","parserThread:init success");
					vosp.Parse();
					Gdx.app.log("d", "parserThread:parsed success");
					Gdx.app.log("d","Ticks"+vosp.Tick2MS.size()+" calculated");
					vosp.SaveMidiFile(midFile.file());
					Gdx.app.log("d","parserThread:saved success");
					synchronized (MusicReady) {
						MusicReady.notifyAll();
					}
				}
				catch (Exception e)
				{
					Gdx.app.log("e","parserThread:exception occured:"+e.getMessage());
				}
			}
		});

		Thread midiThread = new Thread(new Runnable() {
			@Override
			public synchronized void run()  {
				try
				{
					Gdx.app.log("d","playerThread:waiting");
					synchronized (MusicReady)
					{
						MusicReady.wait();
					}
					Gdx.app.log("d", "playerThread:opening");
					midiPlayer.open(filePath.replace(".vos", ".mid"));
					Gdx.app.log("d", "playerThread:sleeping");
					Thread.sleep(500);
					Gdx.app.log("d", "playerThread:playing");
					midiPlayer.play();
				}
				catch (Exception e)
				{
					Gdx.app.log("e","playerThread:exception occured");
				}
			}
		});
		if(filePath!=null)
		{
			parserThread.start();
			midiThread.start();
		}
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(!begun)
		try {
			synchronized (MusicReady)
			{
				MusicReady.wait();
			}
			begun=true;
		}
		catch (Exception e)
		{
			Gdx.app.log("e","render:syn exception");
		}
		int midiPlayedCurrentPostionLastLoop=midiPlayedCurrentPostion;//记录上次render时播放的毫秒数
		if(midiPlayer.isPlaying())
		{

			midiPlayedCurrentPostionLastLoop = midiPlayedCurrentPostion;
			midiPlayedCurrentPostion = midiPlayer.getCurrentPosistion();//获取本次render时播放的毫秒数
			int tempconnter = 0;
			for (; tempconnter < vosp.playNote.size(); tempconnter++)
			{
				if ((midiPlayedCurrentPostionLastLoop <= (vosp.playNote.get(tempconnter).Time - 2000))
						&& ((vosp.playNote.get(tempconnter).Time - 2000) < midiPlayedCurrentPostion))
				{
					Actor tempActor=new Note(vosp.playNote.get(tempconnter),note_texutre);
					mStage.addActor(tempActor);
				}
			}
			notesPlayed = tempconnter;
			//Gdx.app.log("d","notesPlayed:"+notesPlayed);
		}
		msg.setText(""+midiPlayedCurrentPostion);
		mStage.act((midiPlayedCurrentPostion-midiPlayedCurrentPostionLastLoop)/1000);
		mStage.draw();
	}
	@Override
	public void pause()
	{
		if(midiPlayer.isPlaying())
		midiPlayer.pause();

	}
	@Override
	public  void resume()
	{
		if(!midiPlayer.isPlaying())
		midiPlayer.play();

	}

	@Override
	public void dispose() {

	}
}
