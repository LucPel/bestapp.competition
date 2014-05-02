package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.utilities.Commons;
import com.ats.bestapp.savefoods.utilities.MediaFile;
import com.parse.ParseException;
import com.parse.ParseObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class SearchTableAdapter extends BaseAdapter implements OnScrollListener{

	private Context context;
	private ArrayList<Food> items;
	private double currentLatitude;
	private double currentLongitude;
	private int lastVisibleItem = 0;
	private boolean allItemsViewed=false;
	private ProgressDialog searchProgressDialog;
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
		allItemsViewed=false;
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
		
		ImageView catImageView = (ImageView) gridView
				.findViewById(R.id.grid_item_category_image);
		if(item.getType().equalsIgnoreCase(Constants.cat_bibita)){
			catImageView.setImageResource(R.drawable.cat_bibite_icon);
		}
		else if(item.getType().equalsIgnoreCase(Constants.cat_carne)){
			catImageView.setImageResource(R.drawable.cat_carne_icon);
		}
		else if(item.getType().equalsIgnoreCase(Constants.cat_cereali)){
			catImageView.setImageResource(R.drawable.cat_cereali_icon);
		}
		else if(item.getType().equalsIgnoreCase(Constants.cat_crostaceo)){
			catImageView.setImageResource(R.drawable.cat_crostacei_icon);
		}
		else if(item.getType().equalsIgnoreCase(Constants.cat_farinaceo)){
			catImageView.setImageResource(R.drawable.cat_farinacei_icon);
		}
		else if(item.getType().equalsIgnoreCase(Constants.cat_frutta)){
			catImageView.setImageResource(R.drawable.cat_frutta_icon);
		}
		else if(item.getType().equalsIgnoreCase(Constants.cat_latticini)){
			catImageView.setImageResource(R.drawable.cat_latticini_icon);
		}
		else if(item.getType().equalsIgnoreCase(Constants.cat_legumi)){
			catImageView.setImageResource(R.drawable.cat_legumi_icon);
		}
		else if(item.getType().equalsIgnoreCase(Constants.cat_pesce)){
			catImageView.setImageResource(R.drawable.cat_pesce_icon);
		}
		else if(item.getType().equalsIgnoreCase(Constants.cat_verdura)){
			catImageView.setImageResource(R.drawable.cat_verdura_icon);
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
		}
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.d(logTag, "FV "+firstVisibleItem +" VI " +visibleItemCount+ "TI" + totalItemCount);
		if (firstVisibleItem > lastVisibleItem) {
			if(totalItemCount>=5){
				if(++firstVisibleItem+visibleItemCount>totalItemCount && !allItemsViewed){
					SFApplication app=(SFApplication)view.getContext().getApplicationContext();
					GetUserFoodTask gust=new GetUserFoodTask(app.getUserLoggedIn(),currentLatitude,currentLongitude);
					gust.execute();
				}
			}
	    }
		lastVisibleItem = firstVisibleItem;
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
	
	// ASYNC TASKS
	private class GetUserFoodTask extends
			AsyncTask<Void, Integer, ArrayList<Food>> {

		ParseObject userPO;
		double latitude;
		double longitude;

		public GetUserFoodTask(ParseObject userPO_i,double latitude_i,double longitude_i){
			userPO=userPO_i;	
			latitude=latitude_i;
			longitude=longitude_i;
		}

		@Override
		protected ArrayList<Food> doInBackground(Void... params) {
			try {
				Log.i(logTag, "Esecuzione query "+userPO);
				Log.d(logTag, "Latitude:" +latitude);
				FoodProxy foodProxy=new FoodProxy();
				items = foodProxy.getFoods4Location(userPO,latitude,longitude);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonGenerationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return items;
		}

	
		protected void onPostExecute(ArrayList<Food> foods_out) {
			searchProgressDialog.dismiss();
			if(foods_out!=null && foods_out.size()>0){
				items.addAll(foods_out);
				SearchTableAdapter.this.notifyDataSetChanged();
			}
			else if(foods_out.size()==0){
				allItemsViewed=true;
				Log.d(logTag,"Visuaalizzati tutti i foods");
			}
			
		}

		protected void onPreExecute() {
			startDialogLoading();
		}

	}
	
	private void startDialogLoading() {
		searchProgressDialog = new ProgressDialog(context);
		searchProgressDialog.setMessage("Next Foods Loading");
		searchProgressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
		searchProgressDialog.setCancelable(false);
		searchProgressDialog.show();
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
