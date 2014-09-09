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
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.revantis.vosdroid.lib.*;

import java.io.File;

public class VosDroidGame implements ApplicationListener {
	Stage mStage;

	Texture progressbar_texture;
	Texture instrument_texture;
	Texture note_texutre;
	Texture circle_texutre;
	MidiPlayer midiPlayer;
	String vosFilePath;
	Object MusicReady=new Object();
	String filePath;
	BitmapFont font;
	AssetManager assetManager=new AssetManager();
	int midiPlayedCurrentPostion=0;
	int notesPlayed=0;
	VosParser vosp;
	Label msg;
	Label fps;
	Image bgimg;
	ProgressBar progressBar;

	FileHandle vosFile;
	FileHandle midFile;
	public VosDroidGame ( )
	{
		super();
	}
	public VosDroidGame (MidiPlayer _midiPlayer,String path)
	{

		super();
		midiPlayer=_midiPlayer;
		filePath = path;

	}
	@Override
	public void create () {

		mStage=new Stage();

		if(filePath==null)
		{
			filePath=new String("internal file used");
			vosFile=Gdx.files.internal("Canon in D.vos");
			midFile = new FileHandle("/sdcard/vos.mid");
		}
		else
		{
			vosFile = new FileHandle(filePath);
			midFile = new FileHandle(filePath.replace(".vos", ".mid"));
		}
		assetManager.load("circle.png",Texture.class);
		assetManager.load("msyahei_en.fnt",BitmapFont.class);
		assetManager.load("instrument.png", Texture.class);
		assetManager.load("note.png",Texture.class);
		assetManager.load("progressbar.png",Texture.class);
		assetManager.finishLoading();

		font=assetManager.get("msyahei_en.fnt",BitmapFont.class);
		instrument_texture=assetManager.get("instrument.png", Texture.class);
		note_texutre=assetManager.get("note.png",Texture.class);
		circle_texutre=assetManager.get("circle.png",Texture.class);

		progressbar_texture=assetManager.get("progressbar.png",Texture.class);

		bgimg = new Image(instrument_texture);
		bgimg.setBounds(0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		fps = new Label("init",new Label.LabelStyle(font, Color.BLACK));
		fps.setAlignment(Align.right);
		msg = new Label("init",new Label.LabelStyle(font, Color.BLACK));
		msg.setAlignment(Align.center);



		progressBar=new ProgressBar(progressbar_texture);

		msg.setCenterPosition(Gdx.graphics.getWidth()/2,40);

		mStage.addActor(bgimg);
		mStage.addActor(msg);
		mStage.addActor(progressBar);
		mStage.addActor(fps);
		msg.setHeight(progressBar.getHeight());

		double scaler_width=Gdx.graphics.getWidth()/instrument_texture.getWidth();
		double scaler_height=Gdx.graphics.getHeight()/instrument_texture.getHeight();
		double note_width=note_texutre.getWidth()*Gdx.graphics.getWidth()/instrument_texture.getWidth();

		Thread parserThread = new Thread(new Runnable() {
			@Override
			public synchronized void run() {

				try
				{
					Gdx.app.log("d","parserThread:start parse");

					vosp = new VosParser(vosFile.read());
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
					Gdx.app.log("e","parserThread:exception occured:"+e);
					e.printStackTrace();
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
					midiPlayer.open(midFile.path());
					Gdx.app.log("d", "playerThread:sleeping");
					Thread.sleep(500);
					Gdx.app.log("d", "playerThread:playing");
					midiPlayer.play();
				}
				catch (Exception e)
				{
					Gdx.app.log("e","playerThread:exception occured",e);
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
		int midiPlayedCurrentPostionLastLoop=midiPlayedCurrentPostion;//记录上次render时播放的毫秒数
		if(midiPlayer.isPlaying())
		{
			midiPlayedCurrentPostionLastLoop = midiPlayedCurrentPostion;
			midiPlayedCurrentPostion = midiPlayer.getCurrentPosistion();//获取本次render时播放的毫秒数
			int tempconnter = 0;
			for (; tempconnter < vosp.playNote.size(); tempconnter++)
			{
				if ((midiPlayedCurrentPostionLastLoop <= (vosp.playNote.get(tempconnter).Time - 1000))
						&& ((vosp.playNote.get(tempconnter).Time - 1000) < midiPlayedCurrentPostion))
				{
					Actor tempActor=new Note(vosp.playNote.get(tempconnter),note_texutre,circle_texutre,446f);
					mStage.addActor(tempActor);
				}
			}
			notesPlayed = tempconnter;

			progressBar.Progress= ( (float)midiPlayedCurrentPostion/(float)midiPlayer.getDuration()*100f);
		}
		if(midiPlayedCurrentPostion==0)
		{
			if(vosp!=null)
			{
				msg.setText(vosp.MessageString);
				progressBar.Progress=vosp.progress;
			}
			else
			{
				msg.setText("loading");
				progressBar.Progress=100;
			}
		}
		else
		{
			float t1=midiPlayedCurrentPostion/1000f;
			float t2=midiPlayer.getDuration()/1000f;
			msg.setText(String.format("%.2f|%.2f",t1,t2));
		}
		bgimg.toFront();
		fps.setText("fps:"+Gdx.graphics.getFramesPerSecond());
		progressBar.toFront();
		msg.toFront();
		fps.setPosition(Gdx.graphics.getWidth()-fps.getWidth(),Gdx.graphics.getHeight()-fps.getHeight());
		fps.toFront();
		mStage.act((Gdx.graphics.getDeltaTime()));
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
