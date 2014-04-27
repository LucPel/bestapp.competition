package com.ats.bestapp.savefoods;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.utilities.Commons;
import com.ats.bestapp.savefoods.utilities.MediaFile;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchTableAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<Food> items;
	private double currentLatitude;
	private double currentLongitude;
	private final String logTag="SearchTableAdapter";
 
	public SearchTableAdapter(Context context,ArrayList<Food> items,double currLatitude,double currLongitude) {
		this.context = context;
		this.items = items;
		this.currentLatitude=currLatitude;
		this.currentLongitude=currLongitude;
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
			gridView = inflater.inflate(R.layout.search_table_item, null);
			setGridItemUI(gridView, position);
			
		} else {
			gridView = (View) convertView;
			setGridItemUI(gridView, position);
		}
		return gridView;
	}
 
	@Override
	public int getCount() {
		//Log.d(logTag, "Count "+items.size() );
		if(items==null) return 0;
		else return items.size();
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
	
	private void setGridItemUI(View gridView, int position){
		Food item=items.get(position);
		// set value into textview
		TextView textView = (TextView) gridView
				.findViewById(R.id.grid_item_label);
		
		textView.setText(item.getName());
		
		TextView categoryText = (TextView) gridView
				.findViewById(R.id.grid_item_category_label);
		
		categoryText.setText("("+item.getType()+")");
		
		TextView textDueDateView = (TextView) gridView
				.findViewById(R.id.grid_item_due_date);
		
		textDueDateView.setText(Commons.convertToDate(item.getDueDate()));
		
		TextView textQuantityView = (TextView) gridView
				.findViewById(R.id.grid_item_quantity);
		
		String quantity=item.getQuantity();
	    String um=item.getMeasurementunity();
		if(quantity==null || um==null) {
			quantity="1";
			um="Pz";
		}
		textQuantityView.setText(quantity+" "+um );
		
		TextView idItemText = (TextView) gridView
				.findViewById(R.id.grid_item_id);
		idItemText.setText(item.getFoodId());


		TextView ownerItemText = (TextView) gridView
				.findViewById(R.id.grid_owner_label);
		ownerItemText.setText(Commons.getUsernameShow(item.getOwner().getUsername()));
		
		TextView textDistanceView = (TextView) gridView
				.findViewById(R.id.grid_item_distance);
		float[] results=new float[10];
		double distance=0;
		Location.distanceBetween(item.getLatitude(), item.getLongitude(), currentLatitude, currentLongitude, results);
		distance=results[0];
		distance=distance*0.001;
		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		if(distance>0){
			textDistanceView.setText(df.format(distance)+"Km");
		}
		else{
			textDistanceView.setText("0Km");
		}
		
		
		// set image based on selected text
		ImageView imageView = (ImageView) gridView
			.findViewById(R.id.grid_item_image);
		if(item.getImages()!=null && item.getImages().size()!=0){
			Bitmap image=MediaFile.bitmapFromBytesImage(item.getImages().get(0).getImage());
			if(image!=null){
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(Bitmap.createScaledBitmap(image, Constants.standard_image_x_size, Constants.standard_image_y_size, false));
			}
			else{
				imageView.setImageResource(R.drawable.food_no_image_icon);
			}
		}
		else{
			imageView.setImageResource(R.drawable.food_no_image_icon);
		}
	}

	public double getCurrentLatitude() {
		return currentLatitude;
	}

	public void setCurrentLatitude(double currentLatitude) {
		this.currentLatitude = currentLatitude;
	}

	public double getCurrentLongitude() {
		return currentLongitude;
	}

	public void setCurrentLongitude(double currentLongitude) {
		this.currentLongitude = currentLongitude;
	}
}
