package com.andelahackathon.vicemapper;

import java.util.HashMap;

import org.json.JSONArray;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class ReportScreen extends Activity {
	private Switch useCurrentLocation;
	private boolean isUsingLocation;
	private Button reportBribe;
	private JSONArray reports;
	private Firebase fb;
	private Button reportRape;
	private Button reportTheft;
	private Button reportFight;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        
        fb = new Firebase("https://vicemapper.firebaseio.com");
        
        setContentView(R.layout.activity_report_screen);
        
        loadWidgets();
    }
	
	private void loadWidgets() {
		useCurrentLocation = (Switch) findViewById(R.id.currentLocation);
		
		reportBribe = (Button) findViewById(R.id.reportBribery);
		reportRape = (Button) findViewById(R.id.reportRape);
		reportTheft = (Button) findViewById(R.id.reportTheft);
		reportFight = (Button) findViewById(R.id.reportFight);
		
		// Set on click listeners
		reportBribe.setOnClickListener(reportCaseListener("bribe"));
		reportRape.setOnClickListener(reportCaseListener("rape"));
		reportFight.setOnClickListener(reportCaseListener("fight"));
		reportTheft.setOnClickListener(reportCaseListener("theft"));
		
	}
	public OnClickListener reportCaseListener(final String type) {
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(v.getContext(), "Posting new Bribery Act", Toast.LENGTH_SHORT).show();
				Long currentTimeStamp = System.currentTimeMillis()/1000;
				// Get Device Location if the switch is turned on
				
				//SAVE DATA
				HashMap<String, Object> report = new HashMap<String, Object>();
				HashMap<String, Float> location = new HashMap<String, Float>();
				report.put("location", location);
				report.put("timestamp", currentTimeStamp);
				report.put("vice", type);
				fb.child("vice").push().setValue(report);
				//LISTEN FOR REALTIME CHANGES
				fb.addValueEventListener(new ValueEventListener() {
				    @Override
				    public void onDataChange(DataSnapshot snap) {
				    	Vars.setSnapRecords(snap);
				    }
				    @Override
				    public void onCancelled(FirebaseError error) { }
				});
			}
		};
	}
}
