package com.chiller.bme.posture;

import java.text.DecimalFormat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.chiller.bme.posture.db.SessionDAO;
import com.chiller.bme.posture.util.MovingAverage;

@SuppressWarnings("deprecation")
public class PostureService extends Service implements SensorListener{
	
	private float calibrated_angle;
	private SensorManager sm;
	
	//A MovingAverage instance is a queue that returns the average of latest samples
	private MovingAverage average;
	//For alert sound
	private MediaPlayer mp;
	private boolean posture_state;
	private SessionDAO datasource;
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		super.onStartCommand(intent, flags, startId);
		
		
		//Get data from calibration activity
		Bundle Extras = intent.getExtras();
		Log.i("PostureService", String.valueOf(Extras.getFloat("calibrated_angle")));
		calibrated_angle = Extras.getFloat("calibrated_angle");
		Log.i("PostureService", "Service started");
	    
		//Register for sensor notifications
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.registerListener(this, 
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_NORMAL);
		
		//Instantiate averaging algorithm
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String range = prefs.getString("averageRange", "");
		average = new MovingAverage(Integer.valueOf(range));
		//Instantiate sound player
		mp = MediaPlayer.create(PostureService.this, R.raw.timbale);   
        
		//Instantiate database
		datasource = new SessionDAO(this);
	    datasource.open();
	    //Start session, record "START" event then "OK" event
	    datasource.createRecord(String.valueOf(Extras.getFloat("calibrated_angle")) , MainActivity.events[0]);
	    posture_state = true;
	    datasource.createRecord(String.valueOf(Extras.getFloat("calibrated_angle")) , MainActivity.events[2]);
	    
		return START_STICKY;
	}


	@Override
	public void onDestroy() {
		
		sm.unregisterListener(this);
		super.onDestroy();
		mp.release();
		Log.i("PostureService", "Service stopped");
		Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show(); 
		//Write STOP event to database
		datasource.createRecord(String.valueOf(calibrated_angle) , MainActivity.events[3]);    
		datasource.close();
	}



	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {
		synchronized (this) {

        	//Log.i("Orientation", String.valueOf(MainActivity.count));
        	
            if (sensor == SensorManager.SENSOR_ORIENTATION) {
            	
            	average.push(values[1]);
                
                DecimalFormat twoDForm = new DecimalFormat("#");
                
                float new_value = Float.valueOf(twoDForm.format(average.average()));
                if (Math.abs(new_value - calibrated_angle) > 5 ){
                	Log.i("PostureService", "Sit up straight");
        	    	  
        	        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        	         
        	        v.vibrate(300); 
        	        mp.start();
        	        
        	        if (posture_state==true) {
        	        	
        	        	datasource.createRecord(String.valueOf(values[1]) , MainActivity.events[1]);
        	    	    posture_state = false;
        	        }
                } else {
                	if (posture_state==false) {
                		
                		datasource.createRecord(String.valueOf(values[1]) , MainActivity.events[2]);
                	    posture_state = true;
                	}
                	
                }
                
                
      
        }
            
            
	}
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	



}
