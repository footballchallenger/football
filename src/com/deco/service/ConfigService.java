package com.deco.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Observable;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.deco.config.SERVER;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;



public class ConfigService extends Observable{

	private String _CONFIG_URL = SERVER.SERVICE_URL + "nav=config&ver=";
	
	public ConfigService(Context context) {
	}
	
	public void getConfig(int nVer){
		get(_CONFIG_URL + Integer.toString(nVer));
	}
	
	public void get(String URL){
		new RequestTask().execute(URL);
	}
	
	class RequestTask extends AsyncTask<String, Integer, ContentValues>{
	    @Override
	    protected ContentValues doInBackground(String... params) {
	    	ContentValues result = new ContentValues();
	    	result.put("result", "false");	    	
	    	
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(params[0]));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	        	return result;
	        } 
	        catch (IOException e) {
	        	return result;
	        }
	        
	        result.put("result", "true");
	        result.put("data", responseString);
	        return result;
	    }
	    
	    @Override
	    protected void onPostExecute(ContentValues result) {
	        super.onPostExecute(result);
			setChanged();
			notifyObservers(result);
	    }	   
	}
}
