package com.ats.bestapp.savefoods;

import java.io.IOException;
import java.math.RoundingMode;
import java.security.acl.Owner;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;
import org.json.JSONObject;

import com.ats.bestapp.savefoods.R.id;
import com.ats.bestapp.savefoods.data.Comment;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.SavingFoodAssignment;
import com.ats.bestapp.savefoods.data.User;
import com.ats.bestapp.savefoods.data.proxy.FoodProxy;
import com.ats.bestapp.savefoods.data.proxy.UserProxy;
import com.ats.bestapp.savefoods.utilities.Commons;
import com.ats.bestapp.savefoods.utilities.JsonMapper;
import com.ats.bestapp.savefoods.utilities.MediaFile;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.PushService;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FoodAssignmentActivity extends Activity{
	
	private Food food;
	private String logTag="FoodAssignmentActivity";
	private SharedPreferences settings;
	private UserProxy userProxy;
	private FoodProxy foodProxy;
	private CommentTableAdapter commentTableAdapter;
	private UpdateCommentsReceiver updateCommentsReceiver;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_food_assigment);
		init();
	    //Log.d(logTag, JsonMapper.convertObject2String(food));
	    setViewComponents();
		fillGrid();
	}
	
	private void setViewComponents(){
		TextView name_food=(TextView) findViewById(R.id.food_name_label);
	    name_food.setText(food.getName());
	    TextView dueDate_food=(TextView) findViewById(R.id.food_dueDate_label);
	    dueDate_food.setText(Commons.convertToDate(food.getDueDate()));
	    TextView category_food=(TextView) findViewById(R.id.food_category_label);
	    category_food.setText("("+food.getType()+")");
	    
	    String quantity=food.getQuantity();
	    String um=food.getMeasurementunity();
		if(quantity==null || um==null) {
			quantity="1";
			um="Pz";
		}
	    TextView quantity_food=(TextView) findViewById(R.id.food_quantity_label);
	    quantity_food.setText(quantity+" "+um);
	    
	    TextView id_food=(TextView) findViewById(R.id.food_id_label);
	    id_food.setText(food.getFoodId());
		ImageView imageView = (ImageView) findViewById(R.id.food_category_image);
		SFApplication sfa=(SFApplication) getApplication();
		imageView.setImageResource(sfa.getCategoryIcon(food.getType()));
//			if(food.getImages()!=null && food.getImages().size()!=0){
//				Bitmap image=MediaFile.bitmapFromBytesImage(food.getImages().get(0).getImage());
//				if(image!=null){
//					//imageView.setImageBitmap(Bitmap.createScaledBitmap(image, Constants.standard_image_x_size, Constants.standard_image_y_size, false));
//				}
//				else{
//					imageView.setImageResource(R.drawable.food_no_image_icon);
//				}
//			}
//			else{
//				imageView.setImageResource(R.drawable.food_no_image_icon);
//			}
//			
		
			if(food.getOwner().getUsername().equalsIgnoreCase(settings.getString(Constants.userNameSP, null))){
				ImageView distanceImg=(ImageView) findViewById(R.id.food_distance_image);
				distanceImg.setVisibility(View.INVISIBLE);
				TextView distanceLabel=(TextView) findViewById(R.id.food_distance_label);
				distanceLabel.setVisibility(View.INVISIBLE);
				TextView ownerLabel=(TextView) findViewById(R.id.food_owner_label);
				ownerLabel.setText(getText(R.string.mySelfOwnerLabel));
				Spinner statusSpinner=(Spinner)findViewById(R.id.food_status_spinner);
				statusSpinner.setVisibility(View.VISIBLE);
				ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.foodStatus, R.layout.assignment_spinner_item);
				adapter.setDropDownViewResource(R.layout.assignment_spinner_dropdown_list);
				statusSpinner.setAdapter(adapter);
				statusSpinner.setSelection(statusSpinnerPosition(food.getStatus()));
				Log.d(logTag, "Stato "+food.getStatus()+ "Position "+statusSpinnerPosition(food.getStatus()));
				statusSpinner.setOnItemSelectedListener(new FoodStatusSpinnerOnItemClickListener(food));
			}
			else{
				TextView owner_label=(TextView) findViewById(R.id.food_owner_label);
				owner_label.setText(food.getOwner().getUsername().substring(0, food.getOwner().getUsername().indexOf("@")));
				RelativeLayout parent=(RelativeLayout) owner_label.getParent();
				Spinner statusSpinner=(Spinner)findViewById(R.id.food_status_spinner);
				parent.removeView(statusSpinner);
				TextView distanceLabel=(TextView) findViewById(R.id.food_distance_label);
				float[] results = new float[10];
				double distance = 0;
				Location.distanceBetween(food.getLatitude(), food.getLongitude(),
						sfa.getCurrentLatitude(), sfa.getCurrentLongitude(), results);
				distance = results[0];
				distance = distance * 0.001;
				DecimalFormat df = new DecimalFormat("##.##");
				df.setRoundingMode(RoundingMode.DOWN);
				if (distance > 0) {
					distanceLabel.setText(df.format(distance) + "Km");
				} else {
					distanceLabel.setText("0Km");
				}
			}
			EditText comment_text=(EditText) findViewById(R.id.comment_text);
			comment_text.clearFocus();
	}
	
	private int statusSpinnerPosition(String status){
		int status_int=0;
		if(status.equalsIgnoreCase(Constants.foodStatusDisponibile)) status_int=0;
		else if(status.equalsIgnoreCase(Constants.foodStatusInAssegnazione))status_int=1;
		else if(status.equalsIgnoreCase(Constants.foodStatusAssegnato))status_int=2;
		else if(status.equalsIgnoreCase(Constants.foodStatusScaduto))status_int=3;
		return status_int;
	}
	
	private void init(){
		userProxy=new UserProxy();
		foodProxy=new FoodProxy();
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    actionBar.setDisplayShowTitleEnabled(true);
	    actionBar.setTitle(R.string.foodAssignmentABTitle);
	    food=(Food) getIntent().getSerializableExtra(Constants.foodDetailSP);
	    if(food==null){
			try {
				JSONObject json = new JSONObject(getIntent().getExtras().getString("com.parse.Data"));
				String foodId=json.getString(Constants.food_pn);
				food=foodProxy.getFood(foodId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    else{
	    	try {
				food=foodProxy.getFood(food.getFoodId());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
	    IntentFilter filter = new IntentFilter(UpdateCommentsReceiver.UPDATE_COMMENTS);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        updateCommentsReceiver = new UpdateCommentsReceiver();
        registerReceiver(updateCommentsReceiver, filter);
        registerChatChannel();
	    
	}
	
	private void registerChatChannel(){
		PushService.subscribe(this, Constants.chatChannelPrefix+food.getChannel(), FoodAssignmentActivity.class);
		if(food.getOwner().getUsername().equalsIgnoreCase(settings.getString(Constants.userNameSP, null))){
			PushService.unsubscribe(this, Constants.foodSellerChannelPrefix+food.getChannel());
		}
		else{
			PushService.unsubscribe(this, Constants.foodBuyerChannelPrefix+food.getChannel());
		}
		
	}
	
	private void unregisterChatChannel(){
		
		if(food.getOwner().getUsername().equalsIgnoreCase(settings.getString(Constants.userNameSP, null))){
			PushService.subscribe(this, Constants.foodSellerChannelPrefix+food.getChannel(), FoodAssignmentActivity.class);
		}
		else{
			PushService.subscribe(this, Constants.foodBuyerChannelPrefix+food.getChannel(), FoodAssignmentActivity.class);
		}
		PushService.unsubscribe(this, Constants.chatChannelPrefix+food.getChannel());
	}
	
	public void onPause(){
		Log.d(logTag, "Pause: ");
		unregisterChatChannel();
		super.onPause();
		
	}
	
	public void onResume(){
		Log.d(logTag, "Resume: ");
		registerChatChannel();
		super.onResume();
		
	}
	
	public void onRestart(){
		Log.d(logTag, "Restart	: ");
		registerChatChannel();
		super.onRestart();
		
	}
	
	public void onStop(){
		Log.d(logTag, "Stop: ");
		unregisterChatChannel();
		super.onStop();
		
	}
	
	public void onDestroy(){
		Log.d(logTag, "Destroy: ");
		unregisterChatChannel();
		this.unregisterReceiver(updateCommentsReceiver);
		super.onDestroy();
		//Log.d(logTag, "Destroy: "+food.getStatus());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.action_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	setResultActivity();
	        	Log.d(logTag, "HomeBack: "+food.getStatus());
	        	onBackPressed();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void setResultActivity(){
		Intent intent = new Intent();
		intent.putExtra(Constants.foodDetailSP, food);
		setResult(RESULT_OK, intent);
	}
	
	public void sendComment(View view){
		EditText comment_text=(EditText) findViewById(R.id.comment_text);
		if(comment_text.getText().toString().trim().length()!=0){
			Comment comment=new Comment();
			comment.setMessage(comment_text.getText().toString());
			SimpleDateFormat sdf=new SimpleDateFormat("dd/MM HH:mm");
			String nowDateString=(String) sdf.format(new Date());
			Log.d(logTag, "Filtro scaduti "+nowDateString);
			comment.setMessageTime(nowDateString);
			if(food.getOwner().getUsername().equalsIgnoreCase(settings.getString(Constants.userNameSP, null))){
				comment.setUser(food.getOwner());
			}
			else{
				try {
					User user=userProxy.getUser(settings.getString(Constants.userNameSP, null), this);
					comment.setUser(user);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(food.getSavingFoodAssignment()==null){
				food.setSavingFoodAssignment(new SavingFoodAssignment());
			}
			food.getSavingFoodAssignment().addComment(comment);
			try {
				foodProxy.addCommentToAssigment(food);
				commentTableAdapter.setComments(food.getSavingFoodAssignment().getConversation());
				commentTableAdapter.notifyDataSetChanged();
				comment_text.setText("");
				comment_text.clearFocus();
				sendPushNotification(comment);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void sendPushNotification(Comment comment){
		JSONObject dt=new JSONObject();
		JSONObject dt_longer=new JSONObject();
		try {
			dt.put(Constants.action_pn, "com.ats.bestapp.savefoods.UPDATE_COMMENTS");
			dt.put(Constants.food_pn, food.getFoodId());
			ParsePush push = new ParsePush();
			push.setChannel(Constants.chatChannelPrefix+food.getChannel());
			//push.setMessage(comment.getMessage());
			push.setData(dt);
			push.setExpirationTimeInterval(60);
			push.sendInBackground();
			ParsePush pushLonger=new ParsePush();
			pushLonger.setChannel(Constants.foodBuyerChannelPrefix+food.getChannel());
			dt_longer.put(Constants.food_pn, food.getFoodId());
			dt_longer.put(Constants.alert_pn, comment.getMessage());
			dt_longer.put(Constants.title_pn, comment.getMessage());
			if(food.getOwner().getUsername().equalsIgnoreCase(settings.getString(Constants.userNameSP, null))){
				
			}
			else{
				ParsePush pushLongerSeller=new ParsePush();
				pushLongerSeller.setChannel(Constants.foodSellerChannelPrefix+food.getChannel());
				pushLongerSeller.setData(dt_longer);
				pushLongerSeller.sendInBackground();
			}
			pushLonger.setData(dt_longer);
			pushLonger.sendInBackground();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void fillGrid(){
		GridView gridView = (GridView) findViewById(R.id.gridviewComment);
		if(commentTableAdapter==null){
			commentTableAdapter=new CommentTableAdapter(this, food.getSavingFoodAssignment().getConversation(),food.getOwner().getUsername());
		}
		else{
			commentTableAdapter.setComments(food.getSavingFoodAssignment().getConversation());
		}
		gridView.setAdapter(commentTableAdapter);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra(Constants.foodDetailSP, food);
		setResult(RESULT_OK, intent);
		Log.d(logTag, "OnBackPressed: "+food.getStatus());
	    finish();
	}
	
	public class UpdateCommentsReceiver extends BroadcastReceiver{

		private static final String TAG = "UpdateCommentsReceiver";
		public static final String UPDATE_COMMENTS = "com.ats.bestapp.savefoods.UPDATE_COMMENTS";
		 
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    try {
		      String action = intent.getAction();
		      String channel = intent.getExtras().getString("com.parse.Channel");
		      JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
		      Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
		      try {
				food=foodProxy.getFood(food.getFoodId());
				commentTableAdapter.setComments(food.getSavingFoodAssignment().getConversation());
				commentTableAdapter.notifyDataSetChanged();
				//Toast.makeText(context.getApplicationContext(), food.getName() + " is connected.", Toast.LENGTH_LONG).show();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      /*String foodId=json.getString("foodId");
		      Intent foodAssA=new Intent(context.getApplicationContext(), FoodAssignmentActivity.class);
			    foodAssA.putExtra("foodId", foodId);
			    foodAssA.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    context.startActivity(foodAssA);*/
		    } catch (JSONException e) {
		      Log.d(TAG, "JSONException: " + e.getMessage());
		    }
		    
		  }

	}
}
