package com.ats.bestapp.savefoods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.utilities.Commons;
import com.ats.bestapp.savefoods.utilities.MediaFile;

import android.app.ProgressDialog;
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
 
	public HomeTableAdapter(Context context,ArrayList<Food> items) {
		this.context = context;
		this.items = items;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
 
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View gridView;
		Log.d(logTag, "Posizione "+position );
		ProgressDialog dialog= new ProgressDialog(context);
        dialog.setMessage("Loading");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);        
        dialog.show();
		if (convertView == null) {
			Log.d(logTag, "Posizione In "+position );
			gridView = new View(context);
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.home_table_item, null);
			setGridItemUI(gridView, position);
			
		} else {
			gridView = (View) convertView;
			setGridItemUI(gridView, position);
		}
		dialog.dismiss();
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
		if(quantity==null) quantity="1";
		textQuantityView.setText(quantity);
		
		TextView idItemText = (TextView) gridView
				.findViewById(R.id.grid_item_id);
		idItemText.setText(item.getFoodId());

		// set image based on selected text
		ImageView imageView = (ImageView) gridView
			.findViewById(R.id.grid_item_image);
		if(item.getImages()!=null && item.getImages().size()!=0){
			Bitmap image=MediaFile.bitmapFromBytesImage(item.getImages().get(0).getImage());
			if(image!=null){
				imageView.setImageBitmap(Bitmap.createScaledBitmap(image, Constants.standard_image_size, Constants.standard_image_size, false));
			}
			else{
				imageView.setImageResource(R.drawable.food_no_image_icon);
			}
		}
		else{
			imageView.setImageResource(R.drawable.food_no_image_icon);
		}
	}
}
