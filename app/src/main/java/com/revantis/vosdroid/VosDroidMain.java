package com.revantis.vosdroid;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;


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
					if(!getFilesDir().exists())
					{

					}
				    File file = new File(Environment.getExternalStorageDirectory()+"/test.vos");
				    VosParser vosp=new VosParser(new FileInputStream(file));
				    Log.i("init","sucess");
				    vosp.Parse();
				    Log.i("parsed","info:");
				    Log.i("filesize:",vosp.length+" bytes");
				    Iterator<VosSegment> it=vosp.segments.iterator();
				    Log.i("test1","test1");
				    int i=0;
				    while(it.hasNext())
				    {
					    Log.i("test2","test2");
						VosSegment vs=(VosSegment)it.next();
						Log.i("segment"+i+":",vs.getname()+" "+vs.getaddr());
					    i++;
				    }
			    }
			    catch (Exception e)
			    {
				    Log.e("init","exception occur");
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
