package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemSelectedListener;

import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.data.proxy.UserProxy;
import com.ats.bestapp.savefoods.trasformer.FoodTrasformer;
import com.ats.bestapp.savefoods.trasformer.UserTransformer;
import com.ats.bestapp.savefoods.utilities.Commons;
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
import com.parse.PushService;
import com.parse.entity.mime.MIME;

public class AddFoodActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, OnItemSelectedListener {

	private HashMap<String, Object> commonsData;
	// private LocationListenerWrapper locListenerWrap;
	private FoodProxy fproxy;
	private FoodTrasformer ftrasformer;
	private UserProxy userProxy;
	private ParseObject food;
	private PlusClient mPlusClient;
	private UserTransformer userTrasformer;
	private ArrayList<Uri> imegesUri;
	private String logTag = "AddFoodActivity";
	private boolean shareable = false;
	private boolean isUpdate=false;
	private Food foodToUpdate;
	private ProgressDialog addFoodProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_food);
		init();
		foodToUpdate=(Food) getIntent().getSerializableExtra(Constants.foodDetailSP);
		if(foodToUpdate==null){
			Calendar cal = Calendar.getInstance();
			EditText dueDate = (EditText) findViewById(R.id.food_due_date);
			dueDate.clearComposingText();
			dueDate.setText(DateFormat.format(
					"dd-MM-yyyy",
					new GregorianCalendar(cal.get(Calendar.YEAR), cal
							.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))));
		}
		else{
			isUpdate=true;
			commonsData.put(Constants.foodStatusPO, foodToUpdate.getStatus());
			commonsData.put(Constants.foodIdPO, foodToUpdate.getFoodId());
			commonsData.put(Constants.foodChannelPO, foodToUpdate.getChannel());
			setUI4Update();
		}
		// locListenerWrap=new LocationListenerWrapper(this);
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
		case R.id.action_addFoodPhoto:
			addImage();
			return true;
		case R.id.action_saveFoodItem:
			saveFood(getWindow().getDecorView());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void saveFood(View view) {
		Toast.makeText(this,
				getResources().getString(R.string.startSavingFoodMessage),
				Toast.LENGTH_LONG).show();
		
		SFApplication sfapp = (SFApplication) getApplication();
		if(!isUpdate){
			commonsData.put(Constants.latitudeKey, sfapp.getCurrentLatitude());
			commonsData
					.put(Constants.longitudeKey, sfapp.getCurrentLongitude());
		}
		else{
			updateFoodAfterSave(view);
			commonsData.put(Constants.latitudeKey, foodToUpdate.getLatitude());
			commonsData
					.put(Constants.longitudeKey, foodToUpdate.getLongitude());
		}
		new SaveFoodTask().execute("test");
		
	}

	public void showDatePickerDialog(View view) {
		DialogFragment newFragment = new DatePickerDialogFragment(
				(EditText) view);
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void isShareable(View view) {
		Button shareTB = (Button) findViewById(R.id.share_tb);
		if (!shareable) {
			shareTB.setBackgroundResource(R.drawable.google_plus_press);
			shareable = true;
		} else {
			shareTB.setBackgroundResource(R.drawable.google_plus);
			shareable = false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// locListenerWrap.requestLocationUpdates();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// locListenerWrap.removeUpdates();
	}

	private void init() {
		fproxy = new FoodProxy();
		ftrasformer = new FoodTrasformer();
		userTrasformer = new UserTransformer();
		userProxy = new UserProxy();
		imegesUri = new ArrayList<Uri>();
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		SharedPreferences settings = getSharedPreferences(
				Constants.sharedPreferencesName, 0);
		commonsData = new HashMap<String, Object>();
		commonsData.put(Constants.userNameSP,
				settings.getString(Constants.userNameSP, null));
		commonsData.put(Constants.userIdSP,
				settings.getString(Constants.userIdSP, null));
		Spinner categorySpinner = (Spinner) findViewById(R.id.food_category_spinner);
		categorySpinner.setOnItemSelectedListener(this);
	}

	public void addImage() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri fileUri = MediaFile
				.getOutputMediaFileUri(MediaFile.MEDIA_TYPE_IMAGE); // create a
																	// file to
																	// save the
																	// image
		Log.d(logTag, "URI gen " + fileUri.getPath());
		if (fileUri != null) {
			imegesUri.add(fileUri);
			Log.d(logTag, "URI gen " + fileUri.getPath());
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image
																// file name
			// start the image capture Intent
			startActivityForResult(intent,
					Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				if(imegesUri!=null && imegesUri.size()!=0){
					Toast.makeText(
							this,
							"Image saved to:\n"
									+ imegesUri.get(imegesUri.size() - 1).getPath(),
							Toast.LENGTH_LONG).show();
					Bitmap ThumbImage = MediaFile.bitmapResized(
							imegesUri.get(imegesUri.size() - 1),
							Constants.insert_image_x_size,
							Constants.insert_image_y_size);
					ImageView foodImage = (ImageView) findViewById(R.id.imageFood);
					foodImage.setVisibility(View.VISIBLE);
					foodImage.setImageBitmap(ThumbImage);
				}
				
			} else if (resultCode == RESULT_CANCELED) {

			} else {
				Toast.makeText(this, "Error during image saving",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private void shareOnGPlus() {

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

		
		if(imegesUri!=null && imegesUri.size()!=0){
			Uri imageFood = imegesUri.get(0);
			if (imageFood == null) {
				shareGPlusWithoutImage();
			}
			else{
				shareGPlusWithImage(imageFood);
			}
		}
		else{
			shareGPlusWithoutImage();
		}
		
	}

	private void shareGPlusWithImage(Uri imageFood){
		PlusShare.Builder share = new PlusShare.Builder(this)
				.setText(
						"Salva il mio alimento "
								+ food.getString(Constants.foodNamePO)
								+ " che scade il "
								+ Commons.convertToDate(food
										.getString(Constants.foodDueDatePO)))
				.setStream(imageFood)
				.setType(Constants.gp_mime_jpg_type);

		startActivityForResult(share.getIntent(), 0);
		closeIntent();
		Toast.makeText(this,
				getResources().getString(R.string.endSavingFoodMessage),
				Toast.LENGTH_LONG).show();
		finish();

	}
	
	private void shareGPlusWithoutImage() {
		Intent shareIntent = new PlusShare.Builder(this)
				.setType("text/plain")
				.setText(
						"Salva il mio alimento "
								+ food.getString(Constants.foodNamePO)
								+ " che scade il "
								+ Commons.convertToDate(food
										.getString(Constants.foodDueDatePO)))
				.setContentUrl(Uri.parse("https://developers.google.com/+/"))
				.getIntent();

		startActivityForResult(shareIntent, 0);
		closeIntent();
		Toast.makeText(this,
				getResources().getString(R.string.endSavingFoodMessage),
				Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	private void closeIntent(){
		Intent intent_back = new Intent();
		if(isUpdate){
			intent_back.putExtra(Constants.foodDetailSP, foodToUpdate);
			setResult(Constants.UPDATE_FOOD_RESPONSE_CODE, intent_back);
		}
		else{
			setResult(Constants.ADD_FOOD_RESPONSE_CODE, intent_back);
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> aview, View view, int position,
			long id) {
		String cat_value = aview.getItemAtPosition(position).toString();
		ImageView catImageView = (ImageView) findViewById(R.id.imageCategoryFood);
		SFApplication sfa = (SFApplication) getApplication();
		catImageView.setImageResource(sfa.getCategoryIcon(cat_value));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen for landscape and portrait
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setUI4Update(){
		EditText name = (EditText) findViewById(R.id.food_name_text);
		name.setText(foodToUpdate.getName());
		EditText description = (EditText) findViewById(R.id.food_description_text);
		description.setText(foodToUpdate.getDescription());
		EditText quantityET = (EditText) findViewById(R.id.food_quantity_text);
		quantityET.setText(foodToUpdate.getQuantity());
		String[] categories=getResources().getStringArray(R.array.foodCategories);
		Spinner categorySpinner = (Spinner) findViewById(R.id.food_category_spinner);
		for(int i=0; i<categories.length;i++){
			if(categories[i].equalsIgnoreCase(foodToUpdate.getType())){
				categorySpinner.setSelection(i);
			}
		}
		Spinner UMSpinner=(Spinner)findViewById(R.id.food_um_spinner);
		String[] ums=getResources().getStringArray(R.array.foodMeasurementUnit);
		for(int j=0;j<ums.length;j++){
			if(ums[j].equalsIgnoreCase(foodToUpdate.getMeasurementunity())){
				UMSpinner.setSelection(j);
			}
		}
		
		
		ImageView imageView = (ImageView) findViewById(R.id.imageFood);
			if(foodToUpdate.getImages()!=null && foodToUpdate.getImages().size()!=0){
				Bitmap image=MediaFile.bitmapFromBytesImage(foodToUpdate.getImages().get(0).getImage());
				if(image!=null){
					imageView.setVisibility(View.VISIBLE);
					imageView.setImageBitmap(Bitmap.createScaledBitmap(image, Constants.standard_image_x_size, Constants.standard_image_y_size, false));
					
				}
			}
		EditText dueDate = (EditText) findViewById(R.id.food_due_date);
		dueDate.clearComposingText();
		dueDate.setText(DateFormat.format(
				"dd-MM-yyyy",
				new GregorianCalendar(Integer.parseInt(Commons.getYear(foodToUpdate.getDueDate())), Integer.parseInt(Commons.getMonth(foodToUpdate.getDueDate()))-1, Integer.parseInt(Commons.getDay(foodToUpdate.getDueDate())))));
	}
	
	private void updateFoodAfterSave(View view){
		EditText name = (EditText) view.findViewById(R.id.food_name_text);
		foodToUpdate.setName(name.getText().toString());
		EditText description = (EditText) view.findViewById(R.id.food_description_text);
		foodToUpdate.setDescription(description.getText().toString());
		Spinner categorySpinner = (Spinner) view.findViewById(R.id.food_category_spinner);
		TextView catTextView = (TextView)categorySpinner.getSelectedView();
		foodToUpdate.setType(catTextView.getText().toString());
		EditText date = (EditText) view.findViewById(R.id.food_due_date);
		EditText quantityET = (EditText) view.findViewById(R.id.food_quantity_text);
		foodToUpdate.setQuantity(quantityET.getText().toString());
		Spinner UMSpinner=(Spinner) view.findViewById(R.id.food_um_spinner);
		TextView umTextView = (TextView)UMSpinner.getSelectedView();
		foodToUpdate.setMeasurementunity(umTextView.getText().toString());
		String day=date.getText().subSequence(0, 2).toString();
		String month=date.getText().subSequence(3, 5).toString();
		String year=date.getText().subSequence(6, date.getText().length()).toString();
		foodToUpdate.setDueDate(year+month+day);
	}
	
	// ASYNC TASKS
	private class SaveFoodTask extends
				AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
				try {
					ParseObject user = null;
					user = userProxy.getUserParseObject((String) commonsData
							.get(Constants.userNameSP));
					ArrayList<byte[]> imagesByte = new ArrayList<byte[]>();
					if(!isUpdate){
						for (Uri currentUri : imegesUri) {
							imagesByte.add(MediaFile.bitmapResized2Bytes(currentUri, Constants.resized_image_x_size,
									Constants.resized_image_y_size));
						}
					}
					else{
						if(imegesUri.size()==0 && foodToUpdate.getImages().size()!=0){
							imagesByte.add(foodToUpdate.getImages().get(0).getImage());
						}
						else if(imegesUri.size()!=0){
							for (Uri currentUri : imegesUri) {
								imagesByte.add(MediaFile.bitmapResized2Bytes(currentUri, Constants.resized_image_x_size,
										Constants.resized_image_y_size));
							}
						}
					}
					Log.d(logTag, JsonMapper.convertObject2String(imagesByte));
					food = ftrasformer.trasformInFood(getWindow().getDecorView().getRootView(), commonsData,
							imagesByte, user);
					fproxy.addFood(food);
					
					
				}
				catch (JsonGenerationException e) {
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
				} catch (ParseException e){
					e.printStackTrace();
				}
				finally{
					
				}
				return "OK";
			}

			protected void onPostExecute(String foods_out) {
				addFoodProgressDialog.dismiss();
				if(!isUpdate){
					PushService.subscribe(AddFoodActivity.this, Constants.foodSellerChannelPrefix
							+ food.getString(Constants.foodChannelPO),
							FoodAssignmentActivity.class);
				}
				if (shareable) {
					shareOnGPlus();
				} else {
					closeIntent();
					Toast.makeText(
							AddFoodActivity.this,
							getResources().getString(R.string.endSavingFoodMessage),
							Toast.LENGTH_LONG).show();
					finish();
				}
				
			}

			protected void onPreExecute() {
				startDialogLoading();
			}

		}
		
		private void startDialogLoading() {
			addFoodProgressDialog = new ProgressDialog(this);
			addFoodProgressDialog.setMessage("Saving Food...");
			addFoodProgressDialog.setProgressStyle(ProgressDialog.THEME_HOLO_LIGHT);
			addFoodProgressDialog.setCancelable(false);
			addFoodProgressDialog.show();
		}
}
