package com.andelahackathon.vicemapper;

import java.util.ArrayList;
import java.util.HashMap;

import com.firebase.client.Firebase;
import com.firebase.client.DataSnapshot;

import android.location.Location;


public class Vars {
	
	public static Location myLocation= null;
	
	private static Firebase _fb = null;
	public static Firebase dbcon()
	{
		if(_fb==null)
			_fb = new Firebase("https://vicemapper.firebaseio.com");
		return _fb;
	}
	
	public static void savedb(Object o)
	{
		dbcon().setValue(o);
	}
	public static void getdb(Object o)
	{
		
	}
	private static String toLocation;
	private static DataSnapshot snapRecords;

	private static String fromLocation;

	private static double longitude;

	private static double latitude;
	
	public static void setDirectionSet(String fromLocation, String toLocation) {
		Vars.toLocation = toLocation;
		Vars.fromLocation = fromLocation;
	}
	
	public static ArrayList<String> getDirectionSet() {
		ArrayList<String> resultSet = new ArrayList<String>();
		resultSet.add(Vars.toLocation);
		resultSet.add(Vars.fromLocation);
		return resultSet;
	}
	
	public static DataSnapshot getSnapRecords() {
		return snapRecords;
	}

	public static void setSnapRecords(DataSnapshot snapRecords) {
		Vars.snapRecords = snapRecords;
	}

	public static void setCurrentLocation(double longitude, double latitude) {
		// TODO Auto-generated method stub
		Vars.longitude = longitude;
		Vars.latitude = latitude;		
	}

	public static HashMap<String, Double> getLocation() {
		HashMap<String, Double> currentLocation = new HashMap<String, Double>();
		currentLocation.put("longitude", Vars.longitude);
		currentLocation.put("latitude", Vars.latitude);
		return currentLocation;
	}
	
}
