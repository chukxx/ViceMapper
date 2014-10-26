package com.andelahackathon.vicemapper;

import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


public class BinderData extends BaseAdapter {

	LayoutInflater inflater;
	ImageView thumb_image;
	List<HashMap<String,String>> routeDataCollection;
	ViewHolder holder;
	public BinderData() {
		// TODO Auto-generated constructor stub
	}
	
	public BinderData(Activity act, List<HashMap<String,String>> map) {
		
		this.routeDataCollection = map;
		//Log.d(map!=null?map.toString():"is null","\n\nis map not null");
		inflater = (LayoutInflater) act
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	public int getCount() {
		// TODO Auto-generated method stub
//		return idlist.size();
		return routeDataCollection.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		 
		
		
		View vi=convertView;
		
	    if(convertView==null){
	     
	      vi = inflater.inflate(R.layout.route_list_row, null);
	      holder = new ViewHolder();
	      
	      holder.routeDesc = (TextView2)vi.findViewById(R.id.routeDesc); // city name
	      
	      
	      holder.viceDesc = (TextView2)vi.findViewById(R.id.viceDesc); // city weather overview
	     
	   //   holder.foodCat =  (TextView2)vi.findViewById(R.id.foodCat); 
	   //   holder.foodImage =(ImageView)vi.findViewById(R.id.list_image); // thumb image
	      //holder.ratingBar = (RatingBar)vi.findViewById(R.id.ratingBar);
	      vi.setTag(holder);
	    }
	    else{
	    	
	    	holder = (ViewHolder)vi.getTag();
	    }

	      // Setting all values in listview
	   
	      holder.routeDesc.setText(routeDataCollection.get(position).get("routeDesc"));
//	      holder.ratingBar.setRating(Float.valueOf(foodDataCollection.get(position).get(Vars.KEY_RATING)));
	    //  holder.foodCat.setText(foodDataCollection.get(position).get(Vars.KEY_CAL) + " Calories");
	      
	      String c = routeDataCollection.get(position).get("viceCount");
	      if(!c.equals("0"))
	    	  holder.viceDesc.setTextColor(Color.RED);
	      c = c.equals("0")?"no vices":c + " vice(s)";
	      holder.viceDesc.setText(c);
	      //int catID = Integer.valueOf(foodDataCollection.get(position).get(Vars.KEY_CAT));
	      try
	      {
	    	 // String catText = Vars.StoreObject.getJSONArray("cat").getString(catID);
	      	//holder.foodCat.setText(catText);
	      }
	      catch(Exception e0)
	      {
	    	  e0.printStackTrace();
	      }
	      //Setting an image
	      //String uri = "drawable/ic_launcher";//"drawable/"+ weatherDataCollection.get(position).get(KEY_ICON);
	      //int imageResource = vi.getContext().getApplicationContext().getResources().getIdentifier(uri, null, vi.getContext().getApplicationContext().getPackageName());
	      //Drawable image = vi.getContext().getResources().getDrawable(imageResource);
	      //holder.tvWeatherImage.setImageDrawable(image);
	    // CustomListActivity.Instance.getImages(holder.foodImage, Vars.IMAGE_URL(foodDataCollection.get(position).get(Vars.KEY_ICON)));
	      //Log.d("img", Vars.IMAGE_URL(weatherDataCollection.get(position).get(Vars.KEY_ICON)));
	      return vi;
	}
	
	public String filterText = "";
	public void getFilter(String s)
	{
		filterText = s;
	}
	/*
	 * 
	 * */
	static class ViewHolder{
		
		TextView2 routeDesc;
//		RatingBar ratingBar;
//		TextView2 foodCat;
		TextView2 viceDesc;
		ImageView foodImage;
	}
	
}
