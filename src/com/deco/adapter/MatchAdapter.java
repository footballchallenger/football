package com.deco.adapter;


import com.deco.football.FootballActivity;
import com.deco.football.R;
import com.deco.fragment.MatchFragment;
import com.deco.model.LeagueModel;
import com.deco.sql.MATCH;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

public class MatchAdapter extends ArrayAdapter<ContentValues> implements OnClickListener{
	public ArrayList<ContentValues> lsMatch;
	private LayoutInflater mInflater;
	Context _context;
	
	
	public MatchAdapter(Context context, int textViewResourceId, ArrayList<ContentValues> objects) {
		super(context, textViewResourceId, objects);
		_context = context;
		this.lsMatch = objects;
		mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View v, ViewGroup parent){ 
		if (lsMatch.size() < 2){
			return mInflater.inflate(R.layout.living_league_item, null);
		}
		
		ContentValues matchinfo = lsMatch.get(position);
		if (matchinfo != null) {
			String szMatchId = (String)matchinfo.get(MATCH.id);
			if (szMatchId != ""){
		    	// Get Data
		    	String szFirstTime = (String)matchinfo.get(MATCH.first_time);
		    	String szHomeGoals = "";
		    	String szAwayGoals = "";
		    	int nMatchStatus = Integer.parseInt((String)matchinfo.get(MATCH.status));
		    	if (nMatchStatus != 17){
			    	szHomeGoals = (String)matchinfo.get(MATCH.home_goals);
			    	szAwayGoals = (String)matchinfo.get(MATCH.away_goals);		    		
		    	}
		    	String szDate = "";
		    	String szTime = ""; 

		        try {
		        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		        	Date date = df.parse(szFirstTime);
		        	df = new SimpleDateFormat("MM-dd");
		        	szDate = df.format(date);
		        	df = new SimpleDateFormat("HH:mm");
		        	szTime = df.format(date);		
		        	
		        	//df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		            //Date oldDate = df.parse(szFirstTime);
		            //Date cDate = new Date(); 
		            //Long timeDiff = cDate.getTime() - oldDate.getTime();
		            //int day = (int) TimeUnit.MILLISECONDS.toDays(timeDiff);
		            //int hh = (int) (TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(day));
		            //int mm = (int) (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
		            //int a = (int) TimeUnit.MILLISECONDS.toMinutes(timeDiff);
		            //int b = 1;
		            //a = b;
		            //b = a;
		            
		        } catch (ParseException e) {
		        }
		        
		    	// Get Team From DB
		    	String szHomeName = (String)matchinfo.get(MATCH.home_name);
		    	String szAwayName = (String)matchinfo.get(MATCH.away_name);
		    	
				// Set Holder
		    	MatchHolder matchholder = null;
		    	if (v != null && v.getId() != 0)
		    		v = null;
		    	
		    	if (v == null){
		    		v = mInflater.inflate(R.layout.living_match_item, null);
					matchholder = new MatchHolder();
					matchholder.row = (TableLayout)v.findViewById(R.id.listrow);
					matchholder.time = (TextView)v.findViewById(R.id.time);
					matchholder.date = (TextView)v.findViewById(R.id.date);
					matchholder.livingimg = (TextView)v.findViewById(R.id.liveimg);
					matchholder.home = (TextView)v.findViewById(R.id.home);
					matchholder.away = (TextView)v.findViewById(R.id.away);
					matchholder.homegoals = (TextView)v.findViewById(R.id.homegoals);
					matchholder.awaygoals = (TextView)v.findViewById(R.id.awaygoals);
					v.setTag(matchholder);
					v.setOnClickListener(this);
					v.setId(0);
		    	}
		    	else{
		    		matchholder = (MatchHolder) v.getTag();
		    	}
		    	
		    	matchholder.match_id = matchinfo.getAsInteger(MATCH.id);
				matchholder.time.setText(szTime);
				matchholder.date.setText(szDate);
				matchholder.home.setText(szHomeName);
				matchholder.away.setText(szAwayName);
				matchholder.homegoals.setText(szHomeGoals);
				matchholder.awaygoals.setText(szAwayGoals);
				
				if (nMatchStatus == 4){
					matchholder.date.setText("");
					matchholder.livingimg.setVisibility(View.VISIBLE);
					ValueAnimator colorAnim = ObjectAnimator.ofInt(matchholder.livingimg, "backgroundColor", Color.RED, Color.BLUE);
			        colorAnim.setDuration(1000);
			        colorAnim.setEvaluator(new ArgbEvaluator());
			        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
			        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
			        colorAnim.start();
				}
				else{
					matchholder.livingimg.clearAnimation();
					matchholder.livingimg.setVisibility(View.INVISIBLE);
				}
		    }
			else
			{
				LeaguehHolder leagueholder = null;
		    	if (v != null && v.getId() != 1)
		    		v = null;		
		    	
		    	if (v == null){				
					v = mInflater.inflate(R.layout.living_league_item, null);
					leagueholder = new LeaguehHolder();
					leagueholder.league_name = (TextView)v.findViewById(R.id.league_name);
					v.setTag(leagueholder);
					v.setId(1);
		    	}
		    	else{
		    		leagueholder = (LeaguehHolder) v.getTag();
		    	}
		    	
		    	String szLeagueId = (String)matchinfo.get("league_id");
		    	LeagueModel mdlLeague = new LeagueModel(_context);
		    	String szLeagueName = mdlLeague.getLeagueById(szLeagueId);		    	
		    	leagueholder.league_name.setText(szLeagueName);
			}
		}			
		return v;
	}
	
	@Override
	public void onClick(View v) {
		if (v == null)
			return;
		
		if (v.getId() == 1)
			return;
		
		MatchHolder holder = (MatchHolder)v.getTag();
		
		MatchFragment matchFragment = new MatchFragment(_context);
		Bundle args = new Bundle();
		args.putString("matchid", Integer.toString(holder.match_id));
		matchFragment.setArguments(args);
		
		Activity ac = (Activity)_context;
		FragmentTransaction transaction =  ac.getFragmentManager().beginTransaction(); 
		transaction.replace(R.id.fragmentmain, matchFragment);
		transaction.addToBackStack(null); 
		 
		// Commit the transaction 
		transaction.commit(); 		
	}    
	
    static class MatchHolder{
    	int					match_id;
    	public TableLayout 	row;
    	public TextView 	time;
    	public TextView 	date;
    	public TextView		livingimg;
    	public TextView 	home;
    	public TextView 	away;
    	public TextView 	homegoals;
    	public TextView 	awaygoals;
    }
    
    static class LeaguehHolder{
    	public TextView league_name;
    }    
}