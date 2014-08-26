package com.revantis.vosdroid;

/**
 * Created by ReVanTis on 2014/08/26.
 * code from:https://code.google.com/p/libgdx-users/wiki/MidiPlayerInterface
 */
public interface MidiPlayer {
	public void open(String fileName);
	public boolean isLooping();
	public void setLooping(boolean loop);
	public void play();
	public void pause();
	public void stop();
	public void release();
	public boolean isPlaying();
	public void setVolume(float volume);
}