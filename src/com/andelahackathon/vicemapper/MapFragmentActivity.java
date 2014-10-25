package com.andelahackathon.vicemapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MapFragmentActivity extends Fragment {

	public static MapView mapView = null;
	public static GoogleMap map = null;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 View rootView = inflater.inflate(R.layout.activity_map_fragment, container, false);
		 try {
	            MapsInitializer.initialize(getActivity());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) )
	        {
	            case ConnectionResult.SUCCESS:
	                Toast.makeText(getActivity(), "SUCCESS", Toast.LENGTH_SHORT).show();
	                mapView = (MapView) rootView.findViewById(R.id.map);
	                mapView.onCreate(savedInstanceState);
	                // Gets to GoogleMap from the MapView and does initialization stuff
	                if(mapView!=null)
	                {
	                    map = mapView.getMap();
	                    map.getUiSettings().setMyLocationButtonEnabled(true);
	                    map.setMyLocationEnabled(true);
	                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(6.666f, 3.33f), 10);
	                    map.animateCamera(cameraUpdate);
	                }
	                break;
	            case ConnectionResult.SERVICE_MISSING: 
	                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
	                break;
	            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED: 
	                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
	                break;
	            default:// Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
	        }


	        return rootView;
	}
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);
		}

}
