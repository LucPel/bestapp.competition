package com.ats.bestapp.savefoods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ats.bestapp.savefoods.data.Comment;
import com.ats.bestapp.savefoods.data.Food;
import com.ats.bestapp.savefoods.data.SavingFoodAssignment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentTableAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<Comment> items;
	private final String logTag="CommentTableAdapter";
	
	public CommentTableAdapter(Context context,List<Comment> items){
		this.context=context;
		this.items=(ArrayList<Comment>) items;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 
			View commentItemView;
			Log.d(logTag, "Posizione "+position );
			if(convertView==null){
				Comment currComment=items.get(position);
				commentItemView = new View(context);
				// get layout from mobile.xml
				commentItemView = inflater.inflate(R.layout.comment_table_item, null);
				TextView textView = (TextView) commentItemView
						.findViewById(R.id.comment_grid_item_text);
				textView.setText(currComment.getMessage());
			}
			else {
				commentItemView = (View) convertView;
			}
		return commentItemView;
	}
	
	public void setComments(List<Comment> comments){
		items=(ArrayList<Comment>) comments;
	}

}
