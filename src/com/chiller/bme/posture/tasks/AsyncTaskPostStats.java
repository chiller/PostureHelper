package com.chiller.bme.posture.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class AsyncTaskPostStats extends AsyncTask<String, Void, String> {

	public interface UploadVoteCompleteListener {
		   public void onTaskComplete(String aResult);
		   public void onError(String aError);
	}
	
	private Context context = null;
	private ProgressDialog progressDialog = null;
	private UploadVoteCompleteListener listener;
	private String error = null;

	public AsyncTaskPostStats(Context context, UploadVoteCompleteListener aListener) {
	    this.context = context; 
	    listener = aListener;
	}
	
	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(this.context);
	    progressDialog.setMessage("Kérem várjon...");
	    progressDialog.show();
	}
	
	@Override
	protected String doInBackground(String... params) {
		InputStream is = null;
		String result = null;
		try {
			URL url = new URL(params[0]);
	    	URLConnection uc = url.openConnection();
	    	is = uc.getInputStream();
	        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
	        result = in.readLine();
		} catch (Exception e) {
			error = e.getMessage();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {}
			}
		}
		
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
	    progressDialog.dismiss();
	    if (error != null) {
	    	listener.onError(error);
	    }
	    else {
	    	listener.onTaskComplete(result);
	    }
	} 
}