package com.ats.bestapp.savefoods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.utilities.Commons;
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
	private HashMap<String, Food> items;
	private final String logTag="HomeTableAdapter";
 
	public HomeTableAdapter(Context context, HashMap<String, Food> items) {
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
			Iterator it=items.keySet().iterator();
			int pos=0;
			boolean found=false;
			Food item;
			while(it.hasNext() && !found){
				if(pos==position){
					item=items.get(it.next());
					found=true;
					// set value into textview
					TextView textView = (TextView) gridView
							.findViewById(R.id.grid_item_label);
					
					textView.setText(item.getName());
					
					TextView textDueDateView = (TextView) gridView
							.findViewById(R.id.grid_item_due_date);
					
					textDueDateView.setText("Scadenza: "+Commons.convertToDate(item.getDueDate()));
					
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
				}
				else {
					it.next();
					pos++;
				}
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
		Iterator it=items.keySet().iterator();
		int pos=0;
		boolean found=false;
		Object item=null;
		while(it.hasNext() && !found){
			if(pos==position){
				item=items.get(it.next());
				found=true;
			}
			else {
				it.next();
				pos++;
			}
		}
		return item;
	}
 
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void setFoods(HashMap<String, Food> items){
		this.items=items;
	}
	
	
}
