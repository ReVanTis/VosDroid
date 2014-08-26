package com.revantis.vosdroid;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.revantis.vosdroid.lib.*;


public class VosDroidGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	MidiPlayer midiPlayer;
	String vosFilePath;
	Object MusicReady=new Object();
	String filePath;
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

		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");


		Thread parserThread = new Thread(new Runnable() {
			@Override
			public synchronized void run() {
				vosFilePath="vos/";
				FileHandle vosFile=new FileHandle(filePath);
				FileHandle midFile=new FileHandle(filePath.replace(".vos",".mid"));
				try
				{
					Gdx.app.log("d","parserThread:start parse");
					VosParser vosp = new VosParser(vosFile.file());
					Gdx.app.log("d","parserThread:init success");
					vosp.Parse();
					Gdx.app.log("d","parserThread:parsed success");
					vosp.SaveMidiFile(midFile.file());
					Gdx.app.log("d","parserThread:saved success");
					synchronized (MusicReady) {
						MusicReady.notifyAll();
					}
				}
				catch (Exception e)
				{
					Gdx.app.log("e","parserThread:exception occured");
				}
			}
		});
		if(filePath!=null)
		parserThread.start();
		Thread midiThread = new Thread(new Runnable() {
			@Override
			public synchronized void run()  {
				try {
					Gdx.app.log("d","playerThread:waiting");
					synchronized (MusicReady) {
						MusicReady.wait();
					}
					Gdx.app.log("d","playerThread:opening");
					midiPlayer.open(filePath.replace(".vos",".mid"));
					Gdx.app.log("d","playerThread:playing");
					midiPlayer.play();
				}
				catch (Exception e)
				{
					Gdx.app.log("e","playerThread:exception occured");
				}
			}
		});
		if(filePath!=null)
		midiThread.start();
	}
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
}
