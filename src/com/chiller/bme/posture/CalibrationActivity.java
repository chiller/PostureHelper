package com.chiller.bme.posture;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.chiller.bme.posture.util.MovingAverage;


public class CalibrationActivity extends Activity implements SensorListener {
    SensorManager sm = null;
    TextView xViewA = null;
    TextView yViewA = null;
    TextView zViewA = null;
    TextView xViewO = null;
    TextView yViewO = null;
    TextView zViewO = null;
    MovingAverage average;
    private long lastchange;
    private float current_calibrated_angle;
	private TextView yViewOavg;
    private boolean started;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // get reference to SensorManager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.orientation);
        xViewA = (TextView) findViewById(R.id.ybox);
        yViewA = (TextView) findViewById(R.id.ybox);
        zViewA = (TextView) findViewById(R.id.zbox);
        xViewO = (TextView) findViewById(R.id.xboxo);
        yViewO = (TextView) findViewById(R.id.yboxo);
        zViewO = (TextView) findViewById(R.id.zboxo);
        yViewOavg = (TextView) findViewById(R.id.yboxoavg);
        average = new MovingAverage(15);
        lastchange = System.currentTimeMillis();
        current_calibrated_angle = 0;
        started = false;
    }
    public void onSensorChanged(int sensor, float[] values) {
        synchronized (this) {
        	MainActivity.count ++;
        	//Log.i("Orientation", String.valueOf(MainActivity.count));
        	
            if (sensor == SensorManager.SENSOR_ORIENTATION) {
            	average.push(values[1]);
                xViewO.setText("Orientation X: " + values[0]);
                yViewO.setText("Orientation Y: " + values[1]);
                yViewOavg.setText("Orientation moving average Y: " + average.average());
                DecimalFormat twoDForm = new DecimalFormat("#");
                
                float new_value = Float.valueOf(twoDForm.format(average.average()));
                if (Math.abs(new_value-current_calibrated_angle) > 2 ){
                	lastchange = System.currentTimeMillis();
                	current_calibrated_angle = new_value;
                } 
                if (System.currentTimeMillis() - lastchange > 4000){
                	Log.i("Orientation", "Calibrated");
      	    	  
        	        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        	         
        	        v.vibrate(300); 
        	        
        	        if (!started){
        	        
        	        	Intent serviceintent = new Intent(getApplicationContext(), PostureService.class);
        	        	serviceintent.putExtra("calibrated_angle", current_calibrated_angle);
        	        	this.getApplicationContext().startService(serviceintent);
        	        	started = true;
        	        }
        	        finish();
        	        
                }
                
                
                
                zViewO.setText("Current calibrated angle: " + String.valueOf(current_calibrated_angle));
                
                
                
            }
            if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
                xViewA.setText("Accel X: " + values[0]);
                yViewA.setText("Accel Y: " + values[1]);
                zViewA.setText("Accel Z: " + values[2]);
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
                SensorManager.SENSOR_ORIENTATION |SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    @Override
    protected void onStop() {
        // unregister listener
        sm.unregisterListener(this);
        super.onStop();
    }    
}