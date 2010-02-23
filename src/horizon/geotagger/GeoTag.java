package horizon.geotagger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONStringer;

import horizon.android.logging.Logger;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GeoTag 
extends Activity 
{
	private static final Logger logger = Logger.getLogger(GeoTag.class);
	
	private PrintWriter logOut;
	
	@Override
	protected void onDestroy() 
    {
		super.onDestroy();
		logger.verbose("GeoTag.onDestroy()");
		logOut.close();
	}

	@Override
	protected void onPause() 
	{
		super.onPause();
		logger.verbose("GeoTag.onPause()");
	}

	@Override
	protected void onRestart() 
	{
		super.onRestart();
		logger.verbose("GeoTag.onRestart()");
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		logger.verbose("GeoTag.onResume()");
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
		logger.verbose("GeoTag.onStart()");
		File d = new File("/sdcard/geotagger");
		if(!d.exists())
			d.mkdir();
		try
		{
			logOut = new PrintWriter(new BufferedWriter(
					new FileWriter("/sdcard/geotagger/geotagger.log", true)));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		logger.verbose("GeoTag.onStop()");
	}

	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geotag);
        logger.verbose("Geotag.onCreate()");
        
        final Button button01 = (Button)findViewById(R.id.Button01);
        button01.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	Intent i = new Intent(GeoTag.this, TextNote.class);
            	startActivityForResult(i, 10);
            	logger.debug("Button01 pressed");
            }
        });
        
        final Button button02 = (Button)findViewById(R.id.Button02);
        button02.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	Intent i = new Intent(GeoTag.this, VoiceNote.class);
            	startActivityForResult(i, 20);
            	logger.debug("Button02 pressed");
            }
        });
        
        final Button button03 = (Button)findViewById(R.id.Button03);
        button03.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	logger.debug("Button03 pressed");
            }
        });
        
        final Button button04 = (Button)findViewById(R.id.Button04);
        button04.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	logger.debug("Button04 pressed");
            }
        });        
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode != RESULT_OK)
			return;
		if(requestCode == 10)
		{
			String note = data.getCharSequenceExtra("note").toString();
			Location location = (Location)data.getParcelableExtra("location");
			taglog(System.currentTimeMillis(), location, "TEXT=" + note.replace("\n", "\\n").replace("\r", "\\r"));
		}
		else if(requestCode == 20)
		{
			String filename = data.getCharSequenceExtra("fileName").toString();
			Location location = (Location)data.getParcelableExtra("location");
			taglog(System.currentTimeMillis(), location, "VOICE=" + filename);
		}
		else if(requestCode == 30)
		{
		
		}
		else if(requestCode == 40)
		{
			
		}		
	}
	
	private synchronized void taglog(long timestamp, Location location, String data)
	{
		logOut.println(timestamp + ":"
				+ serializeLocation(location) + ":" + data);
		logOut.flush();
	}
	
	private String serializeLocation(Location location)
	{
		JSONStringer data = new JSONStringer();
		try
		{
			data.object();
			data.key("accuracy");
			if(location.hasAccuracy())
				data.value(location.getAccuracy());
			else
				data.value(null);
			
			data.key("altitude");
			if(location.hasAltitude())
				data.value(location.getAltitude());
			else
				data.value(null);
			
			data.key("bearing");
			if(location.hasBearing())
				data.value(location.getBearing());
			else
				data.value(null);
			
			data.key("latitude");
			data.value(location.getLatitude());
			
			data.key("longitude");
			data.value(location.getLongitude());
			
			data.key("provider");
			data.value(location.getProvider());
			
			data.key("speed");
			if(location.hasSpeed())
				data.value(location.getSpeed());
			else
				data.value(null);
			
			Bundle extras = location.getExtras();
			data.key("extras");
			if(extras != null)
				data.value(extras.toString());
			else
				data.value(null);
					
			data.endObject();
		}
		catch(JSONException e)
		{
			throw new RuntimeException(e);
		}
		return data.toString();
	}
}
