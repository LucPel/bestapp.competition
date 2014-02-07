package com.ats.bestapp.savefoods;

import java.util.ArrayList;
import java.util.HashMap;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.utilities.MediaFile;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeTableAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<Food> items;
	private final String logTag="HomeTableAdapter";
 
	public HomeTableAdapter(Context context, ArrayList<Food> items) {
		this.context = context;
		this.items = items;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
		Log.d(logTag, "Posizione "+position );
		if (convertView == null) {
			Log.d(logTag, "Posizione In "+position );
			gridView = new View(context);
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.home_table_item, null);
			Food item=items.get(position);
			// set value into textview
			TextView textView = (TextView) gridView
					.findViewById(R.id.grid_item_label);
			textView.setText(item.getName()+" scade il:"+item.getDueDate());
			TextView idItemText = (TextView) gridView
					.findViewById(R.id.grid_item_id);
			idItemText.setText(item.getFoodId());
 
			// set image based on selected text
			ImageView imageView = (ImageView) gridView
				.findViewById(R.id.grid_item_image);
			if(item.getImages()!=null && item.getImages().size()!=0){
				Bitmap image=MediaFile.bitmapFromBytesImage(item.getImages().get(0).getImage());
				if(image!=null){
					imageView.setImageBitmap(Bitmap.createScaledBitmap(image, 150, 150, false));
				}
				else{
					imageView.setImageResource(R.drawable.logo_launcher);
				}
			}
			else{
				imageView.setImageResource(R.drawable.logo_launcher);
			}
			

		} else {
			gridView = (View) convertView;
		}
 
		return gridView;
	}
 
	@Override
	public int getCount() {
		Log.d(logTag, "Count "+items.size() );
		return items.size();
	}
 
	@Override
	public Object getItem(int position) {
		return items.get(position);
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void setFoods(ArrayList<Food> items){
		this.items=items;
	}
}
