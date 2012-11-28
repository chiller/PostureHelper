package com.chiller.bme.posture;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chiller.bme.posture.db.SessionDAO;
import com.chiller.bme.posture.db.SessionRecord;
import com.chiller.bme.posture.tasks.AsyncTaskPostStats;
import com.chiller.bme.posture.tasks.AsyncTaskPostStats.UploadVoteCompleteListener;


public class MainActivity extends Activity implements UploadVoteCompleteListener{
	
	public static int count;
	public static float calibrated_angle;
	public static String[] events = { 
		"START",
		"WARN",
		"OK",
		"STOP"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		count = 0;
		calibrated_angle = 0;
		
		setContentView(R.layout.main);

		//Button1
	    Button orderButton = (Button)findViewById(R.id.button1);

	    orderButton.setOnClickListener(new View.OnClickListener() {

	      @Override
	      public void onClick(View view) {
	        Intent intent = new Intent(MainActivity.this, CalibrationActivity.class);
	        startActivity(intent);
	      }

	      
	    
	    });

	    //Button2
	    Button orderButton2 = (Button)findViewById(R.id.button2);

	    orderButton2.setOnClickListener(new View.OnClickListener() {

	      @Override
	      public void onClick(View view) {
	        
	        
	        Intent intent = new Intent(MainActivity.this, PostureService.class);
	        getApplicationContext().stopService(intent);
	        
	        
	        
	       
	        
	        
	      }
	    });
	    
	    //Button3
	    Button syncButton = (Button)findViewById(R.id.button3);

	    syncButton.setOnClickListener(new View.OnClickListener() {

	      @Override
	      public void onClick(View view) {
	        
	    	//Getting unsynced data to sync
	    	
	    	Log.i("PostureService", "Syncing!");
			SessionDAO datasource = new SessionDAO(MainActivity.this);
		    datasource.open();
		    String data = datasource.getUnsyncedJson();
		    datasource.close();
	    	  
	    	AsyncTaskPostStats getImagesTask =
      	    new AsyncTaskPostStats(MainActivity.this, MainActivity.this);
      		getImagesTask.execute(
      	    "http://posturehelper.appspot.com/",data); 
	      
	      }
	    });
	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.mymenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itemPreferences)
		{
			Intent settingsActivity = new Intent(this,PreferencesActivity.class);
	        startActivity(settingsActivity);
		} else if (item.getItemId() == R.id.clearDatabase) {
			Log.i("PostureService", "Clearing database!");
			SessionDAO datasource = new SessionDAO(this);
		    datasource.open();
		    datasource.deleteAllRecords();
		    datasource.close();
			
		} else if (item.getItemId() == R.id.dumpDatabase) {
			
			SessionDAO datasource = new SessionDAO(this);
		    datasource.open();
		    for (SessionRecord r: datasource.getAllRecords()){
		    	Log.i("PostureService",r.toString());
		    	
		    } 
		    datasource.close();
			
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTaskComplete(String aResult) {
		// TODO Auto-generated method stub
		Log.i("PostureService", "AsyncTask Finished: " + aResult);
		SessionDAO datasource = new SessionDAO(this);
	    datasource.open();
	    datasource.markAllRecords();
	    datasource.close();
	    Toast.makeText(MainActivity.this, "Sync Complete!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onError(String aError) {
		// TODO Auto-generated method stub
		Log.i("PostureService", "AsyncTask Finished with Error: "+ aError);
	}

}
