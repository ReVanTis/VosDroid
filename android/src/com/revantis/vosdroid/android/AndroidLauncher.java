package com.revantis.vosdroid.android;

import android.os.Bundle;

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
		initialize(new VosDroidGame(midiPlayer), config);
	}
}
