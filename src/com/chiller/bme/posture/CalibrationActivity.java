package com.chiller.bme.posture;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chiller.bme.posture.util.MovingAverage;


@SuppressWarnings("deprecation")
public class CalibrationActivity extends Activity implements SensorListener {
    //Time needed to lock calibration
	private int calibrationclockmilis;
	//Number of sensor samples the average is calculated o
	private static final int SAMPLECOUNT = 10;
	
	SensorManager sm = null;
    TextView yViewO = null;
    TextView zViewO = null;
    private TextView yViewOavg;
    
    //Helper class to calculate average of last x samples
    MovingAverage average;
    
    //Time since last change of calibrated angle
    private long lastchange;
    
    //Holds current calibrated angle, 
    //if enough time passes this is the value the service is called with
    private float current_calibrated_angle;
    
	

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get sample count from shared preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String range = prefs.getString("averageRange", "");
        int samplecount;
        if (range=="") {samplecount = SAMPLECOUNT;}
        else {  samplecount = Integer.valueOf(range);}
        
        //Get calibration time from shared prefs
        String caltime = prefs.getString("calibrationTime", "");
        if (caltime=="") {calibrationclockmilis = 4000;}
        else {  calibrationclockmilis = Integer.valueOf(caltime)*1000;}
        
        Log.i("PostureHelper","Sensor Sample Count set to "+range);
        Log.i("PostureHelper","Calibration time set to "+calibrationclockmilis);
        
        // get reference to SensorManager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.orientation);
        yViewO = (TextView) findViewById(R.id.yboxo);
        zViewO = (TextView) findViewById(R.id.zboxo);
        yViewOavg = (TextView) findViewById(R.id.yboxoavg);
        average = new MovingAverage(samplecount);
        lastchange = System.currentTimeMillis();
        current_calibrated_angle = 0;
        MainActivity.started = false;
    }
    
    
    public void onSensorChanged(int sensor, float[] values) {
        synchronized (this) {;
        	//Log.i("Orientation", String.valueOf(MainActivity.count));
        	
            if (sensor == SensorManager.SENSOR_ORIENTATION) {
            	
            	average.push(values[1]);
                yViewO.setText("Orientation Y: " + values[1]);
                yViewOavg.setText("Orientation moving average Y: " + average.average());
                
                DecimalFormat twoDForm = new DecimalFormat("#");
                
                float new_value = Float.valueOf(twoDForm.format(average.average()));
                
                //If current calibrated value changes by more than 2
                if (Math.abs(new_value-current_calibrated_angle) > 2 ){
                	lastchange = System.currentTimeMillis();
                	current_calibrated_angle = new_value;
                } 
                if (System.currentTimeMillis() - lastchange > calibrationclockmilis){
                	
                	Log.d("Orientation", "Calibrated");
      	    	  
        	        //First vibration is a notification of successful calibration
                	Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        	        v.vibrate(300); 
        	        
        	        if (!MainActivity.started){
        	        
        	        	Intent serviceintent = new Intent(getApplicationContext(), PostureService.class);
        	        	serviceintent.putExtra("calibrated_angle", current_calibrated_angle);
        	        	this.getApplicationContext().startService(serviceintent);
        	        	MainActivity.started = true;
        	        	
        	        }
        	        finish();
        	        
        	        
                }
                 
                zViewO.setText("Current calibrated angle: " + String.valueOf(current_calibrated_angle));
                
            }
           
        }
    }
    
    public void onAccuracyChanged(int sensor, int accuracy) {
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and accelerometer sensors
        sm.registerListener(this, 
                SensorManager.SENSOR_ORIENTATION ,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onStop() {
        // unregister listener
        sm.unregisterListener(this);
        super.onStop();
    }    
}