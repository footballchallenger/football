package com.deco.fragment;

import java.util.ArrayList;

import com.deco.adapter.MatchAdapter;
import com.deco.football.R;
import com.deco.model.MatchModel;
import com.deco.sql.MATCH;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class LivingFragment extends Fragment{
	
	Context		 _context;
	MatchAdapter _adapter;
	
	public LivingFragment(Context context)
	{
		_context = context;
	}
	
    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// ListView Match 
		ArrayList<ContentValues> lsMatch = new ArrayList<ContentValues>();
		_adapter = new MatchAdapter(_context, 0, lsMatch);
		
		View view = inflater.inflate(R.layout.fragment_living, container, false);
		
		ListView listView = (ListView)view.findViewById(R.id.matchlist);
		listView.setAdapter(_adapter); 
		updateListView();    	
    	
        return view; 
    } 
    
	public void updateListView(){
		MatchModel mdlMatch = new MatchModel(_context);
		
		ArrayList<ContentValues>lsMatch = mdlMatch.getLiving();
		int nLeague = 0;
		for (int i=0; i<lsMatch.size(); i++){
			int tmp = lsMatch.get(i).getAsInteger(MATCH.league_id);
			if (nLeague != tmp){
				nLeague = tmp;
				ContentValues item = new ContentValues();
				item.put(MATCH.league_id, Integer.toString(nLeague));
				item.put(MATCH.id, "");
				lsMatch.add(i, item);
				i++;
			}
		}
		
		if (_adapter.getCount() > lsMatch.size())
			_adapter.clear();
		
		for (int i=_adapter.getCount(); i<lsMatch.size(); i++)
			_adapter.add(null);
		
		_adapter.lsMatch = lsMatch;
		_adapter.notifyDataSetChanged();
	}	    
}
