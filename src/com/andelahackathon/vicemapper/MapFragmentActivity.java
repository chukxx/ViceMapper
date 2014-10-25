package com.andelahackathon.vicemapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MapFragmentActivity extends FragmentActivity {

	public  MapView mapView = null;
	public  CameraUpdate cameraUpdate = null;
	public  GoogleMap map = null;
//	private View view = null;
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		 View rootView = inflater.inflate(R.layout.activity_map_fragment, container, false);
//		
//	        view = rootView;
//	        return rootView;
//	}
	private String getDirectionsUrl(LatLng origin,LatLng dest){
		 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
        return url;
    }
	private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
            
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
 
            br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
 
    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{
 
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
 
            // For storing data from web service
            String data = "";
 
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executes in UI thread, after the execution of
        // doInBackground()
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
 
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
 
        // Executes in UI thread, after the parsing process
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
 
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();
 
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
 
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
 
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
 
                    points.add(position);
                }
 
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
 
            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }
 
	private void mapClick(LatLng point)
	{
		if(markerPoints.size()>1){
            markerPoints.clear();
            map.clear();
        }
		
        // Adding new item to the ArrayList
        markerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        /**
        * For the start location, the color of marker is GREEN and
        * for the end location, the color of marker is RED.
        */
        if(markerPoints.size()==1){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else if(markerPoints.size()==2){
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        map.addMarker(options);

        // Checks, whether start and end locations are captured
        if(markerPoints.size() >= 2){
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
	}
	private void showForNow(String txt)
	{
		Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
	}
	
	ArrayList<LatLng> markerPoints = null;
			public void onCreate(Bundle savedInstanceState) {
			

			super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_map_fragment);
				//HomeActivity homeActivity = HomeActivity.Instance;
				
				markerPoints = new ArrayList<LatLng>();
			 try {
				 
					 if(map == null)
					 {
					 	map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
					 	//map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
					 	map.getUiSettings().setCompassEnabled(true);
					 	map.getUiSettings().setTiltGesturesEnabled(true);
					 	map.getUiSettings().setZoomGesturesEnabled(true);

					 	map.setBuildingsEnabled(true);
					 	map.getUiSettings().setRotateGesturesEnabled(true);
					 	map.setMyLocationEnabled(true);
					 	map.getUiSettings().setMyLocationButtonEnabled(true);
					 	
					 	map.setOnMapClickListener(new OnMapClickListener() {
							
							@Override
							public void onMapClick(LatLng arg0) {
								mapClick(arg0);								
							}
						});
					 	LatLng aloc = new LatLng(6.399, 3.35);
					 	cameraUpdate  = CameraUpdateFactory.newLatLngZoom(aloc, 20f); //newCameraPosition(map.getCameraPosition());
					 	
					 	map.animateCamera(cameraUpdate);
					 	Vars.myLocation = map.getMyLocation();
					 	
					 	
					 	final Marker homeMarker = map.addMarker(new MarkerOptions()
				        .position(aloc)
				        .title("Bitches be trippin'").draggable(true).flat(true).snippet("A snippet text for the map view marker ishee"));
					 	CircleOptions circleOptions = new CircleOptions()
					    .center(aloc)
					    .radius(500)
					    .strokeWidth(1f);
					 	
					 	 final Circle circle = map.addCircle(circleOptions);
						    circle.setFillColor(Color.RED);
					 	map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
							
							@Override
							public void onMyLocationChange(Location arg0) {
								Vars.myLocation = arg0;
								LatLng newpostion  = new LatLng(Vars.myLocation.getLatitude(), Vars.myLocation.getLongitude());
								cameraUpdate = CameraUpdateFactory.newLatLng(newpostion);
								homeMarker.setPosition(newpostion);
								circle.setCenter(newpostion);
								map.animateCamera(cameraUpdate);
							}
						});
					 	
					 	
					    
					 	// In meters

					// Get back the mutable Circle
					   
					    
					    LatLng NEWARK = aloc;

//					    GroundOverlayOptions newarkMap = new GroundOverlayOptions()
//					            .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
//					            .position(NEWARK, 100f, 100f);
//					    map.addGroundOverlay(newarkMap);
					    
					 	map.setOnMarkerClickListener(new OnMarkerClickListener() {
							
							@Override
							public boolean onMarkerClick(Marker arg0) {
									arg0.showInfoWindow();
								return false;
							}
						});
					 	showForNow(Vars.myLocation.toString());
					 	Log.d(Vars.myLocation.toString(),"myLocation");
					 }
		        } catch (Exception e) {
		        	Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
		        }

//		        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) )
//		        {
//		            case ConnectionResult.SUCCESS:
//		                Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();
//		                mapView = (MapView) homeActivity.findViewById(R.id.map);
//		                
//		    			if(mapView!=null)
//		                {   
//		    				mapView.onCreate(savedInstanceState);
//		                    map = mapView.getMap();
//		                    map.getUiSettings().setMyLocationButtonEnabled(true);
//		                    map.setMyLocationEnabled(true);
//		                    cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(6.666f, 3.33f), 10);
//		                    map.animateCamera(cameraUpdate);
//		                }
//		    			else Toast.makeText(homeActivity,"Cant find mapview",Toast.LENGTH_LONG).show();
//		                
//		                break;
//		            case ConnectionResult.SERVICE_MISSING: 
//		                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
//		                break;
//		            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: 
//		                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
//		                break;
//		            default:// Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
//		        }

			
			
		}
			@Override
			public boolean onMenuItemSelected(int featureId, MenuItem item) {
				// TODO Auto-generated method stub
				
				if(item.getItemId() == R.id.action_settings)
				{
					
				}
				return super.onMenuItemSelected(featureId, item);
			}
			public boolean onCreateOptionsMenu(Menu menu) {
				// Inflate the menu; this adds items to the action bar if it is present.
				getMenuInflater().inflate(R.menu.home, menu);
				return true;
			}

}
