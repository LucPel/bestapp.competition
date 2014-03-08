package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.data.proxy.UserProxy;
import com.ats.bestapp.savefoods.trasformer.FoodTrasformer;
import com.ats.bestapp.savefoods.trasformer.UserTransformer;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.ats.bestapp.savefoods.utilities.MediaFile;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;

public class AddFoodActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener{

	private HashMap<String, Object> commonsData;
	private LocationListenerWrapper locListenerWrap;
	private FoodProxy fproxy;
	private ProgressDialog progressDialog;
	private FoodTrasformer ftrasformer;
	private UserProxy userProxy;
	private ParseObject food;
	private PlusClient mPlusClient;
	private UserTransformer userTrasformer;
	private ArrayList<Uri> imegesUri;
	private String logTag="AddFoodActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_food);
		init();
	    initCategoryAutoComplete();
		Calendar cal = Calendar.getInstance();
		EditText dueDate=(EditText)findViewById(R.id.food_due_date);
		dueDate.clearComposingText();
		dueDate.setText(DateFormat.format("dd-MM-yyyy", new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))));
		locListenerWrap=new LocationListenerWrapper(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.action_bar_add_food, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	onBackPressed();
	            return true;
	        case R.id.action_addFoodPhoto :
	        	addImage();
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void saveFood(View view){
		startDialogLoading();
		try {
			commonsData.put(Constants.latitudeKey, locListenerWrap.getLatitude());
			commonsData.put(Constants.longitudeKey, locListenerWrap.getLongitude());
			String userd=(String)commonsData.get(Constants.userIdSP);
			ParseObject user=null;
			if(userd!=null && !userd.isEmpty()){
				user=userProxy.getUserParseObject((String)commonsData.get(Constants.userNameSP));
			}
			else{
				user=userTrasformer.createParseObjectUser((String) commonsData.get(Constants.userNameSP));
			}
			ArrayList<byte[]> imagesByte=new ArrayList<byte[]>();
			for(Uri currentUri : imegesUri){
				imagesByte.add(MediaFile.bitmapResized2Bytes(currentUri, 256, 256));		
			}
			Log.d(logTag, JsonMapper.convertObject2String(imagesByte));
			food=ftrasformer.trasformInFood(view.getRootView(), commonsData,imagesByte,user);
			fproxy.addFood(food);
			progressDialog.dismiss();
			shareOnGPlus();
			
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
        		new String[] {"Frutta","Carne","Latticini","Verdura","Pesce","Crostaceo","Cereali","Legumi","Bibite","Farinaceo"}
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
		ftrasformer=new FoodTrasformer();
		userTrasformer=new UserTransformer();
		userProxy=new UserProxy();
		imegesUri=new ArrayList<Uri>();
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    SharedPreferences settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
		commonsData=new HashMap<String, Object>();
		commonsData.put(Constants.userNameSP, settings.getString(Constants.userNameSP, null));
		commonsData.put(Constants.userIdSP, settings.getString(Constants.userIdSP, null));
		Parse.initialize(this, Constants.parseAppId, Constants.parseClientKey);
	  }
	  
	  public void addImage(){
		  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		  Uri fileUri = MediaFile.getOutputMediaFileUri(MediaFile.MEDIA_TYPE_IMAGE); // create a file to save the image
		  Log.d(logTag, "URI gen "+fileUri.getPath());
		  if(fileUri!=null){
			  imegesUri.add(fileUri);
			  Log.d(logTag, "URI gen "+fileUri.getPath());
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
	              Bitmap ThumbImage = MediaFile.bitmapResized(imegesUri.get(imegesUri.size()-1),Constants.standard_image_size, Constants.standard_image_size);
	              ImageView foodImage = (ImageView) findViewById(R.id.imageFood);
	              foodImage.setImageBitmap(ThumbImage);
	          } else if (resultCode == RESULT_CANCELED) {
	        	  
	          } else {
	        	  Toast.makeText(this, "Error during image saving", Toast.LENGTH_LONG).show();
	          }
	      }
	  }
	  
		private void startDialogLoading() {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Loading");
			progressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		
		private void shareOnGPlus(){
			
		mPlusClient = new PlusClient.Builder(this, this, this).setActions(
				"http://schemas.google.com/AddActivity",
				"http://schemas.google.com/BuyActivity")
		// .setScopes(Scopes.PLUS_LOGIN) // recommended login scope for social
		// features
		// .setScopes("profile") // alternative basic login scope
				.build();
		mPlusClient.connect();
				
	    }

		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnected(Bundle arg0) {
			 Intent shareIntent = new PlusShare.Builder(this)
	          .setType("text/plain")
	          .setText("Salva il mio alimento "+food.getString(Constants.foodNamePO)+" che scade il "+food.getString(Constants.foodDueDatePO))
	          .setContentUrl(Uri.parse("https://developers.google.com/+/"))
	          .getIntent();

	      startActivityForResult(shareIntent, 0);
			
		Intent intent_back = new Intent();
		setResult(Constants.ADD_FOOD_RESPONSE_CODE, intent_back);
		finish();
			
		}

		@Override
		public void onDisconnected() {
			// TODO Auto-generated method stub
			
		}
		
}
