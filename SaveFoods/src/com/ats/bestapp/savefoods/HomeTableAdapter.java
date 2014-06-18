package com.ats.bestapp.savefoods;

import java.io.IOException;
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
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeTableAdapter extends BaseAdapter implements OnScrollListener{

	private Context context;
	private ArrayList<Food> items;
	private final String logTag="HomeTableAdapter";
	private ProgressDialog homeProgressDialog;
	private int lastVisibleItem = 0;
	private boolean allItemsViewed=false;
 
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
		textQuantityView.setText(quantity+" "+um);
		
		TextView idItemText = (TextView) gridView
				.findViewById(R.id.grid_item_id);
		idItemText.setText(item.getFoodId());

		ImageView catImageView = (ImageView) gridView
				.findViewById(R.id.grid_item_category_image);
		SFApplication sfa=(SFApplication) context.getApplicationContext();
		catImageView.setImageResource(sfa.getCategoryIcon(item.getType()));
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
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		Log.d(logTag, "FV "+firstVisibleItem +" VI " +visibleItemCount+ "TI" + totalItemCount);
		if (firstVisibleItem > lastVisibleItem) {
			if(totalItemCount>=5){
				if(++firstVisibleItem+visibleItemCount>totalItemCount && !allItemsViewed){
					HashMap<String, Object> paramsTask=new HashMap<String, Object>();
					SFApplication app=(SFApplication)view.getContext().getApplicationContext();
					paramsTask.put("user", app.getUserLoggedIn());
					paramsTask.put("skipItems", totalItemCount);
					new GetUserFoodTask().execute(paramsTask);
				}
			}
	    }
		lastVisibleItem = firstVisibleItem;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}
	
	// ASYNC TASKS

		private class GetUserFoodTask extends
				AsyncTask<HashMap<String,Object>, Integer, ArrayList<Food>> {

			@Override
			protected ArrayList<Food> doInBackground(HashMap<String,Object>... params) {
				ArrayList<Food> foods=null;
				try {
					FoodProxy foodProxy=new FoodProxy();
					foods=foodProxy.getFoods4User((ParseObject)params[0].get("user"),(Integer)params[0].get("skipItems"));
					
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
				return foods;
			}

			protected void onPostExecute(ArrayList<Food> foods_out) {
				homeProgressDialog.dismiss();
				if(foods_out!=null && foods_out.size()>0){
					items.addAll(foods_out);
					HomeTableAdapter.this.notifyDataSetChanged();
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
			homeProgressDialog = new ProgressDialog(context);
			homeProgressDialog.setMessage("Next Foods Loading");
			homeProgressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
			homeProgressDialog.setCancelable(false);
			homeProgressDialog.show();
		}
}
