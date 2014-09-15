package com.deco.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;

import com.deco.model.TeamModel;
import com.deco.sql.TEAM;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

public class TeamImgService extends Observable{
	private static Context _context;
	
	public TeamImgService(Context context) {
		if (context != null)
			_context = context;
	}
	
	public void getImageFromId(String szTeamID){
		new RequestTask().execute(szTeamID);
	}
	
	class RequestTask extends AsyncTask<String, String, ContentValues>{
	    @Override
	    protected ContentValues doInBackground(String... params) {
	    	ContentValues ret = new ContentValues();
	    	String szTeamID = params[0];
	    	String szFilePath = "team" + szTeamID + ".png";
	        InputStream inputStream = null;
            OutputStream outputStream = null;	    	
	    	ret.put("teamid", szTeamID);
	    	
	    	File file = _context.getFileStreamPath(szFilePath);
	    	if(file.exists()){
		        ret.put("result", "true");
		        return ret;	    		
	    	} 	
	    	
	        TeamModel mdlTeam = new TeamModel(_context);
	        ArrayList<String> lsSelect = new ArrayList<String>();
	        lsSelect.add(TEAM.avatar);
	        ContentValues pTeamInfo = mdlTeam.getTeamById(szTeamID, lsSelect);
	        String szAvatarUrl = pTeamInfo.getAsString(TEAM.avatar); 
	        if (szAvatarUrl == ""){
	        	ret.put("result", "false");
	        	return ret;
	        }
	        
	        try {
	            URL url = new URL(szAvatarUrl);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoInput(true);
	            connection.connect();
	            inputStream = connection.getInputStream();
	            outputStream = _context.openFileOutput(szFilePath, Context.MODE_PRIVATE);
	    		int read = 0;
	    		byte[] bytes = new byte[1024];
	     
	    		while ((read = inputStream.read(bytes)) != -1) {
	    			outputStream.write(bytes, 0, read);
	    		}	            

	        } catch (IOException e) {;
	            return null;
	    	} finally {
	    		if (inputStream != null) {
	    			try {
	    				inputStream.close();
	    			} catch (IOException e) {
	    			}
	    		}
	    		if (outputStream != null) {
	    			try {
	    				outputStream.close();
	    			} catch (IOException e) {
	    			}
	    		}
	    	}
	        
	        ret.put("result", "true");
	        return ret;
	    }
   
	    
	    @Override
	    protected void onPostExecute(ContentValues result) {
	        super.onPostExecute(result);
			setChanged();
			notifyObservers(result);	        
	    }	   
	}
}
