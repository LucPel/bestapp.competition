package com.ats.bestapp.savefoods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.ats.bestapp.savefoods.data.Comment;
import com.ats.bestapp.savefoods.utilities.Commons;

import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommentTableAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Comment> items;
	private String owner;
	private final String logTag = "CommentTableAdapter";

	public CommentTableAdapter(Context context, List<Comment> items,
			String owner) {
		this.context = context;
		this.items = (ArrayList<Comment>) items;
		this.owner = owner;
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

		RelativeLayout commentItemView;
		Log.d(logTag, "Posizione " + position);
		if (convertView == null) {
			Log.d(logTag, "PosizioneCW " + position);
			//commentItemView = new View(context);
			// get layout from mobile.xml
			commentItemView = (RelativeLayout) inflater.inflate(R.layout.comment_table_item,
					null);
			setItemView(commentItemView, position);
		} else {
			Log.d(logTag, "PosizioneNCW " + position);
			commentItemView = (RelativeLayout) convertView;
			setItemView(commentItemView, position);
			
		}
		return commentItemView;
	}

	public void setComments(List<Comment> comments) {
		items = (ArrayList<Comment>) comments;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	private void setItemView(RelativeLayout commentItemView, int position){
		Comment currComment = items.get(position);
		TextView textView = (TextView) commentItemView
				.findViewById(R.id.comment_grid_item_text);
		TextView textUserView = (TextView) commentItemView
				.findViewById(R.id.comment_grid_item_text_user);
		textUserView.setText(Commons.getUsernameShow(currComment.getUser().getUsername()));
		textView.setText(currComment.getMessage());
		TextView timestampText = (TextView) commentItemView
				.findViewById(R.id.comment_grid_timestamp_text);
		timestampText.setText(currComment.getMessageTime());
		if (!owner.equalsIgnoreCase(currComment.getUser().getUsername())) {
			commentItemView.setGravity(Gravity.RIGHT);
			RelativeLayout chatBubbleView = (RelativeLayout) commentItemView
					.findViewById(R.id.comment_grid_chat_bubble);
			chatBubbleView.setBackgroundResource(R.drawable.chat_bubbles);
			chatBubbleView.setPadding(15, 5, 15, 5);
			textView.setTextColor(Color.parseColor("#000000"));
			textUserView.setTextColor(Color.parseColor("#ff0000"));
		}
		else{
			commentItemView.setGravity(Gravity.LEFT);
			RelativeLayout chatBubbleView = (RelativeLayout) commentItemView
					.findViewById(R.id.comment_grid_chat_bubble);
			chatBubbleView.setBackgroundResource(R.drawable.chat_bubbles_owner);
			chatBubbleView.setPadding(15, 5, 15, 5);
			textView.setTextColor(Color.parseColor("#000000"));
			textUserView.setTextColor(Color.parseColor("#ff9900"));
			textView.setText(currComment.getMessage());
		}
	}
}
