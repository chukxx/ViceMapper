package com.andelahackathon.vicemapper;



import com.andelahackathon.vicemapper.adapter.PlacesAutoCompleteAdapter;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;


public class MainScreen extends Fragment {
	private String fromLocation;
	private String toLocation;
	private Button searchRoutes;
    @Override
    
    public void onActivityCreated(Bundle savedInstanceState) {
    	
    	super.onActivityCreated(savedInstanceState);
    	 AutoCompleteTextView fromRoute = (AutoCompleteTextView) HomeActivity.Instance.findViewById(R.id.fromRoute);
         AutoCompleteTextView toRoute = (AutoCompleteTextView) HomeActivity.Instance.findViewById(R.id.toRoute);
         
         fromRoute.setAdapter(new PlacesAutoCompleteAdapter(HomeActivity.Instance, R.layout.autocomplete_list_item));
         fromRoute.setOnItemClickListener(new OnItemClickListener() {

 			@Override
 			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
 				// TODO Auto-generated method stub
 				setFromLocation((String) adapterView.getItemAtPosition(position));
 		        //Toast.makeText(adapterView.getContext(), str, Toast.LENGTH_SHORT).show();
 			}
         	
 		});
         
         toRoute.setAdapter(new PlacesAutoCompleteAdapter(HomeActivity.Instance, R.layout.autocomplete_list_item));
         toRoute.setOnItemClickListener(new OnItemClickListener() {

 			@Override
 			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
 				// TODO Auto-generated method stub
 				setToLocation((String) adapterView.getItemAtPosition(position));
 		        //Toast.makeText(adapterView.getContext(), str, Toast.LENGTH_SHORT).show();
 			}
         	
 		});
         
         searchRoutes = (Button) HomeActivity.Instance.findViewById(R.id.searchRoutes);
         searchRoutes.setOnClickListener(new View.OnClickListener() {
 			
 			@Override
 			public void onClick(View v) {
 				try
 				{
	 				if(getToLocation().equals("") || getFromLocation().equals("")) {
	 					Toast.makeText(v.getContext(), "You need to make a selection to get started", Toast.LENGTH_LONG).show();
	 				} else {
		 				Vars.setDirectionSet(fromLocation, toLocation);
		 				startActivity(new Intent(HomeActivity.Instance,MapFragmentActivity.class));
	 				}
 				}
 				catch(Exception e)
 				{
 					Toast.makeText(v.getContext(), "You need to make a selection to get started", Toast.LENGTH_LONG).show();
 				}
 			}
 		});

    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceStat) {
		 View rootView = inflater.inflate(R.layout.activity_main_screen, container, false);
         	
	        return rootView;
	}
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_main_screen);
//        
//           }

	public String getFromLocation() {
		return fromLocation;
	}

	public void setFromLocation(String fromLocation) {
		this.fromLocation = fromLocation;
	}

	public String getToLocation() {
		return toLocation;
	}

	public void setToLocation(String toLocation) {
		this.toLocation = toLocation;
	}
}
