package com.ats.bestapp.savefoods;

import java.util.ArrayList;
import java.util.List;

import com.ats.bestapp.savefoods.data.Comment;

import android.content.Context;
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
			Comment currComment = items.get(position);
			//commentItemView = new View(context);
			// get layout from mobile.xml
			commentItemView = (RelativeLayout) inflater.inflate(R.layout.comment_table_item,
					null);
			TextView textView = (TextView) commentItemView
					.findViewById(R.id.comment_grid_item_text);
			textView.setText(currComment.getMessage());
			if (!owner.equalsIgnoreCase(currComment.getUser().getUsername())) {
				commentItemView.setGravity(Gravity.RIGHT);
			}
		} else {
			Log.d(logTag, "PosizioneNCW " + position);
			commentItemView = (RelativeLayout) convertView;
			Comment currComment = items.get(position);
			TextView textView = (TextView) commentItemView
					.findViewById(R.id.comment_grid_item_text);
			textView.setText(currComment.getMessage());
			if (!owner.equalsIgnoreCase(currComment.getUser().getUsername())) {
				commentItemView.setGravity(Gravity.RIGHT);
			}
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

}
