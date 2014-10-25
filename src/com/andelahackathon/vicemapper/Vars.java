package com.andelahackathon.vicemapper;

import com.firebase.client.Firebase;

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
	
}
