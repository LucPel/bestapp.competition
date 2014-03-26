package com.ats.bestapp.savefoods;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.json.JSONException;

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

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.AdapterView.OnItemClickListener;

public class ChatNotOwnerFoodActivity extends Activity{
	
	private Food food;
	private String logTag="ChatNotOwnerFoodActivity";
	private SharedPreferences settings;
	private UserProxy userProxy;
	private FoodProxy foodProxy;
	private CommentTableAdapter commentTableAdapter;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_notowner);
		init();
	    Log.d(logTag, JsonMapper.convertObject2String(food));
		fillGrid();
		
	}	
	
	private void init(){
		userProxy=new UserProxy();
		foodProxy=new FoodProxy();
		Parse.initialize(this, Constants.parseAppId, Constants.parseClientKey);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    food=(Food) getIntent().getSerializableExtra(Constants.foodDetailSP);
	    settings = getSharedPreferences(Constants.sharedPreferencesName, 0);
	}
	
	public void onPause(){
		super.onPause();
		//Log.d(logTag, "Pause: "+food.getStatus());
	}
	
	public void onStop(){
		super.onStop();
		
	}
	
	public void onDestroy(){
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void fillGrid(){
		GridView gridView = (GridView) findViewById(R.id.gridviewComment);
		if(commentTableAdapter==null){
			//commentTableAdapter=new CommentTableAdapter(this, food.getSavingFoodAssignment().getConversation());
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
	
}
