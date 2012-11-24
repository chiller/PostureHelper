package com.chiller.bme.posture;



import junit.framework.Test;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity{
	
	public static int count;
	public static float calibrated_angle;
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
		}
		return super.onOptionsItemSelected(item);
	}

}
