package com.andelahackathon.vicemapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MapFragmentActivity extends FragmentActivity {

	public  MapView mapView = null;
	public  CameraUpdate cameraUpdate = null;
	public  GoogleMap map = null;
	private Marker startLocation = null, endLocation = null;
	
//	private View view = null;
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		 View rootView = inflater.inflate(R.layout.activity_map_fragment, container, false);
//		
//	        view = rootView;
//	        return rootView;
//	}
	private String getDirectionsUrl(/*LatLng origin,LatLng dest*/){
		 
        // Origin of route
        String str_origin = "origin="+URLEncoder.encode(Vars.getDirectionSet().get(1));//+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+URLEncoder.encode(Vars.getDirectionSet().get(0));//dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false&alternatives=true";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.i(url,"URLPATH");
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
    
    private void populateRouteList (final JSONObject jObject )
    {
    	runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				try
		    	{
			    	JSONArray _routes = jObject.getJSONArray("routes");
			    	String [] routes = new String[_routes.length()];
			    	for(int i = 0; i < _routes.length();i++)
			    	{
			    		JSONObject legs = _routes.getJSONObject(i).getJSONArray("legs").getJSONObject(0);
			    		routes[i] = _routes.getJSONObject(i).getString("summary") + " - "+legs.getJSONObject("distance").getString("text") + " in " +legs.getJSONObject("duration").getString("text") ;
			    	}
			    	showForNow(routes.toString());
			    	ListView lv = (ListView)findViewById(R.id.RouteList);
			    	
					final ArrayAdapter<String> ladapter = new ArrayAdapter<String>(getBaseContext(),R.layout.route_list, R.id.route_desc, routes);
					lv.setAdapter(ladapter);
					
					lv.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int position, long arg3) {
							//String vname = ladapter.getItem(position);
							showForNow(position+" position");
							
						}});
		    		
		    	}
		    	catch(Exception e0)
		    	{
		    		
		    	}
			}
		});
    	
    }
    private void getRouteObject(JSONObject jObject)
    {
    	try
    	{
    		 populateRouteList(jObject);
	    	 final JSONObject locations = jObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
	         final JSONObject start_location = locations.getJSONObject("start_location");
	         final JSONObject end_location = locations.getJSONObject("end_location");
	        // Log.i(start_location.toString() + " - " + end_location.toString()," jsonobject route");
	         showForNow(start_location.toString() + " - " + end_location.toString());
	         
	         runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try
					{
						startLocation.setPosition(new LatLng(start_location.getDouble("lat"), start_location.getDouble("lng")));
				        endLocation.setPosition(new LatLng(end_location.getDouble("lat"), end_location.getDouble("lng")));
				        
				        startLocation.setSnippet(locations.getString("start_address"));
				        endLocation.setSnippet(locations.getString("end_address"));
				         map.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation.getPosition(), 15f));
					}
					catch(JSONException e0){
						e0.printStackTrace();
					}
				}
			});
	         
    	}
    	catch(JSONException e0)
    	{
    		e0.printStackTrace();
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
                getRouteObject(jObject);
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

            //showForNow(result == null ?"no result" :result.toString());
            if(result == null)
            	return;
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
                lineOptions.width(5);
                lineOptions.color(Color.RED);
            }
 
            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
        }
    }
 
	
	private void showForNow(final String txt)
	{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
			}
		});
		
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
					 	
//					 	map.setOnMapClickListener(new OnMapClickListener() {
//							
//							@Override
//							public void onMapClick(LatLng arg0) {
//								mapClick(arg0);								
//							}
//						});
					    
					 	LatLng aloc = new LatLng(6.55, 3.40);
					 	startLocation = map.addMarker(new MarkerOptions().position(aloc).title("Start Location").snippet("This is where you start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				        endLocation = map.addMarker(new MarkerOptions().position(aloc).title("End Location").snippet("This is your destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
				     
					 	cameraUpdate  = CameraUpdateFactory.newLatLngZoom(aloc, 20f); //newCameraPosition(map.getCameraPosition());
					 	map.animateCamera(cameraUpdate);
					 	//Vars.myLocation = map.getMyLocation();
					 	
					 	
					 	final Marker homeMarker = map.addMarker(new MarkerOptions()
				        .position(aloc)
				        .title("Bitches be trippin'").draggable(true).flat(true).snippet("A snippet text for the map view marker ishee"));
					 	CircleOptions circleOptions = new CircleOptions()
					    .center(aloc)
					    .radius(500)
					    .strokeWidth(1f);
					 	
					 	// final Circle circle = map.addCircle(circleOptions);
						 //   circle.setFillColor(Color.RED);
					 	map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
							
							@Override
							public void onMyLocationChange(Location arg0) {
								Vars.myLocation = arg0;
								showForNow("new location "+ arg0.toString());
							//	LatLng newpostion  = new LatLng(Vars.myLocation.getLatitude(), Vars.myLocation.getLongitude());
							//	cameraUpdate = CameraUpdateFactory.newLatLng(newpostion);
							//	homeMarker.setPosition(newpostion);
							//	circle.setCenter(newpostion);
							//	map.animateCamera(cameraUpdate);
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
					 	
					 	String url = getDirectionsUrl();
			            DownloadTask downloadTask = new DownloadTask();

			            downloadTask.execute(url);

					 	//showForNow(Vars.myLocation.toString());
					 	//Log.d(Vars.myLocation.toString(),"myLocation");
					 }
		        } catch (Exception e) {
		        	Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
		        }

//		       		
			
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
