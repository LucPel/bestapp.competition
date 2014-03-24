package com.ats.bestapp.savefoods;

import java.io.IOException;

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

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
	    Log.d(logTag, JsonMapper.convertObject2String(food));
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
		if(quantity==null) quantity="1";
	    TextView quantity_food=(TextView) findViewById(R.id.food_quantity_label);
	    quantity_food.setText(quantity);
	    
	    TextView id_food=(TextView) findViewById(R.id.food_id_label);
	    id_food.setText(food.getFoodId());
		ImageView imageView = (ImageView) findViewById(R.id.food_image);
			if(food.getImages()!=null && food.getImages().size()!=0){
				Bitmap image=MediaFile.bitmapFromBytesImage(food.getImages().get(0).getImage());
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
			
		Spinner statusSpinner=(Spinner)findViewById(R.id.food_status_spinner);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.foodStatus, R.layout.assignment_spinner_item);
		adapter.setDropDownViewResource(R.layout.assignment_spinner_dropdown_list);
		statusSpinner.setAdapter(adapter);
		statusSpinner.setSelection(statusSpinnerPosition(food.getStatus()));
		Log.d(logTag, "Stato "+food.getStatus()+ "Position "+statusSpinnerPosition(food.getStatus()));
		statusSpinner.setOnItemSelectedListener(new FoodStatusSpinnerOnItemClickListener(food));
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
	    food=(Food) getIntent().getSerializableExtra(Constants.foodDetailSP);
	    IntentFilter filter = new IntentFilter(UpdateCommentsReceiver.UPDATE_COMMENTS);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        updateCommentsReceiver = new UpdateCommentsReceiver();
        registerReceiver(updateCommentsReceiver, filter);
	    settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
	}
	
	public void onPause(){
		Log.d(logTag, "Pause: ");
		super.onPause();
		
	}
	
	public void onResume(){
		Log.d(logTag, "Resume: ");
		super.onResume();
		
	}
	
	public void onRestart(){
		Log.d(logTag, "Restart	: ");
		super.onRestart();
		
	}
	
	public void onStop(){
		Log.d(logTag, "Stop: ");
		super.onStop();
		
	}
	
	public void onDestroy(){
		Log.d(logTag, "Destroy: ");
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
		Comment comment=new Comment();
		comment.setMessage(comment_text.getText().toString());
		
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
			sendPushNotification(comment);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendPushNotification(Comment comment){
		JSONObject dt=new JSONObject();
		try {
			dt.put("action", "com.ats.bestapp.savefoods.UPDATE_COMMENTS");
			dt.put("foodId", food.getFoodId());
			ParsePush push = new ParsePush();
			push.setChannel(Constants.foodSellerChannelPrefix+food.getChannel());
			push.setMessage(comment.getMessage());
			push.setData(dt);
			push.sendInBackground();
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
				Toast.makeText(context.getApplicationContext(), food.getName() + " is connected.", Toast.LENGTH_LONG)
				.show();
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
