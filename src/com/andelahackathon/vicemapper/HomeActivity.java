package com.andelahackathon.vicemapper;

import info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import info.androidhive.slidingmenu.model.NavDrawerItem;

import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class HomeActivity extends FragmentActivity {

	public static HomeActivity Instance = null;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private LinearLayout mDrawer;
	private ActionBarDrawerToggle mDrawerToggle;
	
	
	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Instance = this;
		setContentView(R.layout.activity_home);
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawer = (LinearLayout) findViewById(R.id.drawer);
		mDrawerList = (ListView) mDrawer.findViewById(R.id.list_slidermenu);
		try
		{
			navDrawerItems = new ArrayList<NavDrawerItem>();
			navDrawerItems.add(new NavDrawerItem("Home", navMenuIcons.getResourceId(0, 0)));
			try
			{

				for(int i=0;i<navMenuTitles.length;i++)
				{
					navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i+1, 0)));
				}
			}
			catch(Exception e0)
			{

			
			}
			
			
			//navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		
			
	
			// Recycle the typed array
			navMenuIcons.recycle();
	
			mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
	
			// setting the nav drawer list adapter
			adapter = new NavDrawerListAdapter(getApplicationContext(),
					navDrawerItems);
			mDrawerList.setAdapter(adapter);
	
			// enabling action bar app icon and behaving it as toggle button
			getActionBar().setDisplayHomeAsUpEnabled(true);
			//getActionBar().setHomeButtonEnabled(true);
	
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
					R.drawable.ic_drawer, //nav menu toggle icon
					R.string.app_name, // nav drawer open - description for accessibility
					R.string.app_name // nav drawer close - description for accessibility
			) {
				public void onDrawerClosed(View view) {
					getActionBar().setTitle(mTitle);
					// calling onPrepareOptionsMenu() to show action bar icons
					invalidateOptionsMenu();
				}
	
				public void onDrawerOpened(View drawerView) {
					getActionBar().setTitle(mDrawerTitle);
					// calling onPrepareOptionsMenu() to hide action bar icons
					invalidateOptionsMenu();
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);
			if (savedInstanceState == null) {
				// on first time display view for first nav item
				displayView(0);
			}
			
//			  startService(new Intent(this,TimeService.class));
//			  registerReceiver();  
		}
		catch(Exception e0)
		{
			e0.printStackTrace();
			Toast.makeText(getApplicationContext(),e0.toString(),Toast.LENGTH_LONG).show();
		}
	}
	 @Override
	    protected void onDestroy() {
	    	super.onDestroy();
	    }
	    @Override
	    protected void onPause() {
	    	
	    	super.onPause();
	    }
		  protected void onResume() 
		  {
			  
			  //registerReceiver();
			  super.onResume();
		  }
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	private class SlideMenuClickListener implements
	ListView.OnItemClickListener {
	@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}
	public void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		switch (position) {
		case 1:
			try
			{
				//startActivity(new Intent(getApplicationContext(),MainActivity.class));
			}
			catch(Exception e0)
			{
				//Toast.makeText(getApplicationContext(), e0.toString(),Toast.LENGTH_LONG).show();
				//e0.printStackTrace();
			}
			
		break;
		case 0:
			try
			{
				startActivity(new Intent(getApplicationContext(),MapFragmentActivity.class));
			}
			catch(Exception e0)
			{
				Toast.makeText(getApplicationContext(), e0.toString(),Toast.LENGTH_LONG).show();
				//e0.printStackTrace();
			}
			break;
		

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			
		} else {
			// error in creating fragment
			//Log.e("CustomListActivity", "Error in creating fragment");
		}
		mDrawerList.setItemChecked(position, true);
		mDrawerList.setSelection(position);
		if(position==0)
			setTitle("Home");
		else
		{
			try {
			//	setTitle(Vars.StoreObject.getJSONArray("cat").getString(position-1));
			} catch (Exception e) {
				
			}
		}
		
		mDrawerLayout.closeDrawer(mDrawer);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}
	
	 public boolean onOptionsItemSelected(MenuItem item) {
			// toggle nav drawer on selecting action bar app icon/title
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
			// Handle action bar actions click
			switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
}
