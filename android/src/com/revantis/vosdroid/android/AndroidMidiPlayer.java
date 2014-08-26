package com.revantis.vosdroid.android;
import android.content.Context;
import android.media.MediaPlayer;

import com.revantis.vosdroid.MidiPlayer;

import java.io.FileNotFoundException;
import java.io.IOException;



public class AndroidMidiPlayer implements MidiPlayer {

	private MediaPlayer mediaPlayer;
	private Context context;
	private boolean looping;
	private float volume;

	public AndroidMidiPlayer(Context context) {
		this.context = context;
		this.mediaPlayer = new MediaPlayer();

		this.looping = false;
		this.volume = 1;
	}

	public void open(String fileName) {

		reset();
		try {
			mediaPlayer.setDataSource(fileName);
			mediaPlayer.prepare();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//TODO: This should probably be replaced with something better.
	//I had to reset the player to avoid error when
	//opening a second midi file.
	private void reset() {
		mediaPlayer.reset();
		mediaPlayer.setLooping(looping);
		setVolume(volume);
	}


	public boolean isLooping() {
		return mediaPlayer.isLooping();
	}

	public void setLooping(boolean loop) {
		mediaPlayer.setLooping(loop);
	}

	public void play() {
		mediaPlayer.start();
	}

	public void pause() {
		mediaPlayer.pause();
	}

	public void stop() {
		mediaPlayer.stop();
	}

	public void release() {
		mediaPlayer.release();
	}

	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	public void setVolume(float volume) {
		mediaPlayer.setVolume(volume, volume);
	}
}