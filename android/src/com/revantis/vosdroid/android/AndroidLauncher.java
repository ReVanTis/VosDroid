package com.revantis.vosdroid.android;

import android.os.Bundle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.revantis.vosdroid.MidiPlayer;
import com.revantis.vosdroid.VosDroidGame;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		MidiPlayer midiPlayer = new AndroidMidiPlayer(getApplicationContext());
		String vosPath=null;
		try {
			vosPath = getIntent().getData().getPath();
		}
		catch (Exception e)
		{
//			vosPath= Gdx.files.internal("vos/Canon in D.vos").path();
			//vosPath="/sdcard/vos/5cm1.vos";
		}
		initialize(new VosDroidGame(midiPlayer, vosPath), config);

	}
}
