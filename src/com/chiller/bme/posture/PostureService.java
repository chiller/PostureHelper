package com.chiller.bme.posture;

import java.text.DecimalFormat;

import com.chiller.bme.posture.util.MovingAverage;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class PostureService extends Service implements SensorListener{
	private float calibrated_angle;
	private SensorManager sm;
	private MovingAverage average;
	private int count;
	private MediaPlayer mp;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		super.onStartCommand(intent, flags, startId);
		
		Bundle Extras = intent.getExtras();
		Log.i("PostureService", String.valueOf(Extras.getFloat("calibrated_angle")));
		calibrated_angle = Extras.getFloat("calibrated_angle");
		Log.i("PostureService", "Service started");
	    
		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.registerListener(this, 
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_NORMAL);
		
		
		average = new MovingAverage(15);
		count = 0;
		
		
		mp = MediaPlayer.create(PostureService.this, R.raw.timbale);   
        
		
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
                } 
                
                
      
        }
	}
	}
	



}
