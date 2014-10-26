package com.andelahackathon.vicemapper;

import java.util.HashMap;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ReportScreen extends Activity implements LocationListener {
	private Button reportBribe;
	private Button reportRape;
	private Button reportTheft;
	private Button reportFight;
	private Boolean isGPSEnabled, isNetworkEnabled;
	private Location location;
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
	        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	        // getting network status
	        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	        if (!isGPSEnabled && !isNetworkEnabled) {
	            // no network provider is enabled
	        	Toast.makeText(this, "Network Connection Problem", Toast.LENGTH_SHORT).show();
	        } else {
	            // this.canGetLocation = true;
	            if (isNetworkEnabled) {
	                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 10, this);
	                if (locationManager != null) {
	                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                    if (location != null) {
	                    	Vars.setCurrentLocation(location.getLongitude(), location.getLatitude());
	                    }
	                }
	            }
	            // if GPS Enabled get lat/long using GPS Services
	            if (isGPSEnabled) {
	                if (location == null) {
	                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 10, this);
	                    if (locationManager != null) {
	                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                        if (location != null) {
	                        	Vars.setCurrentLocation(location.getLongitude(), location.getLatitude());
	                        }
	                    }
	                }
	            }
	        }
        }
        
        setContentView(R.layout.activity_report_screen);
        
        loadWidgets();
    }
	
	private Dialog loading= null;
	
	public void isLoading(final boolean b)
    {
    	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				try
				{
			    	if(loading==null)
			    	{
			    		loading = new Dialog(getApplicationContext());
			    		loading.setTitle("Working...");
			    		loading.setCancelable(false);
			    		loading.requestWindowFeature(Window.FEATURE_NO_TITLE);
			    		loading.setContentView(R.layout.loading_dialog);
			    	}
			    
			    	if(loading!=null)
			    	{
			    		try
			    		{
			    			if(b&&!loading.isShowing())loading.show();
			    			else loading.cancel();
			    		}
			    		catch(Exception e0){}
			    	}
		    	}
		    	catch(Exception eo){}
			}
		});
    }

	private void loadWidgets() {        
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
				isLoading(true);
				Toast.makeText(v.getContext(), "Posting Vice", Toast.LENGTH_SHORT).show();
				Long currentTimeStamp = System.currentTimeMillis()/1000;
				// Get Device Location if the switch is turned on
				
				//SAVE DATA
				HashMap<String, Object> report = new HashMap<String, Object>();
				HashMap<String, Double> location = Vars.getLocation();
				report.put("location", location);
				report.put("timestamp", currentTimeStamp);
				report.put("vice", type);
<<<<<<< HEAD
				fb.child("vice").push().setValue(report);
				
				//LISTEN FOR REALTIME CHANGES
				fb.addValueEventListener(new ValueEventListener() {
				    @Override
				    public void onDataChange(DataSnapshot snap) {
				    	Vars.setSnapRecords(snap);
				    	isLoading(false);
				    }
				    @Override
				    public void onCancelled(FirebaseError error) { }
				});
=======
				Vars.dbcon().child("vice").push().setValue(report);
				isLoading(false);
				finish();
				
>>>>>>> 26be0f870ea22e9e1a79005db27d8860be88504f
			}
		};
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Vars.setCurrentLocation(location.getLongitude(), location.getLatitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}
