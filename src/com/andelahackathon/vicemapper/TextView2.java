package com.andelahackathon.vicemapper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextView2 extends TextView {

	public TextView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}
	
	public TextView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
		
	}
	
	public TextView2(Context context) {
		super(context);
		init(null);
	}
	
	private void init(AttributeSet attrs) {
		if (attrs!=null) {
			 TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TextView2);
			 String fontName = a.getString(R.styleable.TextView2_fontName);
			 fontName = fontName != null?fontName:"Roboto-Light.ttf";	
			 try
			 {
				 Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/"+fontName);
				 setTypeface(myTypeface);
			 }
			 catch(Exception e0)
			 {

			 }
			 a.recycle();
		}
	}
}
