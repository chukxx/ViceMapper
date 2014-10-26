package com.andelahackathon.vicemapper;

import java.util.HashMap;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ReportScreen extends FragmentActivity implements LocationListener {
	private Button reportBribe;
	private Button reportRape;
	private Button reportTheft;
	private Button reportFight;
	private Boolean isGPSEnabled, isNetworkEnabled;
	private Location location;
	protected LocationManager locationManager;
	protected LocationListener locationListener;
	
	private CameraUpdate cameraUpdate = null;
	private GoogleMap map = null;
	private boolean isAutoLocation = false;
	private Marker locationMarker = null;
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
	                    	setMapLocation(location);
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
	                        	setMapLocation(location);
	                        }
	                    }
	                }
	            }
	        }
        }
        
        setContentView(R.layout.activity_report_screen);
        
        
        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
	 	//map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
	 	map.getUiSettings().setCompassEnabled(true);
	 	map.getUiSettings().setTiltGesturesEnabled(true);
	 	map.getUiSettings().setZoomGesturesEnabled(true);

	 	map.setBuildingsEnabled(true);
	 	map.getUiSettings().setRotateGesturesEnabled(true);
	 	map.setMyLocationEnabled(true);
	 	
	 	map.getUiSettings().setMyLocationButtonEnabled(true);
	 	locationMarker = map.addMarker(new MarkerOptions().position(new LatLng(6.45,3.44)).title("Crime Location").snippet("Select the location, where crime was detected"));
	 	cameraUpdate = CameraUpdateFactory.newLatLngZoom(locationMarker.getPosition(), 15f);
	 	map.animateCamera(cameraUpdate);
	 	
	 	map.setOnMapClickListener(new OnMapClickListener() {
		
			@Override
			public void onMapClick(LatLng arg0) {
								
				locationMarker.setPosition(arg0);
				cameraUpdate = CameraUpdateFactory.newLatLngZoom(arg0, 15f);
				map.animateCamera(cameraUpdate);
				Vars.setCurrentLocation(arg0.longitude, arg0.latitude);
			}
		});

        loadWidgets();
    }
	
	private void setMapLocation(Location _location)
	{
		if(!isAutoLocation)
		{
			if(locationMarker != null)
			{
				locationMarker.setPosition(new LatLng(_location.getLatitude(), _location.getLongitude()));
				cameraUpdate = CameraUpdateFactory.newLatLngZoom(locationMarker.getPosition(), 15f);
				map.animateCamera(cameraUpdate);
				isAutoLocation = true;
			}
		}
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

				Vars.dbcon().child("vice").push().setValue(report);
				isLoading(false);
				finish();
				

			}
		};
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		Vars.setCurrentLocation(location.getLongitude(), location.getLatitude());
		setMapLocation(location);
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
