package com.revantis.vosdroid;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import java.io.File;
import java.io.FileInputStream;



public class VosDroidMain extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vos_droid_main);
	    Button btn =(Button) findViewById(R.id.button);
	    btn.setOnClickListener(new View.OnClickListener()
	    {
		    @Override
		    public void onClick(View view)
		    {
			    try {
				    File file = new File(Environment.getExternalStorageDirectory()+"/Canon in D.vos");
				    File midiFileToWrite = new File(Environment.getExternalStorageDirectory()+"/done1.mid");
				    VosParser vosp=new VosParser(new FileInputStream(file));
				    Log.i("init","sucess");
				    vosp.Parse();
				    vosp.SaveMidiFile(midiFileToWrite);

			    }
			    catch (Exception e)
			    {
				    Log.e("init","exception occur"+e.getMessage());
			    }
		    }
	    });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vos_droid_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
