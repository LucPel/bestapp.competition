package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.data.proxy.UserProxy;
import com.ats.bestapp.savefoods.trasformer.FoodTrasformer;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.ats.bestapp.savefoods.utilities.MediaFile;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

public class AddFoodActivity extends FragmentActivity{

	private HashMap<String, Object> commonsData;
	private LocationListenerWrapper locListenerWrap;
	private FoodProxy fproxy;
	private FoodTrasformer ftransformer;
	private UserProxy userProxy;
	private ArrayList<Uri> imegesUri;
	private String logTag="AddFoodActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setContentView(R.layout.activity_add_food);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    initCategoryAutoComplete();
	    SharedPreferences settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
		commonsData=new HashMap<String, Object>();
		commonsData.put(Constants.userNameSP, settings.getString(Constants.userNameSP, null));
		commonsData.put(Constants.userIdSP, settings.getString(Constants.userIdSP, null));
		Calendar cal = Calendar.getInstance();
		EditText dueDate=(EditText)findViewById(R.id.food_due_date);
		dueDate.clearComposingText();
		String day=Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		String month=Integer.toString(cal.get(Calendar.MONTH)+1);
		String year=Integer.toString(cal.get(Calendar.YEAR));
		dueDate.setText(day+"-"+month+"-"+year);
		locListenerWrap=new LocationListenerWrapper(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar, menu);
		return true;
	}
	
	public void saveFood(View view){
		try {
			commonsData.put(Constants.latitudeKey, locListenerWrap.getLatitude());
			commonsData.put(Constants.longitudeKey, locListenerWrap.getLongitude());
			String userd=(String)commonsData.get(Constants.userIdSP);
			ParseObject user=null;
			if(userd!=null && !userd.isEmpty()){
				user=userProxy.getUserParseObject((String)commonsData.get(Constants.userNameSP));
			}
			ArrayList<byte[]> imagesByte=new ArrayList<byte[]>();
			for(Uri currentUri : imegesUri){
				imagesByte.add(MediaFile.bitmapResized2Bytes(currentUri, 256, 256));		
			}
			Log.d(logTag, JsonMapper.convertObject2String(imagesByte));
			fproxy.addFood(ftransformer.trasformInFood(view.getRootView(), commonsData,imagesByte),user);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initCategoryAutoComplete(){
		AutoCompleteTextView autocomplete = (AutoCompleteTextView) findViewById(R.id.food_category_text);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_dropdown_item_1line, 
        		new String[] {"farinaceo","carne","latticino","verdura"}
        	);
        autocomplete.setAdapter(adapter);
	}
	
	public void showDatePickerDialog(View view){
		DialogFragment newFragment = new DatePickerDialogFragment((EditText)view);
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	  @Override
	  protected void onResume() {
	    super.onResume();
	    locListenerWrap.requestLocationUpdates();
	  }
	  
	  @Override
	  protected void onPause() {
	    super.onPause();
	    locListenerWrap.removeUpdates();
	  }
	  
	  private void init(){
		fproxy=new FoodProxy();
		ftransformer=new FoodTrasformer();
		userProxy=new UserProxy();
		imegesUri=new ArrayList<Uri>();
		Parse.initialize(this, "PlzFknCRYpaxv8Gec6I1aaIUs0BduoFn67fbOOla", "lmYnJlEaVLHNHLfcdQSqGivcXLVqlKGcgT9XEqTp");
	  }
	  
	  public void addImage(View view){
		  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		  Uri fileUri = MediaFile.getOutputMediaFileUri(MediaFile.MEDIA_TYPE_IMAGE); // create a file to save the image
		  Log.d(logTag, "URI gen "+fileUri.getPath());
		  if(fileUri!=null){
			  imegesUri.add(fileUri);
			  intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
			    // start the image capture Intent
			  startActivityForResult(intent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		  }
		 
	  }
	  
	  @Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      if (requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	          if (resultCode == RESULT_OK) {
	              // Image captured and saved to fileUri specified in the Intent
	              Toast.makeText(this, "Image saved to:\n" +
	                       imegesUri.get(imegesUri.size()-1).getPath(), Toast.LENGTH_LONG).show();
	              final int THUMBSIZE = 96;
	              Bitmap ThumbImage = MediaFile.bitmapResized(imegesUri.get(imegesUri.size()-1),THUMBSIZE, THUMBSIZE);
	              ImageView foodImage = (ImageView) findViewById(R.id.imageFood);
	              foodImage.setImageBitmap(ThumbImage);
	          } else if (resultCode == RESULT_CANCELED) {
	              // User cancelled the image capture
	          } else {
	              // Image capture failed, advise user
	          }
	      }
	  }
}
