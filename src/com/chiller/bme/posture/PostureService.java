package com.chiller.bme.posture;

import java.text.DecimalFormat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.chiller.bme.posture.db.PostureSQLiteHelper;
import com.chiller.bme.posture.db.SessionDAO;
import com.chiller.bme.posture.db.SessionRecord;
import com.chiller.bme.posture.util.MovingAverage;

public class PostureService extends Service implements SensorListener{
	private float calibrated_angle;
	private SensorManager sm;
	private MovingAverage average;
	private int count;
	private MediaPlayer mp;
	private boolean posture_state;
	private SessionDAO datasource;
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
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
		average = new MovingAverage(15);
		count = 0;
		
		//Instantiate sound player
		mp = MediaPlayer.create(PostureService.this, R.raw.timbale);   
        
		//Instantiate database
		datasource = new SessionDAO(this);
	    datasource.open();
	    

	    datasource.createRecord(String.valueOf(Extras.getFloat("calibrated_angle")) , MainActivity.events[0]);
	    posture_state = true;
	    datasource.createRecord(String.valueOf(Extras.getFloat("calibrated_angle")) , MainActivity.events[2]);
	    
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		sm.unregisterListener(this);
		super.onDestroy();
		mp.release();
		Log.i("PostureService", "Service stopped");
		Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show(); 
		
		datasource.createRecord(String.valueOf(calibrated_angle) , MainActivity.events[3]);
	    
		datasource.close();
	}



	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub
		
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
	



}
