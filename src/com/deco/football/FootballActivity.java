package com.deco.football;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import com.deco.element.BottomBar;
import com.deco.fragment.LivingFragment;
import com.deco.model.BettingModel;
import com.deco.service.BettingService;
import com.deco.service.MatchService;
import com.deco.sql.USER;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class FootballActivity extends Activity {

	private LivingFragment _livingFragment;
	
	private BottomBar _botBar = new BottomBar(this);
	
	// Timer
	private static Timer _livingTimer;
	private static Timer _ComingTimer;
	
	final Handler myHandler = new Handler();
	
	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_living);
		
		// Get Current User
		_botBar.updateUserData();
		_botBar.updateBettingCount();
		_botBar.updateBottomBar();
			
        // Init Timer
		if (_livingTimer == null){
			_livingTimer = new Timer();
			LivingMatchTimer getLivingMatchTimer = new LivingMatchTimer();
			_livingTimer.schedule(getLivingMatchTimer, 0, 10000);
		}

		if (_ComingTimer == null){
			_ComingTimer = new Timer();		
			ComingMatchTimer getComingMatchTimer = new ComingMatchTimer();
			_ComingTimer.schedule(getComingMatchTimer, 0, 60000);
		}
		
		// Update Betting List
		if (_botBar.getUser().size() > 0){
			BettingService svBetting = new BettingService(this);
			BettingWatcher wtcBetting = new BettingWatcher();
			svBetting.addObserver(wtcBetting);
			BettingModel mdlBetting = new BettingModel(this);
			String szUserId = _botBar.getUser().getAsString(USER.id);
			String szBettingId = mdlBetting.getLastBettingIdByUserId(szUserId);
			svBetting.getBettingList(szUserId, szBettingId);
		}		
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 
		_livingFragment = new LivingFragment(this);
		fragmentTransaction.add(R.id.fragmentmain, _livingFragment);
		fragmentTransaction.commit(); 		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		_botBar.updateBottomBar();
		super.onResume();
	}	
	
	@Override
	protected void onPause() {
		super.onPause();
	}	
	

	
	class LivingMatchTimer extends TimerTask {
		 public void run() {
			myHandler.post(getLivingMatch);
		 }
	}
	
	class ComingMatchTimer extends TimerTask {
		 public void run() {
			myHandler.post(getComingMatch);
		 }
	}	
	
	final Runnable getLivingMatch = new Runnable() {
	      public void run() {
	    	  MatchService svMatch = new MatchService(getApplicationContext());
	    	  MatchWatcher wtcMatch = new MatchWatcher();
	    	  svMatch.addObserver(wtcMatch);
	    	  svMatch.getLivingMatch();		
	      }
	};
	
	final Runnable getComingMatch = new Runnable() {
	      public void run() {
	    	  MatchService svMatch = new MatchService(getApplicationContext());
	    	  MatchWatcher wtcMatch = new MatchWatcher();
	    	  svMatch.addObserver(wtcMatch);
	    	  svMatch.getComingMatch();		
	      }
	};		
	
	class MatchWatcher implements Observer { 
		public void update(Observable obj, Object arg) {
			ContentValues result =  (ContentValues)arg;
			if (result.get("result")=="true")
				_livingFragment.updateListView();
		} 
	}
	
	class BettingWatcher implements Observer {
		public void update(Observable obj, Object arg) {
			ContentValues result =  (ContentValues)arg;
			if (result.get("result")=="true"){
				_botBar.updateBettingCount();
				_botBar.updateBottomBar();				
			}			
		} 		
	}
}
